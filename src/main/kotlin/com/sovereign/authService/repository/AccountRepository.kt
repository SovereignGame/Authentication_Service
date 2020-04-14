package com.sovereign.authService.repository

import com.sovereign.authService.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, String>{
    fun getByUsername(username: String):Account?
}

