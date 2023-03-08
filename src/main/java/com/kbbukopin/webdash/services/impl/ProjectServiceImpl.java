package com.kbbukopin.webdash.services.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;

import com.kbbukopin.webdash.dto.PagedResponse;
import com.kbbukopin.webdash.dto.ResponseHandler;
import com.kbbukopin.webdash.entity.Project;
import com.kbbukopin.webdash.exception.ApiException;
import com.kbbukopin.webdash.exeptions.ResourceNotFoundException;
import com.kbbukopin.webdash.repository.ProjectRepository;
import com.kbbukopin.webdash.services.ProjectService;
import com.kbbukopin.webdash.utils.AppUtils;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

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
    public ResponseEntity<Object> updateProject(Long id, Project newProject) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        project.setUnit(newProject.getUnit());
        project.setCategory(newProject.getCategory());
        Project updatedProject = projectRepository.save(project);

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, updatedProject);
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

                        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                        Double start = Double.parseDouble(getValue(row.getCell(7)).toString());
                        LocalDate startDate = LocalDate.parse(convertDateEvo(start ),dateFormat);

                        Double due = Double.parseDouble(getValue(row.getCell(8)).toString());
                        LocalDate dueDate = LocalDate.parse(convertDateEvo(due), dateFormat);

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
                String value = cell.getStringCellValue();
                System.out.println(value);
                if (value.equals("N/A")){
                    return null;
                }
                return value;
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
}