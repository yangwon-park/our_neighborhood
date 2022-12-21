package ywphsm.ourneighbor.domain.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Review;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.store.Store;

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


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public UploadFile(String uploadedFileName, String storedFileName, String uploadImageUrl) {
        this.uploadedFileName = uploadedFileName;
        this.storedFileName = storedFileName;
        this.uploadImageUrl = uploadImageUrl;
    }

    //FileStore 사용
    public UploadFile(String uploadedFileName, String storedFileName) {
        this.uploadedFileName = uploadedFileName;
        this.storedFileName = storedFileName;
    }

    /*
        === 연관 관계 편의 메소드 ===
     */
    public void addStore(Store store) {
        this.store = store;
        store.setFile(this);
    }

    public void addMenu(Menu menu) {
        this.menu = menu;
        menu.setFile(this);
    }

    public void addReview(Review review) {
        this.review = review;
    }

    public void addMember(Member member) {
        this.member = member;
        member.setFile(this);
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