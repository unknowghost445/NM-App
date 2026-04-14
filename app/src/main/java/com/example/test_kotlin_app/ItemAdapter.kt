package com.example.test_kotlin_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ItemAdapter(
    context: Context,
    val resource: Int,
    val items: List<Post>,
    private val usersById: Map<String, User>
) : ArrayAdapter<Post>(context, resource, items) {
    @Override
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        val post = items[position]
        val postUser = usersById[post.userID]

        val name = v.findViewById<TextView>(R.id.tvIPName)
        val avatarUrl = v.findViewById<ImageView>(R.id.imgIPAvatar)
        val date = v.findViewById<TextView>(R.id.tvIPDate)
        val data = v.findViewById<TextView>(R.id.tvIPData)

        name.text = postUser?.name ?: "Unknown user"
        Glide.with(context)
            .load(postUser?.avatarUrl ?: "")
            .into(avatarUrl)
        date.text = post.date
        data.text = post.data

        return v!!
    }
}
