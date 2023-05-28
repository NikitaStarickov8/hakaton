package com.demo.hakaton.analysis.model.enums;

public enum SpecializationEnum {

    OTOLARINGOLOGY("otolaringology", "врач-оториноларинголог"),
    CARDIOLODY("cardiology", "врач-кардиолог"),
    NEUROLODY("neurology", "врач-невролог"),
    UNDEFINED("undefined", "Специализация отсутствует");

    private final String value;
    private final String russianName;

    SpecializationEnum(String value, String russianName) {
        this.value = value;
        this.russianName = russianName;
    }

    public static SpecializationEnum getSpecializationFromRussianName(String russianName) {
        for (SpecializationEnum specializationEnum : SpecializationEnum.values()) {
            if (specializationEnum.russianName.equals(russianName)) {
                return specializationEnum;
            }
        }
        return SpecializationEnum.UNDEFINED;
    }

    public static SpecializationEnum getSpecializationFromCode(String value) {
        for (SpecializationEnum specializationEnum : SpecializationEnum.values()) {
            if (specializationEnum.value.equals(value)) {
                return specializationEnum;
            }
        }
        return SpecializationEnum.UNDEFINED;
    }

    public String getValue() {
        return value;
    }

    public String getRussianName() {
        return russianName;
    }
}
