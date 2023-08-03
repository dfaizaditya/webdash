package com.kbbukopin.webdash.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.kbbukopin.webdash.entity.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelHelper {
  public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  static String[] HEADERs = {
          "No Tiket/RFC", "Bulan", "Unit", "Kategori", "Kategory \nProyek",
          "Nama Proyek/ Insiden", "User Sponsor", "Platform Aplikasi", "Platform Teknologi",
          "PIC", "Start Date", "Due Date", "Actual \nFinished \nDate","Tipe",
          "Progress Development (%)", "Status", "Keterangan \n(Peran DPTI hingga PRA UAT)", "Change Type",
          "RFC", "Dokumentasi", "Keterangan" };
  static Double[] columnsWidth = {
          26.75 , 11.03, 11.03, 11.03, 11.03, 11.03,
          11.03, 11.03, 11.03, 22.85,
          22.85, 11.03, 11.03, 11.03, 22.45,
          31.58, 25.2, 26.75, 22.72,
          17.15, 13.5, 152.49
  };
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

        // Inisialisasi cellstyle
        CellStyle cellStyle = workbook.createCellStyle();

        // Mengatur cell agar wrap text
        cellStyle.setWrapText(true);

        // Mengatur cell agar posisi konten di tengah
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Mengatur background color hitam
        cellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Mengatur font
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Arial");

        cellStyle.setFont(font);

        cell.setCellStyle(cellStyle);

        headerRow.setHeightInPoints(42);
        sheet.setColumnWidth(col, (int) (columnsWidth[col] * 256));
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

        // melakukan join string pada pic
        List<String> namesPic = new ArrayList<>();
        for (Pic pic : project.getPic()) {
          namesPic.add(pic.getName());
        }
        String joinedNamesPic = String.join("\n", namesPic);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");

        row.createCell(0).setCellValue(project.getId());
        row.createCell(1).setCellValue(project.getMonth());
        row.createCell(2).setCellValue(project.getUnit().equalsIgnoreCase("null") ? "" : project.getUnit());
        row.createCell(3).setCellValue(project.getCategory().equalsIgnoreCase("null") ? "" : project.getCategory());
        row.createCell(4).setCellValue(project.getCategoryProject().equalsIgnoreCase("null") ? "" : project.getCategoryProject());
        row.createCell(5).setCellValue(project.getName().equalsIgnoreCase("null") ? "" : project.getName());

//        row.createCell(5).setCellValue(joinedNamesUserSponsor.equalsIgnoreCase("null") ? "" : joinedNamesUserSponsor);
//        row.createCell(6).setCellValue(joinedNamesAppPlatform.equalsIgnoreCase("null") ? "" : joinedNamesAppPlatform);
//        row.createCell(7).setCellValue(joinedNamesTechPlatform.equalsIgnoreCase("null") ? "" : joinedNamesTechPlatform);
//        row.createCell(8).setCellValue(joinedNamesPic.equalsIgnoreCase("null") ? "" : joinedNamesPic);
        Cell cellUserSponsor = row.createCell(6);
        cellUserSponsor.setCellValue(joinedNamesUserSponsor.equalsIgnoreCase("null") ? "" : joinedNamesUserSponsor);

        Cell cellAppPlatform = row.createCell(7);
        cellAppPlatform.setCellValue(joinedNamesAppPlatform.equalsIgnoreCase("null") ? "" : joinedNamesAppPlatform);

        Cell cellTechPlatform = row.createCell(8);
        cellTechPlatform.setCellValue(joinedNamesTechPlatform.equalsIgnoreCase("null") ? "" : joinedNamesTechPlatform);

        Cell cellPic = row.createCell(9);
        cellPic.setCellValue(joinedNamesPic.equalsIgnoreCase("null") ? "" : joinedNamesPic);

        row.createCell(10).setCellValue(project.getStartDate() == null ? "" : project.getStartDate().format(formatter));
        row.createCell(11).setCellValue(project.getDueDate() == null ? "" : project.getDueDate().format(formatter));
        row.createCell(12).setCellValue(project.getFinishedDate() == null ? "" : project.getFinishedDate().format(formatter));
        row.createCell(13).setCellValue(project.getType().equalsIgnoreCase("null") ? "" : project.getType());
        row.createCell(14).setCellValue(project.getProgress().doubleValue()*100+"%");
        row.createCell(15).setCellValue(project.getStatus().equalsIgnoreCase("null") ? "" : project.getStatus());
        row.createCell(16).setCellValue(project.getInfo1().equalsIgnoreCase("null") ? "" : project.getInfo1());
        row.createCell(17).setCellValue(project.getChangeType().equalsIgnoreCase("null") ? "" : project.getChangeType());
        row.createCell(18).setCellValue(project.getRfc().equalsIgnoreCase("null") ? "" : project.getRfc());
        row.createCell(19).setCellValue(project.getDocumentation().equalsIgnoreCase("null") ? "" : project.getDocumentation());

//        row.createCell(18).setCellValue(project.getInfo2().equalsIgnoreCase("null") ? "" : project.getInfo2());
        Cell cellInfo2 = row.createCell(20);
        cellInfo2.setCellValue(project.getInfo2().equalsIgnoreCase("null") ? "" : project.getInfo2());

        // Inisialisasi cellstyle
        CellStyle cellStyle = workbook.createCellStyle();

        // Mengatur cell agar wrap text
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellInfo2.setCellStyle(cellStyle);
        cellUserSponsor.setCellStyle(cellStyle);
        cellAppPlatform.setCellStyle(cellStyle);
        cellTechPlatform.setCellStyle(cellStyle);
        cellPic.setCellStyle(cellStyle);

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

  public static ByteArrayInputStream templateExcelDayOff() {

    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      String[] dayOffFirstHeader = { "Date\n(ddmmyyyy)","Description" };
      String[] dayOffSecondHeader = { "Example\n8 Januari 2023\nmenjadi\n08012023", "" };
      Double[] columnsWidth = { 17.36 , 46.00 };
      String dayOffSheetName = "Day Off";

      Sheet sheet = workbook.createSheet(dayOffSheetName);

      // First Header
      Row firstHeaderRow = sheet.createRow(0);

      for (int col = 0; col < dayOffFirstHeader.length; col++) {
        Cell cell = firstHeaderRow.createCell(col);
        cell.setCellValue(dayOffFirstHeader[col]);

        // Inisialisasi cellstyle
        CellStyle cellStyle = workbook.createCellStyle();

        // Mengatur cell agar wrap text
        cellStyle.setWrapText(true);

        // Mengatur cell agar posisi konten di tengah
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Mengatur background color hitam
        cellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Mengatur font
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Arial");

        cellStyle.setFont(font);

        cell.setCellStyle(cellStyle);

        firstHeaderRow.setHeightInPoints(42);
        sheet.setColumnWidth(col, (int) (columnsWidth[col] * 256));
      }

      // First Header
      Row secondHeaderRow = sheet.createRow(1);

      for (int col = 0; col < dayOffSecondHeader.length; col++) {
        Cell cell = secondHeaderRow.createCell(col);
        cell.setCellValue(dayOffSecondHeader[col]);

        // Inisialisasi cellstyle
        CellStyle cellStyle = workbook.createCellStyle();

        // Mengatur cell agar wrap text
        cellStyle.setWrapText(true);

        // Mengatur cell agar posisi konten di tengah
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Mengatur background color hitam
        cellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Mengatur font
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Arial");

        cellStyle.setFont(font);

        cell.setCellStyle(cellStyle);

        secondHeaderRow.setHeightInPoints(64);
        sheet.setColumnWidth(col, (int) (columnsWidth[col] * 256));
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to download template excel: " + e.getMessage());
    }
  }
  
}