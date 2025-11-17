package com.kotlin.blui.presentation.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.blui.R
import com.kotlin.blui.R.drawable
import com.kotlin.blui.presentation.component.DatePickerField
import com.kotlin.blui.presentation.component.FormField
import com.kotlin.blui.presentation.component.PrimaryButton
import com.kotlin.blui.ui.theme.BluiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterClick: (name: String, email: String, dateOfBirth: String, password: String) -> Unit = { _, _, _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background decoration di kiri atas
            Image(
                painter = painterResource(id = drawable.regist_decoration),
                contentDescription = "Auth Decoration",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(width = 360.dp, height = 300.dp),
                contentScale = ContentScale.FillWidth
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Name Form Field
                FormField(
                    label = "Nama Lengkap",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Masukkan nama lengkap",
                    leadingIcon = Icons.Default.Person2,
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Form Field
                FormField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Masukkan email",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date of Birth Field with DatePicker
                DatePickerField(
                    label = "Tanggal Lahir",
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    placeholder = "Pilih tanggal lahir",
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Form Field
                FormField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Masukkan password",
                    leadingIcon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Register Button
                PrimaryButton(
                    text = "Register",
                    onClick = { onRegisterClick(name, email, dateOfBirth, password) },
                    modifier = Modifier.widthIn(max = 600.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Login Link
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Sudah punya akun? ",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    BluiTheme {
        RegisterScreen()
    }
}