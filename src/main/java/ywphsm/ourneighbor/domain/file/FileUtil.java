package ywphsm.ourneighbor.domain.file;

import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

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
        final int TARGET_IMAGE_WIDTH = 420;
        final int TARGET_IMAGE_HEIGHT = 200;
        final String TARGET_IMAGE_TYPE = "jpg";

        BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
        bi = resizeImages(bi, TARGET_IMAGE_WIDTH, TARGET_IMAGE_HEIGHT);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, TARGET_IMAGE_TYPE, baos);
        baos.flush();

        return new CustomMultipartFile(baos.toByteArray(), multipartFile.getName(), originalFileName, TARGET_IMAGE_TYPE, false, baos.toByteArray().length);
    }

    private static BufferedImage resizeImages(BufferedImage originalImage, int width, int height) {
        return Scalr.resize(originalImage, width, height);
    }
}
