package com.sovereign.authService.service

import java.security.spec.InvalidKeySpecException
import java.security.NoSuchAlgorithmException
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory
import org.apache.commons.codec.binary.Hex
import java.io.UnsupportedEncodingException
import java.security.SecureRandom
import kotlin.experimental.and


class LoginService {

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