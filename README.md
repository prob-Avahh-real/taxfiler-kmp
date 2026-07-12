# 个税助手 (TaxFiler)

个人所得税综合所得汇算清缴应用 — 收入录入、专项附加扣除、汇算清缴。

## 技术架构

```
taxfiler-kmp/
├── composeApp/              # Compose Multiplatform UI
│   └── src/commonMain/
│       └── kotlin/com/taxfiler/ui/screens/
│           ├── HomeScreen          # 首页
│           ├── IncomeScreen        # 收入录入
│           ├── DeductionScreen     # 专项附加扣除
│           └── PlaceholderScreen   # 占位
├── shared/                  # KMP 共享模块
│   └── src/commonMain/
│       └── kotlin/com/taxfiler/
│           ├── model/       # IncomeRecord, DeductionRecord, TaxProfile 等
│           ├── data/        # HTTP 客户端
│           └── engine/      # TaxEngine 汇算清缴引擎
├── iosApp/                  # iOS 原生入口
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 构建命令

```bash
./gradlew build                # 构建全部
./gradlew :shared:build        # 共享模块
./gradlew :composeApp:build    # UI 模块
```

## 测试命令

```bash
./gradlew :shared:check        # 共享模块测试
./gradlew :composeApp:check    # UI 模块测试
./gradlew allTests             # 全平台测试
```
