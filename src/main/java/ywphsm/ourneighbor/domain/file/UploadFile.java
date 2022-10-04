package ywphsm.ourneighbor.domain.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.menu.Menu;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class UploadFile {

    @Id @GeneratedValue
    private Long id;

    private String uploadedFileName;
    private String storedFileName;

    // Menu와 File 사진
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;


    public UploadFile(String uploadedFileName, String storedFileName) {
        this.uploadedFileName = uploadedFileName;
        this.storedFileName = storedFileName;
    }

    /*
        === 연관 관계 편의 메소드 ===
     */
    public void addMenu(Menu menu) {
        this.menu = menu;
        menu.setFile(this);
    }
}
