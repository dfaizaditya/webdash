package com.kbbukopin.webdash.services.dayOff.impl;

import com.kbbukopin.webdash.dto.ResponseHandler;
import com.kbbukopin.webdash.entity.*;
import com.kbbukopin.webdash.helper.ExcelHelper;
import com.kbbukopin.webdash.repository.DayOffRepository;
import com.kbbukopin.webdash.services.dayOff.DayOffService;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DayOffServiceImpl implements DayOffService {
    @Autowired
    private DayOffRepository dayOffRepository;

    @Override
    public ResponseEntity<Object> importToDb(List<MultipartFile> multipleFiles) {

        if (!multipleFiles.isEmpty()) {
            List<DayOff> dayOffs = new ArrayList<>();
            multipleFiles.forEach(multipartFile -> {
                try {
                    XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
                    XSSFSheet sheet = workbook.getSheetAt(0);

//                    System.out.print(getNumberOfNonEmptyCells(sheet, 0));
                    // looping----------------------------------------------------------------
                    for (int rowIndex = 0; rowIndex < getNumberOfNonEmptyCells(sheet, 0) - 1; rowIndex++) {

                        XSSFRow row = sheet.getRow(rowIndex + 1);
                        // datatoString skip header

                        String stringDate = "";
                        if(row.getCell(0).getCellType() == CellType.NUMERIC){
                            row.getCell(0).setCellType(CellType.STRING);

                            String stringRawDate = String.valueOf(row.getCell(0));
                            if(stringRawDate.length() == 7) {
                                // setting format to yyyy/MM/dd
                                stringDate = ""+
                                        stringRawDate.charAt(3)+stringRawDate.charAt(4)+stringRawDate.charAt(5)+stringRawDate.charAt(6)+
                                        "/"+stringRawDate.charAt(1)+stringRawDate.charAt(2)+
                                        "/0"+stringRawDate.charAt(0);
                            } else if (stringRawDate.length() == 8){
                                stringDate = ""+
                                        stringRawDate.charAt(4)+stringRawDate.charAt(5)+stringRawDate.charAt(6)+stringRawDate.charAt(7)+
                                        "/"+stringRawDate.charAt(2)+stringRawDate.charAt(3)+
                                        "/"+stringRawDate.charAt(0)+stringRawDate.charAt(1);
                            } else {
                                stringDate = null;
                            }
                        }

                        LocalDate date = null;
                        if(!stringDate.equals("")) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                            date = LocalDate.parse(stringDate, formatter);

                            String description = String.valueOf(row.getCell(1));

                            DayOff dayOff = DayOff.builder()
                                    .date(date)
                                    .description(description)
                                    .build();

                            dayOffs.add(dayOff);
                        }
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

            if (!dayOffs.isEmpty()) {
                // save to database
                dayOffRepository.saveAll(dayOffs);
            }
        }

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, " ");
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
