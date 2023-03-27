package com.kbbukopin.webdash.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.kbbukopin.webdash.entity.Project;

public class ExcelHelper {
  public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  static String[] HEADERs = { "No Tiket/RFC",
      "Unit", "Kategori", "Nama Proyek/Insiden",
      "User Sponsor", "Platform Aplikasi",
      "Platform Teknologi", "Start Date", "Due Date",
      "Tipe", "Progress Development (%)", "Status",
      "Keterangan (Peran DPTI hingga PRA UAT)",
      "Change", "Type", "RFC", "Dokumentasi", "Keterangan" };
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
        row.createCell(1).setCellValue(project.getUnit());
        row.createCell(2).setCellValue(project.getCategory());
        row.createCell(3).setCellValue(project.getName());
        row.createCell(4).setCellValue(project.getUserSponsor());
        row.createCell(5).setCellValue(project.getAppPlatform());
        row.createCell(6).setCellValue(project.getTechPlatform());
        row.createCell(7).setCellValue(project.getStartDate());
        row.createCell(8).setCellValue(project.getDueDate());
        row.createCell(9).setCellValue(project.getType());
        row.createCell(10).setCellValue(project.getProgress().doubleValue());
        row.createCell(11).setCellValue(project.getStatus());
        row.createCell(12).setCellValue(project.getInfo1());
        row.createCell(13).setCellValue(project.getChangeType());
        row.createCell(14).setCellValue(project.getRfc());
        row.createCell(15).setCellValue(project.getDocumentation());
        row.createCell(16).setCellValue(project.getInfo2());
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  public static String getCellValueAsString(Cell cell) {
    if (cell == null) {
        return null;
    }
    
    String value = cell.getStringCellValue().trim();
    if (value.equalsIgnoreCase("N/A")) {
        return null;
    } else {
        return value;
    }
  }

  public static Double getCellValueAsDouble(Cell cell) {
    if (cell == null) {
        return null;
    }
    
    if (cell.getCellType() == CellType.NUMERIC) {
        return cell.getNumericCellValue();
    } else {
        String value = cell.getStringCellValue().trim();
        if (value.equalsIgnoreCase("N/A")) {
            return null;
        } else {
            return Double.parseDouble(value);
        }
    }
  }
}