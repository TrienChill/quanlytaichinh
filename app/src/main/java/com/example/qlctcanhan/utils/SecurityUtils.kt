package com.example.qlctcanhan.utils

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.util.Base64

object SecurityUtils {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256 // Kích thước khóa AES
    private const val IV_SIZE = 12 // Kích thước IV cho GCM

    // Tạo khóa AES
    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(KEY_SIZE)
        return keyGenerator.generateKey()
    }

    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"))
    }

    fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.matches(Regex(".*[A-Z].*")) &&
                password.matches(Regex(".*[a-z].*")) &&
                password.matches(Regex(".*\\d.*"))
    }

    fun encryptData(data: String): String {
        // Tạo khóa AES mới
        val secretKey = generateKey()

        // Khởi tạo IV ngẫu nhiên
        val iv = ByteArray(IV_SIZE).apply { SecureRandom().nextBytes(this) }
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val encryptedData = cipher.doFinal(data.toByteArray())

        // Gộp IV và dữ liệu mã hóa thành một chuỗi để lưu trữ
        val encryptedDataWithIv = iv + encryptedData

        // Trả về dữ liệu mã hóa dưới dạng Base64
        return Base64.getEncoder().encodeToString(encryptedDataWithIv)
    }

    fun decryptData(encryptedData: String): String {
        // Giải mã dữ liệu từ Base64
        val encryptedDataWithIv = Base64.getDecoder().decode(encryptedData)
        val iv = encryptedDataWithIv.copyOfRange(0, IV_SIZE)
        val encryptedBytes = encryptedDataWithIv.copyOfRange(IV_SIZE, encryptedDataWithIv.size)

        // Khởi tạo cipher AES
        val cipher = Cipher.getInstance(ALGORITHM)
        val secretKey = generateKey() // Sử dụng khóa đã mã hóa
        val spec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decryptedData = cipher.doFinal(encryptedBytes)

        return String(decryptedData)
    }
}
