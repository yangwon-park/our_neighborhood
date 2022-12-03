package ywphsm.ourneighbor.domain.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/*
    MultipartFile을 생성하기 위해 만든
    MultipartFile 인터페이스의 구현체
 */
public class CustomMultipartFile implements MultipartFile {

    private final byte[] bytes;
    private final String name;
    private final String originalFilename;
    private String contentType;
    private final boolean isEmpty;
    private final long size;

    public CustomMultipartFile(byte[] bytes, String name, String originalFilename, String contentType, boolean isEmpty, long size) {
        this.bytes = bytes;
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.isEmpty = isEmpty;
        this.size = size;
    }

    public CustomMultipartFile(byte[] bytes, String name, String originalFilename, boolean isEmpty, long size) {
        this.bytes = bytes;
        this.name = name;
        this.originalFilename = originalFilename;
        this.isEmpty = isEmpty;
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {

    }
}
