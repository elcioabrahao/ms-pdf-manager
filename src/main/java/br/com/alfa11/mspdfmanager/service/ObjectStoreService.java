package br.com.alfa11.mspdfmanager.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.bc.ObjectStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ObjectStoreService {

    @Autowired
    private MinioClient minioClient;

    public String uploadFile(String bucketName,
                           String objectName,
                           MultipartFile file,
                           String contentType) {

        bucketName = bucketName.toLowerCase(Locale.ROOT);

        log.info("Bucket:"+bucketName+"<--");
        log.info("Nome  :"+objectName+"<--");
        log.info("File  :"+file.getOriginalFilename()+"<--");
        log.info("Tipo  :"+contentType+"<--");

        String response = "";

        try {
            InputStream inputStream =  new BufferedInputStream(file.getInputStream());
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                    inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build());

        StatObjectResponse statObj = minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());

        String fileName = statObj.userMetadata().get("file-name");
        String contentDisposition = URLEncoder.encode("attachment; filename=\"%s\"".formatted(fileName), StandardCharsets.UTF_8);

        response = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .extraQueryParams(Map.of("response-content-disposition", contentDisposition))
                .expiry(1, TimeUnit.HOURS)
                .method(Method.GET)
                .build());

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }

        log.info("Response:"+response);

        return response;
    }

    public byte[] downloadFile(String bucketName, String objectName) {
        byte[] content = null;
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename("uploads/"+objectName)
                            .build());
            File file = new File("uploads/"+objectName);
            content = Files.readAllBytes(file.toPath());
            Files.delete(file.toPath());
            return content;
        } catch (ErrorResponseException e) {
            log.error(e.getLocalizedMessage());
            return content;
        } catch (InsufficientDataException e) {
            log.error(e.getLocalizedMessage());
            return content;
        } catch (InternalException e) {
            log.error(e.getLocalizedMessage());
            return content;
        } catch (InvalidKeyException e) {
            log.error(e.getLocalizedMessage());
            return content;
        } catch (InvalidResponseException e) {
            log.error(e.getLocalizedMessage());
            return content;
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return content;
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getLocalizedMessage());
            return content;
        } catch (ServerException e) {
            log.error(e.getLocalizedMessage());
            return content;
        } catch (XmlParserException e) {
            log.error(e.getLocalizedMessage());
            return content;
        }
    }


}
