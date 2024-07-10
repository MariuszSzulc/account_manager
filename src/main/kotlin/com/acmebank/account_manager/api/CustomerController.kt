package com.acmebank.account_manager.api

import com.acmebank.account_manager.api.dto.TransferRequestDto
import com.acmebank.account_manager.persistence.CustomersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/app")
class CustomerController(
    @Autowired
    val customersRepository: CustomersRepository
) {

    @GetMapping("customer/{id}/balance")
    fun getCustomerBalance(@PathVariable("id") customerId: Int): String {
        // We don't need to validate {customerId}, spring does that automatically

        val customer = customersRepository.findById(customerId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Customer $customerId not found")

        return customer.balance.toPlainString()
    }

    @PostMapping("customer/{id}/transfer")
    fun initiateTransfer(@PathVariable("id") customerId: Int, @RequestBody requestBody: TransferRequestDto): String {
        return "$customerId sends ${requestBody.amount} to ${requestBody.recipientId}"
    }
}
