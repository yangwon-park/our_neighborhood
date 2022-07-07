package ywphsm.ourneighbor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing		//  Spring Data JPA에서 Auditing 기능 사용을 위해 필수 (등록, 수정 추적)
@SpringBootApplication
public class OurNeighborApplication {

	public static void main(String[] args) {
		SpringApplication.run(OurNeighborApplication.class, args);
	}

	// 이게 있어야 작성자 수정자 받을 수 있음
	// UUID 부분은 실무에선 session에서 사용자 아이디를 받아와서 처리
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
