package ywphsm.ourneighbor.domain.embedded;

import lombok.*;

import javax.persistence.Embeddable;
import java.time.LocalTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class BusinessTime {
    private LocalTime openingTime;            // 여는 시간
    private LocalTime closingTime;            // 닫는 시간
    private LocalTime breakStart;             // 쉬는 시간 시작
    private LocalTime breakEnd;               // 쉬는 시간 끝
    private LocalTime lastOrder;

    public BusinessTime(LocalTime openingTime, LocalTime closingTime, LocalTime breakStart, LocalTime breakEnd) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.breakStart = breakStart;
        this.breakEnd = breakEnd;
    }
}
