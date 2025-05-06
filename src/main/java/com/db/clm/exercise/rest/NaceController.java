package com.db.clm.exercise.rest;


import com.db.clm.exercise.model.Nace;
import com.db.clm.exercise.service.NaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/nace")
@Tag(name = "NACE elements", description = "Persists and retrieves NACE data")
public class NaceController {

    @Autowired
    private NaceService naceService;

    @Operation(summary = "Persists data from an Excel file")
    @PutMapping("/save")
    public ResponseEntity<String> putNaceDetails(@RequestParam("file") MultipartFile file) {
        try {
            naceService.saveFromExcel(file);
            return ResponseEntity.ok("Data successfully saved");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing CSV file: " + e.getMessage());
        }
    }

    @Operation(summary = "Gets NACE details from code")
    @GetMapping("/{id}")
    public ResponseEntity<Nace> getNaceDetails(@PathVariable int id) {
        Nace nace = naceService.readNace(id);
        if (nace == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nace);
    }

    @Operation(summary = "Gets all NACE records with pagination")
    @GetMapping
    public ResponseEntity<Page<Nace>> getAllNaceDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Nace> nacePage = naceService.readAll(pageable);
        return ResponseEntity.ok(nacePage);
    }

}

