package com.acmebank.account_manager.api

import com.acmebank.account_manager.api.dto.TransferRequestDto
import com.acmebank.account_manager.persistence.CustomersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

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

    /**
     * This whole method is one transaction to ensure data integrity.
     */
    @PostMapping("customer/{id}/transfer")
    @Transactional
    fun initiateTransfer(@PathVariable("id") customerId: Int, @RequestBody requestBody: TransferRequestDto): String {

        // Validate
        val clientRecord = (customersRepository.findById(customerId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Customer $customerId not found"))

        val recipientRecord = (customersRepository.findById(requestBody.recipientId)
            ?: throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unknown recipient"))

        if (requestBody.amount < BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transfer amount")
        }

        if (clientRecord.balance < requestBody.amount) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient funds")
        }

        // Execute the transfer
        clientRecord.balance -= requestBody.amount
        recipientRecord.balance += requestBody.amount
        customersRepository.save(clientRecord)
        customersRepository.save(recipientRecord)

        return "OK"
    }
}
