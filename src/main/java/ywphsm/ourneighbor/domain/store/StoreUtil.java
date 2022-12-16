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
         
         조건
            0. Default: OPEN
            1. 휴무일이면 CLOSED
            2. 오픈 시간, 닫는 시간이 NULL: NULL로 반환 (알 수 없음 - NOT NULL이라 올바른 데이터가 아님)
            3. 오픈 시간 ~ 닫는 시간 사이가 아니면 CLOSE
            4. 위의 조건을 다 따진 후 BREAK 조건 찾음
            5. 쉬는 시간 시작이 NULL이면 OPEN 처리 (오픈 시간이 없는 경우 NULL이 들어있음)
            6. 쉬는 시간 사이면 BREAK
     */
    public static StoreStatus autoUpdateStatus(BusinessTime businessTime, List<String> offDays) {
        String today = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);             // 오늘 요일 한글로 변경
        LocalTime currentTime = LocalTime.now();
        LocalTime openingTime = businessTime.getOpeningTime();
        LocalTime closingTime = businessTime.getClosingTime();
        LocalTime breakStart = businessTime.getBreakStart();
        LocalTime breakEnd = businessTime.getBreakEnd();

        if (!offDays.isEmpty()) {
            for (String offDay : offDays) {
                if (today.equals(offDay)) {
                    return StoreStatus.CLOSED;
                }
            }
        }

        /*
            null인 경우를 처리하지 않으면 에러 발생
              => 검색 결과가 2개 이상인 경우 그냥 터져버림
         */
        if (openingTime == null
                || closingTime == null) {
            return null;
        }

        /*
            오픈 == 클로즈
                => 24시간 영업이므로 OPEN으로 설정
         */
        if (openingTime.equals(closingTime)) {
            return StoreStatus.OPEN;
        }

        /*
            오픈이 클로즈보다 더 뒤인 경우의 조건
                1. 오픈과 클로즈의 시간대가 모두 오전 또는 오후인 경우
                    => 현재, 오픈, 클로즈 모두에 +12시간 후 오픈과 클로즈를 바꿔줌
                2. 오픈이 오후이고 클로즈가 오전인 경우
                    => 현재, 오픈, 클로즈 모두에 +12시간
                이후 새로 만든 오픈과 클로즈 사이에 현재+12시가 있는지 보고 오픈 여부 판단

         */
        if (openingTime.isAfter(closingTime)) {
            LocalTime newOpeningTime;
            LocalTime newClosingTime;
            LocalTime newCurrentTime;

            if ((openingTime.getHour() < 12 && closingTime.getHour() < 12)      // 조건 1
                    || openingTime.getHour() >= 12 && closingTime.getHour() >= 12) {
                newOpeningTime = closingTime.plusHours(12);
                newClosingTime = openingTime.plusHours(12);
                newCurrentTime = currentTime.plusHours(12);

                if (newCurrentTime.isAfter(newOpeningTime)
                        && newCurrentTime.isBefore(newClosingTime)) {
                    return StoreStatus.CLOSED;
                }
            } else {                                                            // 조건 2
                newOpeningTime = openingTime.plusHours(12);
                newClosingTime = closingTime.plusHours(12);
                newCurrentTime = currentTime.plusHours(12);

                if (!(newCurrentTime.isAfter(newOpeningTime)
                        && newCurrentTime.isBefore(newClosingTime))) {
                    return StoreStatus.CLOSED;
                }
            }
        }

        if (breakStart == null || businessTime.getBreakEnd() == null) {
            return StoreStatus.OPEN;
        }

        /*
            쉬는 시간에 포함되면 BREAK로 설정
         */
        if (currentTime.isAfter(breakStart)
                && currentTime.isBefore(businessTime.getBreakEnd())) {
            return StoreStatus.BREAK;
        }

        /*
            어떤 경우에도 속하지 않으면 OPEN
         */
        return StoreStatus.OPEN;
    }
}
