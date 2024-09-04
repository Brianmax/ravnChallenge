package org.ravn.moviescatalogchallenge.service.impl;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.ravn.moviescatalogchallenge.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);
    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String objectName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("movies")
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            String errorMessage = "Error uploading image " + e.getMessage();
            logger.error(errorMessage);
            return null;
        }
        return objectName;
    }

    @Override
    public String getPresignedUrl(String objectName) {
        Map<String, String> reqParams = Map.of("response-content-type", "application/json");
        new GetPresignedObjectUrlArgs();
        String errorMessage = "";
        if (objectName == null) {
            errorMessage = "The movie does not have an image";
            logger.error(errorMessage);
            return errorMessage;
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs
                            .builder()
                            .method(Method.GET)
                            .bucket("movies")
                            .object(objectName)
                            .expiry(2, TimeUnit.HOURS)
                            .extraQueryParams(reqParams)
                            .build());
        } catch (Exception e) {
            errorMessage = "Error getting presigned url " + e.getMessage();
            logger.error(errorMessage);
            return errorMessage;
        }
    }
}
