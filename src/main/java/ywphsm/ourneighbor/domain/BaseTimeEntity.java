package ywphsm.ourneighbor.domain;


import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


/*
    생성 시간, 수정 시간은 대부분 필요하므로 사용
    BaseTimeEntity에는 시간 관련 Auditing 기능만 작성
    BaseEntity에서 이 클래스를 상속받아 시간 + 작성자, 수정자까지 담당
    필요한 엔티티에 알맞게 상속받아서 사용하면 됨
 */
@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass // 속성만 단순 상속시켜주는 애노테이션
public class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
