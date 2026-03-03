package models

enum class Grade(val value: Double, val range: IntRange) {
    A(4.0, 80..100),
    B_PLUS(3.5, 70..79),
    B(3.0, 60..69),
    C_PLUS(2.5, 55..59),
    C(2.0, 50..54),
    D_PLUS(1.5, 45..49),
    D(1.0, 40..44),
    F(0.0, 0..39);

    companion object {
        fun fromScore(score: Int): Grade {
            return values().find { score in it.range } ?: F
        }

        fun fromTotal(ca: Int, exam: Int): Grade {
            val total = ca + exam
            return fromScore(total)
        }
    }
}

data class CourseGrade(
    val course: Course,
    val caMark: Int, // Over 30
    val examMark: Int // Over 70
) {
    val total: Int get() = caMark + examMark
    val grade: Grade get() = Grade.fromTotal(caMark, examMark)
    val gradePoint: Double get() = grade.value

    fun isValid(): Boolean {
        return caMark in 0..30 && examMark in 0..70 && total in 0..100
    }
}