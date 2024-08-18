package br.com.alfa11.mspdfmanager.service;

import br.com.alfa11.mspdfmanager.exception.StoredFileNotFoundException;
import br.com.alfa11.mspdfmanager.model.*;
import br.com.alfa11.mspdfmanager.util.DataUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
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

import java.awt.print.PageFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.itextpdf.io.font.constants.StandardFonts.TIMES_BOLD;
import static com.itextpdf.io.font.constants.StandardFonts.TIMES_ROMAN;

@Service
@Slf4j
public class PdfService {

    @Autowired
    ObjectStoreService objectStoreService;

    public FileMetadata getFileMetadata(MultipartFile file, String grupo, String url) {


        return FileMetadata.builder()
                .dataExtracao(DataUtil.getAgoraCompleta())
                .nomePdf(file.getOriginalFilename())
                .grupo(grupo)
                .url(url)
                .build();
    }


    protected byte[] mergePdfs(List<String> filesToMerge) throws IOException {

        String dest = "uploads/merged_result.pdf";
        File file = new File(dest);
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        PdfDocument srcDoc = null;
        int numberOfPages = 0;
        PdfMerger merger = new PdfMerger(pdfDoc);
        byte[] content;

        for (String f : filesToMerge) {
            srcDoc = new PdfDocument(new PdfReader(f));
            numberOfPages = srcDoc.getNumberOfPages();
            merger.setCloseSourceDocuments(true)
                    .merge(srcDoc, 1, numberOfPages);
        }

        pdfDoc.close();

        content = Files.readAllBytes(file.toPath());
        Files.delete(file.toPath());
        for (String fileName : filesToMerge) {
            File f = new File(fileName);
            Files.delete(f.toPath());
        }
        return content;
    }

    protected FileMetadata mergeAndStorePdfs(List<String> filesToMerge,
                                             String composedDocumentId,
                                             String grupo) throws IOException {

        String dest = "uploads/merged_result.pdf";
        File file = new File(dest);
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        PdfDocument srcDoc = null;
        int numberOfPages = 0;
        PdfMerger merger = new PdfMerger(pdfDoc);


        for (String f : filesToMerge) {
            srcDoc = new PdfDocument(new PdfReader(f));
            numberOfPages = srcDoc.getNumberOfPages();
            merger.setCloseSourceDocuments(true)
                    .merge(srcDoc, 1, numberOfPages);
        }

        pdfDoc.close();

        String dataHora = DataUtil.getSearchableDate();

        String response = objectStoreService.storeFile("docs-"+grupo,
                dest,"application/octet-stream",dataHora+"_"+composedDocumentId+".pdf");


        Files.delete(file.toPath());
        for (String fileName : filesToMerge) {
            File f = new File(fileName);
            Files.delete(f.toPath());
        }
        return FileMetadata.builder()
                .dataExtracao(dataHora)
                .nomePdf(dataHora+"_"+composedDocumentId+".pdf")
                .grupo(grupo)
                .url(response)
                .build();
    }

    public byte[] stampContent(ComposedDocument composedDocument) throws IOException, StoredFileNotFoundException {
        byte[] content = null;
        String doc = "";
        String src = "";
        String dest = "";
        List<String> filesToMerge = new ArrayList<>();

        for (FileDescription fd : composedDocument.getFiles()) {
            doc = objectStoreService.getDoc("docs-" + fd.getGrupo(), fd.getFileName());
            if (doc == null) {
                String error = "Arquivo não localizado no Bucket: " + fd.getGrupo() + " Nome: " + fd.getFileName();
                log.error(error);
                throw new StoredFileNotFoundException(error);
            }
            src = "uploads/" + doc;
            dest = "uploads/to_merge_" + doc;

            PdfDocument pdf = new PdfDocument(new PdfReader(src),new PdfWriter(dest));
            PdfPage page = pdf.getPage(fd.getPageNumber());

            for(FileData fdd: fd.getFileData()){
                PdfCanvas pdfCanvas = new PdfCanvas(page);
                Rectangle rectangle = new Rectangle(fdd.getIix(), fdd.getIiy(), fdd.getUrx(), fdd.getUry());
                pdfCanvas.rectangle(rectangle);
                if(fdd.isStroke()){
                    pdfCanvas.stroke();
                }
                Canvas canvas = new Canvas(pdfCanvas, rectangle);
                PdfFont font = PdfFontFactory.createFont(TIMES_ROMAN);
                for(String pp: fdd.getParagraphs()){
                    Text paragrafo = new Text(pp).setFont(font);
                    Paragraph p = new Paragraph().add(paragrafo);
                    canvas.add(p);
                    canvas.close();
                }
            }
            pdf.close();

            filesToMerge.add(dest);
            File ff = new File(src);
            Files.delete(ff.toPath());

        }
        content = mergePdfs(filesToMerge);

        return content;
    }

    public FileMetadata stampAndStoreContent(ComposedDocument composedDocument) throws IOException, StoredFileNotFoundException {
        FileMetadata content = null;
        String doc = "";
        String src = "";
        String dest = "";
        List<String> filesToMerge = new ArrayList<>();

        for (FileDescription fd : composedDocument.getFiles()) {
            doc = objectStoreService.getDoc("docs-" + fd.getGrupo(), fd.getFileName());
            if (doc == null) {
                String error = "Arquivo não localizado no Bucket: " + fd.getGrupo() + " Nome: " + fd.getFileName();
                log.error(error);
                throw new StoredFileNotFoundException(error);
            }
            src = "uploads/" + doc;
            dest = "uploads/to_merge_" + doc;

            PdfDocument pdf = new PdfDocument(new PdfReader(src),new PdfWriter(dest));
            PdfPage page = pdf.getPage(fd.getPageNumber());

            for(FileData fdd: fd.getFileData()){
                PdfCanvas pdfCanvas = new PdfCanvas(page);
                Rectangle rectangle = new Rectangle(fdd.getIix(), fdd.getIiy(), fdd.getUrx(), fdd.getUry());
                pdfCanvas.rectangle(rectangle);
                if(fdd.isStroke()){
                    pdfCanvas.stroke();
                }
                Canvas canvas = new Canvas(pdfCanvas, rectangle);
                PdfFont font = PdfFontFactory.createFont(TIMES_ROMAN);
                for(String pp: fdd.getParagraphs()){
                    Text paragrafo = new Text(pp).setFont(font);
                    Paragraph p = new Paragraph().add(paragrafo);
                    canvas.add(p);
                    canvas.close();
                }
            }
            pdf.close();

            filesToMerge.add(dest);
            File ff = new File(src);
            Files.delete(ff.toPath());

        }
        content = mergeAndStorePdfs(filesToMerge,composedDocument.getComposedDocumentId(), composedDocument.getGrupo());

        return content;
    }


}