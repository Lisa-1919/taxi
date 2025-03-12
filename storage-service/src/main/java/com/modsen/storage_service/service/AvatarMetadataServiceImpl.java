package com.modsen.storage_service.service;

import com.modsen.storage_service.entity.Avatar;
import com.modsen.storage_service.repo.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarMetadataServiceImpl implements AvatarMetadataService{

    private final AvatarRepository avatarRepository;

    @Override
    @Cacheable(value = "avatarCache", key = "#userId")
    public Optional<String> getAvatarFilename(UUID userId) {
        return avatarRepository.findById(userId).map(Avatar::getFilename);
    }

    @Override
    @CachePut(value = "avatarCache", key = "#userId")
    public String saveAvatar(UUID userId, String filename) {
        avatarRepository.save(new Avatar(userId, filename));
        return filename;
    }

    @Override
    @CacheEvict(value = "avatarCache", key = "#userId")
    public void deleteAvatar(UUID userId) {
        avatarRepository.deleteById(userId);
    }

}
