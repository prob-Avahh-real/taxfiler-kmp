package com.taxfiler.model

import kotlinx.serialization.Serializable

/// 收入记录
@Serializable
data class IncomeRecord(
    val id: String,
    val userId: String,
    val taxYear: Int,
    val incomeType: String,       // salary / labor / royalty / copyright
    val payerName: String? = null,
    val grossAmount: Double,
    val taxWithheld: Double = 0.0,
    val socialSecurity: Double = 0.0,
    val otherDeductions: Double = 0.0,
    val month: Int? = null,
    val note: String? = null,
    val source: String = "manual",  // manual / ocr / import
    val createdAt: String = "",     // ISO 8601
    val updatedAt: String = ""      // ISO 8601
)

/// 专项附加扣除记录
@Serializable
data class DeductionRecord(
    val id: String,
    val userId: String,
    val taxYear: Int,
    val deductionType: String,    // children_education / continuing_education / serious_illness / housing_loan / housing_rent / elderly_support / infant_care
    val amount: Double,
    val months: Int = 12,
    val details: Map<String, String>? = null,
    val note: String? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
)

/// 纳税人税务画像
@Serializable
data class TaxProfile(
    val id: String,
    val userId: String,
    val name: String,
    val idNumber: String,         // 身份证号
    val phone: String,
    val taxYear: Int,
    val employer: String? = null,
    val hasDomicile: Boolean = true,
    val isFirstFiling: Boolean = true,
    val filingMethod: String = "standard",  // standard / self
    val createdAt: String = "",
    val updatedAt: String = ""
)

/// 汇算结果
@Serializable
data class TaxCalculation(
    val taxYear: Int,
    val totalIncome: Double,
    val totalExpenses: Double = 0.0,
    val taxExemptIncome: Double = 0.0,
    val basicDeduction: Double = 60000.0,
    val specialDeductions: Double = 0.0,
    val specialAdditionalDeductions: Double = 0.0,
    val otherDeductions: Double = 0.0,
    val donationDeductions: Double = 0.0,
    val taxableIncome: Double,
    val calculatedTax: Double,
    val taxWithheld: Double = 0.0,
    val finalTax: Double,
    val calculatedAt: String = ""  // ISO 8601
)
