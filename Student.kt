package models

data class Student(
    val name: String,
    val matricle: String,
    val email: String,
    val results: MutableList<CourseGrade> = mutableListOf()
) {
    // Calculate GPA for all courses
    fun calculateGPA(): Double {
        if (results.isEmpty()) return 0.0

        val totalPoints = results.sumOf { it.gradePoint }
        return totalPoints / results.size
    }

    // Get result for specific course
    fun getCourseResult(course: Course): CourseGrade? {
        return results.find { it.course == course }
    }

    // Check if student is enrolled in all courses
    fun hasAllCourses(): Boolean {
        return Course.values().all { course ->
            results.any { it.course == course }
        }
    }

    // Calculate total score across all courses
    fun totalScores(): Map<Course, Int> {
        return results.associate { it.course to it.total }
    }

    // Get formatted transcript
    fun getTranscript(): String {
        val sb = StringBuilder()
        sb.appendLine("=".repeat(50))
        sb.appendLine("STUDENT TRANSCRIPT")
        sb.appendLine("=".repeat(50))
        sb.appendLine("Name: $name")
        sb.appendLine("Matricle: $matricle")
        sb.appendLine("Email: $email")
        sb.appendLine("-".repeat(50))
        sb.appendLine(String.format("%-30s %-10s %-10s %-10s %-10s", "Course", "CA(30)", "Exam(70)", "Total", "Grade"))
        sb.appendLine("-".repeat(50))

        results.sortedBy { it.course }.forEach { grade ->
            sb.appendLine(String.format("%-30s %-10d %-10d %-10d %-10s",
                grade.course.fullName,
                grade.caMark,
                grade.examMark,
                grade.total,
                grade.grade.name
            ))
        }

        sb.appendLine("-".repeat(50))
        sb.appendLine("Semester GPA: %.2f / 4.0".format(calculateGPA()))
        sb.appendLine("=".repeat(50))

        return sb.toString()
    }
}