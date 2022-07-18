package ywphsm.ourneighbor.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.BaseEntity;
import ywphsm.ourneighbor.domain.BaseTimeEntity;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotBlank
    private String userId;

    @NotBlank
    private String password;    // 암호화

    @NotBlank
    private String username;

    @NotBlank
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private int age;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private int gender;         // 0 : 남자, 1 : 여자

    
    /*
        JPA 연관 관계 매핑
     */

}
