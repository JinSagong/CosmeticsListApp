package com.jin.hwahae

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_items.view.*
import org.jetbrains.anko.startActivity
import java.text.NumberFormat

class GridAdapter(items: ArrayList<Any>) : RecyclerView.Adapter<GridAdapter.MyViewHolder>() {
    private val itemList = items

    class MyViewHolder(v: View) : ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_items,
            parent,
            false
        )
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position] as API.Items
        holder.itemView.titleTextView.text = item.title
        holder.itemView.priceTextView.text =
            (NumberFormat.getNumberInstance().format(item.price.toInt())
                    + holder.itemView.context.getString(R.string.won))

        val u = item.thumbnail_image
        Glide.with(holder.itemView.context).load(u).into(holder.itemView.itemImageView)
        holder.itemView.setOnClickListener {
            Log.d("ForChecking", "[title: ${item.title} / id: ${item.id}]")
            holder.itemView.context.startActivity<DetailActivity>("id" to item.id)
        }
    }
}