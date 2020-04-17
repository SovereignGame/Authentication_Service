package com.sovereign.authService.controller

import com.sovereign.authService.model.Account
import com.sovereign.authService.model.object_parameters.LoginData
import com.sovereign.authService.repository.AccountRepository
import com.sovereign.authService.service.LoginService
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource
import javax.validation.Valid


@RestController
@RequestMapping("/authservice/account")
class AccountController(private val accountRepository: AccountRepository) {

    private val LOG = LoggerFactory.getLogger(AccountController::class.java)

    @Resource
    var env: Environment? = null

    @GetMapping("/test")
    fun testFun():Int{
        LOG.info("Went thorugh")
        return Integer.parseInt(env?.getProperty("local.server.port"))
    }


    //TODO add validation and response
    @PostMapping("/createNewAccount")
    fun createNewAccount(@Valid @RequestBody loginData: LoginData): ResponseEntity<Boolean> {
        if (accountRepository.findById(loginData.username).isPresent) {
            return ResponseEntity(false, HttpStatus.CONFLICT)
        }
        //Account details; Username, pw and salt
        val salt = LoginService.generateSalt()
        val password = LoginService.getHashedPassword(loginData.password, salt)
        val newAccount = Account(username = loginData.username, password = password, salt = salt)
        accountRepository.save(newAccount)

        return ResponseEntity(true, HttpStatus.OK)
    }

    @GetMapping("/accounts")
    fun getUserById(@RequestHeader("Username") username: String): ResponseEntity<Account> {
        return accountRepository.findById(username).map { user ->
            ResponseEntity.ok(user)
        }.orElse(ResponseEntity.notFound().build<Account>())
    }

    @PutMapping("/accounts/changepassword")
    fun changePassword(@RequestHeader("Username") username: String,
                       @Valid @RequestBody info: Array<String>): Any {
        val oldPassword: String = info[0]
        val account: Account = accountRepository.findById(username).get()
        val oldSalt: String = account.salt
        val oldHashedPassword: String = LoginService.getHashedPassword(password = oldPassword, salt = oldSalt)
        if (account.password == oldHashedPassword) {
            val newPassword: String = info[1]
            val salt: String = LoginService.generateSalt()
            val password: String = LoginService.getHashedPassword(password = newPassword, salt = salt)
            val newAccount: Account = account.copy(password = password, salt = salt)
            accountRepository.save(newAccount)
            return ResponseEntity<Any>(HttpStatus.OK)
        }
        return ResponseEntity<Any>(HttpStatus.CONFLICT)
    }



}