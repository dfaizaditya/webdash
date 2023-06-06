package com.kbbukopin.webdash.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.kbbukopin.webdash.entity.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

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

        // melakukan join string pada user sponsor
        List<String> namesUserSponsor = new ArrayList<>();
        for (UserSponsor userSponsor : project.getUserSponsor()) {
          namesUserSponsor.add(userSponsor.getName());
        }
        String joinedNamesUserSponsor = String.join("\n", namesUserSponsor);

        // melakukan join string pada app platform
        List<String> namesAppPlatform = new ArrayList<>();
        for (AppPlatform appPlatform : project.getAppPlatform()) {
          namesAppPlatform.add(appPlatform.getName());
        }
        String joinedNamesAppPlatform = String.join("\n", namesAppPlatform);

        // melakukan join string pada tech platform
        List<String> namesTechPlatform = new ArrayList<>();
        for (TechPlatform techPlatform : project.getTechPlatform()) {
          namesTechPlatform.add(techPlatform.getName());
        }
        String joinedNamesTechPlatform = String.join("\n", namesTechPlatform);

        // melakukan join string pada tech platform
        List<String> namesPic = new ArrayList<>();
        for (Pic pic : project.getPic()) {
          namesPic.add(pic.getName());
        }
        String joinedNamesPic = String.join("\n", namesPic);

        row.createCell(0).setCellValue(project.getId());
        row.createCell(1).setCellValue(project.getMonth());
        row.createCell(2).setCellValue(project.getUnit());
        row.createCell(3).setCellValue(project.getCategory());
        row.createCell(4).setCellValue(project.getName());
        row.createCell(5).setCellValue(joinedNamesUserSponsor);
        row.createCell(6).setCellValue(joinedNamesAppPlatform);
        row.createCell(7).setCellValue(joinedNamesTechPlatform);
        row.createCell(8).setCellValue(joinedNamesPic);
        row.createCell(9).setCellValue(project.getStartDate());
        row.createCell(10).setCellValue(project.getDueDate());
        row.createCell(11).setCellValue(project.getType());
        row.createCell(12).setCellValue(project.getProgress().doubleValue());
        row.createCell(13).setCellValue(project.getStatus());
        row.createCell(14).setCellValue(project.getInfo1());
        row.createCell(15).setCellValue(project.getChangeType());
        row.createCell(16).setCellValue(project.getRfc());
        row.createCell(17).setCellValue(project.getDocumentation());
        row.createCell(18).setCellValue(project.getInfo2());
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

  public static LocalDate parseLocalDate(Cell cell) {
    if (cell.getCellType() == CellType.NUMERIC) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      return cell.getLocalDateTimeCellValue().toLocalDate();
    } else {
      String value = cell.getStringCellValue().trim();
      if (value.equals("") || value.equalsIgnoreCase("N/A")) {
        return null;
      } else {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(value, formatter);
      }
    }
  }
  
}