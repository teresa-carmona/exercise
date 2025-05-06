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
        return null;
    }


}
