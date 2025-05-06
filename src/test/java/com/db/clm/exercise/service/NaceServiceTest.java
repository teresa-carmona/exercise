package com.db.clm.exercise.service;

import com.db.clm.exercise.model.Nace;
import com.db.clm.exercise.repository.NaceRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NaceServiceTest {

    @InjectMocks
    private NaceService naceService;

    @Mock
    private NaceRepository naceRepository;

    @Test
    void readNaceTest() {

        when(naceRepository.findById(1)).thenReturn(Optional.of(Nace.builder().order(1).build()));

        Nace result = naceService.readNace(1);

        assertNotNull(result);
        assertEquals(1, result.getOrder());
    }

    @Test
    void readNaceEmptyTest() {
        when(naceRepository.findById(1)).thenReturn(Optional.empty());

        Nace result = naceService.readNace(1);

        assertNull(result);
    }


    @Test
    void saveFromExcelTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Order");
        header.createCell(1).setCellValue("Level");
        header.createCell(2).setCellValue("Code");
        header.createCell(3).setCellValue("Parent");
        header.createCell(4).setCellValue("Description");
        header.createCell(5).setCellValue("This item includes");
        header.createCell(6).setCellValue("This item also includes");
        header.createCell(7).setCellValue("Rulings");
        header.createCell(8).setCellValue("This item excludes");
        header.createCell(9).setCellValue("Reference to ISIC Rev. 4");

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(1);
        row.createCell(1).setCellValue(2);
        row.createCell(2).setCellValue("code");
        row.createCell(3).setCellValue("parent");
        row.createCell(4).setCellValue("description");
        row.createCell(5).setCellValue("this item includes");
        row.createCell(6).setCellValue("this item also includes");
        row.createCell(7).setCellValue("rulings");
        row.createCell(8).setCellValue("this item excludes");
        row.createCell(9).setCellValue("reference");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new ByteArrayInputStream(out.toByteArray())
        );

        naceService.saveFromExcel(file);

        // Verify saveAll is called once for a file of just 1 row
        verify(naceRepository, times(1)).saveAll(anyList());
    }


    @Test
    void saveFromExcelBatchesTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");

        // Create 1200 rows - for a batch size of 500 this will need 3 batches
        // (2 batches of size 500 and a third batch of size 200)
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Order");
        header.createCell(1).setCellValue("Level");
        header.createCell(2).setCellValue("Code");
        header.createCell(3).setCellValue("Parent");
        header.createCell(4).setCellValue("Description");
        header.createCell(5).setCellValue("This item includes");
        header.createCell(6).setCellValue("This item also includes");
        header.createCell(7).setCellValue("Rulings");
        header.createCell(8).setCellValue("This item excludes");
        header.createCell(9).setCellValue("Reference to ISIC Rev. 4");
        for (int i = 1; i < 1201; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(1);
            row.createCell(1).setCellValue(2);
            row.createCell(2).setCellValue("code");
            row.createCell(3).setCellValue("parent");
            row.createCell(4).setCellValue("description");
            row.createCell(5).setCellValue("this item includes");
            row.createCell(6).setCellValue("this item also includes");
            row.createCell(7).setCellValue("rulings");
            row.createCell(8).setCellValue("this item excludes");
            row.createCell(9).setCellValue("reference");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file", "large.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new ByteArrayInputStream(out.toByteArray())
        );

        naceService.saveFromExcel(file);

        // Verify saveAll was called 3 times
        verify(naceRepository, times(3)).saveAll(anyList());
    }

    @Test
    void readNaceAllTest() {
        Nace nace1 = Nace.builder()
                .order(1)
                .build();
        Nace nace2 = Nace.builder()
                .order(2)
                .build();
        List<Nace> naceList = List.of(nace1, nace2);
        Page<Nace> mockPage = new PageImpl<>(naceList);
        Pageable pageable = PageRequest.of(0, 2);

        when(naceRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Nace> result = naceService.readAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getContent().get(0).getOrder());
        assertEquals(2, result.getContent().get(1).getOrder());
        verify(naceRepository, times(1)).findAll(pageable);
    }

}

