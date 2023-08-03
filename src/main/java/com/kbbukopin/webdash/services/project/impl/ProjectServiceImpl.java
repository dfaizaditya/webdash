package com.kbbukopin.webdash.services.project.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kbbukopin.webdash.dto.KpiHandler;
import com.kbbukopin.webdash.entity.*;
import com.kbbukopin.webdash.repository.*;
import com.kbbukopin.webdash.services.period.impl.PeriodServiceImpl;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kbbukopin.webdash.dto.PagedResponse;
import com.kbbukopin.webdash.dto.ResponseHandler;
import com.kbbukopin.webdash.dto.StatsHandler;
import com.kbbukopin.webdash.dto.TechPlatformDTO;
import com.kbbukopin.webdash.dto.StatHandler;
import com.kbbukopin.webdash.exeptions.ResourceNotFoundException;
import com.kbbukopin.webdash.helper.ExcelHelper;
import com.kbbukopin.webdash.services.project.ProjectService;
import com.kbbukopin.webdash.utils.AppUtils;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserSponsorRepository userSponsorRepository;

    @Autowired
    private AppPlatformRepository appPlatformRepository;

    @Autowired
    private TechPlatformRepository techPlatformRepository;

    @Autowired
    private PicRepository picRepository;

    @Autowired
    private PeriodServiceImpl periodServiceImpl;

    @Autowired
    private DayOffRepository dayOffRepository;

    public Gson gson = new Gson();

    String[] daftarBulan = {
            "Januari", "Februari", "Maret", "April",
            "Mei", "Juni", "Juli", "Agustus",
            "September", "Oktober", "November", "Desember"
    };

    @Override
    public PagedResponse<Project> getAllProjects(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt", "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Project> projects = projectRepository.findAll(pageable);

        List<Project> data = projects.getNumberOfElements() == 0 ? Collections.emptyList() : projects.getContent();

        return new PagedResponse<>(data, projects.getNumber(), projects.getSize(), projects.getTotalElements(),
                projects.getTotalPages(), projects.isLast());
    }

    public ResponseEntity<Object> getPinnedProjects() {
        List<Project> projects = projectRepository.findAll();
        // Get only the first 10 projects
        List<Project> sublistProjects = projects.subList(0, Math.min(10, projects.size()));
        return ResponseHandler.generateResponse("Success", HttpStatus.OK, sublistProjects);
    }

    @Override
    public ResponseEntity<Object> getProjectByIdAndMonthAndYear(Long id, String month, Long year) {
        Period period = getPeriodByYear(year);
        Project project = projectRepository.getProjectByIdAndMonthAndPeriodId(id, month, period.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        return ResponseHandler.generateResponse("Success", HttpStatus.OK, project);
    }

    @Override
    public ResponseEntity<Object> getProjectsByFilter(Long year, String month, String name, String info1, String unit, String type, String category) {

        Period period = this.getPeriodByYear(year);

        List<Project> projects = projectRepository.searchProjects(period.getId(), month, name, info1, unit, type, category);

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, projects);
    }

    @Override
    public ResponseEntity<Object> createProject(@RequestBody Project newProject) {

        if(!projectRepository.existsByIdAndMonth(newProject.getId(), monthWordFixer(newProject.getMonth()), newProject.getPeriod().getId())){
            Project project = Project.builder()
                    .id(newProject.getId())
                    .month(monthWordFixer(newProject.getMonth()))
                    .period(newProject.getPeriod())
                    .unit(newProject.getUnit())
                    .category(newProject.getCategory())
                    .name(newProject.getName())
                    .userSponsor(newProject.getUserSponsor())
                    .appPlatform(newProject.getAppPlatform())
                    .techPlatform(newProject.getTechPlatform())
                    .pic(newProject.getPic())
                    .startDate(newProject.getStartDate())
                    .dueDate(newProject.getDueDate())
                    .finishedDate(newProject.getFinishedDate())
                    .type(newProject.getType())
                    .progress(newProject.getProgress())
                    .status(newProject.getStatus())
                    .info1(newProject.getInfo1())
                    .changeType(newProject.getChangeType())
                    .rfc(newProject.getRfc())
                    .documentation(newProject.getDocumentation())
                    .info2(newProject.getInfo2())
                    .build();

            projectRepository.save(project);

            return ResponseHandler.generateResponse("Success", HttpStatus.OK, " ");
//
        } else {
            return ResponseHandler.generateResponse("Failed", HttpStatus.BAD_REQUEST, " ");
        }

    }

    @Transactional
    @Override
    public ResponseEntity<Object> updateProject(Long id, String month, Long year, @RequestBody Project newProject) {
        Period period = getPeriodByYear(year);
        Project project = projectRepository.getProjectByIdAndMonthAndPeriodId(id, month, period.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        project.setUnit(newProject.getUnit());
        project.setCategory(newProject.getCategory());
        project.setName(newProject.getName());
        project.setUserSponsor(newProject.getUserSponsor());
        project.setAppPlatform(newProject.getAppPlatform());
        project.setTechPlatform(newProject.getTechPlatform());
        project.setPic(newProject.getPic());
        project.setStartDate(newProject.getStartDate());
        project.setDueDate(newProject.getDueDate());
        project.setType(newProject.getType());
        project.setProgress(newProject.getProgress());
        project.setStatus(newProject.getStatus());
        project.setInfo1(newProject.getInfo1());
        project.setChangeType(newProject.getChangeType());
        project.setRfc(newProject.getRfc());
        project.setDocumentation(newProject.getDocumentation());
        project.setInfo2(newProject.getInfo2());

        Project updatedProject = projectRepository.save(project);

        userSponsorRepository.deleteUserSponsorNotExistOnPivot();
        appPlatformRepository.deleteAppPlatformNotExistOnPivot();
        techPlatformRepository.deleteTechPlatformNotExistOnPivot();
        picRepository.deletePicNotExistOnPivot();

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, updatedProject);
    }

    @Transactional
    @Override
    public ResponseEntity<Object> deleteProject(Long id, String month, Long year) {
        Period period = getPeriodByYear(year);
        Project project = projectRepository.getProjectByIdAndMonthAndPeriodId(id, month, period.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        projectRepository.deleteByIdAndMonthAndPeriodId(id, month, period.getId());
        userSponsorRepository.deleteUserSponsorNotExistOnPivot();
        appPlatformRepository.deleteAppPlatformNotExistOnPivot();
        techPlatformRepository.deleteTechPlatformNotExistOnPivot();
        picRepository.deletePicNotExistOnPivot();

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, " ");
    }

    @Transactional
    @Override
    public ResponseEntity<Object> deleteMultipleProjects(List<Long> ids, List<String> months, List<Long> projectPeriodIds){

        projectRepository.deleteProjectEntries(ids, months, projectPeriodIds);
        userSponsorRepository.deleteUserSponsorNotExistOnPivot();
        appPlatformRepository.deleteAppPlatformNotExistOnPivot();
        techPlatformRepository.deleteTechPlatformNotExistOnPivot();
        picRepository.deletePicNotExistOnPivot();

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, " ");
    }

    @Override
    public ResponseEntity<Object> getProjectStat(Long year, String month) {

        Period period = this.getPeriodByYear(year);
//        month = monthParamChangerIfNull(month);

        List<String> clType = projectRepository.getColumnTypeList(period.getId(), month);
        List<String> clComplete = projectRepository.getColumnCompleteList(period.getId(), month);
        List<String> clUnit = projectRepository.getColumnUnitList(period.getId(), month);

        List<Object> listOfType = new ArrayList<>();
        List<Object> listOfComplete = new ArrayList<>();
        List<Object> listOfUnit = new ArrayList<>();

        // Mengambil set entry dari mapCount dan mengubahnya menjadi stream
        Stream<Map.Entry<String, Long>> entryStreamOfType = mapCount(clType).entrySet().stream();
        Stream<Map.Entry<String, Long>> entryStreamOfComplete = mapCount(clComplete).entrySet().stream();
        Stream<Map.Entry<String, Long>> entryStreamOfUnit = mapCount(clUnit).entrySet().stream();

        // Mengurutkan stream berdasarkan nilai (value) secara descending
        entryStreamOfType = entryStreamOfType.sorted(Map.Entry.comparingByValue());
        entryStreamOfComplete = entryStreamOfComplete.sorted(Map.Entry.comparingByValue());
        entryStreamOfUnit = entryStreamOfUnit.sorted(Map.Entry.comparingByValue());

        // Mengkonversi setiap entry menjadi JsonObject dan menambahkannya ke listOfType
        entryStreamOfType.forEach(entry -> {
            JsonObject inputString = createJson(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfType.add(myjson);
        });

        entryStreamOfComplete.forEach(entry -> {
            JsonObject inputString = createJson(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfComplete.add(myjson);
        });

        entryStreamOfUnit.forEach(entry -> {
            JsonObject inputString = createJson(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfUnit.add(myjson);
        });


//        for (var entry : mapCount(clType).entrySet()) {
//            JsonObject inputString = createJson(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
//            Object myjson = gson.fromJson(inputString, Object.class);
//            listOfType.add(myjson);
//        }
//
//        for (var entry : mapCount(clComplete).entrySet()) {
//            JsonObject inputString = createJson(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
//            Object myjson = gson.fromJson(inputString, Object.class);
//
//            listOfComplete.add(myjson);
//        }
//
//        for (var entry : mapCount(clUnit).entrySet()) {
//            JsonObject inputString = createJson(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
//            Object myjson = gson.fromJson(inputString, Object.class);
//
//            listOfUnit.add(myjson);
//        }

        return StatsHandler.generateResponse("Success", HttpStatus.OK, clType.size(), listOfType, listOfComplete,
                listOfUnit);
    }

    @Override
    public ResponseEntity<Object> getProjectStats(Long year, String month) {

        Period period = this.getPeriodByYear(year);

        List<String> clType = projectRepository.getColumnTypeList(period.getId(), month);
        List<String> clComplete = projectRepository.getColumnCompleteList(period.getId(), month);
        List<String> clUnit = projectRepository.getColumnUnitList(period.getId(), month);

        List<Object> listOfType = new ArrayList<>();
        List<Object> listOfComplete = new ArrayList<>();
        List<Object> listOfUnit = new ArrayList<>();

        // Mengambil set entry dari mapCount dan mengubahnya menjadi stream
        Stream<Map.Entry<String, Long>> entryStreamOfType = mapCount(clType).entrySet().stream();
        Stream<Map.Entry<String, Long>> entryStreamOfComplete = mapCount(clComplete).entrySet().stream();
        Stream<Map.Entry<String, Long>> entryStreamOfUnit = mapCount(clUnit).entrySet().stream();

        // Mengurutkan stream berdasarkan nilai (value) secara descending
        entryStreamOfType = entryStreamOfType.sorted(Map.Entry.comparingByValue());
        entryStreamOfComplete = entryStreamOfComplete.sorted(Map.Entry.comparingByValue());
        entryStreamOfUnit = entryStreamOfUnit.sorted(Map.Entry.comparingByValue());

        // Mengkonversi setiap entry menjadi JsonObject dan menambahkannya ke listOfType
        entryStreamOfType.forEach(entry -> {
            JsonObject inputString = createJsonStats(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfType.add(myjson);
        });

        entryStreamOfComplete.forEach(entry -> {
            JsonObject inputString = createJsonStats(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfComplete.add(myjson);
        });

        entryStreamOfUnit.forEach(entry -> {
            JsonObject inputString = createJsonStats(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfUnit.add(myjson);
        });

        return StatsHandler.generateResponse("Success", HttpStatus.OK, clType.size(), listOfType, listOfComplete,
                listOfUnit);
    }

    @Override
    public ResponseEntity<Object> getProjectCompletionStat(Long year, String month) {

        Period period = this.getPeriodByYear(year);

        List<String> clComplete = projectRepository.getColumnCompleteList(period.getId(), month);
        List<Object> listOfComplete = new ArrayList<>();
        Stream<Map.Entry<String, Long>> entryStreamOfComplete = mapCount(clComplete).entrySet().stream();
  
        entryStreamOfComplete = entryStreamOfComplete.sorted(Map.Entry.comparingByValue());

        entryStreamOfComplete.forEach(entry -> {
            JsonObject inputString = createJsonStats(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfComplete.add(myjson);
        });

        return StatHandler.generateResponse("Success", HttpStatus.OK, clComplete.size(), listOfComplete);
    }

    @Override
    public ResponseEntity<Object> getProjectUnitStat(Long year, String month) {

        Period period = this.getPeriodByYear(year);

        List<String> clComplete = projectRepository.getColumnUnitList(period.getId(), month);
        List<Object> listOfComplete = new ArrayList<>();
        Stream<Map.Entry<String, Long>> entryStreamOfComplete = mapCount(clComplete).entrySet().stream();
  
        entryStreamOfComplete = entryStreamOfComplete.sorted(Map.Entry.comparingByValue());

        entryStreamOfComplete.forEach(entry -> {
            JsonObject inputString = createJsonStats(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfComplete.add(myjson);
        });

        return StatHandler.generateResponse("Success", HttpStatus.OK, clComplete.size(), listOfComplete);
    }

    @Override
    public ResponseEntity<Object> getProjectTypeStat(Long year, String month) {

        Period period = this.getPeriodByYear(year);

        List<String> clComplete = projectRepository.getColumnTypeList(period.getId(), month);
        List<Object> listOfComplete = new ArrayList<>();
        Stream<Map.Entry<String, Long>> entryStreamOfComplete = mapCount(clComplete).entrySet().stream();
  
        entryStreamOfComplete = entryStreamOfComplete.sorted(Map.Entry.comparingByValue());

        entryStreamOfComplete.forEach(entry -> {
            JsonObject inputString = createJsonStats(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfComplete.add(myjson);
        });

        return StatHandler.generateResponse("Success", HttpStatus.OK, clComplete.size(), listOfComplete);
    }

    @Override
    public ResponseEntity<Object> getProjectCategoryStat(Long year, String month) {

        Period period = this.getPeriodByYear(year);

        List<String> clComplete = projectRepository.getColumnCategoryList(period.getId(), month);
        List<Object> listOfComplete = new ArrayList<>();
        Stream<Map.Entry<String, Long>> entryStreamOfComplete = mapCount(clComplete).entrySet().stream();
  
        entryStreamOfComplete = entryStreamOfComplete.sorted(Map.Entry.comparingByValue());

        entryStreamOfComplete.forEach(entry -> {
            JsonObject inputString = createJsonStats(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfComplete.add(myjson);
        });

        return StatHandler.generateResponse("Success", HttpStatus.OK, clComplete.size(), listOfComplete);
    }

    @Override
    public ResponseEntity<Object> getProjectDocumentationStat(Long year, String month) {

        Period period = this.getPeriodByYear(year);

        List<String> clComplete = projectRepository.getColumnDocumentationList(period.getId(), month);
        List<Object> listOfComplete = new ArrayList<>();
        Stream<Map.Entry<String, Long>> entryStreamOfComplete = mapCount(clComplete).entrySet().stream();
  
        entryStreamOfComplete = entryStreamOfComplete.sorted(Map.Entry.comparingByValue());

        entryStreamOfComplete.forEach(entry -> {
            JsonObject inputString = createJsonStats(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfComplete.add(myjson);
        });

        return StatHandler.generateResponse("Success", HttpStatus.OK, clComplete.size(), listOfComplete);
    }

    @Override
    public ResponseEntity<Object> getProjectRolloutStatusStat(Long year, String month) {
        Period period = this.getPeriodByYear(year);

        List<Object[]> results = projectRepository.getRolloutStatusCounts(period.getId(), month);
        List<Map<String, Object>> jsonList = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> json = new HashMap<>();
            json.put("name", result[0]);
            json.put("value", result[1]);
            jsonList.add(json);
        }

        return StatHandler.generateResponse("Success", HttpStatus.OK, jsonList.size(), jsonList);
    }

    @Override
    public ResponseEntity<Object> getProjectRolloutUnitStat(Long year, String month) {
        Period period = this.getPeriodByYear(year);

        List<Object[]> results = projectRepository.getRolloutUnitCounts(period.getId(), month);
        List<Map<String, Object>> jsonList = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> json = new HashMap<>();
            json.put("name", result[0]);
            json.put("value", result[1]);
            jsonList.add(json);
        }

        return StatHandler.generateResponse("Success", HttpStatus.OK, jsonList.size(), jsonList);
    }

    @Override
    public ResponseEntity<Object> getProjectAppPlatformStat(Long year, String month) {
        Period period = this.getPeriodByYear(year);

        List<Object[]> results = projectRepository.getColumnAppPlatformList(period.getId(), month);
        List<Map<String, Object>> jsonList = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> json = new HashMap<>();
            json.put("name", result[0]);
            json.put("value", result[1]);
            jsonList.add(json);
        }

        return StatHandler.generateResponse("Success", HttpStatus.OK, jsonList.size(), jsonList);
    }
    
    @Override
    public ResponseEntity<Object> getProjectTechPlatformStat(Long year, String month) {
        Period period = this.getPeriodByYear(year);

        List<Object[]> results = projectRepository.getColumnTechPlatformList(period.getId(), month);
        List<Map<String, Object>> jsonList = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> json = new HashMap<>();
            json.put("name", result[0]);
            json.put("value", result[1]);
            jsonList.add(json);
        }

        return StatHandler.generateResponse("Success", HttpStatus.OK, jsonList.size(), jsonList);
    }

    public Map<String, Long> mapCount(List<String> mylist) {
        return mylist.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
    }

    public JsonObject createJson(String id, String label, Double value) {
        JsonObject jsObj = new JsonObject();
        jsObj.addProperty("id", id);
        jsObj.addProperty("label", id);
        jsObj.addProperty("value", value);
        return jsObj;
    }

    public JsonObject createJsonStats(String id, String label, Double value) {
        JsonObject jsObj = new JsonObject();
        jsObj.addProperty("name", id);
        jsObj.addProperty("value", value);
        return jsObj;
    }

    @Override
    public ResponseEntity<Object> getEvidenceKpi(Long year, String month) {

        Period period = this.getPeriodByYear(year);
        month = monthParamChangerIfNull(month);

        List<String> rangeMonth = new ArrayList<>();
        int index=-1;

        for (int i = 0; i < daftarBulan.length; i++) {
            if(i == 6) {
                rangeMonth.clear();
            }

            rangeMonth.add(daftarBulan[i]);

            if (daftarBulan[i].equals(month)) {
                index = i;

                // Keluar dari perulangan jika bulan ditemukan.
                break;
            }
        }

        if(index == -1) {
            return ResponseHandler.generateResponse("Failed", HttpStatus.BAD_REQUEST, " ");
        }

        String[] types = {"In House", "Insiden", "Join Dev", "Outsource"};
        LinkedMap<String, String> category = new LinkedMap<>();

        for (String type : types) {
            if (type.equalsIgnoreCase("insiden")) {
                category.put(type, "Insiden");
            } else {
                category.put(type, "Proyek");
            }
        }

//        for (String type : types) {
//            KpiHandler.addDataResponse(type, projectRepository.getCountProject(period.getId(), month, category.get(type), type));
//        }

        for (String type : types) {
            KpiHandler.addDataResponse(type, projectRepository.getCountProject(period.getId(), rangeMonth, category.get(type), type));
        }

        ArrayList<String> categoryValueForQueryTotal = new ArrayList<>(category.values());
        ArrayList<String> typeValueForQueryTotal = new ArrayList<>((category.keySet()));

        categoryValueForQueryTotal.replaceAll(String::toLowerCase);
//        typeValueForQueryTotal.replaceAll(String::toLowerCase);
//
//        typeValueForQueryTotal.set(typeValueForQueryTotal.indexOf("insiden"), "%");
//        typeValueForQueryTotal.replaceAll(s -> "%" + s + "%");

        List<DayOff> listDayOff = dayOffRepository.getDayOffByYear(year);

        List<Project> projectsAhead = projectRepository.getFinishedProject(period.getId(), rangeMonth, "ahead");
        List<Project> projectsOverdue = projectRepository.getFinishedProject(period.getId(), rangeMonth, "overdue");

        List<Double> projectAheadMandaysCalculated = new ArrayList<>();
        List<Double> projectOverdueMandaysCalculated = new ArrayList<>();

        for(Project project : projectsAhead){
            projectAheadMandaysCalculated.add(
                    calculateMandays(project.getStartDate(), project.getDueDate(), listDayOff) /
                            calculateMandays(project.getStartDate(), project.getFinishedDate(), listDayOff)
            );

            System.out.println(project.getStartDate());
            System.out.println(project.getDueDate());
            System.out.println(project.getFinishedDate());
            System.out.println("scheduled mandays Ahead: "+calculateMandays(project.getStartDate(), project.getDueDate(), listDayOff));
            System.out.println("actualed mandays Ahead: "+calculateMandays(project.getStartDate(), project.getFinishedDate(), listDayOff));
        }
        for(Project project : projectsOverdue){
            projectOverdueMandaysCalculated.add(
                    calculateMandays(project.getStartDate(), project.getDueDate(), listDayOff) /
                            calculateMandays(project.getStartDate(), project.getFinishedDate(), listDayOff)
            );
            System.out.println(project.getStartDate());
            System.out.println(project.getDueDate());
            System.out.println(project.getFinishedDate());
            System.out.println("scheduled mandays Overdue: "+calculateMandays(project.getStartDate(), project.getDueDate(), listDayOff));
            System.out.println("actualed mandays Overdue: "+calculateMandays(project.getStartDate(), project.getFinishedDate(), listDayOff));
        }

        Double averageAhead = Math.round(calculateAverage(projectAheadMandaysCalculated) * Math.pow(10, 1)) / Math.pow(10, 1);
        Double averageOverdue = Math.round(calculateAverage(projectOverdueMandaysCalculated) * Math.pow(10, 1)) / Math.pow(10, 1);

        System.out.println("average ahead : "+averageAhead);
        System.out.println("average overdue : "+averageOverdue);
//        KpiHandler.addDataResponse("Total", projectRepository.getTotalProject(period.getId(), month, categoryValueForQueryTotal, averageAhead, averageOverdue));
        KpiHandler.addDataResponse("Total", projectRepository.getTotalProject(period.getId(), rangeMonth, categoryValueForQueryTotal, averageAhead, averageOverdue));
        System.out.println("=======================================");
        return KpiHandler.generateResponse("Success", HttpStatus.OK);

    }

    public static double calculateMandays(LocalDate startDate, LocalDate endDate, List<DayOff> listDayOff) {
        double rangeDays = 0;
        LocalDate tempDate = startDate;

        List<LocalDate> dayOffs = new ArrayList<>();
        for(DayOff dayOff : listDayOff){
            dayOffs.add(dayOff.getDate());
        }

        while (tempDate.isBefore(endDate)) {
            if (!tempDate.getDayOfWeek().name().equals("SATURDAY") &&
                    !tempDate.getDayOfWeek().name().equals("SUNDAY") &&
                    !dayOffs.contains(tempDate)) {
                rangeDays++;
            }
            tempDate = tempDate.plusDays(1);
        }
        if(!dayOffs.contains(tempDate)) {
            rangeDays++;
        }
        return rangeDays;
    }

    public double calculateAverage(List<Double> numbers) {
        double sum = 0;
        for (Double number : numbers) {
            sum += number;
        }
        return sum / numbers.size();
    }

    @Override
    public void importToDb(Long period_id, List<MultipartFile> multipleFiles) {
        Period period = new Period();
        period.setId(period_id);

        if (!multipleFiles.isEmpty()) {
            List<Project> projects = new ArrayList<>();
            multipleFiles.forEach(multipartFile -> {
                try {
                    XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
                    XSSFSheet sheet = workbook.getSheetAt(0);

//                    System.out.print(getNumberOfNonEmptyCells(sheet, 0));
                    // looping----------------------------------------------------------------
                    for (int rowIndex = 0; rowIndex < getNumberOfNonEmptyCells(sheet, 0) - 1; rowIndex++) {

                        XSSFRow row = sheet.getRow(rowIndex + 1);
                        // datatoString skip header

                        Long id = Long.parseLong(getValue(row.getCell(0)).toString());
                        String month = monthWordFixer(String.valueOf(row.getCell(1)));
                        String unit = String.valueOf(row.getCell(2));
                        String category = String.valueOf(row.getCell(3));
                        String name = String.valueOf(row.getCell(4));
                        String userSponsor = String.valueOf(row.getCell(5));
                        String appPlatform = String.valueOf(row.getCell(6));
                        String techPlatform = String.valueOf(row.getCell(7));
                        String pic = String.valueOf(row.getCell(8));

                        //parse data to localDate
                        LocalDate startDate = ExcelHelper.parseLocalDate(row.getCell(9));
                        LocalDate dueDate = ExcelHelper.parseLocalDate(row.getCell(10));

                        String type = String.valueOf(row.getCell(11));

                        //handling data progress
                        String tempProgress = String.valueOf(row.getCell(12));
                        BigDecimal progress = null;
                        if(tempProgress.matches("[[0-9.%]+]+")) {
                            if(!(tempProgress.equalsIgnoreCase(".")) ||
                                    !(tempProgress.equalsIgnoreCase("%"))){
                                progress = new BigDecimal(tempProgress);
                            }
                        }

                        String status = String.valueOf(row.getCell(13));
                        String info1 = String.valueOf(row.getCell(14));
                        String changeType = String.valueOf(row.getCell(15));
                        String rfc = String.valueOf(getValue(row.getCell(16)));
                        String documentation = String.valueOf(row.getCell(17));
                        String info2 = String.valueOf(row.getCell(18));

                        // Memisahkan string berdasarkan newline menjadi array
                        String[] nameUserSponsors = userSponsor.split("\n");
                        List<UserSponsor> userSponsors = new ArrayList<>();
                        for (String nameUserSponsor : nameUserSponsors) {
                            nameUserSponsor = nameUserSponsor.trim();

                            // melakukan pengecekan di user sponsor sudah terdaftar atau belum
                            if(!userSponsorRepository.existsByName(nameUserSponsor)){
                                // jika belum terdaftar maka akan create user sponsor
                                UserSponsor tempUserSponsor = new UserSponsor();
                                tempUserSponsor.setName(nameUserSponsor);
                                userSponsorRepository.save(tempUserSponsor);
                            }

                            UserSponsor dataUserSponsor = new UserSponsor();

                            // sudah dapat dipastikan data pasti terdaftar di database
                            dataUserSponsor = userSponsorRepository.getByName(nameUserSponsor);

                            // memasukkan object data tersebut ke list
                            userSponsors.add(dataUserSponsor);
                        }

                        // Memisahkan string berdasarkan newline menjadi array
                        String[] nameAppPlatforms = appPlatform.split("\n");
                        List<AppPlatform> appPlatforms = new ArrayList<>();
                        for (String nameAppPlatform : nameAppPlatforms) {
                            nameAppPlatform = nameAppPlatform.trim();

                            // melakukan pengecekan di user sponsor sudah terdaftar atau belum
                            if(!appPlatformRepository.existsByName(nameAppPlatform)){
                                // jika belum terdaftar maka akan create user sponsor
                                AppPlatform tempAppPlatform = new AppPlatform();
                                tempAppPlatform.setName(nameAppPlatform);
                                appPlatformRepository.save(tempAppPlatform);
                            }

                            AppPlatform dataAppPlatform = new AppPlatform();

                            // sudah dapat dipastikan data pasti terdaftar di database
                            dataAppPlatform = appPlatformRepository.getByName(nameAppPlatform);

                            // memasukkan object data tersebut ke list
                            appPlatforms.add(dataAppPlatform);
                        }

                        // Memisahkan string berdasarkan newline menjadi array
                        String[] nameTechPlatforms = techPlatform.split("\n");
                        List<TechPlatform> techPlatforms = new ArrayList<>();
                        for (String nameTechPlatform : nameTechPlatforms) {
                            nameTechPlatform = nameTechPlatform.trim();

                            // melakukan pengecekan di user sponsor sudah terdaftar atau belum
                            if(!techPlatformRepository.existsByName(nameTechPlatform)){
                                // jika belum terdaftar maka akan create user sponsor
                                TechPlatform tempTechPlatform = new TechPlatform();
                                tempTechPlatform.setName(nameTechPlatform);
                                techPlatformRepository.save(tempTechPlatform);
                            }

                            TechPlatform dataTechPlatform = new TechPlatform();

                            // sudah dapat dipastikan data pasti terdaftar di database
                            dataTechPlatform = techPlatformRepository.getByName(nameTechPlatform);

                            // memasukkan object data tersebut ke list
                            techPlatforms.add(dataTechPlatform);
                        }

                        // Memisahkan string berdasarkan newline menjadi array
                        String[] namePics = pic.split("\n");
                        List<Pic> pics = new ArrayList<>();
                        for (String namePic : namePics) {
                            namePic = namePic.trim();

                            // melakukan pengecekan di user sponsor sudah terdaftar atau belum
                            if(!picRepository.existsByName(namePic)){
                                // jika belum terdaftar maka akan create user sponsor
                                Pic tempPic = new Pic();
                                tempPic.setName(namePic);
                                picRepository.save(tempPic);
                            }

                            Pic dataPic = new Pic();

                            // sudah dapat dipastikan data pasti terdaftar di database
                            dataPic = picRepository.getByName(namePic);

                            // memasukkan object data tersebut ke list
                            pics.add(dataPic);
                        }

                        Project project = Project.builder()
                                .period(period)
                                .id(id)
                                .month(month)
                                .unit(unit)
                                .category(category)
                                .name(name)
                                .userSponsor(userSponsors)
                                .appPlatform(appPlatforms)
                                .techPlatform(techPlatforms)
                                .pic(pics)
                                .startDate(startDate)
                                .dueDate(dueDate)
                                .type(type)
                                .progress(progress)
                                .status(status)
                                .info1(info1)
                                .changeType(changeType)
                                .rfc(rfc)
                                .documentation(documentation)
                                .info2(info2)
                                .build();

                        projects.add(project);

                    }
                    if (workbook != null) {
                        try {
                            workbook.close();
                        } catch (IOException e) {
                            throw new RuntimeException("fail to close Excel file: " + e.getMessage());
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
                }
            });

            if (!projects.isEmpty()) {
                // save to database
                projectRepository.saveAll(projects);
            }
        }
    }

    private Period getPeriodByYear(Long year) {
        if(year == null || year == 0) {

            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long currentYear = localDate.getYear();

            return periodServiceImpl.getPeriodByYear(currentYear);
        } else {
            Period period = new Period();
            if(year != null){
                period = periodServiceImpl.getPeriodByYear(year);
                if(period == null) {
                    period.setId(0);
                }
            }
            return period;
        }
    }

    private String monthParamChangerIfNull(String month) {
        if(month == null || month.equalsIgnoreCase("")) {

            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            return daftarBulan[localDate.getMonthValue()-1];
        } else {
            return month;
        }
    }

    //fungsi untuk standarisasi nilai month
    private String monthWordFixer(String month) {

        month = month.toLowerCase();
        LinkedHashMap<String,String> bulanPattern = new LinkedHashMap<>();

        bulanPattern.put("Januari", "^(j[a-z]*a[a-z]*n[a-z]*)$");
        bulanPattern.put("Februari", "^(f[a-z]*b[a-z]*)$");
        bulanPattern.put("Maret", "^(m[a-z]*r[a-z]*)$");
        bulanPattern.put("April", "^(a[a-z]*p[a-z]*)$");
        bulanPattern.put("Mei", "^(m[a-z]*[e[a-z]*i|a[a-z]*y])$");
        bulanPattern.put("Juni", "^(j[a-z]*u[a-z]*n[a-z]*)$");
        bulanPattern.put("Juli", "^(j[a-z]*u[a-z]*l[a-z]*)$");
        bulanPattern.put("Agustus", "^(a[a-z]*g[a-z]*u[a-z]*)$");
        bulanPattern.put("September", "^(s[a-z]*e[a-z]*p[a-z]*)$");
        bulanPattern.put("Oktober", "^(o[a-z]*k[a-z]*t[a-z]*)$");
        bulanPattern.put("November", "^(n[a-z]*v[a-z]*)$");
        bulanPattern.put("Desember", "^(d[a-z]*s[a-z]*)$");

        // mengambil set semua key dari bulanPattern ke dalam keys
        Set<String> keys = bulanPattern.keySet();

        // mencetak semua kunci
        for (String key : keys) {
            Pattern pattern = Pattern.compile(bulanPattern.get(key));
            Matcher matcher = pattern.matcher(month);
            if(matcher.matches()){
                return key;
            }
        }
        return "";
    }

    public String convertDateEvo(Double date) {
        java.util.Date javaDate = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(date);
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(javaDate);
    }

    private Object getValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return ExcelHelper.getCellValueAsString(cell);
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return cell.getErrorCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            case _NONE:
                return null;
            default:
                break;
        }
        return null;
    }

    public static int getNumberOfNonEmptyCells(XSSFSheet sheet, int columnIndex) {
        int numOfNonEmptyCells = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                XSSFCell cell = row.getCell(columnIndex);
                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    numOfNonEmptyCells++;
                }
            }
        }
        return numOfNonEmptyCells;
    }

    public ByteArrayInputStream load(Long year, String month) {

        Period period = this.getPeriodByYear(year);

        List<Project> projects = projectRepository.searchProjects( period.getId(), month, null, null, null, null, null);

        ByteArrayInputStream in = ExcelHelper.projectsToExcel(projects);
        return in;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}