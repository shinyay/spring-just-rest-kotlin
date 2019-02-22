package io.pivotal.syanagihara.simple

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RESTInterfaceTests(@Autowired val testRestTemplate: TestRestTemplate) {

    @Test
    @DisplayName("ステータスコード確認")
    fun `Assert status code`() {
        println(">> Assert status code for API app")
        val entity = testRestTemplate.getForEntity<String>("/messages")
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
    }
}
