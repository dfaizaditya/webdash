package com.kbbukopin.webdash.services.nonproject.impl;

import com.kbbukopin.webdash.dto.ResponseHandler;
import com.kbbukopin.webdash.entity.*;
import com.kbbukopin.webdash.exeptions.ResourceNotFoundException;
import com.kbbukopin.webdash.helper.ExcelHelper;
import com.kbbukopin.webdash.repository.*;
import com.kbbukopin.webdash.services.nonproject.NonProjectService;
import com.kbbukopin.webdash.services.period.impl.PeriodServiceImpl;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NonProjectServiceImpl implements NonProjectService {

    @Autowired
    private NonProjectRepository nonProjectRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private PicRepository picRepository;

    @Autowired
    private PeriodServiceImpl periodServiceImpl;

    String[] daftarBulan = {
            "Januari", "Februari", "Maret", "April",
            "Mei", "Juni", "Juli", "Agustus",
            "September", "Oktober", "November", "Desember"
    };

    @Override
    public ResponseEntity<Object> getNonProjectsByFilter(Long year, String month, String name, String unit) {
        Period period = this.getPeriodByYear(year);

        List<NonProject> nonProjects = nonProjectRepository.searchNonProjects(period.getId(), month, name, unit);

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, nonProjects);
    }

    @Override
    public ResponseEntity<Object> createNonProject(NonProject newNonProject) {
        if(!nonProjectRepository.existsByData(
                newNonProject.getUnit().isBlank() ? newNonProject.getUnit() : newNonProject.getUnit().trim(),
                newNonProject.getName().isBlank() ? newNonProject.getName() : newNonProject.getName().trim(),
                monthWordFixer(newNonProject.getMonth()),
                newNonProject.getPeriod().getId())
        ){

            List<Pic> listPic = new ArrayList<>();
            for (Pic singlePic : newNonProject.getPic()){
                Pic pic;
                if(singlePic.getId() != 0){
                    pic = picRepository.findById(singlePic.getId()).orElseThrow();
                    pic.setName(singlePic.getName().trim());
                } else {
                    singlePic.setName(singlePic.getName().trim());
                    pic = singlePic;
                }
                listPic.add(pic);
            }

            NonProject nonProject = NonProject.builder()
                    .month(monthWordFixer(newNonProject.getMonth()))
                    .period(newNonProject.getPeriod())
                    .unit(newNonProject.getUnit() == null ? newNonProject.getUnit() : newNonProject.getUnit().trim())
                    .category(newNonProject.getCategory() == null ? newNonProject.getCategory() : newNonProject.getCategory().trim())
                    .name(newNonProject.getName() == null ? newNonProject.getName() : newNonProject.getName().trim())
                    .pic(listPic)
                    .build();

            nonProjectRepository.save(nonProject);

            return ResponseHandler.generateResponse("Success", HttpStatus.OK, " ");

        } else {
            return ResponseHandler.generateResponse("Failed", HttpStatus.BAD_REQUEST, " ");
        }
    }

    @Override
    public ResponseEntity<Object> getNonProjectByPrimaryKey(Long id) {
        NonProject nonProject = nonProjectRepository.getNonProjectByPrimaryKey(id)
                .orElseThrow(() -> new ResourceNotFoundException("Non Project", "primary key", id));
        return ResponseHandler.generateResponse("Success", HttpStatus.OK, nonProject);
    }

    @Transactional
    @Override
    public ResponseEntity<Object> updateNonProject(Long id, NonProject newNonProject) {
        if(!nonProjectRepository.existsByDataWithoutItself(
                id,
                newNonProject.getUnit().trim(),
                newNonProject.getName().trim(),
                newNonProject.getMonth().trim(),
                newNonProject.getPeriod().getId())
        ){
            NonProject nonProject = nonProjectRepository.getNonProjectByPrimaryKey(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Non Project", "primary key", id));

            List<Pic> listPic = new ArrayList<>();
            for (Pic singlePic : newNonProject.getPic()){
                Pic pic;
                if(singlePic.getId() != 0){
                    pic = picRepository.findById(singlePic.getId()).orElseThrow();
                    pic.setName(singlePic.getName().trim());
                } else {
                    pic = singlePic;
                }
                listPic.add(pic);
            }

            nonProject.setMonth(monthWordFixer(newNonProject.getMonth()));
            nonProject.setPeriod(newNonProject.getPeriod());
            nonProject.setUnit(newNonProject.getUnit().trim());
            nonProject.setCategory(newNonProject.getCategory().trim());
            nonProject.setName(newNonProject.getName().trim());
            nonProject.setPic(listPic);

            NonProject updatedNonProject = nonProjectRepository.save(nonProject);

            picRepository.deletePicNotExistOnPivot();

            return ResponseHandler.generateResponse("Success", HttpStatus.OK, updatedNonProject);
        } else {
            return ResponseHandler.generateResponse("Failed", HttpStatus.BAD_REQUEST, " ");
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Object> deleteMultipleNonProjects(List<Long> ids) {

        nonProjectRepository.deleteNonProjectEntries(ids);
        picRepository.deletePicNotExistOnPivot();

        return ResponseHandler.generateResponse("Success", HttpStatus.OK, " ");
    }

    @Override
    public ResponseEntity<Object> importToDb(Long period_id, List<MultipartFile> multipleFiles) {
        Period period = new Period();
        period.setId(period_id);

        if (!multipleFiles.isEmpty()) {
            List<NonProject> nonProjects = new ArrayList<>();
            List<NonProject> listFailedImportNonProjects = new ArrayList<>();
            multipleFiles.forEach(multipartFile -> {
                try {
                    XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
                    XSSFSheet sheet = workbook.getSheetAt(0);

//                    System.out.print(getNumberOfNonEmptyCells(sheet, 0));
                    // looping----------------------------------------------------------------
                    for (int rowIndex = 0; rowIndex < getNumberOfNonEmptyCells(sheet, 1) - 1; rowIndex++) {

                        // datatoString skip header
                        XSSFRow row = sheet.getRow(rowIndex + 1);

                        String month = monthWordFixer(String.valueOf(row.getCell(1)));
                        String unit = String.valueOf(row.getCell(2)).trim();
//                        String category = String.valueOf(row.getCell(3)).trim();
                        String category = "Non Proyek";
                        String name = String.valueOf(row.getCell(5)).trim();
                        String pic = String.valueOf(row.getCell(9));

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

                        NonProject nonProject = NonProject.builder()
                                .period(period)
                                .month(month)
                                .unit(unit)
                                .category(category)
                                .name(name)
                                .pic(pics)
                                .build();

                        if(!(unit.isBlank()) && !(name.isBlank()) && !(month.isBlank())){
                            nonProjects.add(nonProject);
                        } else {
                            listFailedImportNonProjects.add(nonProject);
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

            if (!nonProjects.isEmpty()) {
                // save to database
                nonProjectRepository.saveAll(nonProjects);
            }

            return ResponseHandler.generateResponse("Success", HttpStatus.OK, listFailedImportNonProjects);
        } else {
            return ResponseHandler.generateResponse("Failed", HttpStatus.BAD_REQUEST, " ");
        }
    }

    @Override
    public ByteArrayInputStream load(Long year, String month) {

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
            return null;
        }

        List<NonProject> nonProjects = nonProjectRepository.exportNonProjects(period.getId(), rangeMonth);

        ByteArrayInputStream in = ExcelHelper.nonProjectsToExcel(nonProjects);
        return in;
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
}
