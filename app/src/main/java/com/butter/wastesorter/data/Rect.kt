package com.butter.wastesorter.data

import java.io.Serializable

/*
(x1, y1) = Left Top
(x2, y2) = Right Bottom
 */
data class Rect(val x1: Float, val y1: Float, val x2: Float, val y2: Float) : Serializable
