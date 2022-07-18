package ywphsm.ourneighbor.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;


/*
    Auditing을 위한 등록, 수정 관련 엔티티
 */

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass // 속성만 단순 상속시켜주는 애노테이션
@Getter
public class BaseEntity extends BaseTimeEntity{

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
