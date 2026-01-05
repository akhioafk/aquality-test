package com.alabs.automation.phoenix.constants.formulaservice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserAttribute {
    TIER_MISSION_REWARDS_MODIFIER("tier_mission_rewards_modifier"),
    TIER_MISSION_PROGRESS_BAR_BONUS("tier_mission_progress_bar_bonus"),
    TIER_YARDS_MODIFIER("tier_yards_modifier"),
    TIER_SEASON_CURRENCY_MODIFIER("tier_season_currency_modifier"),
    NEW_ATTRIBUTE("new_attribute"),
    TEST_ATTRIBUTE("test_attribute"),
    ATTRIBUTE_EXCLUDED_FROM_SNAPSHOTS_VIA_PROPERTIES("attribute_excluded_from_snapshots_via_properties"),
    ATTRIBUTE_EXCLUDED_FROM_SNAPSHOTS_VIA_PATTERN("attribute_excluded_from_snapshots_via_pattern"),
    TEST_ATTRIBUTE_REWARD_FILTERING("test_attribute_for_rewards_filtering"),
    NOT_EXISTING_ATTRIBUTE("not_existing_attribute");
    @Getter
    private final String id;
}
