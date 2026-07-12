package com.taxfiler.model

/// 个税常量
object TaxConstants {
    const val ANNUAL_BASIC_DEDUCTION = 60000.0
    const val MONTHLY_BASIC_DEDUCTION = 5000.0

    /// 七级超额累进税率表（年度综合所得）
    val taxBrackets = listOf(
        TaxBracket(0, 36000, 0.03, 0),
        TaxBracket(36000, 144000, 0.10, 2520),
        TaxBracket(144000, 300000, 0.20, 16920),
        TaxBracket(300000, 420000, 0.25, 31920),
        TaxBracket(420000, 660000, 0.30, 52920),
        TaxBracket(660000, 960000, 0.35, 85920),
        TaxBracket(960000, Double.MAX_VALUE, 0.45, 181920)
    )

    /// 专项附加扣除额度（每人/月）
    val deductionLimits = mapOf(
        "children_education" to 2000.0,
        "continuing_education" to 400.0,
        "serious_illness" to 80000.0,  // 上限/年
        "housing_loan" to 1000.0,
        "housing_rent" to 1500.0,
        "elderly_support" to 2000.0,
        "infant_care" to 2000.0
    )

    val incomeTypes = listOf("工资、薪金所得", "劳务报酬所得", "稿酬所得", "特许权使用费所得")
    const val TAX_FILING_DEADLINE = "06-30"
}

data class TaxBracket(
    val from: Double,
    val to: Double,
    val rate: Double,
    val quickDeduction: Double
)
