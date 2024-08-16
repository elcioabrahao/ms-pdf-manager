package br.com.alfa11.mspdfmanager.controller;

import br.com.alfa11.mspdfmanager.service.ObjectStoreService;
import br.com.alfa11.mspdfmanager.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {

    @Autowired
    ObjectStoreService objectStoreService;

    @Autowired
    PdfService pdfService;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    @GetMapping("/uploadpdf")
    public String displayUploadForm() {
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
//        pdfService.searchFields(fileNameAndPath);
        return "index";
    }
}

