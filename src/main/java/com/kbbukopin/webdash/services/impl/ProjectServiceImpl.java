package com.kbbukopin.webdash.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
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
import com.kbbukopin.webdash.entity.Project;
import com.kbbukopin.webdash.exception.ApiException;
import com.kbbukopin.webdash.exeptions.ResourceNotFoundException;
import com.kbbukopin.webdash.helper.ExcelHelper;
import com.kbbukopin.webdash.repository.ProjectRepository;
import com.kbbukopin.webdash.services.ProjectService;
import com.kbbukopin.webdash.utils.AppUtils;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

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
    public ResponseEntity<Object> getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        return ResponseHandler.generateResponse("Success", HttpStatus.OK, project);
    }

    @Override
    public ResponseEntity<Object> getProjectsByFilter(String name, String unit, String category) {
        List<Project> projects = projectRepository.searchProjects(name, unit, category);
        return ResponseHandler.generateResponse("Success", HttpStatus.OK, projects);
    }

    @Override
    public ResponseEntity<Object> updateProject(Long id, @RequestBody Project newProject) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        project.setUnit(newProject.getUnit());
        project.setCategory(newProject.getCategory());
        project.setUserSponsor(newProject.getUserSponsor());
        project.setAppPlatform(newProject.getAppPlatform());
        project.setTechPlatform(newProject.getTechPlatform());
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

    @Override
    public ResponseEntity<Object> deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        projectRepository.deleteById(id);

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, " ");
    }

    @Override
    public ResponseEntity<Object> getProjectStat() {

        List<String> clType = projectRepository.getColumnTypeList();
        List<String> clComplete = projectRepository.getColumnCompleteList();
        List<String> clUnit = projectRepository.getColumnUnitList();

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

    @Override
    public ResponseEntity<Object> getEvidenceKpi() {
        String[] tipe = {"In House", "Insiden", "JoinDev", "Outsource"};
        String[] status = {"Not Done", "Ahead", "On time", "Overdue"};

        LinkedMap<String, String> category = new LinkedMap<>();

        for (int i = 0; i < tipe.length; i++) {
            if(tipe[i].equalsIgnoreCase("insiden")){
                category.put(tipe[i], "Insiden");
            } else {
                category.put(tipe[i], "Proyek");
            }
        }

        LinkedMap<String, Integer> data = new LinkedMap<>();
        LinkedMap<String, Integer> total = new LinkedMap<>();

        total.put("Total Project", 0);
        total.put("Total Selesai", 0);
        total.put("Selesai Cepat", 0);
        total.put("Selesai Overdue", 0);
        total.put("KPI", 0);

        // #dataput
        for (int i = 0; i < tipe.length; i++) {
            int tempCount = 0, tempTotalPerTipe = 0;
            for (int j = 0; j < status.length; j++) {
                if (tipe[i].equalsIgnoreCase("Insiden")) {
                    if (status[j].equalsIgnoreCase("Not Done")) {
                        tempCount = projectRepository.countUnfinishedProject(category.get(tipe[i]), "%");
                        data.put(tipe[i] + " " + status[j], tempCount);
                    } else {
                        tempCount = projectRepository.countFinishedProject(category.get(tipe[i]), "Finished%" + status[j] + "%", "%");
                        data.put(tipe[i] + " " + status[j], tempCount);
                        total.put("Total Selesai", total.get("Total Selesai") + tempCount);
                    }
                } else {
                    if (status[j].equalsIgnoreCase("Not Done")) {
                        tempCount = projectRepository.countUnfinishedProject(category.get(tipe[i]), tipe[i]);
                        data.put(tipe[i] + " " + status[j], tempCount);
                    } else {
                        tempCount = projectRepository.countFinishedProject(category.get(tipe[i]), "Finished%" + status[j] + "%", tipe[i]);
                        data.put(tipe[i] + " " + status[j], tempCount);
                        total.put("Total Selesai", total.get("Total Selesai") + tempCount);
                    }
                }

                if(status[j].equalsIgnoreCase("Ahead")) {
                    total.put("Selesai Cepat", total.get("Selesai Cepat") + tempCount);
                } else if (status[j].equalsIgnoreCase("Overdue")){
                    total.put("Selesai Overdue", total.get("Selesai Overdue") + tempCount);
                }

                tempTotalPerTipe += tempCount;

            }
            data.put(tipe[i] + " Total", tempTotalPerTipe);
            total.put("Total Project", total.get("Total Project") + tempTotalPerTipe);
        }

        // untuk #dataput -nya kurang lebih penginputan ke variabel data seperti ini
//        for (int i = 0; i < tipe.length; i++) {
//            if(tipe[i].equalsIgnoreCase("insiden")) {
//                data.put(tipe[i] + " Not Done", projectRepository.countUnfinishedProject( category.get(tipe[i]),"%"));
//                data.put(tipe[i] + " " + status[0], projectRepository.countFinishedProject( category.get(tipe[i]),"Finished%" + status[0] + "%","%"));
//                data.put(tipe[i] + " " + status[1], projectRepository.countFinishedProject( category.get(tipe[i]),"Finished%" + status[1] + "%", "%"));
//                data.put(tipe[i] + " " + status[2], projectRepository.countFinishedProject( category.get(tipe[i]),"Finished%" + status[2] + "%", "%"));
//            } else {
//                data.put(tipe[i] + " Not Done", projectRepository.countUnfinishedProject( category.get(tipe[i]),tipe[i]));
//                data.put(tipe[i] + " " + status[0], projectRepository.countFinishedProject( category.get(tipe[i]),"Finished%" + status[0] + "%",tipe[i]));
//                data.put(tipe[i] + " " + status[1], projectRepository.countFinishedProject( category.get(tipe[i]),"Finished%" + status[1] + "%", tipe[i]));
//                data.put(tipe[i] + " " + status[2], projectRepository.countFinishedProject( category.get(tipe[i]),"Finished%" + status[2] + "%", tipe[i]));
//            }
//        }

        double convertCalcToDouble = 1.0;
        total.put("KPI", (int) Math.round(((total.get("Total Selesai")*convertCalcToDouble / total.get("Total Project")) +
                        (total.get("Selesai Cepat")*convertCalcToDouble / total.get("Total Selesai")) -
                        (total.get("Selesai Overdue")*convertCalcToDouble / total.get("Total Selesai")))*100)
        );

        // Line 230-232 hanya dummy dengan angka sesuai dokumen
//        total.put("KPI", (int) Math.round(
//                ((10*convertCalcToDouble / 25) + (3*convertCalcToDouble / 10) - (4*convertCalcToDouble / 10))*100)
//        );

        ArrayList<LinkedMap> projects = new ArrayList<LinkedMap>();
        projects.add(data);
        projects.add(total);

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, projects);
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
    public void importToDb(List<MultipartFile> multipleFiles) {
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
                        String unit = String.valueOf(row.getCell(1));
                        String category = String.valueOf(row.getCell(2));
                        String name = String.valueOf(row.getCell(3));
                        String userSponsor = String.valueOf(row.getCell(4));
                        String appPlatform = String.valueOf(row.getCell(5));
                        String techPlatform = String.valueOf(row.getCell(6));

                        //parse data to localDate
                        LocalDate startDate = ExcelHelper.parseLocalDate(row.getCell(7));
                        LocalDate dueDate = ExcelHelper.parseLocalDate(row.getCell(8));

                        String type = String.valueOf(row.getCell(9));

//                        BigDecimal progress = new BigDecimal(row.getCell(10).toString());

                        //handling data progress
                        String tempProgress = String.valueOf(row.getCell(10));
                        BigDecimal progress = null;
                        if(tempProgress.matches("[[0-9.%]+]+")) {
                            if(!(tempProgress.equalsIgnoreCase(".")) ||
                                    !(tempProgress.equalsIgnoreCase("%"))){
                                progress = new BigDecimal(tempProgress);
                            }
                        }

                        String status = String.valueOf(row.getCell(11));
                        String info1 = String.valueOf(row.getCell(12));
                        String changeType = String.valueOf(row.getCell(13));
                        String rfc = String.valueOf(row.getCell(14));
                        String documentation = String.valueOf(row.getCell(15));
                        String info2 = String.valueOf(row.getCell(16));

                        Project project = Project.builder()
                                .id(id)
                                .unit(unit)
                                .category(category)
                                .name(name)
                                .userSponsor(userSponsor)
                                .appPlatform(appPlatform)
                                .techPlatform(techPlatform)
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