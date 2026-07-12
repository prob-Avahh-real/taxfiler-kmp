package com.taxfiler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun DeductionScreen() {
    val deductionItems = listOf(
        DeductionItem("children_education", "子女教育", "每孩 ¥2,000/月", Color(0xFF8B5CF6)),
        DeductionItem("continuing_education", "继续教育", "¥400/月或 ¥3,600/年", Color(0xFF06B6D4)),
        DeductionItem("serious_illness", "大病医疗", "自付超 ¥15,000 部分，上限 ¥80,000", Color(0xFFEC4899)),
        DeductionItem("housing_loan", "住房贷款利息", "¥1,000/月", Color(0xFFF97316)),
        DeductionItem("housing_rent", "住房租金", "¥800-1,500/月", Color(0xFF14B8A6)),
        DeductionItem("elderly_support", "赡养老人", "¥2,000/月（独生）", Color(0xFF6366F1)),
        DeductionItem("infant_care", "婴幼儿照护", "每孩 ¥2,000/月", Color(0xFFF43F5E))
    )

    var checkedItems by remember { mutableStateOf(setOf<String>()) }
    var details by remember { mutableStateOf<Map<String, Int>>(emptyMap()) } // type -> count

    Column(Modifier.padding(16.dp)) {
        Text("专项附加扣除", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("选择适用的扣除项，系统自动计算扣除总额", color = Color.Gray, fontSize = 14.sp)

        Spacer(Modifier.height(16.dp))

        deductionItems.forEach { item ->
            Card(Modifier.fillMaxWidth().padding(bottom = 8.dp), shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(40.dp), shape = RoundedCornerShape(10.dp), color = item.color.copy(alpha = 0.12f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(item.label.first().toString(), color = item.color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(item.description, color = Color.Gray, fontSize = 13.sp)
                    }
                    Checkbox(
                        checked = item.key in checkedItems,
                        onCheckedChange = { selected ->
                            checkedItems = if (selected) checkedItems + item.key else checkedItems - item.key
                        }
                    )
                }

                // 数量选择（子女教育/婴幼儿照护可多孩）
                if (item.key in checkedItems && item.key in listOf("children_education", "infant_care", "elderly_support")) {
                    Row(Modifier.padding(start = 68.dp, bottom = 12.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("人数：", color = Color.Gray, fontSize = 13.sp)
                        val count = details[item.key] ?: 1
                        listOf(1, 2, 3, 4).forEach { n ->
                            FilterChip(
                                selected = count == n,
                                onClick = { details = details + (item.key to n) },
                                label = { Text("$n") },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 扣除总额
        val totalDeductions = checkedItems.sumOf { key ->
            val base = (TaxConstants.deductionLimits[key] ?: 0.0)
            val count = details[key] ?: 1
            if (key == "serious_illness") base else base * count * 12
        }

        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(20.dp)) {
                Text("专项附加扣除合计", color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text("¥ ${"%.0f".format(totalDeductions)} / 年", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text("¥ ${"%.0f".format(totalDeductions / 12)} / 月", color = Color.Gray, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

data class DeductionItem(val key: String, val label: String, val description: String, val color: Color)
