package com.db.clm.exercise.repository;

import com.db.clm.exercise.model.Nace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class NaceRepositoryTest {

    @Autowired
    private NaceRepository naceRepository;

    @Test
    void saveAndFindByIdTest() {
        Nace nace = Nace.builder()
                .order(1)
                .level(2)
                .code("123")
                .parent("parent")
                .description("description")
                .includes("includes")
                .alsoIncludes("also includes")
                .excludes("excludes")
                .rulings("rulings")
                .reference("ref isic rev. 4")
                .build();

        naceRepository.save(nace);

        Optional<Nace> result = naceRepository.findById(1);
        assertTrue(result.isPresent());
        Nace foundNace = result.get();
        assertEquals(1, foundNace.getOrder());
        assertEquals(2, foundNace.getLevel());
        assertEquals("123", foundNace.getCode());
        assertEquals("parent", foundNace.getParent());
        assertEquals("description", foundNace.getDescription());
        assertEquals("includes", foundNace.getIncludes());
        assertEquals("also includes", foundNace.getAlsoIncludes());
        assertEquals("excludes", foundNace.getExcludes());
        assertEquals("rulings", foundNace.getRulings());
        assertEquals("ref isic rev. 4", foundNace.getReference());
    }

    @Test
    void FindByIdWhenEmptyTest() {
        Optional<Nace> result = naceRepository.findById(99);
        assertFalse(result.isPresent());
    }
}

