package com.mishael.gradingcalculator

data class Student(
    val name: String,
    val matricle: String,
    val course: String,
    val caMark: Double,
    val examMark: Double
) {
    val totalMark: Double = caMark + examMark

    val grade: String = when {
        totalMark >= 80 -> "A"
        totalMark >= 70 -> "B+"
        totalMark >= 60 -> "B"
        totalMark >= 55 -> "C+"
        totalMark >= 50 -> "C"
        totalMark >= 45 -> "D+"
        totalMark >= 40 -> "D"
        else -> "F"
    }

    val gradePoint: Double = when {
        totalMark >= 80 -> 4.0
        totalMark >= 70 -> 3.5
        totalMark >= 60 -> 3.0
        totalMark >= 55 -> 2.5
        totalMark >= 50 -> 2.0
        totalMark >= 45 -> 1.5
        totalMark >= 40 -> 1.0
        else -> 0.0
    }

    // Updated pass criteria: A, B+, B, C, C+ are pass grades (Total >= 50)
    val passed: Boolean = totalMark >= 50
}