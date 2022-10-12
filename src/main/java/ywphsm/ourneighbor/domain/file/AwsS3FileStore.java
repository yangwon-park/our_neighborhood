package ywphsm.ourneighbor.domain.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3FileStore {

    private final AmazonS3Client amazonS3Client;

    private final Environment environment;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${cloud.aws.s3.dir}")
    private String dir;

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName;
        String imageUrl;

        if (originalFileName.equals("default.png")) {
            storeFileName = "defaultImg.png";
            imageUrl = "https://neighbor-build.s3.ap-northeast-2.amazonaws.com/images/defaultImg.png";
        } else {
            File uploadFile = convert(multipartFile).orElseThrow(
                    () -> new IllegalArgumentException("전환 실패"));

            imageUrl = getImageUrl(uploadFile);

            storeFileName = createStoreFileName(originalFileName);
        }

        return new UploadFile(originalFileName, storeFileName, imageUrl);
    }

    private String getImageUrl(File uploadFile) {
        String fileName = dir + "/" + uploadFile.getName();
        String uploadImageUrl = storeFileToS3(uploadFile, fileName);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private String storeFileToS3(File uploadFile, String fileName) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead);

        String name = putObjectRequest.getFile().getName();

        amazonS3Client.putObject(putObjectRequest);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제됐습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        String[] activeProfiles = environment.getActiveProfiles();
        List<String> profiles = Arrays.stream(activeProfiles).collect(Collectors.toList());

        String localProfile = "local";
        String testProfile = "test";

        boolean localCheck = profiles.contains(localProfile);
        boolean testCheck = profiles.contains(testProfile);

        File convertFile;

        if (!localCheck && !testCheck) {
            convertFile = new File("/tmp/" + file.getOriginalFilename());
        } else {
            convertFile = new File(file.getOriginalFilename());
        }

        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    private String createStoreFileName(String originalFilename) {
        // 서버 저장명 imageUUID.png
        String uuid = UUID.randomUUID().toString();

        // 확장자 뽑아내기
        String ext = extractExt(originalFilename);

        // 최종 저장 명
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
