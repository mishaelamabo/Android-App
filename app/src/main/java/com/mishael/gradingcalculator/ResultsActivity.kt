package com.mishael.gradingcalculator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ResultsActivity : AppCompatActivity() {

    private lateinit var adapter: StudentAdapter

    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                exportManualResults(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { 
            @Suppress("DEPRECATION")
            onBackPressed() 
        }

        val rvStudents = findViewById<RecyclerView>(R.id.rvStudents)
        val btnClear = findViewById<Button>(R.id.btnClearAll)
        val btnExport = findViewById<Button>(R.id.btnExportResults)

        adapter = StudentAdapter(StudentRepository.getStudents())
        rvStudents.layoutManager = LinearLayoutManager(this)
        rvStudents.adapter = adapter

        btnClear.setOnClickListener {
            StudentRepository.clearStudents()
            adapter.updateData(emptyOfList())
        }

        btnExport.setOnClickListener {
            if (StudentRepository.getStudents().isEmpty()) {
                Toast.makeText(this, "No results to export", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_TITLE, "Manual_Grading_Results.xlsx")
            }
            createFileLauncher.launch(intent)
        }
    }

    private fun exportManualResults(uri: Uri) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Results")
            
            val header = sheet.createRow(0)
            val columns = arrayOf("Student Name", "Matricle", "Course", "CA Mark", "Exam Mark", "Total Mark", "Grade", "Grade Point")
            columns.forEachIndexed { i, title ->
                header.createCell(i).setCellValue(title)
            }

            val students = StudentRepository.getStudents()
            students.forEachIndexed { index, student ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(student.name)
                row.createCell(1).setCellValue(student.matricle)
                row.createCell(2).setCellValue(student.course)
                row.createCell(3).setCellValue(student.caMark)
                row.createCell(4).setCellValue(student.examMark)
                row.createCell(5).setCellValue(student.totalMark)
                row.createCell(6).setCellValue(student.grade)
                row.createCell(7).setCellValue(student.gradePoint)
            }

            contentResolver.openOutputStream(uri)?.use { outputStream ->
                workbook.write(outputStream)
            }

            val path = getPathFromUri(uri)
            Toast.makeText(this, "Results saved to: $path", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to export results", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPathFromUri(uri: Uri): String {
        return if (DocumentsContract.isDocumentUri(this, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            if (split.size > 1) {
                "${split[0]}/${split[1]}"
            } else {
                docId
            }
        } else {
            uri.path ?: "Internal Storage"
        }
    }

    private fun emptyOfList(): List<Student> = emptyList()

    override fun onResume() {
        super.onResume()
        adapter.updateData(StudentRepository.getStudents())
    }
}