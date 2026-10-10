package com.example.data.model

data class AllahName(
    val number: Int,
    val arabic: String,
    val transliteration: String,
    val meaningBn: String,
    val explanationBn: String,
    val spiritualBenefitBn: String
)

data class QuranVerseOfTheDay(
    val surahNameBn: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val arabicText: String,
    val pronunciationBn: String,
    val meaningBn: String,
    val reflectionBn: String
)

data class ZakatCalculationResult(
    val cashInHandAndBank: Double = 0.0,
    val goldValue: Double = 0.0,
    val silverValue: Double = 0.0,
    val businessGoodsValue: Double = 0.0,
    val otherInvestments: Double = 0.0,
    val immediateDebtsOwed: Double = 0.0,
    val nisabThresholdSilverBdt: Double = 95000.0, // Standard Silver Nisab (~52.5 tola silver in BDT)
    val nisabThresholdGoldBdt: Double = 850000.0  // Standard Gold Nisab (~7.5 tola gold in BDT)
) {
    val totalAssets: Double
        get() = cashInHandAndBank + goldValue + silverValue + businessGoodsValue + otherInvestments

    val netZakatEligibleWealth: Double
        get() = (totalAssets - immediateDebtsOwed).coerceAtLeast(0.0)

    val isZakatObligatory: Boolean
        get() = netZakatEligibleWealth >= nisabThresholdSilverBdt

    val zakatPayableBdt: Double
        get() = if (isZakatObligatory) netZakatEligibleWealth * 0.025 else 0.0
}
