package com.kotlin.blui.presentation.auth.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.blui.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val dateOfBirth: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(context: Context) : ViewModel() {
    private val authRepository = AuthRepository(context)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun onDateOfBirthChange(dateOfBirth: String) {
        _uiState.value = _uiState.value.copy(dateOfBirth = dateOfBirth, errorMessage = null)
    }

    fun register() {
        val name = _uiState.value.name.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword
        val dateOfBirth = _uiState.value.dateOfBirth

        if (!validateInput(name, email, password, confirmPassword, dateOfBirth)) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.register(name, email, password, dateOfBirth)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = exception.message ?: "Registrasi gagal. Silakan coba lagi."
                )
            }
        }
    }

    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        dateOfBirth: String
    ): Boolean {
        return when {
            name.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Nama tidak boleh kosong")
                false
            }
            name.length < 3 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Nama minimal 3 karakter")
                false
            }
            email.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Email tidak boleh kosong")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Format email tidak valid")
                false
            }
            password.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Password tidak boleh kosong")
                false
            }
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Password minimal 6 karakter")
                false
            }
            confirmPassword.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Konfirmasi password tidak boleh kosong")
                false
            }
            password != confirmPassword -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Password tidak cocok")
                false
            }
            dateOfBirth.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Tanggal lahir tidak boleh kosong")
                false
            }
            else -> true
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
