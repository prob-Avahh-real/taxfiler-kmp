package com.taxfiler.engine

import com.taxfiler.model.TaxConstants
import kotlin.math.max

/// 收入输入项
data class IncomeInput(
    val grossAmount: Double,
    val incomeType: String,  // salary / labor / royalty / copyright
    val taxWithheld: Double = 0.0,
    val socialSecurity: Double = 0.0
)

/// 汇算结果
data class TaxCalculationResult(
    val taxYear: Int,
    val totalIncome: Double,
    val totalTaxableIncomeAmount: Double,
    val totalExpenses: Double,
    val basicDeduction: Double = TaxConstants.ANNUAL_BASIC_DEDUCTION,
    val specialDeductions: Double = 0.0,
    val specialAdditionalDeductions: Double = 0.0,
    val otherDeductions: Double = 0.0,
    val donationDeductions: Double = 0.0,
    val taxableIncome: Double,
    val calculatedTax: Double,
    val taxWithheld: Double = 0.0,
    val finalTax: Double
) {
    val isOwing: Boolean get() = finalTax > 0.01
    val isRefund: Boolean get() = finalTax < -0.01
    val isBalanced: Boolean get() = kotlin.math.abs(finalTax) < 0.01

    val appliedRate: Double get() {
        for (bracket in TaxConstants.taxBrackets) {
            if (taxableIncome <= bracket.to || bracket.to == Double.MAX_VALUE) {
                return bracket.rate
            }
        }
        return 0.0
    }
}

sealed class ValidationResult {
    abstract val isValid: Boolean
    abstract val errors: List<String>
    val errorMessage: String? get() = errors.firstOrNull()
}

data class Valid(override val errors: List<String> = emptyList()) : ValidationResult() {
    override val isValid = true
}

data class Invalid(override val errors: List<String>) : ValidationResult() {
    override val isValid = false
}

/// 个人所得税综合所得汇算清缴引擎
///
/// 公式：
///   应纳税所得额 = 综合所得收入额 - 60000 - 专项扣除 - 专项附加扣除 - 其他扣除 - 捐赠
///   (其中综合所得收入额 = Σ 每类收入 × 扣除比例)
///   应纳税额 = 应纳税所得额 × 适用税率 - 速算扣除数
///   应补/退税额 = 应纳税额 - 已预缴税额
object TaxEngine {

    private fun expense(gross: Double, type: String): Double = when (type) {
        "labor" -> if (gross <= 4000) 800.0 else gross * 0.20
        "royalty" -> if (gross <= 4000) 800.0 else gross * 0.20
        "copyright" -> if (gross <= 4000) 800.0 else gross * 0.20
        else -> 0.0
    }

    private fun taxableAmount(gross: Double, type: String): Double = when (type) {
        "salary" -> gross
        "labor" -> if (gross <= 4000) gross - 800 else gross * 0.80
        "royalty" -> (if (gross <= 4000) gross - 800 else gross * 0.80) * 0.70
        "copyright" -> if (gross <= 4000) gross - 800 else gross * 0.80
        else -> gross
    }

    private fun calculateTax(taxableIncome: Double): Double {
        if (taxableIncome <= 0) return 0.0
        for (bracket in TaxConstants.taxBrackets) {
            if (taxableIncome <= bracket.to || bracket.to == Double.MAX_VALUE) {
                return taxableIncome * bracket.rate - bracket.quickDeduction
            }
        }
        return 0.0
    }

    fun calculate(
        taxYear: Int,
        incomes: List<IncomeInput>,
        totalSpecialAdditionalDeductions: Double = 0.0,
        otherDeductions: Double = 0.0,
        donationDeductions: Double = 0.0
    ): TaxCalculationResult {
        var totalGross = 0.0
        var totalTaxableAmt = 0.0
        var totalExpenses = 0.0
        var totalTaxWithheld = 0.0
        var totalSocial = 0.0

        for (income in incomes) {
            totalGross += income.grossAmount
            totalTaxWithheld += income.taxWithheld
            totalSocial += income.socialSecurity
            totalExpenses += expense(income.grossAmount, income.incomeType)
            totalTaxableAmt += taxableAmount(income.grossAmount, income.incomeType)
        }

        val taxableIncome = max(0.0, totalTaxableAmt - TaxConstants.ANNUAL_BASIC_DEDUCTION - totalSocial - totalSpecialAdditionalDeductions - otherDeductions - donationDeductions)
        val calculatedTax = calculateTax(taxableIncome)
        val finalTax = calculatedTax - totalTaxWithheld

        return TaxCalculationResult(
            taxYear = taxYear, totalIncome = totalGross,
            totalTaxableIncomeAmount = totalTaxableAmt, totalExpenses = totalExpenses,
            specialDeductions = totalSocial,
            specialAdditionalDeductions = totalSpecialAdditionalDeductions,
            otherDeductions = otherDeductions, donationDeductions = donationDeductions,
            taxableIncome = taxableIncome, calculatedTax = calculatedTax,
            taxWithheld = totalTaxWithheld, finalTax = finalTax
        )
    }

    fun validate(incomes: List<IncomeInput>): ValidationResult {
        val errors = mutableListOf<String>()
        if (incomes.isEmpty()) return Valid()

        for ((i, inc) in incomes.withIndex()) {
            if (inc.grossAmount < 0) errors.add("收入金额不能为负数 (第${i+1}项)")
            if (inc.grossAmount > 100_000_000) errors.add("收入金额超出合理范围 (第${i+1}项)")
            if (inc.taxWithheld < 0) errors.add("预缴税额不能为负数 (第${i+1}项)")
            if (inc.taxWithheld > inc.grossAmount) errors.add("预缴税额不能超过收入金额 (第${i+1}项)")
            val validTypes = listOf("salary", "labor", "royalty", "copyright")
            if (inc.incomeType !in validTypes) errors.add("无效的收入类型: ${inc.incomeType} (第${i+1}项)")
        }

        return if (errors.isEmpty()) Valid() else Invalid(errors)
    }
}
