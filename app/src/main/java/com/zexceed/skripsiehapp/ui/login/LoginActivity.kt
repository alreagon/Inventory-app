package com.zexceed.skripsiehapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.databinding.ActivityLoginBinding
import com.zexceed.skripsiehapp.ui.register.RegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel.currentUserLiveData.observe(this) {
            if (it != null) {
                finish()
            }
        }
        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                if (email == "" || password == "") {
                    Toast.makeText(
                        this@LoginActivity,
                        "Lengkapi data",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val message = loginViewModel.login(email, password)
                        lifecycleScope.launch(Dispatchers.Main) {
                            message.observe(this@LoginActivity) {
                                when (it) {
                                    "SUCCESS" -> {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    "LOADING" -> {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Loading",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else -> {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            it,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
//            tvToRegister.setOnClickListener {
//                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
//                finish()
//            }
        }
    }
}