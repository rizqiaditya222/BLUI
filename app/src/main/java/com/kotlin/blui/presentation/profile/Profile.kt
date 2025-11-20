package com.kotlin.blui.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kotlin.blui.R.drawable
import com.kotlin.blui.presentation.component.DatePickerField
import com.kotlin.blui.presentation.component.DeleteButton
import com.kotlin.blui.presentation.component.FormField
import com.kotlin.blui.presentation.component.ProfileImage
import com.kotlin.blui.ui.theme.BluiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { ProfileViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Update local state when ViewModel state changes
    LaunchedEffect(uiState) {
        if (!isEditMode) {
            name = uiState.name
            email = uiState.email
            dateOfBirth = uiState.dateOfBirth
        }
    }

    // Handle logout navigation
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
        }
    }

    // Show error message
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    Text(
                        if (isEditMode) "Simpan" else "Edit",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .clickable {
                                if (isEditMode) {
                                    // Save changes
                                    viewModel.updateProfile(name, email, dateOfBirth)
                                }
                                isEditMode = !isEditMode
                            }
                    )
                }
            )
        }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            Image(
                painter = painterResource(id = drawable.regist_decoration),
                contentDescription = "Auth Decoration",
                modifier = Modifier
                    .align(Alignment.TopStart),
                contentScale = ContentScale.Fit
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(top = 12.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.clickable(enabled = isEditMode) {
                        // TODO: Handle image selection from gallery
                    }
                ) {
                    ProfileImage()
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Name Form Field
                FormField(
                    label = "Nama Lengkap",
                    value = name,
                    onValueChange = { if (isEditMode) name = it },
                    placeholder = "Masukkan nama lengkap",
                    leadingIcon = Icons.Default.Person2,
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    enabled = isEditMode
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Form Field
                FormField(
                    label = "Email",
                    value = email,
                    onValueChange = { if (isEditMode) email = it },
                    placeholder = "Masukkan email",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    enabled = false // Email tidak bisa diubah
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date of Birth Field with DatePicker
                DatePickerField(
                    label = "Tanggal Lahir",
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    placeholder = "Pilih tanggal lahir",
                    readOnly = !isEditMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Sign Out button - only shown when not in edit mode
                if (!isEditMode) {
                    DeleteButton(
                        onClick = { viewModel.logout() },
                        text = "Sign Out"
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun RegisterProfilePreview() {
    BluiTheme {
        ProfileScreen()
    }
}