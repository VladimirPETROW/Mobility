package com.mobility;

import com.mobility.service.DemandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MobilityApplicationTests {

	@Autowired
	DemandService demandService;

	@Test
	void distributeDemands() {
		demandService.distribute();
	}

}
