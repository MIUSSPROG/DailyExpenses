package com.example.dailyexpenses.utils

import okhttp3.internal.and
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Hasher {
    companion object {
        fun hash(input: String): String {
            val bytes = input.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }

//        salt: String
        fun getSecurePassword(password: String): String? {
//                val saltByteArray = salt.toByteArray()
                var generatedPassword: String? = null
                try {
                    val md = MessageDigest.getInstance("SHA-256")
//                    md.update(saltByteArray)
                    val bytes = md.digest(password.toByteArray())
                    val sb = StringBuilder()
                    for (i in bytes.indices) {
                        sb.append(((bytes[i].and(0xff)) + 0x100).toString(16).substring(1))
                    }
                    generatedPassword = sb.toString()
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
                return generatedPassword
//                return getSecurePassword(generatedPassword!!, salt, iter+1)
        }

    }
}