package com.alabs.automation.phoenix.models.formula.formula;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivedFormulaImportResponse {
    private String code;
    private String message;
    private ArchivedEntityImportDetails details;
}
