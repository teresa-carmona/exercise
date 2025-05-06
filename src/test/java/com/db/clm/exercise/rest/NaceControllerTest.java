package com.db.clm.exercise.rest;

import com.db.clm.exercise.model.Nace;
import com.db.clm.exercise.service.NaceService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(NaceController.class)
class NaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Deprecated and marked for removal but will still use
    // for the purpose of this exercise (allows mockMvc for controller tests)
    @MockBean
    private NaceService naceService;

    @BeforeEach


    @Test
    void getNaceDetails200Test() throws Exception {
        Nace nace = Nace.builder()
                .order(1)
                .code("123")
                .description("Test")
                .build();

        Mockito.when(naceService.readNace(1)).thenReturn(nace);

        mockMvc.perform(get("/api/nace/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order").value(1))
                .andExpect(jsonPath("$.code").value("123"))
                .andExpect(jsonPath("$.description").value("Test"));
    }

    @Test
    void getNaceDetails404Test() throws Exception {
        Mockito.when(naceService.readNace(1)).thenReturn(null);

        mockMvc.perform(get("/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    void putNaceDetailsTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");
        Row row = sheet.createRow(0);
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

        mockMvc.perform(multipart("/api/nace/save")
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk());

        verify(naceService, times(1)).saveFromExcel(eq(file));
    }
}
