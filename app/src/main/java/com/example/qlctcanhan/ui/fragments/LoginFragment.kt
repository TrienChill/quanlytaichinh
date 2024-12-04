package com.example.qlctcanhan.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.qlctcanhan.R
import com.example.qlctcanhan.databinding.FragmentLoginBinding
import com.example.qlctcanhan.utils.SecurityUtils
import com.example.qlctcanhan.viewmodel.AuthViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email == "trienchill@gmail.com" && password == "trienchill") {
                // Bỏ qua kiểm tra mật khẩu mạnh cho tài khoản demo
                Toast.makeText(requireContext(), "Đăng nhập bằng tài khoản demo thành công!", Toast.LENGTH_SHORT).show()
                navigateToMainApp()
            } else if (SecurityUtils.isValidEmail(email) && SecurityUtils.isStrongPassword(password)) {
                // Kiểm tra đăng nhập với Firebase
                authViewModel.login(email, password).observe(viewLifecycleOwner) { result ->
                    result.onSuccess { user ->
                        navigateToMainApp()
                    }.onFailure { exception ->
                        showError(exception.message)
                    }
                }
            } else {
                // Hiển thị thông báo lỗi khi không hợp lệ
                showValidationError()
            }
        }


        return binding.root
    }

    private fun navigateToMainApp() {
        // Điều hướng tới màn hình chính của ứng dụng (cần setup navigation)
        findNavController().navigate(R.id.action_loginFragment_to_transactionsFragment)
    }

    private fun showError(message: String?) {
        // Hiển thị thông báo lỗi
        message?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showValidationError() {
        // Hiển thị thông báo lỗi khi email hoặc mật khẩu không hợp lệ
        Toast.makeText(requireContext(), "Email hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show()
    }
}
