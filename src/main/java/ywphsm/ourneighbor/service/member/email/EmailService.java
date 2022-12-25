package ywphsm.ourneighbor.service.member.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Async
    public void sendEmail(SimpleMailMessage simpleMailMessage) {
        javaMailSender.send(simpleMailMessage);
    }

    public void findUserIdSendEmail(String email, String userId) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);   //보낼 이메일주소 추가
        mailMessage.setSubject("아이디 찾기 Our neighborhood");  //제목
        mailMessage.setText("회원님의 아이디는 " + userId + "입니다.");
        sendEmail(mailMessage);
        log.info("아이디 찾기 이메일 발송 완료 userId={}", userId);
    }

    public void findPasswordSendEmail(String email, String temporaryPassword) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);   //보낼 이메일주소 추가
        mailMessage.setSubject("비밀번호 찾기 Our neighborhood");  //제목
        mailMessage.setText("임시 비밀번호를 발급했습니다.\n" +
                "임시 비밀번호는 " + temporaryPassword + "입니다.\n" +
                "로그인후 마이페이지에서 비밀번호를 변경해 주세요.");
        sendEmail(mailMessage);
        log.info("비밀번호 찾기 이메일 발송 완료 임시비밀번호={}", temporaryPassword);
    }

}
