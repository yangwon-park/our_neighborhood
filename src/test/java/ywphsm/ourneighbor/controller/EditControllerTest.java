package ywphsm.ourneighbor.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.login.LoginService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
public class EditControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginService loginService;

    @Test
    public void 닉네임_수정() throws Exception {
        //given
        Member loginMember = loginService.login("ailey", "12341234");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("nickname", "바뀐_닉네임");
        parameters.add("id", String.valueOf(loginMember.getId()));

        String url = "http://localhost:" + port + "/member_edit";
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, parameters, String.class);

//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus);
        Member findMember = memberRepository.findById(loginMember.getId()).orElse(null);
        assertThat(findMember.getNickname()).isEqualTo("바뀐_닉네임");
    }

    @Test
    public void 비밀번호_수정() throws Exception {
        //given
        Member loginMember = loginService.login("ailey", "12341234");
        HttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("beforePassword", "12341234");
        parameters.add("afterPassword", "Arnold!(97");
        parameters.add("passwordCheck", "Arnold!(97");

        String url = "http://localhost:" + port + "/member_edit/password_edit";

        //when
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, parameters, Object.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Member findMember = memberRepository.findById(loginMember.getId()).orElse(null);
        assertThat(memberService.passwordCheck(findMember.getPassword(), "Arnold!(97")).isTrue();
    }

}
