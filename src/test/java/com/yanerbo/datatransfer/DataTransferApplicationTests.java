package com.yanerbo.datatransfer;


import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataTransferApplicationTests {

	@Test
	void contextLoads() {
		
		for(int i=0;i<10;i++) {
			System.out.println(System.currentTimeMillis());
		}
		
		
	}

}
