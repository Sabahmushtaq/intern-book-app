package com.example.mybooks

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mybooks.model.Book
import com.example.mybooks.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class Dashboardfragment : Fragment() {
        lateinit var recycleDashboard:RecyclerView
        lateinit var layoutManager:RecyclerView.LayoutManager
       // lateinit var Button1:Button
        lateinit var progresslayout:RelativeLayout
        lateinit var progressBar: ProgressBar
    val bookInfoList= arrayListOf<Book>()
        var ratingcomparator= Comparator<Book> { book1, book2 ->
            if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
                book1.bookName.compareTo(book2.bookName, true)
            }
            else{
                book1.bookRating.compareTo(book2.bookRating,true)
            }
        }

//val booklist= arrayListOf<String>("sabah","misbah","mansha","papa","mama","abaji",
  //  "mummy","didi","badapapa","baijan")
   /* val bookInfoList = arrayListOf<Book>(Book("sabah","mush","4.5","rs 299",R.drawable.books),
        Book("sabah1","mush1","4.5","rs 199",R.drawable.books),
        Book("sabah2","mush2","3.5","rs 299",R.drawable.books),
        Book("sabah3","mush3","4.5","rs 399",R.drawable.books),
        Book("sabah4","mush4","5","rs 499",R.drawable.books),
        Book("sabah5","mush5","2.6","rs 599",R.drawable.books),
        Book("sabah6","mush6","4","rs 699",R.drawable.books),
        Book("sabah7","mush7","1.5","rs 799",R.drawable.books),
        Book("sabah8","mush8","2.5","rs 899",R.drawable.books),
        Book("sabah9","mush9","3.4","rs 999",R.drawable.books),)*/

    lateinit var recyclerAdapter:DashboardRecyclerAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_dashboardfragment, container, false)
        setHasOptionsMenu(true)
        recycleDashboard=view.findViewById(R.id.recyclerDashboard)
        progressBar=view.findViewById(R.id.progressbar)
        progresslayout=view.findViewById(R.id.progresslayout)
        progresslayout.visibility=View.VISIBLE
      /*  Button1=view.findViewById(R.id.button)
        Button1.setOnClickListener(){
            if (ConnectionManager().checkConnectivity(activity as Context)){
                val dialog =AlertDialog.Builder(activity as Context)
                dialog.setTitle("success")
                dialog.setMessage("internet connection found")
                dialog.setPositiveButton("ok") { text, listener ->
                    //do nothing
                }
                dialog.setNegativeButton("cancel"){text,listener ->

                }
                dialog.create()
                dialog.show()


            } else{
                val dialog =AlertDialog.Builder(activity as Context)
                dialog.setTitle("ERROR")
                dialog.setMessage("internet connection is not found")
                dialog.setPositiveButton("ok") { text, listener ->
                    //do nothing
                }
                dialog.setNegativeButton("cancel") { text, listener ->
                    //do nothing
                }
                dialog.create()
                dialog.show()
            }
        }*/
        layoutManager=LinearLayoutManager(activity)


            val queue= Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v1/book/fetch_books/"
        if(ConnectionManager().checkConnectivity(activity as Context)){


            val jsonObjectRequest=object: JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
               try{
                   progresslayout.visibility=View.GONE
                   val success=it.getBoolean("success")

                   if(success){
                       val data=it.getJSONArray("data")
                       for (i in 0 until data.length()) {
                           val bookJsonObject = data.getJSONObject(i)
                           val bookObject = Book(
                               bookJsonObject.getString("book_id"),
                               bookJsonObject.getString("name"),
                               bookJsonObject.getString("author"),
                               bookJsonObject.getString("rating"),
                               bookJsonObject.getString("price"),
                               bookJsonObject.getString("image")
                           )

                           bookInfoList.add(bookObject)
                           recyclerAdapter = DashboardRecyclerAdapter(activity as Context, bookInfoList)
                           recycleDashboard.adapter = recyclerAdapter
                           recycleDashboard.layoutManager = layoutManager
                           recycleDashboard.addItemDecoration(
                               DividerItemDecoration(
                                   recycleDashboard.context,
                                   (layoutManager as LinearLayoutManager).orientation

                               )
                           )
                       }

                   }else{
                       Toast.makeText(activity as Context,"ERROR OCCURED",Toast.LENGTH_SHORT).show()
                   }

               }
               catch (e: JSONException){
                   Toast.makeText(activity as Context, "some error occured",Toast.LENGTH_SHORT).show()
               }


            },Response.ErrorListener {
                if(activity !=null){
                Toast.makeText(activity as Context, "some VOLLEY error occured",Toast.LENGTH_SHORT).show()
            }})
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "8e80f62fd86e9e"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)


        }
        else{
            val dialog =AlertDialog.Builder(activity as Context)
            dialog.setTitle("ERROR")
            dialog.setMessage("internet connection is not found")
            dialog.setPositiveButton("open settings") { text, listener ->
                //do nothing
                val settingsintent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsintent)
                activity?.finish()
            }
            dialog.setNegativeButton("exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard,menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id= item?.itemId
        if(id == R.id.action_sort){
            Collections.sort(bookInfoList,ratingcomparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}