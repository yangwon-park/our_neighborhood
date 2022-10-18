package ywphsm.ourneighbor.domain.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.Review;
import ywphsm.ourneighbor.domain.menu.Menu;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UploadFile {

    @Id @GeneratedValue
    private Long id;

    private String uploadedFileName;
    private String storedFileName;

    private String uploadImageUrl;

    // Menu와 File 사진
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public UploadFile(String uploadedFileName, String storedFileName, String uploadImageUrl) {
        this.uploadedFileName = uploadedFileName;
        this.storedFileName = storedFileName;
        this.uploadImageUrl = uploadImageUrl;
    }

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

    public void addReview(Review review) {
        this.review = review;
        review.setFile(this);
    }

    /*
        비즈니스 로직 메소드
     */
    public void updateUploadedFileName(String storedFileName, String uploadedFileName, String uploadImageUrl) {
        this.storedFileName = storedFileName;
        this.uploadedFileName = uploadedFileName;
        this.uploadImageUrl = uploadImageUrl;
    }


}
