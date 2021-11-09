package com.butter.wastesorter.data

data class Trash(val name: String, val code: Int) {
    companion object {
        const val PLASTIC: Int = 1
        const val PAPER: Int = 2
        const val CARDBOARD: Int = 3
        const val CAN: Int = 4
        const val GLASS: Int = 5
        const val METAL: Int = 6
    }
}
