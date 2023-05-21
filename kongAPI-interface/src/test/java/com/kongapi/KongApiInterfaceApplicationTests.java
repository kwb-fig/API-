package com.kongapi;

import com.kongapi.kongapiclientsdk.client.kongApiClient;
import com.kongapi.kongapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class KongApiInterfaceApplicationTests {

	@Resource
	kongApiClient kongApiClient;

	@Test
	void contextLoads() {
		String result1 = kongApiClient.getNameByGet("kong");
		System.out.println(result1);
		User user = new User();
		user.setName("yu");
		String result2 = kongApiClient.getUsernameByPost(user);
		System.out.println(result2);
	}

}
