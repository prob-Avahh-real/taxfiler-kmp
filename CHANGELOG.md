# CHANGELOG

## [1.0.0] — 2025-06-07

### Added
- Initial KMP project for Chinese individual income tax (个税) filing
- Data models: IncomeRecord, DeductionRecord, TaxProfile, TaxCalculation
- TaxConstants with 7-bracket progressive tax table, deduction limits
- TaxEngine implementing comprehensive income tax calculation
  - Correct expense deductions for salary/labor/royalty/copyright
  - Taxable income calculation with all deduction types
  - Input validation for income entries
- HomeScreen with quick estimation form and calculation result card
- IncomeScreen for managing income records
- DeductionScreen with 7 special deduction categories (专项附加扣除)
- Navigation: 4 tabs (首页/收入/扣除/历史)
- CI/CD pipeline for Android and iOS

### Fixed
- TaxEngine `expense()` for "royalty" income type: was mixing expense and taxable logic, now correctly uses 800/std deduction or 20%
- APP.md corrected from generic "LaGe" template

### Security
- INTERNET + ACCESS_NETWORK_STATE declared
- HttpClient configured (no auth implemented — offline-first for calculation)
- TaxEngine validates income inputs (no negative amounts, max 100M cap)
