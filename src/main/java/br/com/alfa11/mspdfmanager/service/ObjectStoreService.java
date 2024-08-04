package br.com.alfa11.mspdfmanager.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.bc.ObjectStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Locale;

@Service
@Slf4j
public class ObjectStoreService {

    @Autowired
    private MinioClient minioClient;

    public void uploadFile(String bucketName,
                           String objectName,
                           MultipartFile file,
                           String contentType) {

        bucketName = bucketName.toLowerCase(Locale.ROOT);

        log.info("Bucket:"+bucketName+"<--");
        log.info("Nome  :"+objectName+"<--");
        log.info("File  :"+file.getOriginalFilename()+"<--");
        log.info("Tipo  :"+contentType+"<--");

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
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }


}
