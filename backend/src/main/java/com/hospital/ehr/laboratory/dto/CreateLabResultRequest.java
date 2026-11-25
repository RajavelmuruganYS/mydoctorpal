package com.hospital.ehr.laboratory.dto;

import jakarta.validation.constraints.Size;

public class CreateLabResultRequest {

    @Size(
            max = 10000,
            message = "Result value must not exceed 10000 characters"
    )
    private String resultValue;

    @Size(
            max = 50,
            message = "Unit must not exceed 50 characters"
    )
    private String unit;

    @Size(
            max = 200,
            message = "Reference range must not exceed 200 characters"
    )
    private String referenceRange;

    @Size(
            max = 5000,
            message = "Interpretation must not exceed 5000 characters"
    )
    private String interpretation;

    public CreateLabResultRequest() {
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(String referenceRange) {
        this.referenceRange = referenceRange;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }
}