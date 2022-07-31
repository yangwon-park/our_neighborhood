package ywphsm.ourneighbor.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailToken {

    private static final Long EMAIL_TOKEN_TIME = 5L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;  //토큰 PK

    private LocalDateTime expirationDate;   //만료시간

    private boolean expired;    //만료여부

    private Long memberId;      //user의 PK값

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;   //생성 시간

    @LastModifiedDate
    private LocalDateTime lastModifiedDate; //마지막 변경 시간

    /**
     * 이메일 인증 토큰 생성
     * @param memberId
     * @return
     */
    public static EmailToken createEmailToken(Long memberId){
        EmailToken emailToken = new EmailToken();
        emailToken.expirationDate = LocalDateTime.now().plusMinutes(EMAIL_TOKEN_TIME); // 5분후 만료
        emailToken.memberId = memberId;
        emailToken.expired = false;
        return emailToken;
    }

    /**
     * 토큰 사용으로 인한 만료
     */
    public void useToken(){
        this.expired = true;
    }
}