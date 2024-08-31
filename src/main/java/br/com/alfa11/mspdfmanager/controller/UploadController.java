package br.com.alfa11.mspdfmanager.controller;

import br.com.alfa11.mspdfmanager.model.ComposedDocument;
import br.com.alfa11.mspdfmanager.model.FileData;
import br.com.alfa11.mspdfmanager.model.FileDescription;
import br.com.alfa11.mspdfmanager.service.ObjectStoreService;
import br.com.alfa11.mspdfmanager.service.PdfService;
import br.com.alfa11.mspdfmanager.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UploadController {

    @Autowired
    ObjectStoreService objectStoreService;

    @Autowired
    PdfService pdfService;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    @GetMapping("/v1/pdf")
    public String displayIndexForm() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadImage(Model model, @RequestParam("pdf") MultipartFile file, @RequestParam("grupo") String grupo) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        fileNames.append(file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
        model.addAttribute("msg", "Uploaded pdfs: " + fileNames.toString());
        objectStoreService.uploadFile("docs-"+grupo,file.getOriginalFilename(),file,"application/octet-stream");
        return "index";
    }

    @GetMapping("/v1/upload")
    public String displayUploadForm() {
        return "upload";
    }

    @PostMapping("/download")
    public ResponseEntity<?> downloadImage(Model model, @RequestParam("valor") String valor, @RequestParam("arquivo") String arquivo) throws IOException {
        byte[] contents = null;
        try {
            List<String> listaTexto = new ArrayList<>();
            listaTexto.add(valor);
            FileData fileData = FileData.builder()
                    .dataId("proposta")
                    .iix(265)
                    .iiy(240)
                    .urx(80)
                    .ury(80)
                    .stroke(false)
                    .paragraphs(listaTexto)
                    .build();
            List<FileData> listaFileData = new ArrayList<>();
            listaFileData.add(fileData);
            FileDescription fileDescription = FileDescription.builder()
                    .fileData(listaFileData)
                    .fileName(arquivo)
                    .grupo("proposta")
                    .pageNumber(6)
                    .build();
            List<FileDescription> listaFileDescription = new ArrayList<>();
            listaFileDescription.add(fileDescription);
            ComposedDocument composedDocument = ComposedDocument.builder()
                    .composedDocumentId(DataUtil.getAgoraCompleta())
                    .grupo("proposta")
                    .files(listaFileDescription)
                    .build();
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
}

