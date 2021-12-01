package com.butter.wastesorter.data

data class Record(
    var plastic: ArrayList<String>,
    var paper: ArrayList<String>,
    var cardboard: ArrayList<String>,
    var can: ArrayList<String>,
    var glass: ArrayList<String>,
    var metal: ArrayList<String>,
    var trash: ArrayList<String>
)