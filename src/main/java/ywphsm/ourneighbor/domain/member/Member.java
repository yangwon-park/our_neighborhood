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

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    private boolean emailConfirm;

    public Member(String userId, String password, String username, String nickname, String email, String phoneNumber, int age, int gender) {
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
    }

    public Member(String userId, String password, String username, String nickname, String email, String phoneNumber, int age, int gender, MemberRole memberRole) {
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.memberRole = memberRole;
    }

    //회원가입때 사용
    public Member(String username, int age, String phoneNumber, int gender, String userId, String password, String email, String nickname) {
        this.username = username;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

    public void emailConfirmSuccess() {
        this.emailConfirm = true;
    }

    /*
        JPA 연관 관계 매핑
     */

}
