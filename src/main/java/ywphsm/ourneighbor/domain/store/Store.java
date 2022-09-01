package ywphsm.ourneighbor.domain.store;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ywphsm.ourneighbor.domain.*;
import ywphsm.ourneighbor.domain.store.days.DaysOfStore;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {
        "id", "name", "lon", "lat",
        "phoneNumber", "openingTime", "closingTime",
        "breakStart", "breakEnd", "notice", "intro", "status"})
@Entity
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "store_id")
    private Long id;

    private String name;

    private Double lat;                // 위도

    private Double lon;                // 경도

    private String phoneNumber;

    @Column(name = "opening_time")
    private LocalTime openingTime;            // 여는 시간

    @Column(name = "closing_time")
    private LocalTime closingTime;            // 닫는 시간

    @Column(name = "break_start")
    private LocalTime breakStart;             // 쉬는 시간 시작

    @Column(name = "break_end")
    private LocalTime breakEnd;               // 쉬는 시간 끝

    private String notice;                    // 가게 소식

    private String intro;                     // 가게 소개

//    @OneToMany(mappedBy = "store")
//    private List<DaysOfStore> daysOfStore;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> offDays;

    @Enumerated(EnumType.STRING)
    private StoreStatus status;               // 가게 오픈 상황

    // 주소는 임베디드 타입으로 받음
    @Embedded
    private Address address;

    /*
        JPA 연관 관계 매핑
     */

    // Menu (1:N)
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Menu> menuList = new ArrayList<>();


    // Category (N:N)
    @OneToMany(mappedBy = "store", cascade = CascadeType.PERSIST)
    private List<CategoryOfStore> categoryOfStoreList = new ArrayList<>();

    // Many To Many인듯
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;




    /*
        생성자
     */
    public Store(String name, Double lat, Double lon,
                 String phoneNumber, LocalTime openingTime, LocalTime closingTime,
                 LocalTime breakStart, LocalTime breakEnd, String notice, String intro,
                 List<String> offDays, StoreStatus status, Address address) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.phoneNumber = phoneNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.breakStart = breakStart;
        this.breakEnd = breakEnd;
        this.notice = notice;
        this.intro = intro;
        this.offDays = offDays;
        this.status = status;
        this.address = address;
    }

    @Builder
    public Store(String name, Double lat, Double lon,
                 String phoneNumber, LocalTime openingTime, LocalTime closingTime,
                 LocalTime breakStart, LocalTime breakEnd, String notice, String intro,
                 List<String> offDays, StoreStatus status, Address address,
                 List<Menu> menuList, List<CategoryOfStore> categoryOfStoreList) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.phoneNumber = phoneNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.breakStart = breakStart;
        this.breakEnd = breakEnd;
        this.notice = notice;
        this.intro = intro;
        this.offDays = offDays;
        this.status = status;
        this.address = address;
        this.menuList = menuList;
        this.categoryOfStoreList = categoryOfStoreList;
    }

    /*
        === 연관 관계 편의 메소드 ===
     */
    public void addMenu(Menu menu) {
        menu.setStore(this);
        menuList.add(menu);
    }


    /*
        === 생성 메소드 ===
    */

    /*
        === 비즈니스 로직 추가 ===
     */
    public void updateStatus(StoreStatus status) {
        this.status = status;
    }

    // Status 업데이트 구문 (검색시 반영되게 만듬)
    public void autoUpdateStatus(List<String> offDays, LocalTime openingTime, LocalTime closingTime, LocalTime breakStart, LocalTime breakEnd) {

        // 오늘의 요일을 한글로 바꿔주는 로직
        String today = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

        // 현재 시간
        LocalTime time = LocalTime.now();

        for (String offDay : offDays) {
            if (today.equals(offDay)) {

                updateStatus(StoreStatus.CLOSED);
                return;
            }
        }

        // null인 경우를 처리하지 않으면 에러 발생 (검색 결과가 2개 이상인 경우 그냥 터져버림)
        if (openingTime == null || closingTime == null) {
            return;
        }

        if (openingTime.equals(closingTime)) {
            updateStatus(StoreStatus.OPEN);
            return;
        }

        if (!(time.isAfter(openingTime) && time.isBefore(closingTime))) {
            updateStatus(StoreStatus.CLOSED);
            return;
        }

        if (breakStart == null) {
            return;
        }

        if (time.isAfter(breakStart) && time.isBefore(breakEnd)) {
            updateStatus(StoreStatus.BREAK);
        }
    }
}
