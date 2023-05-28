package com.demo.hakaton.analysis.service;

import com.demo.hakaton.analysis.entity.DiagnosisEntity;
import com.demo.hakaton.analysis.model.count.ReceptionCountRs;
import com.demo.hakaton.analysis.model.count.ReceptionItem;
import com.demo.hakaton.analysis.model.enums.DiagnosisStatusEnum;
import com.demo.hakaton.analysis.model.enums.SpecializationEnum;
import com.demo.hakaton.analysis.repository.DiagnosisExaminationLnkRepository;
import com.demo.hakaton.analysis.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceptionCountService {

    public static final String SECTION_SPECIALIZATION = "specialization";
    public static final String SECTION_DATE = "date";
    public static final String DAY_NAME = "day";
    public static final String WEEK_NAME = "week";
    public static final String MONTH_NAME = "month";
    public static final String QUARTER_NAME = "quarter";
    public static final String YEAR_NAME = "year";
    private final DiagnosisRepository diagnosisRepository;

    //Запрос на отрисовку дашборда
    public ReceptionCountRs getCount(String section, Date dateFrom, Date dateTo, List<String> specialization) {
        var response = new ReceptionCountRs();
        List<ReceptionItem> responseItemsList = new ArrayList<>();
        var from = convertToLocalDate(dateFrom);
        var to = convertToLocalDate(dateTo);

        if (Objects.isNull(specialization)) {
            specialization = Arrays.stream(SpecializationEnum.values()).map(SpecializationEnum::getValue).collect(Collectors.toList());
        }

        var allByFilter = diagnosisRepository.getAllByDoctorAndDate(specialization, from, to);

        if (section.equals(SECTION_SPECIALIZATION)) {
            for (String spec : specialization) {
                var allByDoctor = allByFilter.stream().filter(diagnosis -> diagnosis.getDoctor().equals(spec)).collect(Collectors.toList());
                responseItemsList.add(createItem(spec, allByDoctor));
            }
        } else if (section.equals(SECTION_DATE)) {

            var timePeriod = identifyTimePeriod(dateFrom, dateTo);
            var dates = allByFilter.stream().map(DiagnosisEntity::getReceiptDate).collect(Collectors.toSet());

            switch (timePeriod) {
                case DAY_NAME -> daySort(responseItemsList, allByFilter, dates);
                case WEEK_NAME -> weekSort(responseItemsList, from, allByFilter);
                case MONTH_NAME -> monthSort(responseItemsList, from, allByFilter);
                case QUARTER_NAME -> quarterSort(responseItemsList, from, allByFilter);
                case YEAR_NAME -> yearSort(responseItemsList, from, allByFilter);
            }
        } else {
            response.setItems(responseItemsList);
            return response;
        }
        response.setItems(responseItemsList);
        return response;
    }

    //Сортировка ответа для отрисовки дашборда, в случае если диапозон фильтра соответствует годам
    private void yearSort(List<ReceptionItem> responseItemsList, LocalDate from, List<DiagnosisEntity> allByReceiptDateBetween) {
        var firstYearEnd = from.plusYears(1);
        var firstYear = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isBefore(firstYearEnd)).toList();
        if (!firstYear.isEmpty()) {
            var receptionItem = createItem(from + " - " + firstYearEnd, firstYear);
            responseItemsList.add(receptionItem);
        }
    }

    //Сортировка ответа для отрисовки дашборда, в случае если диапозон фильтра соответствует кварталам
    private void quarterSort(List<ReceptionItem> responseItemsList, LocalDate from, List<DiagnosisEntity> allByReceiptDateBetween) {
        var firstQuarterEnd = from.plusMonths(3);
        var secondQuarterEnd = firstQuarterEnd.plusMonths(3);
        var thirdQuarterEnd = secondQuarterEnd.plusMonths(3);
        var fourthQuarterEnd = thirdQuarterEnd.plusMonths(3);

        var firstQuarter = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isBefore(firstQuarterEnd)).toList();
        if (!firstQuarter.isEmpty()) {
            var receptionItem = createItem(from + " - " + firstQuarterEnd, firstQuarter);
            responseItemsList.add(receptionItem);
        }

        var secondQuarter = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isBefore(thirdQuarterEnd) && diagnosis.getReceiptDate().isAfter(firstQuarterEnd)).toList();
        if (!secondQuarter.isEmpty()) {
            var receptionItem = createItem(firstQuarterEnd + " - " + secondQuarterEnd, secondQuarter);
            responseItemsList.add(receptionItem);
        }

        var thirdQuarter = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isBefore(fourthQuarterEnd) && diagnosis.getReceiptDate().isAfter(secondQuarterEnd)).toList();
        if (!thirdQuarter.isEmpty()) {
            var receptionItem = createItem(secondQuarterEnd + " - " + thirdQuarterEnd, thirdQuarter);
            responseItemsList.add(receptionItem);
        }

        var fourthQuarter = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isAfter(thirdQuarterEnd)).toList();
        if (!fourthQuarter.isEmpty()) {
            var receptionItem = createItem(thirdQuarterEnd + " - " + fourthQuarterEnd, fourthQuarter);
            responseItemsList.add(receptionItem);
        }
    }

    //Сортировка ответа для отрисовки дашборда, в случае если диапозон фильтра соответствует месяцам
    private void monthSort(List<ReceptionItem> responseItemsList, LocalDate from, List<DiagnosisEntity> allByReceiptDateBetween) {
        var firstMonthEnd = from.plusMonths(1);
        var secondMonthEnd = firstMonthEnd.plusMonths(1);
        var thirdMonthEnd = secondMonthEnd.plusMonths(1);

        var firstMonth = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isBefore(firstMonthEnd)).toList();
        if (!firstMonth.isEmpty()) {
            var receptionItem = createItem(from + " - " + firstMonthEnd, firstMonth);
            responseItemsList.add(receptionItem);
        }

        var secondMonth = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isBefore(thirdMonthEnd) && diagnosis.getReceiptDate().isAfter(firstMonthEnd)).toList();
        if (!secondMonth.isEmpty()) {
            var receptionItem = createItem(firstMonthEnd + " - " + secondMonthEnd, secondMonth);
            responseItemsList.add(receptionItem);
        }

        var thirdMonth = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isAfter(thirdMonthEnd)).toList();
        if (!thirdMonth.isEmpty()) {
            var receptionItem = createItem(secondMonthEnd + " - " + thirdMonthEnd, thirdMonth);
            responseItemsList.add(receptionItem);
        }
    }

    //Сортировка ответа для отрисовки дашборда, в случае если диапозон фильтра соответствует дням
    private void daySort(List<ReceptionItem> responseItemsList, List<DiagnosisEntity> allByReceiptDateBetween, Set<LocalDate> dates) {
        for (LocalDate date : dates) {
            var diagnosisEntities = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().equals(date)).collect(Collectors.toList());
            var receptionItem = createItem(date.toString(), diagnosisEntities);
            responseItemsList.add(receptionItem);
        }
    }

    //Сортировка ответа для отрисовки дашборда, в случае если диапозон фильтра соответствует неделям
    private void weekSort(List<ReceptionItem> responseItemsList, LocalDate from, List<DiagnosisEntity> allByReceiptDateBetween) {
        var firstWeekEnd = from.plusDays(7);
        var secondWeekEnd = firstWeekEnd.plusWeeks(1);
        var thirdWeekEnd = secondWeekEnd.plusWeeks(1);
        var fourthWeekEnd = thirdWeekEnd.plusWeeks(1);

        var firstWeek = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isBefore(firstWeekEnd)).toList();
        if (!firstWeek.isEmpty()) {
            var receptionItem = createItem(from + " - " + firstWeekEnd, firstWeek);
            responseItemsList.add(receptionItem);
        }

        var secondWeek = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isBefore(thirdWeekEnd) && diagnosis.getReceiptDate().isAfter(firstWeekEnd)).toList();
        if (!secondWeek.isEmpty()) {
            var receptionItem = createItem(firstWeekEnd + " - " + secondWeekEnd, secondWeek);
            responseItemsList.add(receptionItem);
        }

        var thirdWeek = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isBefore(fourthWeekEnd) && diagnosis.getReceiptDate().isAfter(secondWeekEnd)).toList();
        if (!thirdWeek.isEmpty()) {
            var receptionItem = createItem(secondWeekEnd + " - " + thirdWeekEnd, thirdWeek);
            responseItemsList.add(receptionItem);
        }

        var fourthWeek = allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getReceiptDate().isAfter(thirdWeekEnd)).toList();
        if (!fourthWeek.isEmpty()) {
            var receptionItem = createItem(thirdWeekEnd + " - " + fourthWeekEnd, fourthWeek);
            responseItemsList.add(receptionItem);
        }
    }


    //Определение временного диапазона фильтров дат
    private String identifyTimePeriod(Date dateFrom, Date dateTo) {
        long diffInMillies = Math.abs(dateTo.getTime() - dateFrom.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diff < 7) return DAY_NAME;
        else if (diff < 30) return WEEK_NAME;
        else if (diff < 90) return MONTH_NAME;
        else if (diff < 365) return QUARTER_NAME;
        else return YEAR_NAME;

    }

    //Создать айтем для ответа
    private ReceptionItem createItem(String secName, List<DiagnosisEntity> allByReceiptDateBetween) {
        var receptionItem = new ReceptionItem();
        receptionItem.setName(secName);
        receptionItem.setStandard((int) allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getStatus().equals(DiagnosisStatusEnum.STANDARD.getValue())).count());
        receptionItem.setPoorQuality((int) allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getStatus().equals(DiagnosisStatusEnum.POOR_QUALITY.getValue())).count());
        receptionItem.setSuboptimal((int) allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getStatus().equals(DiagnosisStatusEnum.SUB_OPTIMAL.getValue())).count());
        receptionItem.setUnverifiable((int) allByReceiptDateBetween.stream().filter(diagnosis -> diagnosis.getStatus().equals(DiagnosisStatusEnum.UNVERIFIABLE.getValue())).count());
        receptionItem.setId(UUID.randomUUID().toString());
        return receptionItem;
    }

    //Конверт фильтра даты до LocalDate для работы с БД
    private LocalDate convertToLocalDate(Date dateToConvert) {
        if (Objects.nonNull(dateToConvert)) {
            return dateToConvert.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
    }


}
