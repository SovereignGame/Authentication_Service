package com.sovereign.authService.service

import com.sovereign.authService.model.Session
import com.sovereign.authService.repository.SessionRepository
import org.joda.time.Instant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class SessionService(private var sessionRepository: SessionRepository) {

    fun isAuthenticated(token: String, username: String): Boolean {
        val sessionOpt: Optional<Session> = sessionRepository.findById(token)
        val session: Session? = if (sessionOpt.isPresent) sessionOpt.get() else null
        when {
            session == null -> return false
            session.username != username -> return false
        }
        return true
    }
}