package com.taxfiler.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaxEngineTest {

    @Test
    fun calculate_salary_only() {
        val result = TaxEngine.calculate(
            taxYear = 2025,
            incomes = listOf(IncomeInput(120_000.0, "salary", taxWithheld = 5_000.0))
        )
        // taxableIncome = 120000 - 60000 = 60000
        // tax = 60000 * 0.10 - 2520 = 3480
        // final = 3480 - 5000 = -1520 (refund)
        assertEquals(60_000.0, result.taxableIncome, 0.01)
        assertEquals(3_480.0, result.calculatedTax, 0.01)
        assertEquals(-1_520.0, result.finalTax, 0.01)
        assertTrue(result.isRefund)
    }

    @Test
    fun calculate_labor_with_deduction() {
        val result = TaxEngine.calculate(
            taxYear = 2025,
            incomes = listOf(IncomeInput(5_000.0, "labor"))
        )
        // taxableAmount = 5000 * 0.80 = 4000
        // taxableIncome = 4000 - 60000 = 0 (floor at 0)
        assertEquals(0.0, result.taxableIncome, 0.01)
        assertEquals(0.0, result.calculatedTax, 0.01)
    }

    @Test
    fun validate_catches_negative_amount() {
        val result = TaxEngine.validate(
            listOf(IncomeInput(-100.0, "salary"))
        )
        assertTrue(result.isValid == false, "Should reject negative income")
    }

    @Test
    fun validate_passes_clean_input() {
        val result = TaxEngine.validate(
            listOf(IncomeInput(50_000.0, "salary", taxWithheld = 2_000.0))
        )
        assertTrue(result.isValid, "Should accept valid income")
    }
}
