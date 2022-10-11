package ywphsm.ourneighbor.domain.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3FileStore {

    private final AmazonS3Client amazonS3Client;

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
        File file;
        if (originalFileName.equals("default.png")) {

            // 기본이미지 사용 시, 별도로 파일을 업로드해서 만들지 않음
            // 저장 파일 자체를 미리 로컬에 만들어뒀음
            storeFileName = "default.png";
        } else {
            file = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환이 실패했습니다."));

            // ex) UUID.png
            // 서버 저장 파일명
            storeFileName = createStoreFileName(originalFileName);

            removeNewFile(file);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getInputStream().available());

//        amazonS3Client.putObject(bucketName, storeFileName, multipartFile.getInputStream(), objectMetadata);
            amazonS3Client.putObject(new PutObjectRequest(bucketName, storeFileName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }

        return new UploadFile(originalFileName, storeFileName);
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제됐습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());

        if (convertFile.createNewFile()) {
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
        return dir + "/" + uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
