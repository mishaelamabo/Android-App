package com.mishael.gradingcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Update stats
        updateStats()

        // Manual Entry button
        findViewById<Button>(R.id.btnManualEntry).setOnClickListener {
            startActivity(Intent(this, ManualEntryActivity::class.java))
        }

        // Import Excel button
        findViewById<Button>(R.id.btnImportExcel).setOnClickListener {
            startActivity(Intent(this, ExcelImportActivity::class.java))
        }

        // View Results button
        findViewById<Button>(R.id.btnViewResults).setOnClickListener {
            startActivity(Intent(this, ResultsActivity::class.java))
        }
    }

    private fun updateStats() {
        val students = StudentRepository.getStudents()
        val passing = GradeCalculator.getPassingStudents(students)
        val average = GradeCalculator.classAverage(students)

        findViewById<TextView>(R.id.tvTotalStudents).text = students.size.toString()
        findViewById<TextView>(R.id.tvPassCount).text = passing.size.toString()
        findViewById<TextView>(R.id.tvAvgScore).text = "%.0f%%".format(average)
    }

    override fun onResume() {
        super.onResume()
        updateStats()
    }
}