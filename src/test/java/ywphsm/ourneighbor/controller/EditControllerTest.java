package ywphsm.ourneighbor.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.controller.form.PhoneCertifiedForm;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.login.SessionConst;


import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
@Transactional
@Slf4j
public class EditControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Member login(String userId, String password) {

        return memberRepository.findByUserId(userId)
                .filter(member -> passwordEncoder.matches(password, member.getPassword()))
                .orElse(null);

    }

    @Test
    public void 닉네임_수정() throws Exception {
        //given
        Member loginMember = login("arnold1998", "Arnold!(97");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        mvc.perform(
                        MockMvcRequestBuilders.post("/user/member_edit")
                                .param("nickname", "바뀐_닉네임")
                                .param("id", String.valueOf(loginMember.getId()))
                                .session(session))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    public void 비밀번호_수정() throws Exception {
        //given
        Member loginMember = login("arnold1998", "Arnold!(97");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        mvc.perform(
                        MockMvcRequestBuilders.post("/user/member_edit/password_edit")
                                .param("beforePassword", "Arnold!(97")
                                .param("afterPassword", "Arnold!(97!")
                                .param("passwordCheck", "Arnold!(97!")
                                .session(session))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());


        Member findMember = memberService.findById(loginMember.getId());
        assertThat(memberService.passwordCheck(findMember.getPassword(), "Arnold!(97!")).isTrue();
    }

    @Test
    public void 전화번호인증_Post() throws Exception {
        //given
        Member loginMember = login("arnold1998", "Arnold!(97");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        PhoneCertifiedForm phone = new PhoneCertifiedForm();
        phone.setPhoneNumber("01038352379");
        phone.setCertifiedNumber("123456");
        session.setAttribute(SessionConst.PHONE_CERTIFIED, phone);

        //핸드폰인증이 성공시
        mvc.perform(
                        MockMvcRequestBuilders.post("/user/member_edit/phoneCertified")
                                .session(session)
                                .param("phoneNumber", "01038352379")
                                .param("certifiedNumber", "123456"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/logout"));

        //then
        assertThat(memberService.findById(loginMember.getId()).getPhoneNumber()).isEqualTo("01038352379");
    }
}