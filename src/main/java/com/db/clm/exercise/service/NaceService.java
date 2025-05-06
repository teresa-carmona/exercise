package com.db.clm.exercise.service;

import com.db.clm.exercise.model.Nace;
import com.db.clm.exercise.repository.NaceRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class NaceService {

    private static final int BATCH_SIZE = 500;

    @Autowired
    private NaceRepository naceRepository;

    public void saveFromCsv(MultipartFile file) throws Exception {
        char delimiter = detectSeparator(file.getInputStream());

        try (
                Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
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


    public Nace readNace(String id) {
        return naceRepository.findById(id).orElse(null);
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


}
