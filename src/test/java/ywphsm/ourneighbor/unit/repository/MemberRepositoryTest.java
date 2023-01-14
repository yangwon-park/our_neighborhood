package ywphsm.ourneighbor.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.repository.member.MemberRepository;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    TestEntityManager tem;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {

        Member member1 = Member.builder()
                .userId("member1")
                .password("1234")
                .username("이름1")
                .nickname("닉네임1")
                .email("email1@naver.com")
                .phoneNumber("01011111111")
                .birthDate("20000101")
                .age(24)
                .gender(1)
                .role(Role.USER)
                .build();


        UploadFile uploadFile1 = new UploadFile("업로드명1", "저장명1", "URL1");

        tem.persist(member1);

        uploadFile1.addMember(member1);

    }

    @Test
    @DisplayName("회원 저장")
    void should_SaveAMember() {
        Member member = Member.builder()
                .userId("member0")
                .password("1234")
                .username("이름0")
                .nickname("닉네임0")
                .email("email0@naver.com")
                .phoneNumber("01000000000")
                .birthDate("20000101")
                .age(24)
                .gender(1)
                .role(Role.USER)
                .build();

        Member savedMember = memberRepository.save(member);

        assertThat(savedMember.getUserId()).isEqualTo(member.getUserId());
    }

    @Test
    @DisplayName("회원 수정")
    void should_UpdateAMember() {
        Member member = Member.builder()
                .userId("member0")
                .password("1234")
                .username("이름0")
                .nickname("닉네임0")
                .email("email0@naver.com")
                .phoneNumber("01000000000")
                .birthDate("20000101")
                .age(24)
                .gender(1)
                .role(Role.USER)
                .build();

        tem.persist(member);

        member.updateMember("updateNickname", "update@naver.com");

        assertThat(member.getNickname()).isEqualTo("updateNickname");
        assertThat(member.getEmail()).isEqualTo("update@naver.com");
    }

    @Test
    @DisplayName("회원 삭제시 IsEmpty 확인")
    void should_IsEmpty_When_DeleteAMember() {
        Member member = Member.builder()
                .userId("member0")
                .password("1234")
                .username("이름0")
                .nickname("닉네임0")
                .email("email0@naver.com")
                .phoneNumber("01000000000")
                .birthDate("20000101")
                .age(24)
                .gender(1)
                .role(Role.USER)
                .build();

        tem.persist(member);

        memberRepository.deleteById(member.getId());

        Optional<Member> findMember = memberRepository.findById(member.getId());

        assertThat(findMember).isEmpty();
    }

    @Test
    @DisplayName("닉네임으로 회원 조회")
    void should_FindMember_When_Nickname() {
        Member member = memberRepository.findByNickname("닉네임1").get();

        assertThat(member.getNickname()).isEqualTo("닉네임1");
    }

    @Test
    @DisplayName("아이디로 회원 조회")
    void should_FindMember_When_UserId() {
        Member member = memberRepository.findByUserId("member1").get();

        assertThat(member.getUserId()).isEqualTo("member1");
    }

    @Test
    @DisplayName("이메일으로 회원 조회")
    void should_FindMember_When_Email() {
        Member member = memberRepository.findByEmail("email1@naver.com").get();

        assertThat(member.getNickname()).isEqualTo("email1@naver.com");
    }

    @Test
    @DisplayName("전화번호로 회원 조회")
    void should_FindMember_When_PhoneNumber() {
        Member member = memberRepository.findByPhoneNumber("01011111111").get();

        assertThat(member.getNickname()).isEqualTo("01011111111");
    }
}
