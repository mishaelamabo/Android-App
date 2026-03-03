package ui

import models.Student
import models.Course
import models.CourseGrade
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class MainWindow : JFrame("Student Grade Calculator - Course Management System") {

    private val students = mutableListOf<Student>()

    // Input Fields
    private val nameField = JTextField(15)
    private val matricleField = JTextField(10)
    private val emailField = JTextField(15)

    // Course Selection
    private val courseCombo = JComboBox<Course>().apply {
        Course.values().forEach { addItem(it) }
    }

    // Marks Input
    private val caField = JTextField(5)
    private val examField = JTextField(5)

    // Student Selection
    private val studentCombo = JComboBox<String>()

    // Display Areas
    private val resultArea = JTextArea(15, 50).apply {
        isEditable = false
        font = Font("Monospaced", Font.PLAIN, 12)
    }

    private val tableModel = DefaultTableModel(
        arrayOf("Matricle", "Name", "Email", "Course", "CA(30)", "Exam(70)", "Total", "Grade", "GP"), 0
    )
    private val table = JTable(tableModel)

    // Summary Labels
    private val totalStudentsLabel = JLabel("Total Students: 0")
    private val avgGpaLabel = JLabel("Average GPA: 0.00")

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1200, 700)
        setLocationRelativeTo(null)

        val mainPanel = JPanel(BorderLayout(10, 10))
        mainPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        // Create all panels
        mainPanel.add(createTopPanel(), BorderLayout.NORTH)
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER)
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH)

        add(mainPanel)
        updateStudentCombo()
        updateSummary()
    }

    private fun createTopPanel(): JPanel {
        val panel = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createTitledBorder("Student Registration & Marks Entry")
        }
        val gbc = GridBagConstraints().apply {
            insets = Insets(5, 5, 5, 5)
            fill = GridBagConstraints.HORIZONTAL
        }

        // Student Registration Section
        gbc.gridx = 0; gbc.gridy = 0
        panel.add(JLabel("Registration:"), gbc)

        gbc.gridx = 1
        panel.add(JLabel("Name:"), gbc)
        gbc.gridx = 2
        panel.add(nameField, gbc)

        gbc.gridx = 3
        panel.add(JLabel("Matricle:"), gbc)
        gbc.gridx = 4
        panel.add(matricleField, gbc)

        gbc.gridx = 5
        panel.add(JLabel("Email:"), gbc)
        gbc.gridx = 6
        panel.add(emailField, gbc)

        gbc.gridx = 7
        val addStudentBtn = JButton("Add Student").apply {
            addActionListener { addStudent() }
        }
        panel.add(addStudentBtn, gbc)

        // Marks Entry Section
        gbc.gridy = 1; gbc.gridx = 0
        panel.add(JLabel("Marks Entry:"), gbc)

        gbc.gridx = 1
        panel.add(JLabel("Select Student:"), gbc)
        gbc.gridx = 2
        panel.add(studentCombo, gbc)

        gbc.gridx = 3
        panel.add(JLabel("Course:"), gbc)
        gbc.gridx = 4
        panel.add(courseCombo, gbc)

        gbc.gridx = 5
        panel.add(JLabel("CA (0-30):"), gbc)
        gbc.gridx = 6
        panel.add(caField, gbc)

        gbc.gridx = 7
        panel.add(JLabel("Exam (0-70):"), gbc)
        gbc.gridx = 8
        panel.add(examField, gbc)

        gbc.gridx = 9
        val addMarksBtn = JButton("Add Marks").apply {
            addActionListener { addMarks() }
        }
        panel.add(addMarksBtn, gbc)

        return panel
    }

    private fun createCenterPanel(): JPanel {
        val panel = JPanel(BorderLayout())

        val tabbedPane = JTabbedPane()

        // Table View
        table.fillsViewportHeight = true
        table.autoCreateRowSorter = true
        val scrollPane = JScrollPane(table)
        tabbedPane.addTab("All Records", scrollPane)

        // Transcript View
        val textScrollPane = JScrollPane(resultArea)
        tabbedPane.addTab("Student Transcript", textScrollPane)

        panel.add(tabbedPane, BorderLayout.CENTER)
        return panel
    }

    private fun createBottomPanel(): JPanel {
        val panel = JPanel(BorderLayout())

        // Summary Panel
        val summaryPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(totalStudentsLabel)
            add(Box.createHorizontalStrut(20))
            add(avgGpaLabel)
        }

        // Button Panel
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))

        val calculateBtn = JButton("Calculate All GPAs").apply {
            addActionListener { calculateAllGPAs() }
        }

        val viewTranscriptBtn = JButton("View Selected Transcript").apply {
            addActionListener { viewTranscript() }
        }

        val refreshBtn = JButton("Refresh").apply {
            addActionListener { refreshDisplay() }
        }

        val clearBtn = JButton("Clear Inputs").apply {
            addActionListener {
                nameField.text = ""
                matricleField.text = ""
                emailField.text = ""
                caField.text = ""
                examField.text = ""
            }
        }

        buttonPanel.add(calculateBtn)
        buttonPanel.add(viewTranscriptBtn)
        buttonPanel.add(refreshBtn)
        buttonPanel.add(clearBtn)

        panel.add(summaryPanel, BorderLayout.WEST)
        panel.add(buttonPanel, BorderLayout.EAST)

        return panel
    }

    private fun addStudent() {
        val name = nameField.text.trim()
        val matricle = matricleField.text.trim()
        val email = emailField.text.trim()

        when {
            name.isEmpty() || matricle.isEmpty() || email.isEmpty() -> {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE)
            }
            students.any { it.matricle == matricle } -> {
                JOptionPane.showMessageDialog(this, "Matricle number already exists!", "Error", JOptionPane.ERROR_MESSAGE)
            }
            else -> {
                students.add(Student(name, matricle, email))
                updateStudentCombo()
                refreshDisplay()
                clearRegistrationFields()
                JOptionPane.showMessageDialog(this, "Student added successfully!")
            }
        }
    }

    private fun addMarks() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add a student first!", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val selectedIndex = studentCombo.selectedIndex
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student!", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val student = students[selectedIndex]
        val course = courseCombo.selectedItem as Course
        val ca = caField.text.trim().toIntOrNull()
        val exam = examField.text.trim().toIntOrNull()

        // Check if course already has marks
        if (student.getCourseResult(course) != null) {
            val confirm = JOptionPane.showConfirmDialog(this,
                "Marks for ${course.fullName} already exist. Overwrite?",
                "Confirm Overwrite",
                JOptionPane.YES_NO_OPTION)

            if (confirm != JOptionPane.YES_OPTION) {
                return
            }
            // Remove existing result
            student.results.removeIf { it.course == course }
        }

        if (ca == null || exam == null) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for CA and Exam!", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val courseGrade = CourseGrade(course, ca, exam)

        if (!courseGrade.isValid()) {
            JOptionPane.showMessageDialog(this, "Invalid marks! CA must be 0-30, Exam 0-70", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        student.results.add(courseGrade)
        clearMarksFields()
        refreshDisplay()

        JOptionPane.showMessageDialog(this,
            "Marks added successfully!\nTotal: ${courseGrade.total} | Grade: ${courseGrade.grade.name}")
    }

    private fun calculateAllGPAs() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to calculate GPA for!")
            return
        }

        val result = StringBuilder()
        result.appendLine("=".repeat(60))
        result.appendLine("STUDENT GPA REPORT")
        result.appendLine("=".repeat(60))
        result.appendLine(String.format("%-15s %-20s %-15s %-10s %-10s",
            "Matricle", "Name", "Email", "Courses", "GPA"))
        result.appendLine("-".repeat(60))

        var totalGpa = 0.0
        var studentCount = 0

        students.forEach { student ->
            val gpa = student.calculateGPA()
            totalGpa += gpa
            studentCount++

            result.appendLine(String.format("%-15s %-20s %-15s %-10d %-10.2f",
                student.matricle,
                student.name.take(20),
                student.email.take(15),
                student.results.size,
                gpa
            ))
        }

        result.appendLine("-".repeat(60))
        if (studentCount > 0) {
            result.appendLine("Average GPA: %.2f".format(totalGpa / studentCount))
        } else {
            result.appendLine("Average GPA: 0.00")
        }
        result.appendLine("Total Students: $studentCount")
        result.appendLine("=".repeat(60))

        resultArea.text = result.toString()
        updateSummary()
    }

    private fun viewTranscript() {
        val selectedIndex = studentCombo.selectedIndex
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student!", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val student = students[selectedIndex]
        resultArea.text = student.getTranscript()

        // Switch to transcript tab
        val tabbedPane = (contentPane as JPanel).components
            .filterIsInstance<JPanel>()
            .first()
            .components
            .filterIsInstance<JTabbedPane>()
            .first()

        tabbedPane.selectedIndex = 1
    }

    private fun refreshDisplay() {
        updateTable()
        updateSummary()
    }

    // FIXED: This is the corrected updateTable function with explicit typing
    private fun updateTable() {
        tableModel.setRowCount(0)
        students.forEach { student ->
            if (student.results.isEmpty()) {
                // Explicitly type the array to avoid the warning
                val rowData: Array<Any> = arrayOf(
                    student.matricle,
                    student.name,
                    student.email,
                    "No courses",
                    "-", "-", "-", "-", "-"
                )
                tableModel.addRow(rowData)
            } else {
                student.results.forEach { grade ->
                    val rowData: Array<Any> = arrayOf(
                        student.matricle,
                        student.name,
                        student.email,
                        grade.course.fullName,
                        grade.caMark,
                        grade.examMark,
                        grade.total,
                        grade.grade.name,
                        "%.1f".format(grade.gradePoint)
                    )
                    tableModel.addRow(rowData)
                }
            }
        }
    }

    private fun updateStudentCombo() {
        studentCombo.removeAllItems()
        students.forEach { student ->
            studentCombo.addItem("${student.matricle} - ${student.name}")
        }
    }

    private fun updateSummary() {
        totalStudentsLabel.text = "Total Students: ${students.size}"

        val avgGpa = if (students.isNotEmpty()) {
            students.map { it.calculateGPA() }.average()
        } else 0.0

        avgGpaLabel.text = "Average GPA: %.2f".format(avgGpa)
    }

    private fun clearRegistrationFields() {
        nameField.text = ""
        matricleField.text = ""
        emailField.text = ""
    }

    private fun clearMarksFields() {
        caField.text = ""
        examField.text = ""
    }

    companion object {
        fun showGUI() {
            SwingUtilities.invokeLater {
                val window = MainWindow()
                window.isVisible = true
            }
        }
    }
}