package ywphsm.ourneighbor.domain.member;

import lombok.*;
import ywphsm.ourneighbor.domain.BaseTimeEntity;
import ywphsm.ourneighbor.domain.store.Review;
import ywphsm.ourneighbor.domain.store.RequestAddStore;

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

    //(N:N) Store
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<MemberOfStore> memberOfStoreList = new ArrayList<>();

    //(1:N) Review
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RequestAddStore> requestAddStoreList = new ArrayList<>();

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

    @Builder
    public Member(String username, String email, Role role) {
        this.username = username;
        this.email = email;
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

    public Member updateOAuth(String name, String picture) {
        this.username = name;
//        this.picture = picture;

        return this;
    }

    //시큐리티 때매 추가
    public String getRoleKey() {
        return this.role.getKey();
    }


    //회원 수정
    public void updateMember(String nickname, String email) {
        this.nickname = nickname;
        this.email =email;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void addReview(Review review) {
        review.setMember(this);
        reviewList.add(review);
    }

    /*
        JPA 연관 관계 매핑
     */

}