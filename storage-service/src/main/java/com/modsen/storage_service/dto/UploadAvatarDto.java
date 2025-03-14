package com.modsen.storage_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UploadAvatarDto(
        MultipartFile file,
        UUID userId
) {}
