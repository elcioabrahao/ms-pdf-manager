package br.com.alfa11.mspdfmanager.service;


import br.com.alfa11.mspdfmanager.model.FieldMetadata;
import br.com.alfa11.mspdfmanager.model.FileMetadata;
import br.com.alfa11.mspdfmanager.util.DataUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class PdfService {

//    public  void scanFields(String path) throws IOException {
//        PdfReader pdfReader = new PdfReader(path);
//        AcroFields acroFields = pdfReader.getAcroFields();
//        HashMap<String,AcroFields.Item> fields = acroFields.getFields();
//        Set<Map.Entry<String, Item>> entrySet = fields.entrySet();
//        for (Map.Entry<String, Item> entry : entrySet) {
//            String key = entry.getKey();
//        }
//    }

    public void searchFields(Path path) {

        PdfReader reader = null;
        try {
            reader = new PdfReader(path.toFile());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        PdfDocument pdfDoc = new PdfDocument(reader);
// Get the fields from the reader (read-only!!!)
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
// Loop over the fields and get info about them
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
}
