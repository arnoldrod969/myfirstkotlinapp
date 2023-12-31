package com.example.myfirstapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception

class SQLiteHelper(context:Context) :SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

   companion object{

       private const val DATABASE_VERSION  = 1
       private const val DATABASE_NAME = "student.db"
       private const val TBL_STUDENT = "tbl_student"
       private const val ID = "id"
       private const val NAME = "name"
       private const val EMAIL = "email"
       private const val PASSWORD = "password"
   }

    override fun onCreate(db: SQLiteDatabase?) {

         //To change body of created functions use File | Settings | File Templates.
        val createTblStudent = (
                " create table "+ TBL_STUDENT+" ("+
                ID+" INTEGER PRIMARY KEY, "+ NAME+" TEXT, "
                + EMAIL+" TEXT,  "+ PASSWORD+" TEXT ) "
                )
        db?.execSQL(createTblStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db!!.execSQL("DROP TABLE IF EXISTS $TBL_STUDENT ")
        onCreate(db)
    }
    fun insertStudent(std: StudentModel):Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.name)
        contentValues.put(EMAIL, std.email)
        contentValues.put(PASSWORD, std.password)

        val success = db.insert(TBL_STUDENT,null,contentValues)
        db.close()
        return success
    }
    //Fetch all students from database
    fun getAllStudent():ArrayList<StudentModel>{
        val stdList : ArrayList<StudentModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_STUDENT"
        val db = this.readableDatabase

        val cursor : Cursor ?

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name : String
        var email : String
        var password : String

        if (cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                name = cursor.getString(cursor.getColumnIndex("name"))
                email = cursor.getString(cursor.getColumnIndex("email"))
                password = cursor.getString(cursor.getColumnIndex("password"))

                val std = StudentModel(id = id, name = name, email = email, password = password)
                stdList.add(std)

            }while (cursor.moveToNext())
        }
        return stdList
    }

    //Update student using id
    fun updateStudent(std: StudentModel):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.name)
        contentValues.put(EMAIL, std.email)
        contentValues.put(PASSWORD, std.password)

        val success = db.update(TBL_STUDENT, contentValues, "id="+std.id,null)
        db.close()

        return success
    }

    //Delete student by id
    fun deleteStudentById(id:Int): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID,id)

        val success = db.delete(TBL_STUDENT,"id=$id",null)
        db.close()
        return success

    }
}