package com.swaps.swap_cards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class SwapCardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwapCardsApplication.class, args);
	}

}
