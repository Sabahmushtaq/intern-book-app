package com.example.mybooks

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mybooks.database.BookDatabase
import com.example.mybooks.database.BookEntity
import com.example.mybooks.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method

class DescriptionActivity : AppCompatActivity() {
    lateinit var txtbookname:TextView
    lateinit var txtbookauthor:TextView
    lateinit var txtbookprice:TextView
    lateinit var textbookrating:TextView
    lateinit var imgbookimage:ImageView
    lateinit var txtbookdesc:TextView
    lateinit var btnaddtofavv:Button
    lateinit var progressbar:ProgressBar
    lateinit var progresslayout:RelativeLayout
    lateinit var toolbar:Toolbar
    var bookid:String?="100"
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtbookname=findViewById(R.id.textbookname)
        txtbookauthor=findViewById(R.id.textbookauthor)
        txtbookprice=findViewById(R.id.textbookprice)
        textbookrating=findViewById(R.id.rating)
        imgbookimage=findViewById(R.id.imgbookimg)
        txtbookdesc=findViewById(R.id.textabout)
        btnaddtofavv=findViewById(R.id.btnaddtofav)
        progressbar=findViewById(R.id.progress)
        progresslayout=findViewById(R.id.progresslay)
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="book details"
        progressbar.visibility= View.VISIBLE
        progresslayout.visibility=View.VISIBLE
        if(intent !=null){
            bookid=intent.getStringExtra("book_id")
        }else{
            finish()
            Toast.makeText(this@DescriptionActivity,"ERROR OCCURED", Toast.LENGTH_SHORT).show()
        }
        if(bookid=="100"){
            finish()
            Toast.makeText(this@DescriptionActivity,"some ERROR OCCURED", Toast.LENGTH_SHORT).show()
        }
        val queue=Volley.newRequestQueue(this@DescriptionActivity)
        val url="http://13.235.250.119/v1/book/get_book/"
        val jsonParams=JSONObject()
        jsonParams.put("book_id",bookid)
       if(ConnectionManager().checkConnectivity(this@DescriptionActivity)){

            val jsonRequest= object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {
                try {
                    val success=it.getBoolean("success")
                    if (success)
                    {val bookJsonObject=it.getJSONObject("book_data")
                        progresslayout.visibility=View.GONE

                        val bookImgUrl=bookJsonObject.getString("image")

                        Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.books).into(imgbookimage)
                        txtbookname.text=bookJsonObject.getString("name")
                        txtbookauthor.text=bookJsonObject.getString("author")
                        txtbookprice.text=bookJsonObject.getString("price")
                        textbookrating.text=bookJsonObject.getString("rating")
                        txtbookdesc.text=bookJsonObject.getString("description")

                        val bookEntity = BookEntity(
                            bookid?.toInt() as Int,
                            txtbookname.text.toString(),
                            txtbookauthor.text.toString(),
                            txtbookprice.text.toString(),
                            textbookrating.text.toString(),
                            txtbookdesc.text.toString(),
                            bookImgUrl
                        )
                        val checkfav=DBAsyncTask(applicationContext,bookEntity, mode = 1).execute()
                        val isfav=checkfav.get()
                        if(isfav){
                            btnaddtofavv.text="remove from favourites"
                            val favcolor=ContextCompat.getColor(applicationContext,R.color.colorfavourite)
                            btnaddtofavv.setBackgroundColor(favcolor)
                        }
                        else{
                            btnaddtofavv.text="add to favourites"
                            val nofavcolor=ContextCompat.getColor(applicationContext,R.color.orange)
                            btnaddtofavv.setBackgroundColor(nofavcolor)
                        }
                        btnaddtofavv.setOnClickListener {
                            if (!DBAsyncTask(applicationContext, bookEntity, mode = 1).execute().get()) {
                                val async =
                                    DBAsyncTask(applicationContext, bookEntity, mode = 2).execute()
                                val result = async.get()
                                if (result) {
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "book added to favourites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnaddtofavv.text = "remove from favourites"
                                    val favcolor = ContextCompat.getColor(
                                        applicationContext,
                                        R.color.colorfavourite
                                    )
                                    btnaddtofavv.setBackgroundColor(favcolor)

                                } else {
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "some ERROR OCCURED",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val async =
                                    DBAsyncTask(applicationContext, bookEntity, mode = 3).execute()
                                val result = async.get()
                                if (result) {
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "removed from favourites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnaddtofavv.text = "add to favourites"
                                    val nofavcolor =
                                        ContextCompat.getColor(applicationContext, R.color.orange)
                                    btnaddtofavv.setBackgroundColor(nofavcolor)


                                }
                                else{
                                    Toast.makeText(this@DescriptionActivity,"some ERROR OCCURED", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(this@DescriptionActivity,"some ERROR OCCURED", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception)
                { Toast.makeText(this@DescriptionActivity,"some ERROR OCCURED3", Toast.LENGTH_SHORT).show()}
            },Response.ErrorListener {
                Toast.makeText(this@DescriptionActivity,"some ERROR OCCURED", Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "8e80f62fd86e9e"
                    return headers
                }

            }
            queue.add(jsonRequest)
    }
        else{
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("ERROR")
            dialog.setMessage("internet connection is not found")
            dialog.setPositiveButton("open settings") { text, listener ->
                //do nothing
                val settingsintent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsintent)
                finish()
            }
            dialog.setNegativeButton("exit") { text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()

        }

    }
    class DBAsyncTask(val context:Context,val bookEntity: BookEntity,val mode:Int): AsyncTask<Void,Void,Boolean>(){
        val db= Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {

when(mode){
                1 -> {
                    // check if book is in db
                    val book:BookEntity?=db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()

                    return book != null
                }
                2 -> {
                    //add book to db
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3 -> {
                    //remove book from db
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true

                }
            }

            return false
        }

    }
}