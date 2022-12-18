package ywphsm.ourneighbor.domain.store;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.BaseEntity;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.member.Member;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RequestAddStore extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "request_add_store_id")
    private Long id;

    private String name;

    private String content;

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public RequestAddStore(Long id, String name, Address address, Member member, String content) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.member = member;
        this.content = content;
    }
}
