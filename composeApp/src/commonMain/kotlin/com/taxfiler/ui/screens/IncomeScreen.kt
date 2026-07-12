package com.taxfiler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taxfiler.model.TaxConstants

@Composable
fun IncomeScreen() {
    var incomes by remember { mutableStateOf(listOf<IncomeEntry>()) }
    var showAdd by remember { mutableStateOf(true) }
    var grossAmount by remember { mutableStateOf("") }
    var incomeType by remember { mutableStateOf("salary") }
    var taxWithheld by remember { mutableStateOf("") }
    var payerName by remember { mutableStateOf("") }

    fun addIncome() {
        val amount = grossAmount.toDoubleOrNull() ?: return
        val withheld = taxWithheld.toDoubleOrNull() ?: 0.0
        incomes = incomes + IncomeEntry(
            id = "INC${incomes.size + 1}",
            payerName = payerName.ifEmpty { null },
            grossAmount = amount, incomeType = incomeType,
            taxWithheld = withheld
        )
        grossAmount = ""; taxWithheld = ""; payerName = ""
        showAdd = false
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("收入管理", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("添加和管理您的综合所得收入记录", color = Color.Gray, fontSize = 14.sp)

        Spacer(Modifier.height(20.dp))

        // 已添加的收入
        incomes.forEach { entry ->
            Card(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        entry.payerName?.let { Text(it, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                        Text(typeLabel(entry.incomeType), color = Color.Gray, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("¥ ${"%.0f".format(entry.grossAmount)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        Text("预扣 ¥ ${"%.0f".format(entry.taxWithheld)}", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }

        // 添加表单
        if (showAdd) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("添加收入记录", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = payerName, onValueChange = { payerName = it }, label = { Text("付款单位") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = grossAmount, onValueChange = { grossAmount = it }, label = { Text("收入金额（元）") }, leadingIcon = { Icon(Icons.Outlined.Payments, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(8.dp))

                    // 收入类型
                    Row(Modifier.fillMaxWidth()) {
                        val types = listOf("salary", "labor", "royalty", "copyright")
                        TaxConstants.incomeTypes.forEachIndexed { i, label ->
                            FilterChip(selected = incomeType == types[i], onClick = { incomeType = types[i] }, label = { Text(label, fontSize = 12.sp) }, modifier = Modifier.padding(end = 4.dp))
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = taxWithheld, onValueChange = { taxWithheld = it }, label = { Text("已预缴税额（元）") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { showAdd = false }, modifier = Modifier.weight(1f)) { Text("取消") }
                        Button(onClick = { addIncome() }, modifier = Modifier.weight(1f), enabled = grossAmount.toDoubleOrNull() != null) { Text("添加") }
                    }
                }
            }
        }

        if (!showAdd) {
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { showAdd = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("添加收入记录")
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

data class IncomeEntry(
    val id: String,
    val payerName: String?,
    val grossAmount: Double,
    val incomeType: String,
    val taxWithheld: Double
)

fun typeLabel(type: String): String = when (type) {
    "salary" -> "工资薪金"
    "labor" -> "劳务报酬"
    "royalty" -> "稿酬所得"
    "copyright" -> "特许权使用费"
    else -> type
}
