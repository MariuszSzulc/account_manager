package com.acmebank.account_manager.persistence

import jakarta.persistence.*
import org.springframework.data.repository.Repository
import java.math.BigDecimal

@Entity
@Table(name = "customers")
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    internal val id: Int,
    internal var balance: BigDecimal
)

interface CustomersRepository : Repository<Customer, Int> {
    fun findById(id: Int): Customer?
    fun save(customer: Customer): Customer
}