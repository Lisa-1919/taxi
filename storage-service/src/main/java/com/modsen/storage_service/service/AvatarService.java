package com.modsen.storage_service.service;

import com.modsen.storage_service.dto.ResponseDto;
import com.modsen.storage_service.dto.UploadAvatarDto;

import java.util.UUID;

public interface AvatarService {

    ResponseDto uploadFile (UploadAvatarDto uploadAvatarDto);
    byte[] getAvatar(UUID userId);
    void deleteAvatar(UUID userId);
    ResponseDto editAvatar(UploadAvatarDto uploadAvatarDto);

}
