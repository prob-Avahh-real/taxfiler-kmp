package com.taxfiler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

enum class TaxTab { HOME, INCOME, DEDUCTION, HISTORY }

@Composable
fun TaxFilerApp() {
    var selectedTab by remember { mutableStateOf(TaxTab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == TaxTab.HOME,
                    onClick = { selectedTab = TaxTab.HOME },
                    icon = { Icon(Icons.Filled.Home, "首页") },
                    label = { Text("首页") }
                )
                NavigationBarItem(
                    selected = selectedTab == TaxTab.INCOME,
                    onClick = { selectedTab = TaxTab.INCOME },
                    icon = { Icon(Icons.Outlined.ReceiptLong, "收入") },
                    label = { Text("收入") }
                )
                NavigationBarItem(
                    selected = selectedTab == TaxTab.DEDUCTION,
                    onClick = { selectedTab = TaxTab.DEDUCTION },
                    icon = { Icon(Icons.Outlined.Savings, "扣除") },
                    label = { Text("扣除") }
                )
                NavigationBarItem(
                    selected = selectedTab == TaxTab.HISTORY,
                    onClick = { selectedTab = TaxTab.HISTORY },
                    icon = { Icon(Icons.Outlined.History, "历史") },
                    label = { Text("历史") }
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (selectedTab) {
                TaxTab.HOME -> HomeScreen()
                TaxTab.INCOME -> IncomeScreen()
                TaxTab.DEDUCTION -> DeductionScreen()
                TaxTab.HISTORY -> PlaceholderScreen("申报历史", "暂无历史记录")
            }
        }
    }
}
