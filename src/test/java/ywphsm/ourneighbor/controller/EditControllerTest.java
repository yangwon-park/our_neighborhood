//package ywphsm.ourneighbor.controller;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import ywphsm.ourneighbor.controller.form.PhoneCertifiedForm;
//import ywphsm.ourneighbor.domain.member.Member;
//import ywphsm.ourneighbor.repository.member.MemberRepository;
//import ywphsm.ourneighbor.service.MemberService;
//import ywphsm.ourneighbor.service.login.LoginService;
//import ywphsm.ourneighbor.service.login.SessionConst;
//
//import java.util.NoSuchElementException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@Rollback
//public class EditControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    MemberService memberService;
//
//    @Autowired
//    LoginService loginService;
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Test
//    public void 닉네임_수정() throws Exception {
//        //given
//        Member loginMember = loginService.login("ailey", "12341234");
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
//        parameters.add("nickname", "바뀐_닉네임");
//        parameters.add("id", String.valueOf(loginMember.getId()));
//
//        String url = "http://localhost:" + port + "/member_edit";
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, parameters, String.class);
//
//        Member findMember = memberRepository.findById(loginMember.getId()).orElse(null);
//        assertThat(findMember.getNickname()).isEqualTo("바뀐_닉네임");
//    }
//
//    @Test
//    public void 비밀번호_수정() throws Exception {
//        //given
//        Member loginMember = loginService.login("ailey", "12341234");
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        mvc.perform(
//                        MockMvcRequestBuilders.post("/member_edit/password_edit")
//                                .param("beforePassword", "12341234")
//                                .param("afterPassword", "Arnold!(97")
//                                .param("passwordCheck", "Arnold!(97")
//                                .session(session))
//                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
//
//
//        Member findMember = memberService.findById(loginMember.getId());
//        assertThat(memberService.passwordCheck(findMember.getPassword(), "Arnold!(97")).isTrue();
//    }
//
//    @Test
//    public void 이메일인증_Get() throws Exception {
//        //given
//
//        Member loginMember2 = loginService.login("ailey", "12341234");
//        MockHttpSession session2 = new MockHttpSession();
//        session2.setAttribute(SessionConst.LOGIN_MEMBER, loginMember2);
//
//        Member loginMember = loginService.login("ailey", "12341234");
//        loginMember.emailConfirmSuccess();
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        //이메일 인증이 false
//        mvc.perform(
//                        MockMvcRequestBuilders.get("/member_edit/confirm_email")
//                                .session(session2))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.view().name("edit/EmailConfirmForm"));
//
//        //이메일 인증이 true
//        mvc.perform(
//                        MockMvcRequestBuilders.get("/member_edit/confirm_email")
//                                .session(session))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.view().name("edit/alreadyEmailConfirm"));
//    }
//
//    @Test
//    public void 이메일인증_Post() throws Exception {
//        //given
//        Member loginMember = loginService.login("ailey", "12341234");
//        loginMember.emailConfirmSuccess();
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        mvc.perform(
//                        MockMvcRequestBuilders.post("/member_edit/confirm_email")
//                                .session(session)
//                                .param("email", loginMember.getEmail()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.view().name("signUp/confirmEmail"));
//    }
//
//    @Test
//    public void 전화번호인증_Post() throws Exception {
//        //given
//        Member loginMember = loginService.login("ailey", "12341234");
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        PhoneCertifiedForm phone = new PhoneCertifiedForm();
//        phone.setPhoneNumber("01038352376");
//        phone.setCertifiedNumber("123456");
//        session.setAttribute(SessionConst.PHONE_CERTIFIED, phone);
//
//        //핸드폰인증이 성공시
//        mvc.perform(
//                        MockMvcRequestBuilders.post("/member_edit/phoneCertified")
//                                .session(session)
//                                .param("phoneNumber", "01038352376")
//                                .param("certifiedNumber", "123456"))
//                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
//                .andExpect(MockMvcResultMatchers.redirectedUrl("/logout"));
//
//        //then
//        assertThat(memberService.findById(loginMember.getId()).getPhoneNumber()).isEqualTo("01038352376");
//    }
//
//    @Test
//    public void 회원탈퇴() throws Exception {
//        //given
//        Member loginMember = loginService.login("ailey", "12341234");
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        mvc.perform(
//                        MockMvcRequestBuilders.get("/member_edit/withdrawal")
//                                .session(session))
//                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
//                .andExpect(MockMvcResultMatchers.redirectedUrl("/logout"));
//
//        Assertions.assertThrows(NoSuchElementException.class, () -> {
//            memberService.findById(loginMember.getId());
//        });
//    }
//}