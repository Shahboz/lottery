package com.example.lottery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class LotteryApplicationTests {

	private final LotteryApplication lotteryApplication;

	@Autowired
	LotteryApplicationTests(LotteryApplication lotteryApplication) {
		this.lotteryApplication = lotteryApplication;
	}

	@Test
	void contextLoads() {
		assertNotNull(lotteryApplication);
	}

}