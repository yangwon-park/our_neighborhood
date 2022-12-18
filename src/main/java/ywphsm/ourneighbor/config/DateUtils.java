package ywphsm.ourneighbor.config;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

public class DateUtils {

    //년 월 일 차이 가져오기
    public static String periodDate(LocalDateTime createDate) {
        LocalDateTime now = LocalDateTime.now();

        Period diff = Period.between(createDate.toLocalDate(), now.toLocalDate());

        if (diff.getYears() > 0) {
            return diff.getYears() + "년 전";
        } else if (diff.getMonths() > 0) {
            return diff.getMonths() + "달 전";
        } else if (diff.getDays() == 0) {
            return durationDate(createDate);
        } else {
            return diff.getDays() + "일 전";
        }
    }

    //시 분 초 차이 가져오기
    public static String durationDate(LocalDateTime createDate) {
        LocalDateTime now = LocalDateTime.now();

        Duration diff = Duration.between(createDate.toLocalTime(), now.toLocalTime());
        long hours = Math.abs(diff.toHours());
        long minutes = Math.abs(diff.toMinutes());
        long seconds = Math.abs(diff.toSeconds());

        if (hours > 0) {
            return hours + "시간 전";
        } else if (minutes > 0) {
            return minutes + "분 전";
        } else {
            return seconds + "초 전";
        }
    }

}
