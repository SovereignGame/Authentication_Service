package com.sovereign.authService.service

import com.sovereign.authService.model.Account
import com.sovereign.authService.model.object_parameters.LoginData
import com.sovereign.authService.repository.AccountRepository
import java.security.spec.InvalidKeySpecException
import java.security.NoSuchAlgorithmException
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory
import org.apache.commons.codec.binary.Hex
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import java.io.UnsupportedEncodingException
import java.security.SecureRandom
import javax.validation.Valid
import kotlin.experimental.and

@Service
class AccountService (private val accountRepository: AccountRepository){

    fun createNewAccount(loginData: LoginData): Boolean {
        if (accountRepository.findById(loginData.username).isPresent) {
            return false
    }
        //Account details; Username, pw and salt
        val salt = generateSalt()
        val password = getHashedPassword(loginData.password, salt)
        val newAccount = Account(username = loginData.username, password = password, salt = salt)
        accountRepository.save(newAccount)
        return true
    }

    fun changePassword(username: String, info: Array<String>): Boolean {
        val oldPassword: String = info[0]
        val account: Account = accountRepository.findById(username).get()
        val oldSalt: String = account.salt
        val oldHashedPassword: String = AccountService.getHashedPassword(password = oldPassword, salt = oldSalt)
        if (account.password == oldHashedPassword) {
            val newPassword: String = info[1]
            val salt: String = AccountService.generateSalt()
            val password: String = AccountService.getHashedPassword(password = newPassword, salt = salt)
            val newAccount: Account = account.copy(password = password, salt = salt)
            accountRepository.save(newAccount)
            return true
        }
        return false
    }


     companion object {
         private const val SALT_LENGTH = 128
         private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

         fun getHashedPassword(password: String, salt: String): String {
             val iterations = 14534 // 10 000 to 20 000 iterations will take 0.5 to 1 sec 2.6 GHz CPU and 16 GB RAM
             val keyLength = 512
             val passwordChars = password.toCharArray()
             val saltBytes = salt.toByteArray()

             try {
                 val hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength)
                 return Hex.encodeHexString(hashedBytes)
             } catch (e: UnsupportedEncodingException) {
                 throw RuntimeException(e)
             }
         }

         private fun hashPassword(password: CharArray, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray {

             try {
                 val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                 val spec = PBEKeySpec(password, salt, iterations, keyLength)
                 val key = skf.generateSecret(spec)
                 return key.encoded
             } catch (e: NoSuchAlgorithmException) {
                 throw RuntimeException(e)
             } catch (e: InvalidKeySpecException) {
                 throw RuntimeException(e)
             }
         }


         fun generateSalt(): String {
             val random = SecureRandom()
             val bytes = ByteArray(SALT_LENGTH)
             random.nextBytes(bytes)

             return (0 until bytes.size)
                     .map { i ->
                         charPool.get((bytes[i] and 0xFF.toByte() and (charPool.size - 1).toByte()).toInt())
                     }.joinToString("")
         }
     }
}