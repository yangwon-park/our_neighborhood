package ywphsm.ourneighbor.domain.file;

import java.util.UUID;

public class FileUtil {

    public static String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
    public static String createStoreFileName(String originalFilename) {
        // 서버 저장명 imageUUID.png
        String uuid = UUID.randomUUID().toString();

        // 확장자 뽑아내기
        String ext = extractExt(originalFilename);

        // 최종 저장 명
        return uuid + "." + ext;
    }
}
