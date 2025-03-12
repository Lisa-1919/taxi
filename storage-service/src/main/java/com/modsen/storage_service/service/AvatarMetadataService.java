package com.modsen.storage_service.service;

import java.util.Optional;
import java.util.UUID;

public interface AvatarMetadataService {


    Optional<String> getAvatarFilename(UUID userId);

    String saveAvatar(UUID userId, String filename);

    void deleteAvatar(UUID userId);

}
