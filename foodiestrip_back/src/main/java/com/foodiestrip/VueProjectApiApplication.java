package com.foodiestrip;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
//@MapperScan(basePackages = "com.ssafy.enjoytrip.model.dao")
public class VueProjectApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VueProjectApiApplication.class, args);
		System.out.println("과연 언제 실행~??");
	}
	
	@Bean
	public CommandLineRunner runner() {
		return (args)->{
			System.out.println("실행");
//			KMP.getKmp();
		};
	}

}
