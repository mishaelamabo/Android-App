package com.mishael.gradingcalculator

object StudentRepository {
    private val students = mutableListOf<Student>()

    fun addStudent(student: Student) {
        students.add(student)
    }

    fun getStudents(): List<Student> = students.toList()

    fun clearStudents() {
        students.clear()
    }

    fun addStudents(newStudents: List<Student>) {
        students.addAll(newStudents)
    }
}