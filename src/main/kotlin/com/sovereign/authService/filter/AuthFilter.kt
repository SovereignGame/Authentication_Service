package com.sovereign.authService.filters


import java.io.IOException

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import com.sovereign.authService.controller.SessionController
import com.sovereign.authService.model.Session
import com.sovereign.authService.repository.AccountRepository
import com.sovereign.authService.repository.SessionRepository
import org.joda.time.Instant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*


@Component
@Order(1)
class AuthFilter : Filter {

    val dev = true //If dev is true the authentication filter is ignored

    @Autowired
    val sessionRepository: SessionRepository? = null


    private val skipPaths = Arrays.asList(
            "/api/login",
            "/api/resumeSession",
            "/config.js",
            "/api/createNewAccount",
            "/api/dbcontroller",
            "/favicon.ico"
    )

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig?) {
        LOG.info("Initializing filter :{}", this)
    }

    fun isAuthenticated(token: String, username: String): Boolean {
        val sessionOpt: Optional<Session> = sessionRepository!!.findById(token)
        val session: Session? = if (sessionOpt.isPresent) sessionOpt.get() else null
        when {
            session == null -> return false
            session.username != username -> return false
            session.expires < Instant.now().millis -> {
                sessionRepository!!.delete(session)
                return false
            }
        }
        sessionRepository!!.save(session!!.copy(expires = Instant.now().millis + 8 * 3600000))
        return true
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (dev) {
            chain.doFilter(request, response)
            return
        }

        val req = request as HttpServletRequest
        LOG.info("Starting Transaction for req :{}", req.requestURI)
        val path = req.requestURI

        if (!skipPaths.contains(path)) {
            val auth = req.getHeader("Auth")
            val username = req.getHeader("Username")
            if (auth != null && !auth.isEmpty()) {
                if (isAuthenticated(auth, username)) {
                    chain.doFilter(request, response)
                } else {
                    LOG.info("Not authenticated")
                    (response as HttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.")

                }
            } else {
                LOG.info("No header set")
                (response as HttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.")

            }
        } else {
            chain.doFilter(request, response)
        }
        LOG.info("Committing Transaction for req :{}", req.requestURI)
    }

    override fun destroy() {
        LOG.warn("Destructing filter :{}", this)
    }

    companion object {

        private val LOG = LoggerFactory.getLogger(AuthFilter::class.java)
    }


}