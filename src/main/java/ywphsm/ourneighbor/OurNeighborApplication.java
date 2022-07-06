package ywphsm.ourneighbor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing		//  Spring Data JPA에서 Auditing 기능 사용을 위해 필수 (등록, 수정 추적)
@SpringBootApplication
public class OurNeighborApplication {

	public static void main(String[] args) {
		SpringApplication.run(OurNeighborApplication.class, args);
	}

}
