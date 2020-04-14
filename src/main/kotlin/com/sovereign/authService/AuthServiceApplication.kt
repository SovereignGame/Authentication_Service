package com.sovereign.authService

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class AuthServiceApplication : SpringBootServletInitializer() {
	//private val authenticationService: AuthenticationService = AuthenticationService()
	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			SpringApplication.run(AuthServiceApplication::class.java, *args)
		}
	}
}
