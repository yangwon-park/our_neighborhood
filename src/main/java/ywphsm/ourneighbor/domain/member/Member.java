package ywphsm.ourneighbor.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.BaseEntity;
import ywphsm.ourneighbor.domain.BaseTimeEntity;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String userId;

    @NotEmpty
    private String password;    // 암호화

    @NotEmpty
    private String username;

    @NotEmpty
    private String nickname;

    private String email;

    @NotEmpty
    private int age;

    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private int gender;         // 0 : 남자, 1 : 여자

    
    /*
        JPA 연관 관계 매핑
     */

}
