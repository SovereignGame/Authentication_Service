package com.sovereign.authService

import com.sovereign.authService.controller.AccountController
import com.sovereign.authService.controller.SessionController
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired
import com.sovereign.authService.model.object_parameters.LoginData
import org.junit.BeforeClass
import org.springframework.http.HttpStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.jupiter.api.BeforeAll



@SpringBootTest
class AuthServiceApplicationTests {

	private val TESTUSER :LoginData = LoginData("TestUsername","TestPassword")

	@Autowired
	private val sessionController: SessionController? = null

	@Autowired
	private val accountController: AccountController? = null


	@Test
	fun contextLoads() {
		assertThat(sessionController).isNotNull
		assertThat(accountController).isNotNull
	}

	@BeforeAll
	fun setup(){
		accountController!!.createNewAccount(TESTUSER)
	}

	@Test
	fun doLoginTest(){
		assert(sessionController!!.doLogin(TESTUSER).statusCode== HttpStatus.OK)
		assert(sessionController.doLogin(LoginData("notUsername","notPassword")).statusCode!=HttpStatus.OK)

	}

}
