package com.modsen.storage_service.service;

import com.modsen.exception_handler.exception.InvalidFileFormatException;
import com.modsen.storage_service.dto.ResponseDto;
import com.modsen.storage_service.dto.UploadAvatarDto;
import com.modsen.storage_service.util.ExceptionMessages;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final MinioClient minioClient;
    private final AvatarMetadataService metadataService;

    private static final String BUCKET_NAME = "avatars";
    private static final List<String> ALLOWED_FORMATS = List.of("image/jpeg", "image/png", "image/webp");

    @Override
    public ResponseDto uploadFile(UploadAvatarDto uploadAvatarDto) {
        try {

            ensureBucketExists();

            String originalFilename = uploadAvatarDto.file().getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            validateFile(uploadAvatarDto.file(), extension);


            String objectName = UUID.randomUUID() + extension;

            InputStream fileStream = uploadAvatarDto.file().getInputStream();
            String contentType = uploadAvatarDto.file().getContentType();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .stream(fileStream, uploadAvatarDto.file().getSize(), -1)
                            .contentType(contentType)
                            .build()
            );

            metadataService.saveAvatar(uploadAvatarDto.userId(), objectName);
            return new ResponseDto(objectName);
        } catch (Exception e) {
            throw new RuntimeException(ExceptionMessages.UNKNOWN_ERROR.format(), e);
        }
    }

    @Override
    public byte[] getAvatar(UUID userId) {
        String objectName = metadataService.getAvatarFilename(userId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.AVATAR_NOT_FOUND.format()));

        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(objectName)
                        .build())) {

            return IOUtils.toByteArray(response);

        } catch (Exception e) {
            throw new EntityNotFoundException(ExceptionMessages.AVATAR_NOT_FOUND.format());
        }
    }

    @Override
    public void deleteAvatar(UUID userId) {
        String objectName = metadataService.getAvatarFilename(userId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.AVATAR_NOT_FOUND.format()));

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .build()
            );

            metadataService.deleteAvatar(userId);
        } catch (Exception e) {
            throw new RuntimeException(ExceptionMessages.UNKNOWN_ERROR.format(), e);
        }
    }

    @Override
    public ResponseDto editAvatar(UploadAvatarDto uploadAvatarDto) {
        deleteAvatar(uploadAvatarDto.userId());
        return uploadFile(uploadAvatarDto);
    }

    private void validateFile(MultipartFile file, String extension) {
        if (!ALLOWED_FORMATS.contains(file.getContentType()) || extension == null || extension.isEmpty()) {
            throw new InvalidFileFormatException(ExceptionMessages.INVALID_FILE_FORMAT.format(ALLOWED_FORMATS));
        }
    }

    private void ensureBucketExists() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(BUCKET_NAME).build()
        );

        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        }
    }

    private String getFileExtension(String filename) {
        return filename != null && filename.contains(".")
                ? filename.substring(filename.lastIndexOf("."))
                : "";
    }
}
