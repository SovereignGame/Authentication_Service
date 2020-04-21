package com.sovereign.authService.controller

import com.sovereign.authService.model.Account
import com.sovereign.authService.model.object_parameters.LoginData
import com.sovereign.authService.repository.AccountRepository
import com.sovereign.authService.service.AccountService
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct
import javax.annotation.Resource
import javax.validation.Valid


@RestController
@RequestMapping("/authenticationService/account")
class AccountController(private val  accountService: AccountService, private val accountRepository: AccountRepository) {

    private val LOG = LoggerFactory.getLogger(AccountController::class.java)

    @Resource
    var env: Environment? = null

    @GetMapping("/test")
    fun testFun():Int{
        LOG.info("Went thorugh")
        return Integer.parseInt(env?.getProperty("local.server.port"))
    }


    @PostMapping("/createNewAccount")
    fun createNewAccount(@Valid @RequestBody loginData: LoginData): ResponseEntity<Boolean> {
        if (accountService.createNewAccount(loginData)) {
            return ResponseEntity(true, HttpStatus.OK)
        }
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
        if(accountService.changePassword(username,info)) return ResponseEntity<Any>(HttpStatus.OK)
        return ResponseEntity<Any>(HttpStatus.CONFLICT)
    }

    @PostConstruct
    private fun postConstruct() {
        LOG.info("Testuser created: "+ createNewAccount(LoginData("TestUser","password")))
    }



}