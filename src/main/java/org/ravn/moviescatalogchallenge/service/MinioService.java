package org.ravn.moviescatalogchallenge.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    String uploadImage(MultipartFile file);
}
