package com.butter.wastesorter.data

data class Trash(
    val name: String,
    val code: Int,
    val ways: ArrayList<String>,
    val tips: ArrayList<String>
) {
    companion object {
        const val PLASTIC: Int = 0
        const val PAPER: Int = 1
        const val CARDBOARD: Int = 2
        const val CAN: Int = 3
        const val GLASS: Int = 4
        const val METAL: Int = 5
    }
}
