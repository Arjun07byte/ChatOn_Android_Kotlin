package com.example.chaton.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chaton.MainActivity
import com.example.chaton.R
import com.example.chaton.models.Chats

class ChatHistoryAdapter : RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder>() {
    inner class ChatHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eachNumber: TextView = itemView.findViewById(R.id.user_name)
        val parentContext: Context = itemView.context
        val lastMessage: TextView = itemView.findViewById(R.id.last_message)
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Chats>() {
        override fun areItemsTheSame(oldItem: Chats, newItem: Chats): Boolean {
            return oldItem.messageText == newItem.messageText
        }

        override fun areContentsTheSame(oldItem: Chats, newItem: Chats): Boolean {
            return oldItem == newItem
        }
    }

    val myDifferList = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHistoryViewHolder {
        return ChatHistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chats_list_rv_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatHistoryViewHolder, position: Int) {
        holder.eachNumber.text = myDifferList.currentList[position].phoneNumber
        holder.lastMessage.text = myDifferList.currentList[position].messageText
        holder.itemView.setOnClickListener {
            moveToChats(holder.eachNumber.text.toString(), holder.parentContext)
        }
    }

    private fun moveToChats(givenNum: String, parentContext: Context) {
        val parentMainActivity: MainActivity = parentContext as MainActivity
        parentMainActivity.moveToChats(givenNum)
    }

    override fun getItemCount(): Int {
        return myDifferList.currentList.size
    }
}