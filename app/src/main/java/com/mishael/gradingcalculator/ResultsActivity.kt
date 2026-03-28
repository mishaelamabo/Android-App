package com.mishael.gradingcalculator

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class ResultsActivity : AppCompatActivity() {

    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val rvStudents = findViewById<RecyclerView>(R.id.rvStudents)
        val btnClear = findViewById<Button>(R.id.btnClearAll)

        adapter = StudentAdapter(StudentRepository.getStudents())
        rvStudents.layoutManager = LinearLayoutManager(this)
        rvStudents.adapter = adapter

        btnClear.setOnClickListener {
            StudentRepository.clearStudents()
            adapter.updateData(emptyList())
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateData(StudentRepository.getStudents())
    }
}