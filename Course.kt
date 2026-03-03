package models

enum class Course(val code: String, val fullName: String) {
    ANDROID("CSCD 301", "Android Application Development"),
    WEB("CSCD 302", "Web Development"),
    DATABASE("CSCD 303", "Database Systems"),
    DATA_SCIENCE("CSCD 304", "Data Science");

    override fun toString(): String = "$code - $fullName"
}

data class StudentResult(
    val course: Course,
    val grade: CourseGrade,
    val semester: String = "Semester 1"
)