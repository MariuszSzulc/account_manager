package com.acmebank.account_manager.api

import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `balance should return 200 for real customer`() {
        val customerId = 12345678

        mvc.perform(MockMvcRequestBuilders.get("/app/customer/$customerId/balance"))
            .andExpect(status().isOk)
            .andExpect(content().string(equalTo("1000000.00")))
    }

    @Test
    fun `balance should return 404 when customer not found`() {
        val customerId = 1

        mvc.perform(MockMvcRequestBuilders.get("/app/customer/$customerId/balance"))
            .andExpect(status().isNotFound)
    }
}