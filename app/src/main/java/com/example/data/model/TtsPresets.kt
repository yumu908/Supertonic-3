package com.example.data.model

import java.util.Locale

object TtsPresets {

    val SPEAKERS = listOf(
        SpeakerPersona("alex", "Alex", "Male", "Deep Narrator", pitchMultiplier = 0.88f, speedMultiplier = 0.96f, listOf("male", "en_us_male_1")),
        SpeakerPersona("james", "James", "Male", "News & Tech", pitchMultiplier = 1.00f, speedMultiplier = 1.05f, listOf("male", "en_us_male_2")),
        SpeakerPersona("robert", "Robert", "Male", "Rich Baritone", pitchMultiplier = 0.80f, speedMultiplier = 0.92f, listOf("male", "deep")),
        SpeakerPersona("sam", "Sam", "Neutral", "Conversational", pitchMultiplier = 1.02f, speedMultiplier = 1.00f, listOf("neutral", "conversational")),
        SpeakerPersona("daniel", "Daniel", "Male", "Articulate Orator", pitchMultiplier = 0.94f, speedMultiplier = 0.98f, listOf("male", "british", "en_gb")),
        SpeakerPersona("sarah", "Sarah", "Female", "Warm Storyteller", pitchMultiplier = 1.18f, speedMultiplier = 0.98f, listOf("female", "en_us_female_1")),
        SpeakerPersona("lily", "Lily", "Female", "Bright & Youthful", pitchMultiplier = 1.28f, speedMultiplier = 1.05f, listOf("female", "young", "en_us_female_2")),
        SpeakerPersona("jessica", "Jessica", "Female", "Crisp Executive", pitchMultiplier = 1.10f, speedMultiplier = 1.02f, listOf("female", "professional")),
        SpeakerPersona("olivia", "Olivia", "Female", "Melodic & Expressive", pitchMultiplier = 1.22f, speedMultiplier = 0.95f, listOf("female", "expressive")),
        SpeakerPersona("emily", "Emily", "Female", "Gentle Audiobook", pitchMultiplier = 1.06f, speedMultiplier = 0.92f, listOf("female", "soft", "calm"))
    )

    val LANGUAGES = listOf(
        LanguageItem("en-US", "English", "English (US)", "🇺🇸", Locale.US),
        LanguageItem("zh-CN", "Chinese", "中文 (普通话)", "🇨🇳", Locale.SIMPLIFIED_CHINESE),
        LanguageItem("ja-JP", "Japanese", "日本語", "🇯🇵", Locale.JAPAN),
        LanguageItem("ko-KR", "Korean", "한국어", "🇰🇷", Locale.KOREA),
        LanguageItem("es-ES", "Spanish", "Español", "🇪🇸", Locale("es", "ES")),
        LanguageItem("fr-FR", "French", "Français", "🇫🇷", Locale.FRANCE),
        LanguageItem("de-DE", "German", "Deutsch", "🇩🇪", Locale.GERMANY),
        LanguageItem("it-IT", "Italian", "Italiano", "🇮🇹", Locale.ITALY),
        LanguageItem("pt-BR", "Portuguese", "Português", "🇧🇷", Locale("pt", "BR")),
        LanguageItem("ru-RU", "Russian", "Русский", "🇷🇺", Locale("ru", "RU")),
        LanguageItem("ar-SA", "Arabic", "العربية", "🇸🇦", Locale("ar", "SA")),
        LanguageItem("hi-IN", "Hindi", "हिन्दी", "🇮🇳", Locale("hi", "IN")),
        LanguageItem("nl-NL", "Dutch", "Nederlands", "🇳🇱", Locale("nl", "NL")),
        LanguageItem("pl-PL", "Polish", "Polski", "🇵🇱", Locale("pl", "PL")),
        LanguageItem("tr-TR", "Turkish", "Türkçe", "🇹🇷", Locale("tr", "TR")),
        LanguageItem("vi-VN", "Vietnamese", "Tiếng Việt", "🇻🇳", Locale("vi", "VN")),
        LanguageItem("id-ID", "Indonesian", "Bahasa Indonesia", "🇮🇩", Locale("id", "ID")),
        LanguageItem("th-TH", "Thai", "ไทย", "🇹🇭", Locale("th", "TH")),
        LanguageItem("sv-SE", "Swedish", "Svenska", "🇸🇪", Locale("sv", "SE")),
        LanguageItem("da-DK", "Danish", "Dansk", "🇩🇰", Locale("da", "DK")),
        LanguageItem("fi-FI", "Finnish", "Suomi", "🇫🇮", Locale("fi", "FI")),
        LanguageItem("no-NO", "Norwegian", "Norsk", "🇳🇴", Locale("no", "NO")),
        LanguageItem("cs-CZ", "Czech", "Čeština", "🇨🇿", Locale("cs", "CZ")),
        LanguageItem("el-GR", "Greek", "Ελληνικά", "🇬🇷", Locale("el", "GR")),
        LanguageItem("ro-RO", "Romanian", "Română", "🇷🇴", Locale("ro", "RO")),
        LanguageItem("hu-HU", "Hungarian", "Magyar", "🇭🇺", Locale("hu", "HU")),
        LanguageItem("uk-UA", "Ukrainian", "Українська", "🇺🇦", Locale("uk", "UA")),
        LanguageItem("sk-SK", "Slovak", "Slovenčina", "🇸🇰", Locale("sk", "SK")),
        LanguageItem("bg-BG", "Bulgarian", "Български", "🇧🇬", Locale("bg", "BG")),
        LanguageItem("hr-HR", "Croatian", "Hrvatski", "🇭🇷", Locale("hr", "HR")),
        LanguageItem("lt-LT", "Lithuanian", "Lietuvių", "🇱🇹", Locale("lt", "LT")),
        LanguageItem("lv-LV", "Latvian", "Latviešu", "🇱🇻", Locale("lv", "LV")),
        LanguageItem("et-EE", "Estonian", "Eesti", "🇪🇪", Locale("et", "EE"))
    )

