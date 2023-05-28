package com.demo.hakaton.analysis.controller;

import com.demo.hakaton.analysis.model.count.ReceptionCountRs;
import com.demo.hakaton.analysis.model.file.ImportResponse;
import com.demo.hakaton.analysis.model.info.ReceptionInfo;
import com.demo.hakaton.analysis.model.list.ReceptionListResponse;
import com.demo.hakaton.analysis.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController()
@RequiredArgsConstructor
public class AnalysisController {

    private final UploadFileService uploadFileService;
    private final ReceptionCountService receptionCountService;
    private final ReceptionListService receptionListService;

    private final ReceptionInfoService receptionInfoService;

    private final ExportFileService exportFileService;

    @PostMapping("/file/import")
    //Рест загрузки файла
    public ImportResponse parseFile(@RequestParam("file") MultipartFile file) {
        return uploadFileService.parseFile(file);
    }

    @GetMapping("/reception/count")
    //Рест формирования дашборда
    public ReceptionCountRs getCount(@RequestParam("section") String section,
                                     @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateFrom,
                                     @RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateTo,
                                     @RequestParam(value = "specialization", required = false) List<String> specialization) {
        return receptionCountService.getCount(section, dateFrom, dateTo, specialization);
    }

    @GetMapping("/reception/list")
    //Рест формирования таблицы
    public ReceptionListResponse getList(@RequestParam(value = "limit") Integer limit,
                                         @RequestParam(value = "offset") Integer offset,
                                         @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateFrom,
                                         @RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateTo,
                                         @RequestParam(value = "client", required = false) Integer client,
                                         @RequestParam(value = "specialization", required = false) List<String> specialization) {
        return receptionListService.getReceptionsList(limit, offset, dateFrom, dateTo, specialization, client);
    }

    @GetMapping("/reception/info/{id}")
    //Получение подробной инфы по айди приема
    public ReceptionInfo getInfo(@PathVariable("id") String id) {
        return receptionInfoService.getInfo(Long.valueOf(id));
    }

    @GetMapping("/file/export")
    //Рест выгрузки файлов
    public byte[] exportFile(@RequestParam(value = "section", required = false) String section,
                             @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateFrom,
                             @RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateTo,
                             @RequestParam(value = "specialization", required = false) List<String> specialization) {
        return exportFileService.exportFile(section, dateFrom, dateTo, specialization);
    }

}
