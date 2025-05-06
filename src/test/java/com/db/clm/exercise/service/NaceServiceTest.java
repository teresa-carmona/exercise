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
        // Create csv file with enough rows to require more than 1 batch
        // (batch size is 500)
        String csvContent = prepareLargeCSVFile();

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "nace.csv", "text/csv", csvContent.getBytes()
        );

        naceService.saveFromCsv(mockFile);

        // Verify saveAll() was called 3 veces (2 batches of size 500 + 1 batch of size 200)
        verify(naceRepository, times(3)).saveAll(anyList());
    }

    @Test
    void detectSeparatorCommaTest() throws IOException {
        // Use comma separator
        String csvContent = prepareCsvContent(",");

        detectSeparatorTest(csvContent, ',');
    }

    @Test
    void detectSeparatorSemicolonTest() throws IOException {
        // Use semicolon separator
        String csvContent = prepareCsvContent(";");

        detectSeparatorTest(csvContent, ';');
    }

    void detectSeparatorTest(String csvContent, char expectedSeparator) throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "nace.csv", "text/csv", csvContent.getBytes()
        );

        char actualSeparator = naceService.detectSeparator(file.getInputStream());

        assertThat(actualSeparator).isEqualTo(expectedSeparator);
    }


    String prepareCsvContent(String separator) {
        StringBuilder content = new StringBuilder();
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

    String prepareLargeCSVFile() {
        StringBuilder content = new StringBuilder("Order, Level, Code, Parent, Description, This item includes, " +
                "This item also includes, This item excludes, Rulings, Reference to ISIC Rev. 4\n");
        for (int i = 1; i <= 1200; i++) {
            content.append(i).append(", 1").append(", code").append(", 1").append(", description ")
                    .append(", includes").append(", also includes").append(", excludes")
                    .append(", rulings").append(", ref").append("\n");
        }
        return content.toString();
    }
}

