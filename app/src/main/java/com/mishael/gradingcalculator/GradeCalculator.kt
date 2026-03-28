package com.mishael.gradingcalculator

object GradeCalculator {

    // Arrow function to calculate total mark
    val calculateTotal = { caMark: Double, examMark: Double -> caMark + examMark }

    // Arrow function to assign grade
    val assignGrade = { total: Double ->
        when {
            total >= 80 -> "A"
            total >= 70 -> "B+"
            total >= 60 -> "B"
            total >= 55 -> "C+"
            total >= 50 -> "C"
            total >= 45 -> "D+"
            total >= 40 -> "D"
            else -> "F"
        }
    }

    // Arrow function to assign grade point
    val assignGradePoint = { total: Double ->
        when {
            total >= 80 -> 4.0
            total >= 70 -> 3.5
            total >= 60 -> 3.0
            total >= 55 -> 2.5
            total >= 50 -> 2.0
            total >= 45 -> 1.5
            total >= 40 -> 1.0
            else -> 0.0
        }
    }

    // Higher order function - process a list of students
    fun processStudents(students: List<Student>): List<Student> {
        return students.map { it }
    }

    // Filter - passing students (D and above)
    fun getPassingStudents(students: List<Student>): List<Student> {
        return students.filter { it.passed }
    }

    // Filter - failing students
    fun getFailingStudents(students: List<Student>): List<Student> {
        return students.filter { !it.passed }
    }

    // Higher order function - group students by grade
    fun groupByGrade(students: List<Student>): Map<String, List<Student>> {
        return students.groupBy { it.grade }
    }

    // Higher order function - calculate class average
    fun classAverage(students: List<Student>): Double {
        return if (students.isEmpty()) 0.0
        else students.map { it.totalMark }.average()
    }

    // Higher order function - calculate average GPA
    fun averageGPA(students: List<Student>): Double {
        return if (students.isEmpty()) 0.0
        else students.map { it.gradePoint }.average()
    }
}