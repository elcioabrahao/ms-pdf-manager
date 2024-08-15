package br.com.alfa11.mspdfmanager.service;


import br.com.alfa11.mspdfmanager.model.FieldMetadata;
import br.com.alfa11.mspdfmanager.model.FileMetadata;
import br.com.alfa11.mspdfmanager.util.DataUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class PdfService {

    public void searchFields(Path path) {

        PdfReader reader = null;
        try {
            reader = new PdfReader(path.toFile());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getAllFormFields();

        for (Map.Entry<String, PdfFormField> entry : fields.entrySet()) {
            PdfFormField field = entry.getValue();
            if (field instanceof PdfButtonFormField) {
                log.info("Botão: "+entry.getKey());
            } else if (field instanceof PdfTextFormField) {
                log.info("Texto : "+entry.getKey());
                log.info("Valor : "+field.getValueAsString());
            }else{
                log.info("Field: "+entry.getKey());
            }
        }

    }

    public FileMetadata getFileMetadata(MultipartFile file, String grupo, String url){

        List<FieldMetadata> fieldMetadataList = new ArrayList<>();
        FieldMetadata fieldMetadata = null;
        try {
            PdfReader pdfReader = new PdfReader(file.getInputStream());
            PdfDocument pdfDoc = new PdfDocument(pdfReader);
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            Map<String, PdfFormField> fields = form.getAllFormFields();
            for (Map.Entry<String, PdfFormField> entry : fields.entrySet()) {
                PdfFormField field = entry.getValue();
                if (field instanceof PdfTextFormField) {
                    fieldMetadata = FieldMetadata.builder()
                            .nomeCampo(entry.getKey())
                            .tipoCampo("text")
                            .valorCampo(field.getValueAsString())
                            .build();
                    fieldMetadataList.add(fieldMetadata);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return FileMetadata.builder()
                .campos(fieldMetadataList)
                .dataExtracao(DataUtil.getAgoraCompleta())
                .nomePdf(file.getOriginalFilename())
                .grupo(grupo)
                .url(url)
                .build();
    }

    public byte[] mergeUsingPDFBox(List<String> pdfFiles, String outputFile)  {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName("/uploads/"+outputFile);
        log.info("Arquivo: "+outputFile);

        pdfFiles.forEach(file -> {
            try {
                pdfMergerUtility.addSource(new File("/uploads/"+file));
                log.info("Arquivo: "+file);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
        });

        File file = new File("/uploads/"+outputFile);
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
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),14);
                contentStream.showText(content + ", page:" + i);
                contentStream.endText();
            }
        }
        document.save("uploads/" + filePath);
        document.close();
    }
}
