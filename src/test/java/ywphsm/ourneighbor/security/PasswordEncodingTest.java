//package ywphsm.ourneighbor.security;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import ywphsm.ourneighbor.service.MemberService;
//
//@SpringBootTest
//public class PasswordEncodingTest {
//
//    @Autowired
//    MemberService memberService;
//
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Test
//    void passwordEncode() {
//        //given
//        String rawPassword = "1234";
//
//        //when
//        String encodePassword = passwordEncoder.encode(rawPassword);
//
//        //then
//        Assertions.assertThat(rawPassword).isNotEqualTo(encodePassword);
//        Assertions.assertThat(passwordEncoder.matches(rawPassword, encodePassword)).isTrue();
//    }
//}
