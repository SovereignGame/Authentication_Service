package com.sovereign.authService.controller

import com.sovereign.authService.model.object_parameters.LoginData
import com.sovereign.authService.model.Session
import com.sovereign.authService.repository.AccountRepository
import com.sovereign.authService.repository.SessionRepository
import com.sovereign.authService.service.LoginService
import com.sovereign.authService.service.SessionService
import org.joda.time.Instant
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import java.util.*
import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/authservice/session")
class SessionController(private val sessionService: SessionService, private val sessionRepository: SessionRepository,
                        private val accountRepository: AccountRepository) {


    @Transactional
    @PostMapping("/login")
    fun doLogin(@Valid @RequestBody loginData: LoginData): ResponseEntity<String> {
        val account = accountRepository.getByUsername(loginData.username)
        if (account != null && LoginService.getHashedPassword(loginData.password, account.salt) == account.password) {
            sessionRepository.deleteAllByUsername(loginData.username)
            val headers = HttpHeaders()
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(createSession(loginData.username))

        }
        return ResponseEntity("Wrong combination of Username and Password", HttpStatus.UNAUTHORIZED)

    }

    @GetMapping("/checkAuthentication/{username}")
    fun checkAuthentication(@PathVariable("username") username: String, @RequestParam auth: String): Boolean {
        //session[0] = token, session[1] = username
        return sessionService.isAuthenticated(auth, username)
    }

    fun flushSessions(username: String) {
        sessionRepository.deleteAllByExpiresLessThan(Instant.now().millis)
        sessionRepository.deleteAllByUsername(username)
    }

    //TODO Test
    fun createSession(username: String): String {
        val token = UUID.randomUUID().toString()
        sessionRepository.save(Session(username = username, token = token, expires = Instant.now().plus(8 * 3600000).millis)) //8 hour sessions
        return token
    }
}