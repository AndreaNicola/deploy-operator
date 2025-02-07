package it.makeit.operator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SampleJettyApplicationTests {

	@Autowired
	private TestRestTemplate testRestTemplate;

// TODO: review test
	@Test
	public void testHome() throws Exception {

		ResponseEntity<String> responseEntity = this.testRestTemplate.getForEntity("/health", String.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("OK", responseEntity.getBody());
	}
}
