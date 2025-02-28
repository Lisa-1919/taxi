package com.modsen.storage_service.controller;

import com.modsen.storage_service.dto.UploadAvatarDto;
import com.modsen.storage_service.service.AvatarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/avatars")
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping
    public ResponseEntity<?> uploadAvatar(@Valid @ModelAttribute UploadAvatarDto uploadAvatarDto){
        avatarService.uploadFile(uploadAvatarDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Avatar uploaded successfully");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID userId) {
        byte[] image = avatarService.getAvatar(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteAvatar(@PathVariable UUID userId) {
        avatarService.deleteAvatar(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> editAvatar(@PathVariable UUID userId, @ModelAttribute UploadAvatarDto uploadAvatarDto) {
        avatarService.editAvatar(uploadAvatarDto);
        return ResponseEntity.ok("Your avatar was successfully edit");
    }
}
