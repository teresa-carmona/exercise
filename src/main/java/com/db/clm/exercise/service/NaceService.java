package com.db.clm.exercise.service;

import com.db.clm.exercise.model.Nace;
import com.db.clm.exercise.repository.NaceRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.List;

@Service
public class NaceService {

    @Autowired
    private NaceRepository naceRepository;

    public void saveFromCsv(MultipartFile file) throws Exception {

        List<Nace> naceEntities = parseCSV(file);
        naceRepository.saveAll(naceEntities);

    }

    public Nace readNace(String id) {
        return naceRepository.findById(id).orElse(null);
    }

    public List<Nace> parseCSV(MultipartFile file) throws Exception {
        char delimiter = detectSeparator(file.getInputStream());

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<Nace> csvToBean = new CsvToBeanBuilder<Nace>(reader).withType(Nace.class).withSeparator(delimiter).withIgnoreQuotations(true).withIgnoreLeadingWhiteSpace(true).build();

            return csvToBean.parse();
        }
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
