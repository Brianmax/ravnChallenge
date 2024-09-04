package org.ravn.moviescatalogchallenge.service.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.ravn.moviescatalogchallenge.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);
    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String objectName = file.getOriginalFilename();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("movies")
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build());
        } catch (Exception e) {
            String errorMessage = "Error uploading image " + e.getMessage();
            logger.error(errorMessage);
            return null;
        }
        return objectName;
    }
}
