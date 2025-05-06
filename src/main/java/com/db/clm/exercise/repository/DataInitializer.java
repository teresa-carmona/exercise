package com.db.clm.exercise.repository;

import com.db.clm.exercise.model.Nace;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(NaceRepository naceRepository) {
        return args -> {
            try (Reader reader = new InputStreamReader(
                    getClass().getResourceAsStream("/data/init.csv"))) {

                CsvToBean<Nace> csvToBean = new CsvToBeanBuilder<Nace>(reader)
                        .withType(Nace.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                List<Nace> nacelist = csvToBean.parse();
                naceRepository.saveAll(nacelist);

                log.info("Data initialized");
            } catch (Exception e) {
                log.error("Error initializing data: " + e.getMessage());
            }
        };
    }
}

