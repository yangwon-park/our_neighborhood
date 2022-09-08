package ywphsm.ourneighbor.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mail.MailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.login.SessionConst;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Rollback
class findUserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void 아이디_찾기_post() throws Exception{

        mvc.perform(
                        MockMvcRequestBuilders.post("/findUserId")
                                .param("email", "arnold1998@naver.com"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

    }

    @Test
    public void 비밀번호_찾기_post() throws Exception{

        mvc.perform(
                        MockMvcRequestBuilders.post("/findPassword")
                                .param("userId", "ailey")
                                .param("newPassword", "Arnold!(97")
                                .param("passwordCheck", "Arnold!(97"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        Member ailey = memberRepository.findByUserId("ailey").orElse(null);
        Assertions.assertThat(passwordEncoder.matches("Arnold!(97", ailey.getPassword())).isTrue();
    }

}