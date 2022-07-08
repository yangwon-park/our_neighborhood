package ywphsm.ourneighbor.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@ToString(of = {
        "id", "userId", "password",
        "username", "nickname", "email",
        "phoneNumber", "age", "gender"
})
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
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private int age;

    @NotNull
    private int gender;         // 0 : 남자, 1 : 여자


    public Member(String userId, String username, String password,
                  String nickname, String email, String phoneNumber, int age, int gender) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
    }

    /*
        JPA 연관 관계 매핑
     */

}
