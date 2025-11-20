package com.kotlin.blui.presentation.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.blui.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(private val context: Context) : ViewModel() {
    private val authRepository = AuthRepository(context)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.getProfile()

            result.onSuccess { userResponse ->
                _uiState.value = _uiState.value.copy(
                    name = userResponse.fullName,
                    email = userResponse.email,
                    dateOfBirth = userResponse.dateOfBirth ?: "",
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Gagal memuat profil"
                )
            }
        }
    }

    fun updateProfile(name: String, email: String, dateOfBirth: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.updateProfile(name, dateOfBirth, null)

            result.onSuccess { userResponse ->
                _uiState.value = _uiState.value.copy(
                    name = userResponse.fullName,
                    email = userResponse.email,
                    dateOfBirth = userResponse.dateOfBirth ?: "",
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Gagal memperbarui profil"
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = _uiState.value.copy(isLoggedOut = true)
    }
}