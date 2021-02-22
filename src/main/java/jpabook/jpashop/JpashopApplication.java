package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.hibernate.Hibernate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	// 잭슨이 jpa프록시객체 해석하게 해줌.
	// 엔티티를 노출해서 이런 방법이 있다~~
	// 하이버모듈을 등록해서 쓰는건 좀.. 아니당...
	@Bean
	Hibernate5Module hibernate5Module() {

		// json 생성 시점에 강제로 지연 로딩 시키는 방법
		// 지연 로딩 모두를 실행해서 불필요한 쿼리까지 실행함. 자원 낭비.
		Hibernate5Module hibernate5Module = new Hibernate5Module();
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;

//		return new Hibernate5Module();
	}
}
