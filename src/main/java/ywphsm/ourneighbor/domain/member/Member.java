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

//    @NotBlank
    private String userId;

//    @NotBlank
    private String password;    // 암호화

//    @NotBlank
    private String username;

//    @NotBlank
    private String nickname;

//    @Email
    private String email;

//    @NotBlank
    private String phoneNumber;

//    @NotNull
    private int age;

//    @NotBlank
    private String birthDate;

//    @NotNull
    private int gender;         // 0 : 남자, 1 : 여자

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    private boolean emailConfirm;

    // 생성 메소드
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

    public Member(String username, String birthDate, int age, String phoneNumber, int gender, String userId, String password, String email, String nickname) {
        this.username = username;
        this.birthDate = birthDate;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

    //카카오 로그인시 openId 회원 저장
    public Member(String email, String username, int gender) {
        this.email = email;
        this.username = username;
        this.gender = gender;
    }

    //구글 로그인시 openId 회원 저장
    public Member(String email, String username, boolean emailConfirm, int gender) {
        this.email = email;
        this.username = username;
        this.emailConfirm = emailConfirm;
        this.gender = gender;
    }

    //네이버 로그인시 회원 저장
    public Member(String username, int gender, String email, String phoneNumber, String birthDate, int age) {
        this.username = username;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.age = age;
    }


    //이메일 인증 성공

    public void emailConfirmSuccess() {
        this.emailConfirm = true;
    }


    //회원 수정
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    /*
        JPA 연관 관계 매핑
     */

}
