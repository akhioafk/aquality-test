package com.alabs.automation.phoenix.models.formula.formula;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivedEntityImportDetails {
    private String model;
    private String value;
    private String property;
}
