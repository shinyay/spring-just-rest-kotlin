package io.pivotal.syanagihara.blog

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogApplicationTests(@Autowired val testRestTemplate: TestRestTemplate) {

    @Test
    @DisplayName("ステータスコード確認")
    fun `Assert status code`() {
        println(">> Assert blog page title, content and status code")
        val entity = testRestTemplate.getForEntity<String>("/blog")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    @DisplayName("ページ内容確認")
    fun `Assert blog page`() {
        println(">> Assert blog page title, content and status code")
        val entity = testRestTemplate.getForEntity<String>("/blog")
        assertThat(entity.body).contains("<h1>Blog</h1>")
    }

}
