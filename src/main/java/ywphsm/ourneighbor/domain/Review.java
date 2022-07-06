package ywphsm.ourneighbor.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Review extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    private String title;
    private String content;
    


}
