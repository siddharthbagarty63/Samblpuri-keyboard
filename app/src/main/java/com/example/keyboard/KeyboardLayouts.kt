package com.example.keyboard

object KeyboardLayouts {

    // Quick Sambalpuri greetings and frequent Kosali words
    val sambalpuriQuickPhrases = listOf(
        "ଜୁହାର" to "Johar (Greeting)",
        "ଭଲ ଅଛୁ ?" to "How are you?",
        "କେନ୍ତା ଅଛ ?" to "How do you do?",
        "ମୋର ସଙ୍ଗାତ" to "My friend",
        "ଜୟ ମାଁ ସମଲେଇ" to "Jai Maa Samalei",
        "ନୂଆଁଖାଇ ଜୁହାର" to "Nuakhai Johar",
        "ହଇହୋ" to "Yes surely",
        "ବାବୁ ରେ" to "Affectionate",
        "ଧନ୍ୟବାଦ" to "Thank you",
        "ସତେ କାଏଁ ?" to "Really?"
    )

    // Odia / Sambalpuri Matras and common vowel signs
    val odiaMatras = listOf("ା", "ି", "ୀ", "ୁ", "ୂ", "ୃ", "େ", "ୈ", "ୋ", "ୌ", "୍", "ଂ", "ଃ", "ଁ")

    // Odia Vowels (Independent)
    val odiaVowels = listOf(
        listOf("ଅ", "ଆ", "ଇ", "ଈ", "ଉ", "ଊ", "ଋ"),
        listOf("ଏ", "ଐ", "ଓ", "ଔ", "ଂ", "ଃ", "ଁ")
    )

    // Odia Consonants rows with Odia Danda '।' full-stop
    val odiaConsonantRows = listOf(
        listOf("କ", "ଖ", "ଗ", "ଘ", "ଙ", "ଚ", "ଛ", "ଜ", "ଝ", "ଞ"),
        listOf("ଟ", "ଠ", "ଡ", "ଢ", "ଣ", "ତ", "ଥ", "ଦ", "ଧ", "ନ"),
        listOf("ପ", "ଫ", "ବ", "ଭ", "ମ", "ଯ", "ର", "ଲ", "ଳ", "ଵ"),
        listOf("ଶ", "ଷ", "ସ", "ହ", "ୟ", "ଡ଼", "ଢ଼", "କ୍ଷ", "ଜ୍ଞ", "।")
    )

    // English QWERTY
    val englishRow1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val englishRow2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val englishRow3 = listOf("z", "x", "c", "v", "b", "n", "m")

    // Numbers & Symbols
    val odiaNumbers = listOf("୦", "୧", "୨", "୩", "୪", "୫", "୬", "୭", "୮", "୯")
    val standardNumbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val symbolRow1 = listOf("।", "॥", "?", "!", ",", ".", "@", "#", "$", "%")
    val symbolRow2 = listOf("&", "*", "(", ")", "-", "+", "=", "/", ":", ";")
    val symbolRow3 = listOf("\"", "'", "~", "_", "<", ">", "{", "}", "[", "]")
}
