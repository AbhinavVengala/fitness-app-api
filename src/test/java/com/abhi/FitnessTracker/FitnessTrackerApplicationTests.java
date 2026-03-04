package com.abhi.FitnessTracker;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Full Spring context test - disabled because it requires a live MongoDB connection.
 * Unit tests in service/controller layers provide adequate coverage without a running DB.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Disabled("Requires live MongoDB connection - run integration tests separately")
class FitnessTrackerApplicationTests {

	@Test
	void contextLoads() {
	}

}




