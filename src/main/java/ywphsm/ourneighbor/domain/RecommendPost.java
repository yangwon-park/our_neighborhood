package ywphsm.ourneighbor.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecommendPost extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "recommend_post_id")
    private Long id;

    private String skyStatus;

    private String pm10Value;

    private String tmp;

    private String pop;

    private String header;

    private String content;

    @Builder
    public RecommendPost(Long id, String skyStatus, String pm10Value,
                         String tmp, String pop, String header, String content) {
        this.id = id;
        this.skyStatus = skyStatus;
        this.pm10Value = pm10Value;
        this.tmp = tmp;
        this.pop = pop;
        this.header = header;
        this.content = content;
    }
}
