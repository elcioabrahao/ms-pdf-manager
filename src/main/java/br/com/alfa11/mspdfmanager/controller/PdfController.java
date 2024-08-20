package br.com.alfa11.mspdfmanager.controller;

import br.com.alfa11.mspdfmanager.model.ComposedDocument;
import br.com.alfa11.mspdfmanager.model.FileMetadata;
import br.com.alfa11.mspdfmanager.service.ObjectStoreService;
import br.com.alfa11.mspdfmanager.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/merge")
    @Operation(summary = "Mescla arquivos PDF - Retorna PDF",
            description = "Permite que um ou mais arquivos PDF sejam concatenados e que informação seja incorporada em um documento específico.")
    public ResponseEntity<?> mergePDF(@RequestBody ComposedDocument composedDocument) {

        byte[] contents = null;
        try {
            contents = pdfService.stampContent(composedDocument);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "result.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
            return response;
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }


    }

    @GetMapping("/merge/store")
    @Operation(summary = "Mescla arquivos PDF - Retorna Storage URL",
            description = "Permite que um ou mais arquivos PDF sejam concatenados e que informação seja incorporada em um documento específico.")
    public ResponseEntity<?> mergeAndStorePDF(@RequestBody ComposedDocument composedDocument) {

        FileMetadata contents = null;
        try {
            contents = pdfService.stampAndStoreContent(composedDocument);
            return ResponseEntity.ok(contents);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }


    }


    @GetMapping("/download/{grupo}/{file}")
    @Operation(summary = "Faz download de um arquivo PDF a partir do repositorio remoto",
            description = "Permite que uma arquivo seja acessado a partir do repositório remoto")
    public ResponseEntity<?> downloadPDF(@PathVariable String grupo, @PathVariable String file) {

        byte[] contents = objectStoreService.downloadFile("docs-"+grupo,file);
        if(contents != null){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "result.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
            return response;
        }else{
            return ResponseEntity.notFound().build();
        }

    }

}
