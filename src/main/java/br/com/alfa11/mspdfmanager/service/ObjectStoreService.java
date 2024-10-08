package br.com.alfa11.mspdfmanager.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
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

    public String getDoc(String bucketName, String objectName){



        try {
            String destino = "edited_"+objectName;
            log.info("INFOS2: Grupo: "+bucketName);
            log.info("INFOS2: Nome : "+objectName);
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename("uploads/"+destino)
                            .build());
            return destino;
        } catch (ErrorResponseException e) {
            log.error("GetObject1: "+e.getLocalizedMessage());
            return null;
        } catch (InsufficientDataException e) {
            log.error("GetObject2: "+e.getLocalizedMessage());
            return null;
        } catch (InternalException e) {
            log.error("GetObject3: "+e.getLocalizedMessage());
            return null;
        } catch (InvalidKeyException e) {
            log.error("GetObject4: "+e.getLocalizedMessage());
            return null;
        } catch (InvalidResponseException e) {
            log.error("GetObject5: "+e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            log.error("GetObject6: "+e.getLocalizedMessage());
            return null;
        } catch (NoSuchAlgorithmException e) {
            log.error("GetObject7: "+e.getLocalizedMessage());
            return null;
        } catch (ServerException e) {
            log.error("GetObject8: "+e.getLocalizedMessage());
            return null;
        } catch (XmlParserException e) {
            log.error("GetObject9: "+e.getLocalizedMessage());
            return null;
        }
    }

    public String storeFile(String bucketName,
                             String fileName,
                             String contentType,
                            String fileId) {

        bucketName = bucketName.toLowerCase(Locale.ROOT);

        File file = new File (fileName);

        String response = "";

        try {
            InputStream inputStream =  new FileInputStream(file);
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileId).stream(
                                    inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build());

            StatObjectResponse statObj = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileId)
                    .build());

            String name = statObj.userMetadata().get("file-name");
            String contentDisposition = URLEncoder.encode("attachment; filename=\"%s\"".formatted(name), StandardCharsets.UTF_8);

            response = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(fileId)
                    .extraQueryParams(Map.of("response-content-disposition", contentDisposition))
                    .expiry(1, TimeUnit.HOURS)
                    .method(Method.GET)
                    .build());

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }

        return response;
    }

}
