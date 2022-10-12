package ywphsm.ourneighbor.domain;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Review extends BaseEntity{

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

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private UploadFile file;

    public void setFile(UploadFile file) {
        this.file = file;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    @Builder
    public Review(Long id, String content, Integer rating, Member member, Store store, UploadFile file) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.member = member;
        this.store = store;
        this.file = file;
    }


}
