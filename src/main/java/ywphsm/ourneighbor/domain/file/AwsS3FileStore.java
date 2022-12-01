package ywphsm.ourneighbor.domain.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.file.FileUtil.*;

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

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();           // 생성될 때 마다 새로운 리스트를 생성해줘야 함

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(getResizedMultipartFile(multipartFile, multipartFile.getOriginalFilename())));
            }
        }

        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        final String defaultImgUrl = "https://neighbor-build.s3.ap-northeast-2.amazonaws.com/images/defaultImg.png";
        final String defaultImgName = "defaultImg.png";

        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName;
        String imageUrl;

        if (originalFileName.equals(defaultImgName)) {
            storeFileName = defaultImgName;
            imageUrl = defaultImgUrl;
        } else {
            storeFileName = createStoreFileName(originalFileName);

            File uploadFile = convert(multipartFile).orElseThrow(
                    () -> new IllegalArgumentException("전환 실패"));

            imageUrl = getImageUrl(uploadFile, storeFileName);
        }

        return new UploadFile(originalFileName, storeFileName, imageUrl);
    }

    public void deleteFile(String storedFileName) {
        try {
            deleteFileInS3(storedFileName);
            log.info("s3 파일 삭제를 완료했습니다. S3 저장명={}", storedFileName);
        } catch (Exception e) {
            log.info("s3 파일 삭제에 실패했습니다.");
        }
    }

    /*
        MultipartFile => File
     */
    private Optional<File> convert(MultipartFile multipartFile) throws IOException {
        List<String> profiles = Arrays.stream(environment.getActiveProfiles()).
                collect(Collectors.toList());

        String localProfile = "local";
        String testProfile = "test";

        boolean localCheck = profiles.contains(localProfile);
        boolean testCheck = profiles.contains(testProfile);

        File convertFile;

        if (!localCheck && !testCheck) {
            convertFile = new File("/tmp/" + multipartFile.getOriginalFilename());
        } else {
            convertFile = new File(multipartFile.getOriginalFilename());
        }

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    private String getImageUrl(File uploadFile, String storeFileName) {
        String fileName = dir + "/" + storeFileName;
        String uploadImageUrl = storeFileToS3(uploadFile, fileName);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제에 성공했습니다.");
        } else {
            log.info("파일이 삭제에 실패했습니다.");
        }
    }

    private String storeFileToS3(File uploadFile, String fileName) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3Client.putObject(putObjectRequest);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    private void deleteFileInS3(String storedFileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, dir + "/" + storedFileName));
    }
}