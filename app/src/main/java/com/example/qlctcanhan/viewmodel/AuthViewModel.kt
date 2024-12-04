package com.example.qlctcanhan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qlctcanhan.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // Tài khoản demo
    private val demoUser = mapOf(
        "trienchill@gmail.com" to "trienchill"
    )

    fun login(email: String, password: String): LiveData<Result<FirebaseUser?>> {
        val result = MutableLiveData<Result<FirebaseUser?>>()

        // Kiểm tra tài khoản demo
        if (demoUser[email] == password) {
            result.value = Result.success(null) // Không cần FirebaseUser cho tài khoản demo
        } else {
            // Tiếp tục kiểm tra với Firebase Authentication
            repository.login(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.user?.let {
                            result.value = Result.success(it)
                        }
                    } else {
                        result.value = Result.failure(
                            task.exception ?: Exception("Đăng nhập thất bại")
                        )
                    }
                }
        }

        return result
    }
}
