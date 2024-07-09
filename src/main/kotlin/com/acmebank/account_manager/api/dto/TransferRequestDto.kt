package com.acmebank.account_manager.api.dto

import java.math.BigDecimal

class TransferRequestDto(val amount: BigDecimal, val recipientId: Int) {
}