package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.NetworkInfo


class GroupAdapter(private val groups: List<MainActivity.Group>) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.imageView)
        val textViewBold: TextView = itemView.findViewById(R.id.textViewBold)
        val textViewWhite: TextView = itemView.findViewById(R.id.textViewWhite)
        val textViewBoldBottom: TextView = itemView.findViewById(R.id.textViewBoldBottom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]

        // Set data to views
        holder.textViewBold.text = group.boldText
        holder.textViewWhite.text = group.whiteText
        holder.textViewBoldBottom.text = group.boldBottomText

        // Handle click event
        holder.itemView.setOnClickListener {
            // Navigate to new activity or fragment, passing necessary data
            val intent = Intent(holder.itemView.context, Detail::class.java)
            intent.putExtra("groupId", group.id )
            intent.putExtra("quality", group.whiteText)
            intent.putExtra("technology", group.boldBottomText)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }
}
