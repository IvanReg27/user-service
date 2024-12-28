package com.aston.userservice.service;

import com.aston.userservice.domain.response.SimpleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class ImageService {

    private static final Path IMAGE_PATH = Path.of("target/classes/static/Aston_logo.png");

    public SimpleMessage uploadImage(MultipartFile multipartFile) {
        if (multipartFile.getContentType() == null) {
            throw new IllegalArgumentException("No content");
        } else if (!MediaType.IMAGE_PNG_VALUE.equals(multipartFile.getContentType())) {
            throw new InvalidMediaTypeException(multipartFile.getContentType(),
                                                "Not comparable media type");
        }

        return new SimpleMessage(
                "File uploaded successfully: " + multipartFile.getOriginalFilename());
    }

    public ByteArrayResource downloadImage() {
        byte[] bytes;

        try {
            bytes = Files.readAllBytes(IMAGE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayResource(bytes);
    }
}
