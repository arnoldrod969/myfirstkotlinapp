package com.example.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var edName: EditText
    private lateinit var edEmail: EditText
    private lateinit var edPwd : EditText
    private lateinit var btnAdd: Button
    private lateinit var btnView: Button
    private lateinit var btnUpdate : Button

    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: StudentAdapter? = null
    private var std:StudentModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initRecyclerView()
        sqLiteHelper = SQLiteHelper(this)

        btnAdd.setOnClickListener{addStudent()}
        btnView.setOnClickListener{getStudents()}
        btnUpdate.setOnClickListener { updateStudent() }
        adapter?.setOnClickItem {
            Toast.makeText(this,it.name,Toast.LENGTH_SHORT).show()
            edName.setText(it.name)
            edEmail.setText(it.email)
            edPwd.setText(it.password)
            std = it
        }
        adapter?.setOnClickDeleteItem {
            deleteStudent(it.id)
        }
    }

    private fun updateStudent() {
        val name = edName.text.toString()
        val email = edEmail.text.toString()
        val password = edPwd.text.toString()

        if (name == std?.name && email == std?.email && password == std?.password){
            Toast.makeText(this,"Record not changed !!!",Toast.LENGTH_SHORT).show()
            return
        }
        if (std == null) return

        val std = StudentModel(id = std!!.id, name = name, email = email, password = password)
        val status = sqLiteHelper.updateStudent(std)
        if(status > -1){
            clearEditText()
            getStudents()
        }else{
            Toast.makeText(this,"Updated failed !!!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getStudents() {
        val stdList = sqLiteHelper.getAllStudent()
        Log.e("Test","${stdList.size}")

        //Now we need to display data in RecyclerView
        adapter?.addItems(stdList)
    }

    private fun addStudent() {
        val name = edName.text.toString()
        val email = edEmail.text.toString()
        val password = edPwd.text.toString()

        //Check
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter required fields !!!",Toast.LENGTH_SHORT).show()
        }else{
            val std = StudentModel(name = name,email = email, password = password)
            val status = sqLiteHelper.insertStudent(std)
            //Check insert success or not success
            if(status > -1){
                Toast.makeText(this,"Student added !!!",Toast.LENGTH_SHORT).show()
                clearEditText()
                getStudents()
            }else{
                Toast.makeText(this,"Record not saved !!!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteStudent(id:Int){

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this item ?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){dialog, _ ->
            sqLiteHelper.deleteStudentById(id)
            getStudents()
            dialog.dismiss()
            Toast.makeText(this,"Item deleted successfully !!!",Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this,"Action cancelled !!!",Toast.LENGTH_SHORT).show()
        }

        val alert = builder.create()
        alert.show()
    }

    private fun clearEditText() {
        edName.setText("")
        edEmail.setText("")
        edPwd.setText("")
        edName.requestFocus()

    }

    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter()
        recyclerView.adapter = adapter

    }

    private fun initView(){
        edName = findViewById(R.id.edName)
        edEmail = findViewById(R.id.edEmail)
        edPwd = findViewById(R.id.edPwd)
        btnAdd = findViewById(R.id.btnAdd)
        btnView = findViewById(R.id.btnView)
        btnUpdate = findViewById(R.id.btnUpdate)
        recyclerView = findViewById(R.id.recyclerView)
    }

}
