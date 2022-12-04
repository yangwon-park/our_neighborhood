package ywphsm.ourneighbor;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class OurNeighborApplication {

	/*
	 	AWS 메타데이터 로딩으로 인한 서비스 연결 시점의 지연을 막기 위해
		disable 옵션을 true로 선언
		이걸 키면 credentials.instanceProfile을 불러오지 못함
	 */
	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}

	public static void main(String[] args) {
		SpringApplication.run(OurNeighborApplication.class, args);
	}

	/*
	 	JPAQueryFactory 스프링 빈으로 등록
	 */
	@Bean
	JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
		return new JPAQueryFactory(entityManager);
	}

}