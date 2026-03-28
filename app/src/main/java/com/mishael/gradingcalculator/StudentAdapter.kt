package com.mishael.gradingcalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(private var students: List<Student>) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvMatricle: TextView = view.findViewById(R.id.tvItemMatricle)
        val tvMarks: TextView = view.findViewById(R.id.tvItemMarks)
        val tvGrade: TextView = view.findViewById(R.id.tvItemGrade)
        val tvTotal: TextView = view.findViewById(R.id.tvItemTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.tvName.text = student.name
        holder.tvMatricle.text = student.matricle
        holder.tvMarks.text = "CA: ${student.caMark} | Exam: ${student.examMark}"
        holder.tvGrade.text = student.grade
        holder.tvTotal.text = "${student.totalMark.toInt()}%"
    }

    override fun getItemCount() = students.size

    fun updateData(newStudents: List<Student>) {
        this.students = newStudents
        notifyDataSetChanged()
    }
}