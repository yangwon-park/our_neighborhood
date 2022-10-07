package ywphsm.ourneighbor.domain.file;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 파일 저장 로직 처리만을 담당하는 스프링 빈
@Slf4j
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    // 여러 이미지 업로드
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {

        // 생성될 때 마다 새로운 리스트를 생성해줘야 함
        List<UploadFile> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }

        return storeFileResult;
    }

    // 이미지 하나 업로드
    // form에서 받은 multipartFile을 우리가 만든 UploadFile 타입으로 변환하여 저장함
    // 이를 controller에서 item 엔티티(엔티티에서 파일 타입은 UploadFile 타입)으로 변환하여 넘겨주고 DB에 저장
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName;

        if (originalFileName.equals("default.png")) {
            
            // 기본이미지 사용 시, 별도로 파일을 업로드해서 만들지 않음
            // 저장 파일 자체를 미리 로컬에 만들어뒀음
            storeFileName = "default.png";
        } else {

            // ex) UUID.png
            // 서버 저장 파일명
            storeFileName = createStoreFileName(originalFileName);

            // 경로 + 서버 저장 파일명을 가지고 File을 생성
            // transferTo => 업로드한 파일 데이터를 new File(getFullPath(storeFileName)로 저장
            multipartFile.transferTo(new File(getFullPath(storeFileName)));
        }

        return new UploadFile(originalFileName, storeFileName);
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
