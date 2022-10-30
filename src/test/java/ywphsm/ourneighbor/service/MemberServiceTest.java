package ywphsm.ourneighbor.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.controller.form.PhoneCertifiedForm;
import ywphsm.ourneighbor.domain.dto.Member.MemberDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ywphsm.ourneighbor.domain.member.QMember.*;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class MemberServiceTest {

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @Autowired
    MemberService memberService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void before() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        queryFactory = new JPAQueryFactory(em);
    }
    @Test
    @DisplayName("회원 가입 -> 수정 -> 삭제")
    void join_update_delete() throws Exception {
        MemberDTO.Add dto = MemberDTO.Add.builder()
                .username("테스트1")
                .nickname("닉네임1")
                .birthDate("1994-05-23")
                .email("test@1234.com")
                .phoneNumber("01012341234")
                .gender(0)
                .certifiedNumber("789234")
                .userId("testId")
                .password("!1234")
                .passwordCheck("!1234")
                .build();

        //회원 저장
        String url = "http://localhost:" + port + "/member/add";

        ResultActions resultActions_save = mvc.perform(post(url)
                        .param("username", dto.getUsername())
                        .param("nickname", dto.getNickname())
                        .param("birthDate", dto.getBirthDate())
                        .param("email", dto.getEmail())
                        .param("phoneNumber", dto.getPhoneNumber())
                        .param("gender", String.valueOf(dto.getGender()))
                        .param("certifiedNumber", dto.getCertifiedNumber())
                        .param("userId", dto.getUserId())
                        .param("password", dto.getPassword())
                        .param("passwordCheck", dto.getPasswordCheck()))
                .andDo(print())
                .andExpect(status().isOk());

        Long memberId = Long.parseLong(resultActions_save.andReturn().getResponse().getContentAsString());
        Member findMember = memberService.findById(memberId);
        assertThat(findMember.getNickname()).isEqualTo(dto.getNickname());

        //회원 수정(닉네임, 이메일)
        url = "http://localhost:" + port + "/member/edit";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, findMember);

        ResultActions resultActions_edit = mvc.perform(put(url)
                        .param("nickname", "수정닉네임")
                        .param("email", "updateEmail@update.com")
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk());

        String result = resultActions_edit.andReturn().getResponse().getContentAsString();
        findMember = memberService.findById(memberId);
        assertThat(result).isEqualTo("성공");
        assertThat(findMember.getNickname()).isEqualTo("수정닉네임");
        assertThat(findMember.getEmail()).isEqualTo("updateEmail@update.com");

        //회원수정(비밀번호)
        url = "http://localhost:" + port + "/member/edit/password";

        ResultActions resultActions_edit_password = mvc.perform(put(url)
                        .param("beforePassword", "!1234")
                        .param("afterPassword", "updatePW!2")
                        .param("passwordCheck", "updatePW!2")
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk());

        result = resultActions_edit_password.andReturn().getResponse().getContentAsString();
        findMember = memberService.findById(memberId);
        assertThat(result).isEqualTo("성공");
        assertThat(memberService.passwordCheck(
                findMember.getPassword(), "updatePW!2")).isTrue();

        //회원수정(전화번호)
        url = "http://localhost:" + port + "/member/edit/phoneNumber";

        PhoneCertifiedForm certifiedForm = new PhoneCertifiedForm();
        certifiedForm.setPhoneNumber("01012341234");
        certifiedForm.setCertifiedNumber("123456");
        session.setAttribute(SessionConst.PHONE_CERTIFIED, certifiedForm);

        ResultActions resultActions_edit_phoneNumber = mvc.perform(put(url)
                        .param("phoneNumber", "01012341234")
                        .param("certifiedNumber", "123456")
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk());

        result = resultActions_edit_phoneNumber.andReturn().getResponse().getContentAsString();
        findMember = memberService.findById(memberId);
        assertThat(result).isEqualTo("성공");
        assertThat(findMember.getPhoneNumber()).isEqualTo("01012341234");

        //회원 삭제
        url = "http://localhost:" + port + "/member/withdrawal";

        ResultActions resultActions_delete = mvc.perform(delete(url)
                        .param("memberId", String.valueOf(memberId)))
                .andDo(print())
                .andExpect(status().isOk());

        assertThatThrownBy(() -> memberService.findById(memberId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("아이디, 비밀번호 찾기")
    void findUser() throws Exception {
        MemberDTO.Add dto = MemberDTO.Add.builder()
                .username("테스트1")
                .nickname("닉네임1")
                .birthDate("1994-05-23")
                .email("test@1234.com")
                .phoneNumber("01012341234")
                .gender(0)
                .certifiedNumber("789234")
                .userId("testId")
                .password("!1234")
                .passwordCheck("!1234")
                .build();

        //회원 저장
        String url = "http://localhost:" + port + "/member/add";

        ResultActions resultActions_save = mvc.perform(post(url)
                        .param("username", dto.getUsername())
                        .param("nickname", dto.getNickname())
                        .param("birthDate", dto.getBirthDate())
                        .param("email", dto.getEmail())
                        .param("phoneNumber", dto.getPhoneNumber())
                        .param("gender", String.valueOf(dto.getGender()))
                        .param("certifiedNumber", dto.getCertifiedNumber())
                        .param("userId", dto.getUserId())
                        .param("password", dto.getPassword())
                        .param("passwordCheck", dto.getPasswordCheck()))
                .andDo(print())
                .andExpect(status().isOk());

        //아이디 찾기
        url = "http://localhost:" + port + "/findUserId";

        ResultActions resultActions_findUserId = mvc.perform(post(url)
                        .param("email", "test@1234.com"))
                .andDo(print())
                .andExpect(status().isOk());

        String result = resultActions_findUserId.andReturn().getResponse().getContentAsString();
        assertThat(result).isEqualTo("성공");

        //비밀번호 찾기
        url = "http://localhost:" + port + "/findPassword";

        ResultActions resultActions_findPassword = mvc.perform(post(url)
                        .param("email", "test@1234.com")
                        .param("userId", "testId"))
                .andDo(print())
                .andExpect(status().isOk());

        result = resultActions_findPassword.andReturn().getResponse().getContentAsString();
        assertThat(result).isEqualTo("성공");
    }

    @Test
    @DisplayName("10대, 20대인 회원 찾기")
    void findMembers1020() {
        List<Member> memberList = queryFactory
                .select(member)
                .from(member)
                .where(member.age.goe(10).and(member.age.lt(30)))
                .fetch();

        assertThat(memberList).extracting("username").contains("박양원", "문한성");
    }

    @Test
    @DisplayName("멤버 Role 조회하기")
    void findRole() {
        String code = "판매자";

        Role role = Role.of(code);
        System.out.println("role.getKey() = " + role.getKey());
        System.out.println("role.getTitle() = " + role.getTitle());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("계정의 권한 업데이트")
    void updateRole() throws Exception {
        String userId = "ywonp94";
        String role = "판매자";

        Member findMember = memberService.findByUserId(userId);

        assertThat(findMember.getRole()).isEqualTo(Role.ADMIN);

        String url = "http://localhost:" + port + "/admin/update_role/" + findMember.getId();

        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(role))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(findMember.getRole()).isEqualTo(Role.SELLER);
    }
}
















