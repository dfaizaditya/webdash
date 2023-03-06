package com.kbbukopin.webdash.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.kbbukopin.webdash.entity.Project;

public class ExcelHelper {
  public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  static String[] HEADERs = { "Id", "Title", "Description", "Published" };
  static String SHEET = "Projects";

  public static boolean hasExcelFormat(MultipartFile file) {

    if (!TYPE.equals(file.getContentType())) {
      return false;
    }

    return true;
  }

  public static ByteArrayInputStream projectsToExcel(List<Project> projects) {

    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
      Sheet sheet = workbook.createSheet(SHEET);

      // Header
      Row headerRow = sheet.createRow(0);

      for (int col = 0; col < HEADERs.length; col++) {
        Cell cell = headerRow.createCell(col);
        cell.setCellValue(HEADERs[col]);
      }

      int rowIdx = 1;
      for (Project project : projects) {
        Row row = sheet.createRow(rowIdx++);

        row.createCell(0).setCellValue(project.getId());
        row.createCell(1).setCellValue(project.getTitle());
        row.createCell(2).setCellValue(project.getDescription());
        row.createCell(3).setCellValue(project.isPublished());
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  public static List<Project> excelToProjects(InputStream is) {
    try {
      Workbook workbook = WorkbookFactory.create(is);
      Sheet sheet = workbook.getSheetAt(0);
      
      Iterator<Row> rows = sheet.iterator();

      List<Project> projects = new ArrayList<Project>();

      int rowNumber = 0;
      while (rows.hasNext()) {
        Row currentRow = rows.next();

        // skip header
        if (rowNumber == 0) {
          rowNumber++;
          continue;
        }

        Iterator<Cell> cellsInRow = currentRow.iterator();

        Project project = new Project();

        int cellIdx = 0;
        while (cellsInRow.hasNext()) {
          Cell currentCell = cellsInRow.next();

          switch (cellIdx) {
          case 0:
            project.setId((long) currentCell.getNumericCellValue());
            break;

          case 1:
            project.setTitle(currentCell.getStringCellValue());
            break;

          case 2:
            project.setDescription(currentCell.getStringCellValue());
            break;

          case 3:
            project.setPublished(Boolean.parseBoolean(currentCell.getStringCellValue()));
            break;

          default:
            break;
          }

          cellIdx++;
        }

        projects.add(project);
      }

      workbook.close();

      return projects;
    } catch (IOException e) {
      throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
    }
  }
}