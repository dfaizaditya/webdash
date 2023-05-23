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

import com.kbbukopin.webdash.dto.KpiHandler;
import com.kbbukopin.webdash.entity.Period;
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
import com.kbbukopin.webdash.entity.Project;
import com.kbbukopin.webdash.exeptions.ResourceNotFoundException;
import com.kbbukopin.webdash.helper.ExcelHelper;
import com.kbbukopin.webdash.repository.ProjectRepository;
import com.kbbukopin.webdash.services.project.ProjectService;
import com.kbbukopin.webdash.utils.AppUtils;

import javax.transaction.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PeriodServiceImpl periodServiceImpl;

    public Gson gson = new Gson();

    @Override
    public PagedResponse<Project> getAllProjects(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Project> projects = projectRepository.findAll(pageable);

        List<Project> data = projects.getNumberOfElements() == 0 ? Collections.emptyList() : projects.getContent();

        return new PagedResponse<>(data, projects.getNumber(), projects.getSize(), projects.getTotalElements(),
                projects.getTotalPages(), projects.isLast());
    }

    @Override
    public ResponseEntity<Object> getProjectByIdAndMonth(Long id, String month) {
        Project project = projectRepository.getProjectByIdAndMonth(id,month)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        return ResponseHandler.generateResponse("Success", HttpStatus.OK, project);
    }

    @Override
    public ResponseEntity<Object> getProjectsByFilter(Long id_period, String month, String name, String unit, String category) {

        List<Project> projects = projectRepository.searchProjects(id_period, month, name, unit, category);

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, projects);
    }

    @Override
    public ResponseEntity<Object> createProject(@RequestBody Project newProject) {

        if(!projectRepository.existsByIdAndMonth(newProject.getId(), monthWordFixer(newProject.getMonth()))){
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

        } else {
            return ResponseHandler.generateResponse("Failed", HttpStatus.BAD_REQUEST, " ");
        }

    }

    @Override
    public ResponseEntity<Object> updateProject(Long id, String month, @RequestBody Project newProject) {
        Project project = projectRepository.getProjectByIdAndMonth(id,month)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        project.setPeriod(newProject.getPeriod());
        project.setUnit(newProject.getUnit());
        project.setCategory(newProject.getCategory());
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

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, updatedProject);
    }

    @Transactional
    @Override
    public ResponseEntity<Object> deleteProject(Long id, String month) {
        Project project = projectRepository.getProjectByIdAndMonth(id,month)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        projectRepository.deleteByIdAndMonth(id, month);

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, " ");
    }

    @Override
    public ResponseEntity<Object> getProjectStat(Long id_period, String month) {

        month = monthParamChangerIfNull(month);
        id_period = idPeriodParamChangerIfNull(id_period);

        List<String> clType = projectRepository.getColumnTypeList(id_period, month);
        List<String> clComplete = projectRepository.getColumnCompleteList(id_period, month);
        List<String> clUnit = projectRepository.getColumnUnitList(id_period, month);

        List<Object> listOfType = new ArrayList<>();
        List<Object> listOfComplete = new ArrayList<>();
        List<Object> listOfUnit = new ArrayList<>();

        for (var entry : mapCount(clType).entrySet()) {
            JsonObject inputString = createJson(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);
            listOfType.add(myjson);
        }

        for (var entry : mapCount(clComplete).entrySet()) {
            JsonObject inputString = createJson(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);

            listOfComplete.add(myjson);
        }

        for (var entry : mapCount(clUnit).entrySet()) {
            JsonObject inputString = createJson(entry.getKey(), entry.getKey(), entry.getValue().doubleValue());
            Object myjson = gson.fromJson(inputString, Object.class);

            listOfUnit.add(myjson);
        }

        return StatsHandler.generateResponse("Success", HttpStatus.OK, clType.size(), listOfType, listOfComplete,
                listOfUnit);
    }

    public Map<String, Long> mapCount(List<String> mylist) {
        return mylist.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
    }

    public String formatJson(String id, String label, Double value) {
        return "{\n" +
                "\"id\" : \"" + id + "\",\n" +
                "\"label\" : \"" + label + "\",\n" +
                "\"value\" : \"" + value + "\"\n" +
                "}";
    }

    public JsonObject createJson(String id, String label, Double value) {
        JsonObject jsObj = new JsonObject();
        jsObj.addProperty("id", id);
        jsObj.addProperty("label", id);
        jsObj.addProperty("value", value);
        return jsObj;
    }

    @Override
    public ResponseEntity<Object> getEvidenceKpi(Long id_period, String month) {

        id_period = idPeriodParamChangerIfNull(id_period);
        month = monthParamChangerIfNull(month);

        System.out.println(id_period);

        String[] types = {"In House", "Insiden", "JoinDev", "Outsource"};
        LinkedMap<String, String> category = new LinkedMap<>();

        for (String type : types) {
            if (type.equalsIgnoreCase("insiden")) {
                category.put(type, "Insiden");
            } else {
                category.put(type, "Proyek");
            }
        }

        for (String type : types) {
            KpiHandler.addDataResponse(type, projectRepository.getCountProject(id_period, month, category.get(type), type));
        }

        KpiHandler.addDataResponse("Total", projectRepository.getTotalProject(id_period, month));

        return KpiHandler.generateResponse("Success", HttpStatus.OK);

    }

    @Override
    public void importToDb(Long id_project, List<MultipartFile> multipleFiles) {
        Period period = new Period();
        period.setId(id_project);

        if (!multipleFiles.isEmpty()) {
            List<Project> projects = new ArrayList<>();
            multipleFiles.forEach(multipartFile -> {
                try {
                    XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
                    XSSFSheet sheet = workbook.getSheetAt(0);

                    System.out.print(getNumberOfNonEmptyCells(sheet, 0));
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

//                        BigDecimal progress = new BigDecimal(row.getCell(10).toString());

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
                        String rfc = String.valueOf(row.getCell(16));
                        String documentation = String.valueOf(row.getCell(17));
                        String info2 = String.valueOf(row.getCell(18));

                        Project project = Project.builder()
                                .period(period)
                                .id(id)
                                .month(month)
                                .unit(unit)
                                .category(category)
                                .name(name)
                                .userSponsor(userSponsor)
                                .appPlatform(appPlatform)
                                .techPlatform(techPlatform)
                                .pic(pic)
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

    private Long idPeriodParamChangerIfNull(Long id_period) {
        if(id_period == null || id_period == 0) {

            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long year = localDate.getYear();

            Period period = periodServiceImpl.getPeriodByYear(year);

            return period.getId();
        } else {
            return id_period;
        }
    }

    private String monthParamChangerIfNull(String month) {
        if(month == null || month.equalsIgnoreCase("")) {

            String[] daftarBulan = {
                    "Januari", "Februari", "Maret", "April",
                    "Mei", "Juni", "Juli", "Agustus",
                    "September", "Oktober", "November", "Desember"
            };

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

    public ByteArrayInputStream load() {
        List<Project> projects = projectRepository.findAll();

        ByteArrayInputStream in = ExcelHelper.projectsToExcel(projects);
        return in;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}