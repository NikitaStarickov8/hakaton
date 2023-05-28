package com.demo.hakaton.analysis.model.enums;

public enum ExaminationStatusEnum {

    STANDARD("Обязательно и назначено", "standard"),
    MISSING("Обязательно и не назначено", "missing"),
    POSSIBLE("Не обязательно и назначено", "possible"),
    EXCESS("Не соответствует диагнозу и назначено или нет такого исследования в стандарте", "excess");

    private final String value;
    private final String code;

    ExaminationStatusEnum(String value, String code) {
        this.value = value;
        this.code = code;
    }

    public static ExaminationStatusEnum getSpecializationFromCode(String code) {
        for (ExaminationStatusEnum statusEnum : ExaminationStatusEnum.values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum;
            }
        }
        return ExaminationStatusEnum.EXCESS;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