    val QUALITY_OPTIONS = listOf(
        QualityStep(4, "4 Steps", "Ultra-fast low-latency speech synthesis"),
        QualityStep(8, "8 Steps", "High fidelity balanced studio sound"),
        QualityStep(16, "16 Steps", "Maximum neural detail & resonance")
    )

    const val DEFAULT_SCRIPT_TEXT = """Hello. Today, I would like to talk about one of the long-standing philosophical debates: "Which came first, the chicken or the egg?" This question may seem like simple curiosity, but in fact, it is a topic that allows us to deeply explore how we understand life, evolution, cause, and effect.

First, let's define the question a little more precisely. We often wonder about the order of the egg and the chicken. Which came first, the egg or the chicken? Here, the "egg" could mean a general "egg" or specifically a "chicken egg." This distinction determines the direction of the discussion.

From a philosophical perspective, this problem stimulates reflection on cause and effect. What started first? Causes produce effects, and effects in turn shape causes. Following this logic, we discover a cycle. Eggs exist because there are chickens, and chickens come from eggs. They depend on each other, making it difficult to find a starting point.

However, from a scientific perspective, we can offer a slightly different answer. Modern biology and evolutionary theory show that the chicken we know today is the result of long evolution. Chickens are a species of birds whose ancestors gradually changed over tens of millions of years to take their current form. In other words, today's chicken stands on a continuum of ancestors slightly different from past chickens.

Here comes an important clue. There was an egg laid by a chicken ancestor that was not yet a true chicken. A mutation or genetic change occurred in that egg, resulting in the first chicken as we know it today. From this perspective, if we define the egg as a "chicken-laid egg," then the egg came first. There was an egg in which the changes necessary to become a chicken occurred, and the first chicken hatched from it.

While the evolutionary answer is like this, philosophical discussions are still valid. Because cause and effect cannot be completely separated, it requires circular thinking. This question is not mere curiosity; it is a door that makes us ponder causality, the flow of time, and the origin of life.

Today's conclusion can be summarized as follows. From a philosophical, logical, and mythological perspective, it is a circular question. From a scientific and evolutionary perspective, the egg came first—the egg in which the genetic changes necessary to become a chicken occurred. So the answer depends on the criteria and perspective we choose.

Lastly, let's think about the meaning of this debate. The order of the egg and chicken is not just intellectual curiosity. Through this question, we can reflect on the essence of life, the process of change, and our perception of time."""

    const val DEFAULT_QUOTE_TEXT = """"You have power over your mind - not outside events. Realize this, and you will find strength.
The happiness of your life depends upon the quality of your thoughts.
Waste no more time arguing about what a good man should be. Be one."
— Marcus Aurelius, Meditations"""

    const val DEFAULT_PARAGRAPH_TEXT = """The cosmos is within us. We are made of star-stuff. We are a way for the cosmos to know itself. Across billions of years, the elements forged in dying stars converged to produce consciousness, emotion, and curiosity. Every spoken word resonates with the ancient rhythm of life seeking to understand its own origin."""

    const val DEFAULT_FREEFORM_TEXT = """Welcome to Supertonic TTS. Type, paste, or compose your text here to transform it into rich, natural speech right on your device."""

    fun getSampleFor(type: TextPresetType, languageCode: String): String {
        val isZh = languageCode.startsWith("zh")
        return when (type) {
            TextPresetType.FREEFORM -> if (isZh) {
                "欢迎使用 Supertonic 本地语音合成。在此输入任意文字，即可离线生成清晰自然的人声配音。"
            } else {
                DEFAULT_FREEFORM_TEXT
            }
            TextPresetType.QUOTE -> if (isZh) {
                "“生活不是等待暴风雨过去，而是学会在风雨中跳舞。心若向阳，无谓悲伤。”\n—— 思想随笔"
            } else {
                DEFAULT_QUOTE_TEXT
            }
            TextPresetType.PARAGRAPH -> if (isZh) {
                "星空深邃而广袤，我们皆由星尘所铸。数十亿年的岁月演化，赋予了我们感知生命与世界的能力。声音是思想的桥梁，每一句真挚的话语，都是心与心之间最温暖的共鸣。"
            } else {
                DEFAULT_PARAGRAPH_TEXT
            }
            TextPresetType.SCRIPT -> if (isZh) {
                """大家好。今天我想聊一聊长久以来的哲学探讨：“先有鸡还是先有蛋？”这个问题看似简单，实则带领我们深入探讨生命演化与因果法则。

从生物进化论的角度来看，在真正的现代鸡出现之前，其鸟类祖先在产下的蛋中发生基因突变，孕育了第一只真正意义上的鸡。因此从生物学上看，蛋先于鸡存在。然而从哲学因果辩证来看，二者相辅相成，互为因果。这不仅是一场知识探求，更是对生命与时间流转的深刻沉思。"""
            } else {
                DEFAULT_SCRIPT_TEXT
            }
        }
    }
}
