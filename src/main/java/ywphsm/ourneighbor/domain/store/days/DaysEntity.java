package ywphsm.ourneighbor.domain.store.days;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


// 답변 보고 정하자
//@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DaysEntity {

    @Id
    @GeneratedValue
    @Column(name = "days_entity_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Days days;

    public DaysEntity(Days days) {
        this.days = days;
    }
}
