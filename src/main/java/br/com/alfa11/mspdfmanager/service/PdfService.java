package br.com.alfa11.mspdfmanager.service;

import br.com.alfa11.mspdfmanager.model.*;
import br.com.alfa11.mspdfmanager.util.DataUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.Document;

@Service
@Slf4j
public class PdfService {

    @Autowired
    ObjectStoreService objectStoreService;

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
        pdfMergerUtility.setDestinationFileName(outputFile);
        log.info("Arquivo: " + outputFile);

        pdfFiles.forEach(file -> {
            try {
                pdfMergerUtility.addSource(new File(file));
                log.info("Arquivo: " + file);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
        });

        File file = new File(outputFile);
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
                log.info("Mergaando: "+fileName);
                pdfReader = new PdfReader(fileName);
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
            log.error("Erro no mergeUsingItext:"+e.getMessage());
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            log.error("Erro no mergeUsingItext:"+e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Erro no mergeUsingItext:"+e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public byte[] manipulatePdf(String bucketName, String src, String dest)  {

        String doc = objectStoreService.getDoc(bucketName,src);
        if(doc==null){
            return null;
        }
        src = "uploads/"+doc;
        dest = "uploads/"+dest;
        byte[] content = null;


        try {
            PdfReader reader = new PdfReader(src);
            Rectangle pagesize = reader.getPageSize(1);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
//            AcroFields form = stamper.getAcroFields();
//            form.setField("Name", "Jennifer");
//            form.setField("Company", "iText's next customer");
//            form.setField("Country", "No Man's Land");
            PdfPTable table = new PdfPTable(2);
            table.addCell("#");
            table.addCell("description");
            table.setHeaderRows(1);
            table.setWidths(new int[]{1, 15});
            for (int i = 1; i <= 150; i++) {
                table.addCell(String.valueOf(i));
                table.addCell("test " + i);
            }
            ColumnText column = new ColumnText(stamper.getOverContent(1));
            Rectangle rectPage1 = new Rectangle(36, 36, 559, 540);
            column.setSimpleColumn(rectPage1);
            column.addElement(table);
            int pagecount = 1;
            Rectangle rectPage2 = new Rectangle(36, 36, 559, 806);
            int status = column.go();
            while (ColumnText.hasMoreText(status)) {
                status = triggerNewPage(stamper, pagesize, column, rectPage2, ++pagecount);
            }
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            File file = new File(dest);
            File file2 = new File(src);
            content = Files.readAllBytes(file.toPath());
            Files.delete(file.toPath());
            Files.delete(file2.toPath());
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

    public int triggerNewPage(PdfStamper stamper, Rectangle pagesize, ColumnText column, Rectangle rect, int pagecount) throws DocumentException {
        stamper.insertPage(pagecount, pagesize);
        PdfContentByte canvas = stamper.getOverContent(pagecount);
        column.setCanvas(canvas);
        column.setSimpleColumn(rect);
        return column.go();
    }


    public byte[] mergeFiles(ComposedDocument composedDocument){
        log.info("ComposedDocument: "+composedDocument.toString());
        byte[] content = null;
        String doc = "";
        String src = "";
        String dest = "";
        List<String> filesToMerge = new ArrayList<>();
        try{
            for(FileDescription fd: composedDocument.getFiles()){
                doc = objectStoreService.getDoc("docs-"+fd.getGrupo(),fd.getFileName());
                if(doc==null){
                    return null;
                }
                src = "uploads/"+doc;
                dest = "uploads/to_merge_"+doc;
                log.info("Origem : "+src);
                log.info("Destino: "+dest);
                PdfReader reader = new PdfReader(src);
                Rectangle pagesize = reader.getPageSize(fd.getPageNumber());
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
                for(FileData fdd: fd.getFileData()){
                    log.info("Tem Dados!");
                    PdfPTable table = new PdfPTable(2);
                    table.setWidths(new int[]{fdd.getKeySize(), fdd.getValueSize()});
                    for (TableRow row: fdd.getRows()) {
                        log.info("Tem row!");
                        PdfPCell pdfPCellKey = new PdfPCell(new Phrase(row.getKeyCell()));
                        pdfPCellKey.setBorder(0);
                        table.addCell(pdfPCellKey);
                        PdfPCell pdfPCellValue = new PdfPCell(new Phrase(row.getValueCell()));
                        pdfPCellValue.setBorder(0);
                        table.addCell(pdfPCellValue);
//                        table.addCell(row.getKeyCell());
//                        table.addCell(row.getValueCell());
                    }
                    ColumnText column = new ColumnText(stamper.getOverContent(fd.getPageNumber()));
                    Rectangle rectPage1 = new Rectangle(fdd.getIix(), fdd.getIiy(), fdd.getUrx(), fdd.getUry());
                    column.setSimpleColumn(rectPage1);
                    column.addElement(table);
                    column.go();

                }
                stamper.setFormFlattening(true);
                stamper.close();
                reader.close();
                filesToMerge.add(dest);
                File ff =  new File(src);
                Files.delete(ff.toPath());

            }
            content = mergeUsingIText(filesToMerge, "result.pdf");
            for(String fileName: filesToMerge){
                File f =  new File(fileName);
                Files.delete(f.toPath());
            }

        } catch (Exception e){
            log.error("Erro no mergeFiles: "+e.getMessage());
        }
        return content;
    }

}
