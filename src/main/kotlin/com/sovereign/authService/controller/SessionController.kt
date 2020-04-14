package com.sovereign.authService.controller

import com.sovereign.authService.model.object_parameters.LoginData
import com.sovereign.authService.model.Session
import com.sovereign.authService.repository.AccountRepository
import com.sovereign.authService.repository.SessionRepository
import com.sovereign.authService.service.LoginService
import org.joda.time.Instant
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*
import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class SessionController(private val sessionRepository: SessionRepository,
                        private val accountRepository: AccountRepository) {


    @Transactional
    @PostMapping("/login")
    fun doLogin(@Valid @RequestBody loginData: LoginData): ResponseEntity<String> {
        val account = accountRepository.getByUsername(loginData.username)
        if (account==null || LoginService.getHashedPassword(loginData.password, account.salt) == account.password) {
            sessionRepository.deleteAllByUsername(loginData.username)
            val headers = HttpHeaders()
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(createSession(loginData.username))

        } else {
            sessionRepository.deleteAllByExpiresLessThan(Instant.now().millis)
            return ResponseEntity("Wrong combination of Username and Password", HttpStatus.UNAUTHORIZED)
        }

    }

    //TODO make session update.
    /*
    @PostMapping("resumeSession")
    fun resumeSession(@Valid @RequestBody session: Session):ResponseEntity<String> {
         if (isAuthenticated(session.token,session.username)) {
             return ResponseEntity.ok()body(session.token)
        } else {
            return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }
    }*/

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