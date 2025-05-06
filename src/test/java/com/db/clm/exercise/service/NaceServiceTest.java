package com.db.clm.exercise.service;

import com.db.clm.exercise.model.Nace;
import com.db.clm.exercise.repository.NaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NaceServiceTest {

    @InjectMocks
    private NaceService naceService;

    @Mock
    private NaceRepository naceRepository;

    @Test
    void readNaceTest() {

        Mockito.when(naceRepository.findById("1")).thenReturn(Optional.of(Nace.builder().order("1").build()));

        Nace result = naceService.readNace("1");

        assertNotNull(result);
        assertEquals("1", result.getOrder());
    }

    @Test
    void readNaceEmptyTest() {
        Mockito.when(naceRepository.findById("1")).thenReturn(Optional.empty());

        Nace result = naceService.readNace("1");

        assertNull(result);
    }

    @Test
    void saveFromCsvTest() throws Exception {
        String csvContent = "Order, Level, Code, Parent, Description, This item includes, This item also includes," +
                "This item excludes, Rulings, Reference to ISIC Rev. 4\n" +
                "1, 1, 123, Parent, Test desc, Includes, Also includes, Excludes, Rulings, Ref";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "nace.csv",
                "text/csv",
                csvContent.getBytes()
        );

        naceService.saveFromCsv(file);

        // Verify the save method in the repository is called
        verify(naceRepository, times(1)).saveAll(anyList());
    }

    @Test
    void parseCSVTest() throws Exception {
        // Use comma separator
        String csvContent = prepareCsvContent(",");

        MockMultipartFile file = new MockMultipartFile(
                "file", "nace.csv", "text/csv", csvContent.getBytes()
        );

        List<Nace> parsedData = naceService.parseCSV(file);

        // Verify parsing
        assertThat(parsedData).isNotNull();
        assertThat(parsedData.size()).isEqualTo(1);
        Nace nace = parsedData.get(0);
        assertThat(nace.getOrder()).isEqualTo("1");
        assertThat(nace.getCode()).isEqualTo("123");
        assertThat(nace.getDescription()).isEqualTo("Test desc");
    }



    String prepareCsvContent(String separator) {
        StringBuffer content = new StringBuffer();
        content.append("Order" + separator);
        content.append("Level" + separator);
        content.append("Code" + separator);
        content.append("Parent" + separator);
        content.append("Description" + separator);
        content.append("This item includes" + separator);
        content.append("This item also includes" + separator);
        content.append("This item excludes" + separator);
        content.append("Rulings" + separator);
        content.append("Reference to ISIC Rev. 4\n");
        content.append("1" + separator);
        content.append("1" + separator);
        content.append("123" + separator);
        content.append("Parent" + separator);
        content.append("Test desc" + separator);
        content.append("Includes" + separator);
        content.append("Also includes" + separator);
        content.append("Excludes" + separator);
        content.append("Rulings" + separator);
        content.append("Ref");

        return content.toString();
    }
}