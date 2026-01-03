package com.example.listarepositorios.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listarepositorios.R
import com.example.listarepositorios.model.Repository

class MyAdapter(
    private val myList: List<Repository>,
    private val context: Context
): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    var iconListener: (Repository) -> Unit = {}
    var itemListener: (Repository) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repository_name_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            val tvRepositoryName: TextView = findViewById(R.id.tv_repository_name)
            val iconShare: ImageView = findViewById(R.id.ic_share)
            val layout: RelativeLayout = findViewById(R.id.layout)

            tvRepositoryName.text = myList[position].name
            iconShare.setOnClickListener {
                val repository = myList[position]
                iconListener(repository)
            }

            layout.setOnClickListener {
                val repository = myList[position]
                itemListener(repository)
            }
        }
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}