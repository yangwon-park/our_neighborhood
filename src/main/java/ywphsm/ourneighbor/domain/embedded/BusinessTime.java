package ywphsm.ourneighbor.domain.embedded;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class BusinessTime {
    private LocalTime openingTime;            // 여는 시간
    private LocalTime closingTime;            // 닫는 시간
    private LocalTime breakStart;             // 쉬는 시간 시작
    private LocalTime breakEnd;               // 쉬는 시간 끝
}
