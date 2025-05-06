package com.db.clm.exercise.rest;

import com.db.clm.exercise.model.Nace;
import com.db.clm.exercise.service.NaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.eq;
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
                .order("1")
                .code("123")
                .description("Test")
                .build();

        Mockito.when(naceService.readNace("1")).thenReturn(nace);

        mockMvc.perform(get("/api/nace/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order").value("1"))
                .andExpect(jsonPath("$.code").value("123"))
                .andExpect(jsonPath("$.description").value("Test"));
    }

    @Test
    void getNaceDetails404Test() throws Exception {
        Mockito.when(naceService.readNace("1")).thenReturn(null);

        mockMvc.perform(get("/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void putNaceDetailsTest() throws Exception {
        String csvContent = "Order, Level, Code, Parent, Description, This item includes, This item also includes," +
                "This item excludes, Rulings, Reference to ISIC Rev. 4\n" +
                "1, 1, 123, Parent, Test desc, Includes, Also includes, Excludes, Rulings, Ref";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "nace.csv",
                "text/csv",
                csvContent.getBytes()
        );

        mockMvc.perform(multipart("/api/nace/save")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());

        // Verify the service was called
        verify(naceService).saveFromCsv(eq(mockFile));
    }
}
