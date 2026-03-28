package com.mishael.gradingcalculator

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class ManualEntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val etName = findViewById<TextInputEditText>(R.id.etStudentName)
        val etMatricle = findViewById<TextInputEditText>(R.id.etMatricle)
        val etCourse = findViewById<TextInputEditText>(R.id.etCourse)
        val etCA = findViewById<TextInputEditText>(R.id.etCAMark)
        val etExam = findViewById<TextInputEditText>(R.id.etExamMark)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val matricle = etMatricle.text.toString().trim()
            val course = etCourse.text.toString().trim()
            val caString = etCA.text.toString().trim()
            val examString = etExam.text.toString().trim()

            if (name.isEmpty() || matricle.isEmpty() || course.isEmpty() || caString.isEmpty() || examString.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val caMark = caString.toDoubleOrNull() ?: 0.0
            val examMark = examString.toDoubleOrNull() ?: 0.0

            if (caMark > 30 || examMark > 70) {
                Toast.makeText(this, "CA max is 30, Exam max is 70", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val student = Student(name, matricle, course, caMark, examMark)
            StudentRepository.addStudent(student)

            Toast.makeText(this, "Student saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}