package ywphsm.ourneighbor.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.controller.form.EditForm;
import ywphsm.ourneighbor.controller.form.PasswordEditForm;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.email.TokenService;
import ywphsm.ourneighbor.service.login.LoginService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EditController.class)
@Rollback
public class EditConrollerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoginService loginService;

    @Test
    public void 닉네임_수정() throws Exception {
        //given
        Member loginMember = loginService.login("ailey", "12341234");
        HttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        mvc.perform(post("/member_edit")
                .param("nickname", "바뀐_닉네임")
                .param("id", String.valueOf(loginMember.getId())))
                .andExpect(status().isOk());

        Member findMember = memberRepository.findById(loginMember.getId()).orElse(null);
        assertThat(findMember.getNickname()).isEqualTo("바뀐_닉네임");
    }

    @Test
    public void 비밀번호_수정() throws Exception {
        //given
        Member loginMember = loginService.login("ailey", "12341234");
        HttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        PasswordEditForm passwordEditForm = new PasswordEditForm();
        passwordEditForm.setBeforePassword("12341234");
        passwordEditForm.setAfterPassword("Arnold!(97");
        passwordEditForm.setPasswordCheck("Arnold!(97");

//        String url = "http://localhost:" + port + "/member_edit/password_edit";

        //when
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, passwordEditForm, String.class);

        //then
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Member findMember = memberRepository.findById(loginMember.getId()).orElse(null);
        assertThat(memberService.passwordCheck(findMember.getPassword(), "Arnold!(97")).isTrue();
    }

}
