package com.sovereign.authService

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class AuthServiceApplication : SpringBootServletInitializer() {
	//private val authenticationService: AuthenticationService = AuthenticationService()
	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			SpringApplication.run(AuthServiceApplication::class.java, *args)
		}
	}
}
