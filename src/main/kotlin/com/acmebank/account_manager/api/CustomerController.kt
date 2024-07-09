package com.acmebank.account_manager.api

import com.acmebank.account_manager.api.dto.TransferRequestDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/app")
class CustomerController {

    @GetMapping("customer/{id}/balance")
    fun getCustomerBalance(@PathVariable("id") customerId: Int): String {
        return customerId.toString()
    }

    @PostMapping("customer/{id}/transfer")
    fun initiateTransfer(@PathVariable("id") customerId: Int, @RequestBody requestBody: TransferRequestDto): String {
        return "$customerId sends ${requestBody.amount} to ${requestBody.recipientId}"
    }
}
