package br.com.alfa11.mspdfmanager.controller;


import br.com.alfa11.mspdfmanager.model.FileMetadata;
import br.com.alfa11.mspdfmanager.service.ObjectStoreService;
import br.com.alfa11.mspdfmanager.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/v1/pdf")
@Tag(name = "API para Gerenciamento de PDFs")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    ObjectStoreService objectStoreService;

    @PostMapping("/upload")
    @Operation(summary = "API Upload PDFs",
            description = "Permite que um ou mais arquivos PDF sejam carregados, armazena no S3 e retorna uma lista de referências")
    public ResponseEntity<?> uploadPDF(
            @RequestParam("pdf") List<MultipartFile> files,
            @RequestParam("grupo") String grupo) {
        List<FileMetadata> fileMetadataList = new ArrayList<>();
        for (MultipartFile file : files) {
            String response = objectStoreService.uploadFile("docs-"+grupo,
                    file.getOriginalFilename(),file,"application/octet-stream");
            fileMetadataList.add(pdfService.getFileMetadata(
                    file,grupo,response));
        }
        return ResponseEntity.ok(fileMetadataList);
    }

}
