# TaxFiler (个税助手) — KMP 项目

## 项目概述
个人所得税综合所得汇算清缴应用，Kotlin Multiplatform + Compose Multiplatform。

## 构建命令
- 全量构建: `./gradlew build`
- 共享模块: `./gradlew :shared:build`
- UI 模块: `./gradlew :composeApp:build`
- 全平台测试: `./gradlew allTests`
- 清理: `./gradlew clean`

## 文件组织
```
composeApp/src/commonMain/  → Compose UI (screens 含收入/扣除录入)
shared/src/commonMain/      → 共享业务逻辑 (model, data, engine)
  model/      → IncomeRecord, DeductionRecord, TaxProfile, TaxCalculation
  data/       → HttpClient
  engine/     → TaxEngine 汇算引擎 (计算+校验)
iosApp/                     → iOS 原生入口
```

## 关键配置
- 项目名: `TaxFiler` (settings.gradle.kts)
- 模块: `:composeApp`, `:shared`
- 序列化: kotlinx.serialization
- HTTP: Ktor Client
- UI: Compose Multiplatform + Material3
- 核心: TaxEngine 实现七级超额累进税率计算
- 税务常量: 起征点 60000/年, 7 档税率, 7 类专项附加扣除额度
