package org.ravn.moviescatalogchallenge.controller;

import org.ravn.moviescatalogchallenge.service.MinioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/minio")
public class MinioController {

    private final MinioService minioService;

    public MinioController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        return minioService.uploadImage(file);
    }

    @GetMapping("/presigned-url")
    public String getPresignedUrl(@RequestParam String objectName) {
        return minioService.getPresignedUrl(objectName);
    }
}
