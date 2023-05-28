package com.demo.hakaton.analysis.model.enums;

public enum DiagnosisStatusEnum {

    STANDARD("standard", "Соответствует стандарту"),
    POOR_QUALITY("poorQuality", "Не соответствует качеству медицинской услуги"),
    SUB_OPTIMAL("suboptimal", "Не оптимальное"),
    UNVERIFIABLE("unverifiable", "Диагноз отсутствует в справочнике");

    private final String value;
    private final String description;

    DiagnosisStatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static DiagnosisStatusEnum fromStatusCode(String status) {
        for (DiagnosisStatusEnum diagnosisStatusEnum : DiagnosisStatusEnum.values()) {
            if (diagnosisStatusEnum.value.equals(status)) {
                return diagnosisStatusEnum;
            }
        }
        return DiagnosisStatusEnum.UNVERIFIABLE;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
