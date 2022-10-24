package ywphsm.ourneighbor.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.member.MemberRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Rollback
class FindUserControllerTest {

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
                                .param("userId", "arnold1998")
                                .param("newPassword", "Arnold!(97")
                                .param("passwordCheck", "Arnold!(97"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        Member arnold1998 = memberRepository.findByUserId("arnold1998").orElse(null);
        Assertions.assertThat(passwordEncoder.matches("Arnold!(97", arnold1998.getPassword())).isTrue();
    }

}

