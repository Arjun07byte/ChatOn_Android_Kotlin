package com.example.chaton.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.example.chaton.R

class ChatFragment(val givenNum: String) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
    }

    private fun setUpViews() {
        val backButton: ImageButton = requireView().findViewById(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.home_fragment,HomeFragment()
            ).commit()
        }

        val chatNumberTV: TextView = requireView().findViewById(R.id.chat_number)
        chatNumberTV.text = givenNum
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.beginTransaction().replace(
                        R.id.home_fragment,HomeFragment()
                    ).commit()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }
}