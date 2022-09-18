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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ywphsm.ourneighbor.controller.form.naver.NaverTokenResponse;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.MemberService;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {

    private final MemberService memberService;

    @Value("${naver.token.url}")
    private String NAVER_TOKEN_URL;
    @Value("${naver.client.id}")
    private String NAVER_CLIENT_ID;
    @Value("${naver.client.secret}")
    private String NAVER_CLIENT_SECRET;

    public Member getUserInfo(String code, String state) throws JsonProcessingException {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", NAVER_CLIENT_ID);
        params.add("client_secret", NAVER_CLIENT_SECRET);
        params.add("code", code);
        params.add("state", state);

        //HTTP Request를 위한 RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        //JSON 파싱을 위한 기본값 세팅
        //요청시 파라미터는 스네이크 케이스로 세팅되므로 Object mapper에 미리 설정해준다.
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

        //AccessToken 발급 요청
        ResponseEntity<String> resultEntity = restTemplate.postForEntity(NAVER_TOKEN_URL, params, String.class);
        log.info("resultEntity:{}", resultEntity);

        //Token Request
        NaverTokenResponse naverTokenResponse = mapper.readValue(resultEntity.getBody(),
                new TypeReference<NaverTokenResponse>() {});

        String access_token = naverTokenResponse.getAccess_token();

        //프로필 요청 헤더 작성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + access_token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //프로필정보 요청
        ResponseEntity<String> naverUserInfo = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class
        );
        log.info("naverUserInfo:{}", naverUserInfo);

        JsonParser jsonParser = new JsonParser();
        JsonElement parse = jsonParser.parse(naverUserInfo.getBody());

        String username = parse.getAsJsonObject().get("response").getAsJsonObject().get("nickname").getAsString();
        String StringGender = parse.getAsJsonObject().get("response").getAsJsonObject().get("gender").getAsString();
        String email = parse.getAsJsonObject().get("response").getAsJsonObject().get("email").getAsString();
        String mobile = parse.getAsJsonObject().get("response").getAsJsonObject().get("mobile").getAsString();
        String birthday = parse.getAsJsonObject().get("response").getAsJsonObject().get("birthday").getAsString();
        String birthyear = parse.getAsJsonObject().get("response").getAsJsonObject().get("birthyear").getAsString();

        int gender = 1;
        if (StringGender.equals("M")) {
            gender = 0;
        }

        log.info("birthday01:{}", birthday.substring(0, 2));
        log.info("birthday34:{}", birthday.substring(3, 5));
        String birthDate = birthyear + birthday.substring(0, 2) + birthday.substring(3, 5);
        int age = memberService.ChangeBirthToAge(birthDate);

        Member member = new Member(username, gender, email, mobile, birthDate, age);
        if (memberService.findByEmail(email) == null) {
            member.emailConfirmSuccess();   //이메일 인증 성공
            memberService.join(member);
        }

        return member;

    }
}
