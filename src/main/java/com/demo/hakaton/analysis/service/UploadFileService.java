package com.demo.hakaton.analysis.service;

import com.demo.hakaton.analysis.entity.DiagnosisEntity;
import com.demo.hakaton.analysis.entity.DiagnosisExaminationLnkEntity;
import com.demo.hakaton.analysis.entity.PatientEntity;
import com.demo.hakaton.analysis.model.enums.DiagnosisStatusEnum;
import com.demo.hakaton.analysis.model.enums.ExaminationStatusEnum;
import com.demo.hakaton.analysis.model.enums.SpecializationEnum;
import com.demo.hakaton.analysis.model.file.AnalysisReportRequestModel;
import com.demo.hakaton.analysis.model.file.ImportResponse;
import com.demo.hakaton.analysis.repository.DiagnosisExaminationLnkRepository;
import com.demo.hakaton.analysis.repository.DiagnosisRepository;
import com.demo.hakaton.analysis.repository.PatientRepository;
import com.demo.hakaton.dictionary.entity.ExaminationDictionaryEntity;
import com.demo.hakaton.dictionary.entity.ExaminationEntity;
import com.demo.hakaton.dictionary.entity.SicknessEntity;
import com.demo.hakaton.dictionary.repository.ComparingRepository;
import com.demo.hakaton.dictionary.repository.ExaminationDictionaryRepository;
import com.demo.hakaton.dictionary.repository.ExaminationRepository;
import com.demo.hakaton.dictionary.repository.SicknessRepository;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UploadFileService {

    private final ExaminationDictionaryRepository examinationDictionaryRepository;
    private final SicknessRepository sicknessRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final PatientRepository patientRepository;
    private final ExaminationRepository examinationRepository;
    private final ComparingRepository comparingEntityRepository;

    private final DiagnosisExaminationLnkRepository diagnosisExaminationLnkRepository;

    //Метод импорта файла в БД
    public ImportResponse parseFile(MultipartFile multipartFile) {
        List<AnalysisReportRequestModel> analysisReportRequestModels;
        try {
            var stream = multipartFile.getInputStream();
            analysisReportRequestModels = parseFileToModel(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        analysisReportRequestModels.forEach(this::createDiagnosis);

        var response = new ImportResponse();
        response.setIsSuccess(true);
        return response;
    }

    //Создание приема в БД
    private void createDiagnosis(AnalysisReportRequestModel model) {
        if (checkExistDiagnosis(model)) {
            var diagnosis = new DiagnosisEntity();
            var status = identifyDiagnosisStatus(model);
            diagnosis.setStatus(status);
            diagnosis.setDoctor(SpecializationEnum.getSpecializationFromRussianName(model.getSpecialization()).getValue());
            diagnosis.setReceiptDate(parseDate(model.getDateOfVisit()));

            var sickness = sicknessRepository.getByCode(model.getSicknessCode());
            if (Objects.nonNull(sickness)) {
                diagnosis.setSickness(sickness);
            } else {
                sickness = createSickness(model);
                diagnosis.setSickness(sickness);
            }

            var patient = createPatient(model);

            diagnosis.setPatient(patient);
            var savedDiagnosis = diagnosisRepository.save(diagnosis);

            var appointments = model.getAppointment().split(" ");
            for (String appointment : appointments) {
                var examination = examinationDictionaryRepository.getFirstByCodeIgnoreCase(appointment);
                if (examination != null) {
                    createDiagnosisExaminationLnk(examination, savedDiagnosis);
                }
            }
        }
    }

    //Создание связи приема и исследования
    private void createDiagnosisExaminationLnk(ExaminationDictionaryEntity examination, DiagnosisEntity diagnosis) {
        var diagnosisExaminationLnk = new DiagnosisExaminationLnkEntity();
        diagnosisExaminationLnk.setExamination(examination);
        diagnosisExaminationLnk.setDiagnosis(diagnosis);
        diagnosisExaminationLnk.setStatus(addStatusToLnkExamination(examination, diagnosis));

        diagnosisExaminationLnkRepository.save(diagnosisExaminationLnk);
    }

    //Алгоритм выбора статуса исследования
    private String addStatusToLnkExamination(ExaminationDictionaryEntity examination, DiagnosisEntity diagnosis) {
        var comparingEntity = comparingEntityRepository.getBySicknessGroupCode(diagnosis.getSickness().getComparingGroup());
        if (comparingEntity != null) {
            var allByComparingGroup = examinationRepository.getAllByComparingGroup(comparingEntity.getExaminationGroupCode());

            for (ExaminationEntity examinationEntity : allByComparingGroup) {
                if (examinationEntity.getCode().equals(examination.getCode()) && examinationEntity.getPriority() >= 0.8) {
                    return ExaminationStatusEnum.STANDARD.getCode();
                } else if (examinationEntity.getCode().equals(examination.getCode()) && examinationEntity.getPriority() < 0.8) {
                    return ExaminationStatusEnum.POSSIBLE.getCode();
                } else if (!examinationEntity.getCode().equals(examination.getCode())) {
                    if (examinationEntity.getPriority() > 0.8) {
                        var examinationDictionaryEntity = examinationDictionaryRepository.getFirstByCodeIgnoreCase(examinationEntity.getCode());
                        if (examinationDictionaryEntity != null) {
                            //Если исследования не было назначено, но оно имеется в рекомендуемых - создаем исследование с кодом MISSING
                            if (!diagnosisExaminationLnkRepository.existsByDiagnosisAndExamination(diagnosis, examinationDictionaryEntity)) {
                                var diagnosisExaminationLnk = new DiagnosisExaminationLnkEntity();
                                diagnosisExaminationLnk.setExamination(examinationDictionaryEntity);
                                diagnosisExaminationLnk.setDiagnosis(diagnosis);
                                diagnosisExaminationLnk.setStatus(ExaminationStatusEnum.MISSING.getCode());
                                diagnosisExaminationLnkRepository.save(diagnosisExaminationLnk);
                            }
                        }
                    }
                }
            }
        }
        return ExaminationStatusEnum.EXCESS.getCode();
    }


    //Создание объекта болячки
    private SicknessEntity createSickness(AnalysisReportRequestModel model) {
        var sickness = new SicknessEntity();
        sickness.setCode(model.getSicknessCode());
        sickness.setName(model.getDiagnosis());
        return sicknessRepository.save(sickness);

    }

    //Для предотвращения дубликатов записи
    private Boolean checkExistDiagnosis(AnalysisReportRequestModel model) {
        var patient = patientRepository.getByEmiasId(model.getPatientId());
        var sickness = sicknessRepository.getByCode(model.getSicknessCode());
        return !diagnosisRepository.existsByPatientAndSicknessAndReceiptDate(patient, sickness, parseDate(model.getDateOfVisit()));
    }

    private PatientEntity createPatient(AnalysisReportRequestModel model) {
        var oldPatient = patientRepository.getByEmiasId(model.getPatientId());
        if (oldPatient != null) {
            return oldPatient;
        } else {
            var patient = new PatientEntity();
            patient.setSex(model.getSex());
            patient.setBirthDate(parseDate(model.getDateOfBirth()));
            patient.setEmiasId(model.getPatientId());
            return patientRepository.save(patient);
        }
    }

    private LocalDate parseDate(String text) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

        try {
            return format.parse(text).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            return LocalDate.now();
        }
    }

    //Алгоритм выбора статуса диагноза. Упрощен, т.к. в тестовых данных очень мало назначений, при большом рекомендательном списке Мосгорздрава и Минздрава
    private String identifyDiagnosisStatus(AnalysisReportRequestModel model) {
        var sicknessEntity = sicknessRepository.getByCode(model.getSicknessCode());
        if (Objects.isNull(sicknessEntity)) {
            return DiagnosisStatusEnum.UNVERIFIABLE.getValue();
        }
        var comparingEntity = comparingEntityRepository.getBySicknessGroupCode(sicknessEntity.getComparingGroup());
        if (Objects.isNull(comparingEntity)) {
            return DiagnosisStatusEnum.UNVERIFIABLE.getValue();
        }
        var examinations = examinationRepository.getAllByComparingGroup(comparingEntity.getExaminationGroupCode());
        var sizeOfExaminations = examinations.size();

        if (sizeOfExaminations == 0) {
            return DiagnosisStatusEnum.UNVERIFIABLE.getValue();
        }
        var standardExaminations = examinations.stream().filter(examinationEntity -> examinationEntity.getPriority() > 0.8).map(ExaminationEntity::getCode).toList();
        var lessStandardExaminations = examinations.stream().filter(examinationEntity -> examinationEntity.getPriority() < 0.8).map(ExaminationEntity::getCode).toList();

        var standard = 0;
        var optimal = 0;
        var currentAppointments = model.getAppointment().split(" ");
        for (String currentAppointment : currentAppointments) {
            if (standardExaminations.contains(currentAppointment)) {
                standard++;
            } else if (lessStandardExaminations.contains(currentAppointment)) {
                optimal++;
            }
        }

        if (standard > optimal) {
            return DiagnosisStatusEnum.STANDARD.getValue();
        } else if (standard == 0 && optimal == 0) {
            return DiagnosisStatusEnum.SUB_OPTIMAL.getValue();
        } else {
            return DiagnosisStatusEnum.POOR_QUALITY.getValue();
        }

    }


    // Парсинг строки файла в Java объект для дальнейшей работы
    private List<AnalysisReportRequestModel> parseFileToModel(InputStream stream) {
        var options = PoijiOptions.PoijiOptionsBuilder.settings()
                .addListDelimiter(";")
                .build();

        List<AnalysisReportRequestModel> models = Poiji.fromExcel(stream, PoijiExcelType.XLSX, AnalysisReportRequestModel.class, options);


        models.forEach(analysisReportRequestModel -> {
            var appointmentCell = analysisReportRequestModel.getAppointment();
            analysisReportRequestModel.setAppointment("");
            var correctAppointments = appointmentCell.replaceAll("\n\n", "\n");
            var correctAppointments2 = correctAppointments.replaceAll(";", "\n");
            var appointments = correctAppointments2.split("\n");
            for (String appointment : appointments) {
                var dictEntity = examinationDictionaryRepository.getFirstByNameIgnoreCase(appointment);
                if (Objects.isNull(dictEntity)) {
                    dictEntity = examinationDictionaryRepository.getFirstByNameContainingIgnoreCase(appointment);
                }
                if (Objects.nonNull(dictEntity)) {
                    var oldAppointment = analysisReportRequestModel.getAppointment();
                    var newAppointment = oldAppointment + " " + dictEntity.getCode();
                    analysisReportRequestModel.setAppointment(newAppointment.trim());
                }
            }
        });


        return models;
    }
}
