package br.com.alfa11.mspdfmanager.service;

import br.com.alfa11.mspdfmanager.model.FileMetadata;
import br.com.alfa11.mspdfmanager.util.DataUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import com.itextpdf.text.Document;

@Service
@Slf4j
public class PdfService {


    public FileMetadata getFileMetadata(MultipartFile file, String grupo, String url){


        return FileMetadata.builder()
                .dataExtracao(DataUtil.getAgoraCompleta())
                .nomePdf(file.getOriginalFilename())
                .grupo(grupo)
                .url(url)
                .build();
    }

    public byte[] mergeUsingPDFBox(List<String> pdfFiles, String outputFile) {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName("/uploads/" + outputFile);
        log.info("Arquivo: " + outputFile);

        pdfFiles.forEach(file -> {
            try {
                pdfMergerUtility.addSource(new File("/uploads/" + file));
                log.info("Arquivo: " + file);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
        });

        File file = new File("/uploads/" + outputFile);
        byte[] content;
        try {
            pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupTempFileOnly().streamCache);
            content = Files.readAllBytes(file.toPath());
            return content;
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    public static void createPDFDoc(String content, String filePath) throws IOException {
        PDDocument document = new PDDocument();
        for (int i = 0; i < 3; i++) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.showText(content + ", page:" + i);
                contentStream.endText();
            }
        }
        document.save("uploads/" + filePath);
        document.close();
    }

    public byte[] mergeUsingIText(List<String> pdfFiles, String outputFile) {
        outputFile = "uploads/" + outputFile;
        List<PdfReader> pdfReaders = null;
        Document document = new Document();
        FileOutputStream fos = null;
        PdfWriter writer = null;
        PdfReader pdfReader = null;
        try {
            fos = new FileOutputStream(outputFile);
            writer = PdfWriter.getInstance(document, fos);
            document.open();
            PdfContentByte directContent = writer.getDirectContent();
            PdfImportedPage pdfImportedPage;
            for (String fileName : pdfFiles) {
                pdfReader = new PdfReader("uploads/"+fileName);
                int currentPdfReaderPage = 1;
                while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pdfImportedPage = writer.getImportedPage(pdfReader, currentPdfReaderPage);
                    directContent.addTemplate(pdfImportedPage, 0, 0);
                    currentPdfReaderPage++;
                }
            }
            fos.flush();
            document.close();
            fos.close();
            File file = new File(outputFile);
            byte[] content;
            content = Files.readAllBytes(file.toPath());
            Files.delete(file.toPath());
            return content;
        } catch (DocumentException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
