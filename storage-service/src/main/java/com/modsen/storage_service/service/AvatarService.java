package com.modsen.storage_service.service;

import com.modsen.storage_service.dto.UploadAvatarDto;

import java.util.UUID;

public interface AvatarService {

    void uploadFile (UploadAvatarDto uploadAvatarDto);
    byte[] getAvatar(UUID userId);
    void deleteAvatar(UUID userId);
    void editAvatar(UploadAvatarDto uploadAvatarDto);

}
