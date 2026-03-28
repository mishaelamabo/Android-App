package com.mishael.gradingcalculator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelImportActivity : AppCompatActivity() {

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                importExcel(uri)
            }
        }
    }

    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                exportExcel(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excel_import)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        if (toolbar == null) {
            // If toolbar is missing in the XML, we might need to handle it, 
            // but based on previous turns it should be there or added via layout update.
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }

        findViewById<Button>(R.id.btnPickFile).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            pickFileLauncher.launch(intent)
        }

        findViewById<Button>(R.id.btnExportFile).setOnClickListener {
            if (StudentRepository.getStudents().isEmpty()) {
                Toast.makeText(this, "No student data to export", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_TITLE, "Grading_Results.xlsx")
            }
            createFileLauncher.launch(intent)
        }
    }

    private fun importExcel(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0)
                val newStudents = mutableListOf<Student>()

                // Columns expected: 0: Name, 1: Matricle, 2: CA Mark, 3: Exam Mark
                for (i in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(i) ?: continue
                    
                    val name = row.getCell(0)?.toString()?.trim() ?: ""
                    val matricle = row.getCell(1)?.toString()?.trim() ?: ""
                    
                    // Safe numeric extraction
                    val caMark = try {
                        row.getCell(2)?.numericCellValue ?: 0.0
                    } catch (e: Exception) {
                        row.getCell(2)?.toString()?.toDoubleOrNull() ?: 0.0
                    }
                    
                    val examMark = try {
                        row.getCell(3)?.numericCellValue ?: 0.0
                    } catch (e: Exception) {
                        row.getCell(3)?.toString()?.toDoubleOrNull() ?: 0.0
                    }

                    if (name.isNotEmpty()) {
                        // "Imported" is used as the default course name for imported files
                        newStudents.add(Student(name, matricle, "Imported", caMark, examMark))
                    }
                }
                StudentRepository.addStudents(newStudents)
                Toast.makeText(this, "Successfully imported ${newStudents.size} students. Calculations complete!", Toast.LENGTH_LONG).show()
                finish() // Go back to dashboard to see updated stats
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error reading Excel file. Ensure it has 4 columns: Name, Matricle, CA, Exam.", Toast.LENGTH_LONG).show()
        }
    }

    private fun exportExcel(uri: Uri) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Grading Results")
            
            // Modern Header
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
            Toast.makeText(this, "Results downloaded successfully to your device!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save the file.", Toast.LENGTH_SHORT).show()
        }
    }
}