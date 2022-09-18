package ywphsm.ourneighbor.service.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ywphsm.ourneighbor.controller.form.kakao.KakaoTokenResponse;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.MemberService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final MemberService memberService;

    public Member getKakaoAccessToken(String code) throws JsonProcessingException {

        String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "cd429446ad94ee1e20c77038ad37b1a2");
        params.add("redirect_uri", "http://localhost:8080/login/kakao");
        params.add("code", code);

        //HTTP Request를 위한 RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        //JSON 파싱을 위한 기본값 세팅
        //요청시 파라미터는 스네이크 케이스로 세팅되므로 Object mapper에 미리 설정해준다.
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

        //AccessToken 발급 요청
        ResponseEntity<String> resultEntity = restTemplate.postForEntity(KAKAO_TOKEN_URL, params, String.class);
        log.info("resultEntity:{}", resultEntity);

        //Token Request
        KakaoTokenResponse kakaoTokenResponse = mapper.readValue(resultEntity.getBody(),
                new TypeReference<KakaoTokenResponse>() {});

        String access_token = kakaoTokenResponse.getAccess_token();

        //프로필 요청 헤더 작성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + access_token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //프로필정보 요청
        ResponseEntity<String> kakaoUserInfo = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class
        );
        log.info("kakaoUserInfo:{}", kakaoUserInfo);

        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(kakaoUserInfo.getBody());

        Long id = element.getAsJsonObject().get("id").getAsLong();
        String username = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();

        boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
        boolean hasGender = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_gender").getAsBoolean();
        boolean is_email_verified = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("is_email_verified").getAsBoolean();
        String email = "";
        int gender = 1;

        if (hasEmail) {
            email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
        }
        if (hasGender) {
            String kakaoGender = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("gender").getAsString();
            if (kakaoGender.equals("male")) {
                gender = 0;
            }
            log.info("kakaoGender {}", kakaoGender);
        }

        Member member = new Member(email, username, gender);
        Member findMember = memberService.findByEmail(email);
        if (findMember == null) {
            if (is_email_verified) {
                member.emailConfirmSuccess();   //이메일 인증 성공
            }
            memberService.join(member);
        }

        return member;
    }

}
