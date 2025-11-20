package com.kotlin.blui.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kotlin.blui.presentation.component.BottomNavigationBar
import com.kotlin.blui.presentation.home.HomeScreen
import com.kotlin.blui.presentation.home.HomeScreenViewModel
import com.kotlin.blui.presentation.profile.ProfileScreen

@Composable
fun MainScreen(
    onNavigateToTransaction: (String) -> Unit = {},
    onNavigateToDetail: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val homeViewModel = remember { HomeScreenViewModel(context) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                onAddTransactionClick = {
                    onNavigateToTransaction("add")
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedItem) {
                0 -> HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToTransaction = { onNavigateToTransaction("add") }
                )
                2 -> ProfileScreen(
                    onLogout = onNavigateToLogin
                )
            }
        }
    }
}
