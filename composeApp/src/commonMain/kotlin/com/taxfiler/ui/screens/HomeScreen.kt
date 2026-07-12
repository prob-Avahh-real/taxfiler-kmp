package com.taxfiler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.taxfiler.engine.IncomeInput
import com.taxfiler.engine.TaxEngine
import com.taxfiler.engine.TaxCalculationResult
import com.taxfiler.model.TaxConstants
import kotlin.math.abs

@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()
    var showCalculation by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<TaxCalculationResult?>(null) }
    var incomeTotal by remember { mutableStateOf("") }
    var incomeType by remember { mutableStateOf("salary") }
    var taxWithheld by remember { mutableStateOf("") }
    var deductions by remember { mutableStateOf("") }

    Column(Modifier.verticalScroll(scrollState).padding(16.dp)) {
        Text("个税汇算", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(if (TaxConstants.TAX_FILING_DEADLINE >= "06-30") "2025年度汇算进行中" else "2025年度汇算已截止", color = Color.Gray, fontSize = 14.sp)

        Spacer(Modifier.height(20.dp))

        // 快速计算卡片
        if (!showCalculation || result == null) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(20.dp)) {
                    Text("快速估算", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = incomeTotal, onValueChange = { incomeTotal = it }, label = { Text("年度综合所得（元）") }, leadingIcon = { Icon(Icons.Outlined.Payments, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(8.dp))

                    // 收入类型选择
                    Row(Modifier.fillMaxWidth()) {
                        TaxConstants.incomeTypes.forEachIndexed { i, label ->
                            val types = listOf("salary", "labor", "royalty", "copyright")
                            FilterChip(
                                selected = incomeType == types[i],
                                onClick = { incomeType = types[i] },
                                label = { Text(label, fontSize = 12.sp) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = taxWithheld, onValueChange = { taxWithheld = it }, label = { Text("已预缴税额（元）") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = deductions, onValueChange = { deductions = it }, label = { Text("专项附加扣除合计（元）") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        val inc = incomeTotal.toDoubleOrNull() ?: return@Button
                        val withheld = taxWithheld.toDoubleOrNull() ?: 0.0
                        val ded = deductions.toDoubleOrNull() ?: 0.0
                        result = TaxEngine.calculate(
                            taxYear = 2025,
                            incomes = listOf(IncomeInput(inc, incomeType, withheld)),
                            totalSpecialAdditionalDeductions = ded
                        )
                        showCalculation = true
                    }, modifier = Modifier.fillMaxWidth()) { Text("开始计算") }
                }
            }
        }

        // 计算结果
        result?.let { res ->
            Spacer(Modifier.height(16.dp))
            CalculationResultCard(res)

            Spacer(Modifier.height(16.dp))
            Button(onClick = { showCalculation = false; result = null }, modifier = Modifier.fillMaxWidth()) { Text("重新计算") }
        }

        // 快捷入口
        if (!showCalculation) {
            Spacer(Modifier.height(24.dp))
            Text("快捷服务", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickServiceCard(Icons.Outlined.Upload, "OCR\n导入票据", Color(0xFF8B5CF6), Modifier.weight(1f))
                QuickServiceCard(Icons.Outlined.Article, "查看\n申报记录", Color(0xFF06B6D4), Modifier.weight(1f))
                QuickServiceCard(Icons.Outlined.Info, "扣除\n政策指南", Color(0xFFF97316), Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun CalculationResultCard(result: TaxCalculationResult) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(20.dp)) {
            // 结果横幅
            val (label, color) = when {
                result.isRefund -> "应退税" to Color(0xFF059669)
                result.isOwing -> "应补税" to Color(0xFFDC2626)
                else -> "不退不补" to Color(0xFF64748B)
            }

            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.1f)) {
                Column(Modifier.padding(16.dp)) {
                    Text(label, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("¥ ${"%.2f".format(abs(result.finalTax))}", color = color, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // 明细
            CalculationRow("综合所得收入额", result.totalTaxableIncomeAmount)
            CalculationRow("减除费用（起征点）", -result.basicDeduction)
            if (result.specialDeductions > 0) CalculationRow("专项扣除（社保公积金）", -result.specialDeductions)
            if (result.specialAdditionalDeductions > 0) CalculationRow("专项附加扣除", -result.specialAdditionalDeductions)
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            CalculationRow("应纳税所得额", result.taxableIncome, bold = true)
            CalculationRow("应纳税额（${"%.0f".format(result.appliedRate * 100)}%）", result.calculatedTax, bold = true)
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            CalculationRow("已预缴税额", result.taxWithheld)
            CalculationRow("应补/退税额", result.finalTax, bold = true, color = color)
        }
    }
}

@Composable
fun CalculationRow(label: String, value: Double, bold: Boolean = false, color: Color = Color.Unspecified) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            if (value >= 0) "¥ ${"%.2f".format(value)}" else "-¥ ${"%.2f".format(-value)}",
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (color != Color.Unspecified) color else Color.Unspecified
        )
    }
}

@Composable
fun QuickServiceCard(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.W500)
        }
    }
}
