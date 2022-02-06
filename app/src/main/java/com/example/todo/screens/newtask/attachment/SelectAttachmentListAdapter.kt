package com.example.todo.screens.newtask.attachment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R

class ItemAttachmentViewHolder(val imageView: ImageView): RecyclerView.ViewHolder(imageView){

}

class SelectAttachmentListAdapter: RecyclerView.Adapter<ItemAttachmentViewHolder>(){
    var data = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: ItemAttachmentViewHolder, position: Int) {
//        val item = data[position]
        holder.imageView.setImageResource(R.drawable.ic_balloon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAttachmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.item_attachment_view, parent, false) as ImageView
        return ItemAttachmentViewHolder(view)
    }
}