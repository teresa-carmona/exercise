package com.db.clm.exercise.repository;

import com.db.clm.exercise.exception.FileException;
import com.db.clm.exercise.model.Nace;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private NaceRepository naceRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Nace> naceList = new ArrayList<>();

        try (InputStream inputStream = new ClassPathResource("data/init.xlsx").getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;

            for (Row row : sheet) {
                if (rowCount++ == 0) continue;

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

                naceList.add(nace);
            }
        }

        naceRepository.saveAll(naceList);
        log.error("Database initialized");
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
