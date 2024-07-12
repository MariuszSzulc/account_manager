package com.acmebank.account_manager.api

import com.acmebank.account_manager.api.dto.TransferRequestDto
import com.acmebank.account_manager.persistence.CustomersRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var customersRepository: CustomersRepository

    @Test
    @Transactional
    fun `balance should return 200 for real customer`() {
        val customerId = 12345678

        mvc.perform(get("/app/customer/$customerId/balance"))
            .andExpect(status().isOk)
            .andExpect(content().string(equalTo("1000000.00")))
    }

    @Test
    @Transactional
    fun `balance should return 404 when customer not found`() {
        val customerId = 1

        mvc.perform(get("/app/customer/$customerId/balance"))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun `transfer request should move money around`() {
        val customerId = 12345678
        val transferRequest = TransferRequestDto(amount = BigDecimal.valueOf(128), recipientId = 88888888)

        mvc.perform(
            post("/app/customer/$customerId/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest))
        ).andExpect(status().isOk)

        assertThat(customersRepository.findById(customerId)?.balance).isEqualTo(BigDecimal("999872.00"))
        assertThat(customersRepository.findById(transferRequest.recipientId)?.balance).isEqualTo(BigDecimal("1000128.00"))
    }

    @Test
    @Transactional
    fun `transfer request should handle fractions`() {
        val customerId = 12345678
        val transferRequest = TransferRequestDto(amount = BigDecimal.valueOf(128.75), recipientId = 88888888)

        mvc.perform(
            post("/app/customer/$customerId/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest))
        ).andExpect(status().isOk)

        assertThat(customersRepository.findById(customerId)?.balance).isEqualTo(BigDecimal("999871.25"))
        assertThat(customersRepository.findById(transferRequest.recipientId)?.balance).isEqualTo(BigDecimal("1000128.75"))
    }

    @Test
    @Transactional
    fun `transfer request should fail over insufficient funds`() {
        val customerId = 12345678
        val transferRequest = TransferRequestDto(amount = BigDecimal.valueOf(2000000.00), recipientId = 88888888)

        mvc.perform(
            post("/app/customer/$customerId/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest))
        ).andExpect(status().isUnprocessableEntity)
    }

    @Test
    @Transactional
    fun `transfer request amount must be positive`() {
        val customerId = 12345678
        val transferRequest = TransferRequestDto(amount = BigDecimal.valueOf(-15.00), recipientId = 88888888)

        mvc.perform(
            post("/app/customer/$customerId/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest))
        ).andExpect(status().isBadRequest)
    }

    @Test
    @Transactional
    fun `transfer request recipient account must exist`() {
        val customerId = 12345678
        val transferRequest = TransferRequestDto(amount = BigDecimal.valueOf(10.00), recipientId = 666)

        mvc.perform(
            post("/app/customer/$customerId/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest))
        ).andExpect(status().isUnprocessableEntity)
    }
}