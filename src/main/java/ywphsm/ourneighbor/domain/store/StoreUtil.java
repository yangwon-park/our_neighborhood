package ywphsm.ourneighbor.domain.store;

import lombok.extern.slf4j.Slf4j;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Slf4j
public class StoreUtil {

    /*
        Status 업데이트
         => 검색시 DTO에만 반영되게 만들고 DB에는 반영되지 않음
         => DB 자체 값은 항상 OPEN
     */
    public static StoreStatus autoUpdateStatus(BusinessTime businessTime, List<String> offDays) {
        String today = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);             // 오늘 요일 한글로 변경

        LocalTime currentTime = LocalTime.now();

        if (!offDays.isEmpty()) {
            for (String offDay : offDays) {
                if (today.equals(offDay)) {
                    log.info("휴무입니다.");
                    return StoreStatus.CLOSED;
                }
            }
        }

        /*
            null인 경우를 처리하지 않으면 에러 발생
              => 검색 결과가 2개 이상인 경우 그냥 터져버림
         */
        if (businessTime.getOpeningTime() == null || businessTime.getClosingTime() == null) {
            return null;
        }

        if (businessTime.getOpeningTime().equals(businessTime.getClosingTime())) {
            return StoreStatus.OPEN;
        }

        if (!(currentTime.isAfter(businessTime.getOpeningTime()) && currentTime.isBefore(businessTime.getClosingTime()))) {
            return StoreStatus.CLOSED;
        }

        if (businessTime.getBreakStart() == null) {
            return null;
        }

        if (currentTime.isAfter(businessTime.getBreakStart()) && currentTime.isBefore(businessTime.getBreakEnd())) {
            return StoreStatus.BREAK;
        }

        return null;
    }
}
