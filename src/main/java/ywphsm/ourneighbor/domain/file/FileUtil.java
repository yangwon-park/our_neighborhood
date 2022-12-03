package ywphsm.ourneighbor.domain.file;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class FileUtil {

    public static String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public static String createStoreFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();         // 서버 저장명 imageUUID.png
        String ext = extractExt(originalFilename);          // 확장자 뽑아내기

        return uuid + "." + ext;                            // 최종 저장 명
    }

    /*
        이미지 리사이징 메소드
     */
    public static MultipartFile getResizedMultipartFile(MultipartFile multipartFile, String originalFileName) throws IOException {
        log.info("리사이징 동작 = {}", originalFileName);
        final int TARGET_IMAGE_WIDTH = 450;
        final int TARGET_IMAGE_HEIGHT = 450;
        final String IMAGE_TYPE = "png";

        BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
        bi = resizeImages(bi, TARGET_IMAGE_WIDTH, TARGET_IMAGE_HEIGHT);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean check = ImageIO.write(bi, IMAGE_TYPE, baos);

        log.info("check={}",check);

        baos.flush();

        return new CustomMultipartFile(baos.toByteArray(), multipartFile.getName(), originalFileName, "image/*", false, baos.toByteArray().length);
    }

    private static BufferedImage resizeImages(BufferedImage originalImage, int width, int height) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, width, height);
    }
}
