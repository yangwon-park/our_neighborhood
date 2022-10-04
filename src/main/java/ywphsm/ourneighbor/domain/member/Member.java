package ywphsm.ourneighbor.domain.member;

import lombok.*;
import ywphsm.ourneighbor.domain.BaseTimeEntity;
import ywphsm.ourneighbor.domain.Review;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    private String userId;

    private String password;    // 암호화

    private String username;

    private String nickname;

    private String email;

    private String phoneNumber;

    private int age;

    private String birthDate;

    private int gender;         // 0 : 남자, 1 : 여자

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean emailConfirm;

    //(1:N) Store
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Store> storeList = new ArrayList<>();

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

    public Member(String userId, String password, String username, String nickname, String email, String phoneNumber, int age, int gender, Role role) {
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.role = role;
    }

    //회원가입때 사용

    public Member(String username, String birthDate, int age, String phoneNumber, int gender, String userId, String password, String email, String nickname, Role role) {
        this.username = username;
        this.birthDate = birthDate;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    //카카오 로그인시 openId 회원 저장
    public Member(String email, String username, int gender) {
        this.email = email;
        this.username = username;
        this.gender = gender;
    }

    //구글 로그인시 openId 회원 저장
    @Builder
    public Member(String email, String username, Role role, int gender) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.gender = gender;
    }

    public Member updateOAuth(String name, String picture) {
        this.username = name;
//        this.picture = picture;

        return this;
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

    //시큐리티 때매 추가
    public String getRoleKey() {
        return this.role.getKey();
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
