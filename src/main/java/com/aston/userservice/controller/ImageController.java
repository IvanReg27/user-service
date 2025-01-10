package com.aston.userservice.controller;

import com.aston.userservice.domain.response.SimpleMessage;
import com.aston.userservice.service.impl.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/frontend-practice/images")
@RequiredArgsConstructor
@Tag(name = "Images")
public class ImageController {

    private final ImageService imageService;

    @GetMapping
    @Operation(summary = "image-1: download image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success download response",
                            content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE,
                                    schema = @Schema(implementation = Resource.class))),
                    @ApiResponse(responseCode = "500", description = "file not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SimpleMessage.class)))
            }
    )
    public ResponseEntity<Resource> downloadImage() {
        ByteArrayResource resource = imageService.downloadImage();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    @PostMapping
    @Operation(summary = "image-2: upload image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success upload image",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SimpleMessage.class))),

                    @ApiResponse(responseCode = "400", description = "No content",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SimpleMessage.class))),

                    @ApiResponse(responseCode = "415", description = "Unsupported media type",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SimpleMessage.class)))
            }
    )
    public ResponseEntity<SimpleMessage> uploadImage(
            @Parameter(description = "data from 'image' parameter",
                    content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE))
            @RequestParam("image") MultipartFile multipartFile) {
        SimpleMessage simpleMessage = imageService.uploadImage(multipartFile);
        return ResponseEntity.ok(simpleMessage);
    }
}
