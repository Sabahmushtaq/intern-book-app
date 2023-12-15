package com.example.mybooks

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mybooks.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context: Context, val itemList:ArrayList<Book>):
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    class DashboardViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textview1:TextView=view.findViewById(R.id.books)
        val textview2:TextView=view.findViewById(R.id.booksauthor)
        val textview3:TextView=view.findViewById(R.id.price)
        val textview4:TextView=view.findViewById(R.id.rate)
        val img:ImageView=view.findViewById(R.id.bookimg)
        val liContent:LinearLayout=view.findViewById(R.id.liContent)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view =LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single, parent,false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.textview1.text=book.bookName
        holder.textview2.text=book.bookAuthor
        holder.textview3.text=book.bookPrice
        holder.textview4.text=book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.books).into(holder.img)
       /* holder.img.setImageResource(book.bookImage)*/
        holder.liContent.setOnClickListener(){
            val intent= Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)

            Toast.makeText(
                context,
                "${holder.textview1.text}",
                Toast.LENGTH_SHORT
            ).show()

        }


    }

    override fun getItemCount(): Int {
        return itemList.size
    }





}