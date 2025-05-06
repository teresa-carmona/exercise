package com.db.clm.exercise.service;

import com.db.clm.exercise.exception.FileException;
import com.db.clm.exercise.model.Nace;
import com.db.clm.exercise.repository.NaceRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class NaceService {

    private static final int BATCH_SIZE = 500;

    @Autowired
    private NaceRepository naceRepository;

    public void saveFromCsv(MultipartFile file) throws Exception {
        char delimiter = detectSeparator(file.getInputStream());

        try (
                Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))
        ) {
            CsvToBean<Nace> csvToBean = new CsvToBeanBuilder<Nace>(reader)
                    .withType(Nace.class)
                    .withSeparator(delimiter)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreQuotations(true)
                    .build();

            // Read csv line by line to avoid OOM errors processing large files
            Iterator<Nace> iterator = csvToBean.iterator();

            List<Nace> batch = new ArrayList<>();

            while (iterator.hasNext()) {
                batch.add(iterator.next());

                // Save only in batches of size BATCH_SIZE
                // to reduce the load in database operations for large files
                if (batch.size() == BATCH_SIZE) {
                    naceRepository.saveAll(batch);
                    // Reset batch
                    batch.clear();
                }
            }

            // Save rest
            if (!batch.isEmpty()) {
                naceRepository.saveAll(batch);
            }
        }
    }


    public Nace readNace(int id) {
        return naceRepository.findById(id).orElse(null);
    }

    public Page<Nace> readAll(Pageable pageable) {
        return naceRepository.findAll(pageable);
    }



    public char detectSeparator(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String firstLine = reader.readLine();

            if (firstLine == null) throw new IOException("Empty file!");

            int commas = firstLine.split(",").length;
            int semicolons = firstLine.split(";").length;

            return semicolons > commas ? ';' : ',';  // choose the one that appears most
        }
    }


    public void saveFromExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Nace> batch = new ArrayList<>(BATCH_SIZE);

            try {

                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Nace nace = Nace.builder()
                            .order(getCellValueInt(row.getCell(0)))
                            .level(getCellValueInt(row.getCell(1)))
                            .code(getCellValueString(row.getCell(2)))
                            .parent(getCellValueString(row.getCell(3)))
                            .description(getCellValueString(row.getCell(4)))
                            .includes(getCellValueString(row.getCell(5)))
                            .alsoIncludes(getCellValueString(row.getCell(6)))
                            .rulings(getCellValueString(row.getCell(7)))
                            .excludes(getCellValueString(row.getCell(8)))
                            .reference(getCellValueString(row.getCell(9)))
                            .build();

                    batch.add(nace);

                    if (batch.size() >= BATCH_SIZE) {
                        naceRepository.saveAll(batch);
                        batch.clear();
                    }
                }

                if (!batch.isEmpty()) {
                    naceRepository.saveAll(batch);
                }
            } catch(FileException fe) {
                log.error(fe.getMessage(), fe);
                throw fe;
            }
        }
    }

    private String getCellValueString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private Integer getCellValueInt(Cell cell) {
        if (cell == null) return null;
        if (CellType.NUMERIC == cell.getCellType()) return (int) cell.getNumericCellValue();
        else throw new FileException("Expected cell type NUMERIC but was " + cell.getCellType().toString());
    }

}
