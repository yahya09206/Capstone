package com.yahya.thehorn.models

import androidx.annotation.DrawableRes

data class HomeMenu(
    @DrawableRes
    val icon: Int,
    val title: String
)

data class NumberData(
    val number: Int = 0,
    val translation: String = "",
    val sound: String = ""
)

data class PhraseData(
    val original: String = "",
    val sound: String = "",
    val translation: String = ""
)

data class FoodData(
        val image: String = "",
        val name: String = "",
        val sound: String = "",
        val translation: String = ""
)