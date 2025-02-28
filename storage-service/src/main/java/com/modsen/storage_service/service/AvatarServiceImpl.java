package com.modsen.storage_service.service;

import com.modsen.storage_service.dto.UploadAvatarDto;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final MinioClient minioClient;

    private static final String BUCKET_NAME = "avatars";

    @Override
    public void uploadFile(UploadAvatarDto uploadAvatarDto) {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(BUCKET_NAME).build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }

            String objectName = uploadAvatarDto.userId() + ".jpg";

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

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while uploading: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] getAvatar(UUID userId) {
        String objectName = userId + ".jpg";

        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(objectName)
                        .build())) {
            return IOUtils.toByteArray(response);

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving file", e);
        }
    }

    @Override
    public void deleteAvatar(UUID userId) {
        String objectName = userId + ".jpg";
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file", e);
        }
    }

    @Override
    public void editAvatar(UploadAvatarDto uploadAvatarDto) {
        deleteAvatar(uploadAvatarDto.userId());
        uploadFile(uploadAvatarDto);
    }
}
