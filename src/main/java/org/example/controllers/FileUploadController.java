package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.Client;
import org.example.services.ClientService;
import org.example.utils.FileParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class FileUploadController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/file/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("inside uploadFile method");
        try {
            List<Client> clients = FileParser.parseFile(file);
            clientService.saveAllClients(clients);
            return "File uploaded and data added to the database successfully!";
        } catch (Exception e) {
            log.error("error");

            return "Error uploading file: " + e.getMessage();
        }
    }

    @GetMapping("/avgsalary/profession")
    public ResponseEntity<Object>  calculate() {
        return ResponseEntity.ok(clientService.calculateAverageSalaryByProfession());
    }
}
