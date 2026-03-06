package services

import models.Student
import models.Course
import models.CourseGrade
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.swing.JOptionPane
import javax.swing.JFrame

class ExcelService(private val parentFrame: JFrame) {

    /**
     * Import students and marks from Excel file
     * Expected format:
     * | Matricle | Name | Email | Android CA | Android Exam | Web CA | Web Exam | DB CA | DB Exam | DS CA | DS Exam |
     */
    fun importFromExcel(file: File): List<Student> {
        val students = mutableListOf<Student>()
        
        try {
            FileInputStream(file).use { fis ->
                val workbook = WorkbookFactory.create(fis)
                val sheet = workbook.getSheetAt(0)
                val iterator = sheet.iterator()
                
                // Skip header row
                if (iterator.hasNext()) iterator.next()
                
                while (iterator.hasNext()) {
                    val row = iterator.next()
                    val student = createStudentFromRow(row)
                    if (student != null) {
                        students.add(student)
                    }
                }
                workbook.close()
            }
            
            JOptionPane.showMessageDialog(
                parentFrame,
                "Successfully imported ${students.size} students from Excel!",
                "Import Successful",
                JOptionPane.INFORMATION_MESSAGE
            )
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                parentFrame,
                "Error importing Excel file: ${e.message}",
                "Import Error",
                JOptionPane.ERROR_MESSAGE
            )
            e.printStackTrace()
        }
        
        return students
    }
    
    private fun createStudentFromRow(row: Row): Student? {
        return try {
            val matricle = getCellValue(row.getCell(0))
            val name = getCellValue(row.getCell(1))
            val email = getCellValue(row.getCell(2))
            
            if (matricle.isEmpty() || name.isEmpty()) return null
            
            val student = Student(name, matricle, email)
            
            // Add course grades (columns 3-10)
            addCourseGrade(student, Course.ANDROID, 
                getCellIntValue(row.getCell(3)), 
                getCellIntValue(row.getCell(4)))
            
            addCourseGrade(student, Course.WEB, 
                getCellIntValue(row.getCell(5)), 
                getCellIntValue(row.getCell(6)))
            
            addCourseGrade(student, Course.DATABASE, 
                getCellIntValue(row.getCell(7)), 
                getCellIntValue(row.getCell(8)))
            
            addCourseGrade(student, Course.DATA_SCIENCE, 
                getCellIntValue(row.getCell(9)), 
                getCellIntValue(row.getCell(10)))
            
            student
        } catch (e: Exception) {
            null
        }
    }
    
    private fun addCourseGrade(student: Student, course: Course, ca: Int?, exam: Int?) {
        if (ca != null && exam != null) {
            try {
                val courseGrade = CourseGrade(course, ca, exam)
                if (courseGrade.isValid()) {
                    student.results.add(courseGrade)
                }
            } catch (e: IllegalArgumentException) {
                // Skip invalid marks
            }
        }
    }
    
    /**
     * Export student grades to Excel file
     */
    fun exportToExcel(students: List<Student>, file: File) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Student Grades")
            
            // Create header row
            createHeaderRow(sheet)
            
            // Add student data
            students.forEachIndexed { index, student ->
                createStudentRow(sheet, index + 1, student)
            }
            
            // Auto-size columns
            for (i in 0..14) {
                sheet.autoSizeColumn(i)
            }
            
            // Write to file
            FileOutputStream(file).use { fos ->
                workbook.write(fos)
            }
            workbook.close()
            
            JOptionPane.showMessageDialog(
                parentFrame,
                "Successfully exported ${students.size} students to Excel!\nFile: ${file.absolutePath}",
                "Export Successful",
                JOptionPane.INFORMATION_MESSAGE
            )
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                parentFrame,
                "Error exporting to Excel: ${e.message}",
                "Export Error",
                JOptionPane.ERROR_MESSAGE
            )
            e.printStackTrace()
        }
    }
    
    private fun createHeaderRow(sheet: Sheet) {
        val headerRow = sheet.createRow(0)
        val headers = arrayOf(
            "S/N", "Matricle", "Name", "Email",
            "Android CA", "Android Exam", "Android Total", "Android Grade",
            "Web CA", "Web Exam", "Web Total", "Web Grade",
            "DB CA", "DB Exam", "DB Total", "DB Grade",
            "DS CA", "DS Exam", "DS Total", "DS Grade",
            "GPA"
        )
        
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = createHeaderStyle(sheet)
        }
    }
    
    private fun createStudentRow(sheet: Sheet, rowNum: Int, student: Student) {
        val row = sheet.createRow(rowNum)
        
        row.createCell(0).setCellValue(rowNum.toDouble()) // S/N
        row.createCell(1).setCellValue(student.matricle)
        row.createCell(2).setCellValue(student.name)
        row.createCell(3).setCellValue(student.email)
        
        // Add course data
        var colIndex = 4
        Course.values().forEach { course ->
            val result = student.getCourseResult(course)
            if (result != null) {
                row.createCell(colIndex).setCellValue(result.caMark.toDouble())
                row.createCell(colIndex + 1).setCellValue(result.examMark.toDouble())
                row.createCell(colIndex + 2).setCellValue(result.total.toDouble())
                row.createCell(colIndex + 3).setCellValue(result.grade.name)
            } else {
                row.createCell(colIndex).setCellValue("-")
                row.createCell(colIndex + 1).setCellValue("-")
                row.createCell(colIndex + 2).setCellValue("-")
                row.createCell(colIndex + 3).setCellValue("-")
            }
            colIndex += 4
        }
        
        // Add GPA
        row.createCell(colIndex).setCellValue(student.calculateGPA())
    }
    
    private fun createHeaderStyle(sheet: Sheet): CellStyle {
        val style = sheet.workbook.createCellStyle()
        val font = sheet.workbook.createFont()
        font.bold = true
        style.setFont(font)
        style.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        return style
    }
    
    private fun getCellValue(cell: Cell?): String {
        return when (cell?.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toString()
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> try {
                cell.stringCellValue
            } catch (e: Exception) {
                cell.numericCellValue.toString()
            }
            else -> ""
        }
    }
    
    private fun getCellIntValue(cell: Cell?): Int? {
        return try {
            when (cell?.cellType) {
                CellType.NUMERIC -> cell.numericCellValue.toInt()
                CellType.STRING -> cell.stringCellValue.toIntOrNull()
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    companion object {
        fun getExcelFileFilter(): javax.swing.filechooser.FileFilter {
            return object : javax.swing.filechooser.FileFilter() {
                override fun accept(f: File): Boolean {
                    return f.isDirectory || 
                           f.name.endsWith(".xlsx") || 
                           f.name.endsWith(".xls")
                }
                
                override fun getDescription(): String {
                    return "Excel Files (*.xlsx, *.xls)"
                }
            }
        }
    }
}
