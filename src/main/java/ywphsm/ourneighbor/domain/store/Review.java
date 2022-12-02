package ywphsm.ourneighbor.domain.store;

import lombok.*;
import ywphsm.ourneighbor.domain.BaseEntity;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(of = {
        "id", "content", "rating"
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    private String content;

    private Integer rating;

    /*
        JPA 연관 관계 매핑
    */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UploadFile> fileList = new ArrayList<>();

    public void addFile(List<UploadFile> fileList) {
        this.fileList = fileList;
        for (UploadFile uploadFile : fileList) {
            uploadFile.addReview(this);
        }
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    @Builder
    public Review(Long id, String content, Integer rating, Member member, Store store, List<UploadFile> fileList) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.member = member;
        this.store = store;
        this.fileList = fileList;
    }


}
