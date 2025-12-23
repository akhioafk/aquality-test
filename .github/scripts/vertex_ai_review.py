import os
import json
import sys
from typing import List, Dict, Any
from github import Github
from google.cloud import aiplatform
from vertexai.generative_models import GenerativeModel, Part, Content, GenerationConfig

# Configuration
GITHUB_TOKEN = os.environ.get('GITHUB_TOKEN')
GCP_PROJECT_ID = os.environ.get('GCP_PROJECT_ID')
GCP_LOCATION = os.environ.get('GCP_LOCATION', 'us-central1')
VERTEX_AI_MODEL = os.environ.get('VERTEX_AI_MODEL', 'gemini-1.5-pro')

# System prompt for the AI reviewer
SYSTEM_MESSAGE = """
You are `@coderabbitai` (aka `github-actions[bot]`), a language model 
acting as a highly experienced test automation engineer. Provide a thorough 
review of the code hunks and suggest code snippets to improve key areas such as:
  - Logic
  - Security
  - Performance
  - Data races
  - Consistency
  - Error handling
  - Maintainability
  - Modularity
  - Complexity
  - Optimization
  - Best practices: DRY, SOLID, KISS

Do not comment on minor code style issues, missing comments/documentation. 
Identify and resolve significant concerns to improve overall code quality 
while deliberately disregarding minor issues. Take into account that the 
code under review are automated tests. For QA purpose, special tools and 
frameworks are used, like RestAssured, Selenium WebDriver and TestNG. 
These tools may already include some core validations inside.

Provide your review in the following JSON format:
{
  "summary": "Overall summary of the PR",
  "files": [
    {
      "filename": "path/to/file",
      "issues": [
        {
          "line": <line_number>,
          "severity": "high|medium|low",
          "category": "logic|security|performance|etc",
          "message": "Description of the issue",
          "suggestion": "Code snippet or suggestion to fix"
        }
      ]
    }
  ]
}
"""

class VertexAIPRReviewer:
    def __init__(self):
        # Initialize Vertex AI
        aiplatform.init(project=GCP_PROJECT_ID, location=GCP_LOCATION)
        
        # Initialize the model
        self.model = GenerativeModel(
            VERTEX_AI_MODEL,
            system_instruction=SYSTEM_MESSAGE
        )
        
        # Configure generation parameters
        self.generation_config = GenerationConfig(
            temperature=0.05,  # Low temperature for consistent reviews
            max_output_tokens=8192,
            top_p=0.95,
        )
        
        # Initialize GitHub client
        self.github = Github(GITHUB_TOKEN)
        
    def get_pr_info(self):
        """Get PR information from GitHub context"""
        # Get repository and PR number from GitHub event
        with open(os.environ.get('GITHUB_EVENT_PATH', ''), 'r') as f:
            event_data = json.load(f)
        
        repo_name = event_data['repository']['full_name']
        pr_number = event_data['pull_request']['number']
        
        repo = self.github.get_repo(repo_name)
        pr = repo.get_pull(pr_number)
        
        return repo, pr
    
    def get_pr_diff(self, pr):
        """Get the diff of the PR"""
        files = pr.get_files()
        diff_content = []
        
        for file in files:
            if file.patch:  # Only process files with changes
                diff_content.append({
                    'filename': file.filename,
                    'status': file.status,
                    'additions': file.additions,
                    'deletions': file.deletions,
                    'patch': file.patch
                })
        
        return diff_content
    
    def review_code(self, diff_content: List[Dict]) -> Dict[str, Any]:
        """Send code to Vertex AI for review"""
        # Prepare the prompt
        code_review_prompt = f"""
        Please review the following pull request changes:
        
        Files changed: {len(diff_content)}
        
        Detailed changes:
        """
        
        for file_info in diff_content:
            code_review_prompt += f"\n\nFile: {file_info['filename']}\n"
            code_review_prompt += f"Status: {file_info['status']}\n"
            code_review_prompt += f"Additions: +{file_info['additions']} Deletions: -{file_info['deletions']}\n"
            code_review_prompt += f"Patch:\n```diff\n{file_info['patch']}\n```\n"
        
        code_review_prompt += "\n\nPlease provide a comprehensive review in JSON format as specified."
        
        # Generate review
        try:
            response = self.model.generate_content(
                code_review_prompt,
                generation_config=self.generation_config
            )
            
            # Parse the JSON response
            review_text = response.text
            
            # Extract JSON from the response (handle markdown code blocks)
            if '```json' in review_text:
                review_text = review_text.split('```json')[1].split('```')[0]
            elif '```' in review_text:
                review_text = review_text.split('```')[1].split('```')[0]
            
            return json.loads(review_text)
        except Exception as e:
            print(f"Error generating review: {e}")
            return {
                "summary": "Error occurred during code review",
                "files": []
            }
    
    def post_review_comments(self, pr, review_result: Dict[str, Any]):
        """Post review comments to the PR"""
        # Post the overall summary as a PR review
        if review_result.get('summary'):
            pr.create_review(
                body=f"## 🤖 AI Code Review Summary\n\n{review_result['summary']}",
                event='COMMENT'
            )
        
        # Post inline comments for specific issues
        for file_review in review_result.get('files', []):
            filename = file_review['filename']
            
            for issue in file_review.get('issues', []):
                comment_body = f"""
**{issue['severity'].upper()} - {issue['category'].title()}**

{issue['message']}

{f"**Suggestion:**\n```python\n{issue['suggestion']}\n```" if issue.get('suggestion') else ""}
"""
                
                try:
                    # Get the commit SHA for the PR
                    commit = pr.get_commits().reversed[0]
                    
                    # Post inline comment
                    pr.create_review_comment(
                        body=comment_body,
                        commit=commit,
                        path=filename,
                        line=issue.get('line', 1)
                    )
                except Exception as e:
                    print(f"Error posting inline comment: {e}")
                    # Fall back to posting as a regular comment
                    pr.create_issue_comment(
                        f"**Comment for `{filename}` (line {issue.get('line', 'N/A')})**\n\n{comment_body}"
                    )
    
    def run(self):
        """Main execution flow"""
        try:
            print("Starting PR review with Vertex AI...")
            
            # Get PR information
            repo, pr = self.get_pr_info()
            print(f"Reviewing PR #{pr.number}: {pr.title}")
            
            # Get PR diff
            diff_content = self.get_pr_diff(pr)
            
            if not diff_content:
                print("No changes to review")
                return
            
            print(f"Found {len(diff_content)} files to review")
            
            # Review code using Vertex AI
            review_result = self.review_code(diff_content)
            
            # Post review comments
            self.post_review_comments(pr, review_result)
            
            print("Review completed successfully!")
            
        except Exception as e:
            print(f"Error during PR review: {e}")
            sys.exit(1)

if __name__ == "__main__":
    reviewer = VertexAIPRReviewer()
    reviewer.run()