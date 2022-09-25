package com.example.chaton.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chaton.R
import com.example.chaton.adapters.ChatHistoryAdapter
import com.example.chaton.models.Chats
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity
    private lateinit var currAuthInst: FirebaseAuth
    private lateinit var parentView: View
    private lateinit var databaseInt: DatabaseReference
    private lateinit var chatsRV: RecyclerView
    private lateinit var chatsRvAdapter: ChatHistoryAdapter
    private lateinit var myShimmerFrameLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViewVariables(view)

        checkUser()
    }

    // Handling the updates of the Recent chats sent to the User
    private fun setUpChats() {
        val userChats: ArrayList<Chats> = ArrayList()

        // fetching all the recent chats of the user and
        // submitting the list obtained to the adapter
        databaseInt.child("users").child(currAuthInst.currentUser!!.phoneNumber.toString())
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (eachUser in dataSnapshot.children) {
                            eachUser.getValue(Chats::class.java)?.let { userChats.add(it) }
                        }

                        myShimmerFrameLayout.stopShimmerAnimation()
                        myShimmerFrameLayout.visibility = View.GONE
                        chatsRvAdapter.myDifferList.submitList(userChats)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Snackbar.make(parentView, "Network Error", Snackbar.LENGTH_LONG).show()
                    }
                }
            )

        myShimmerFrameLayout.stopShimmerAnimation()
        myShimmerFrameLayout.visibility = View.GONE
    }

    // function to check whether the user is a new user or not
    // if its a new user then we will create a new series of
    // chats for the user
    private fun checkUser() {
        myShimmerFrameLayout.startShimmerAnimation()
        databaseInt.child("users").child(currAuthInst.currentUser!!.phoneNumber.toString())
            .get().addOnSuccessListener {
                if (it.value == null) {
                    databaseInt.child("users")
                        .child(currAuthInst.currentUser!!.phoneNumber.toString())
                        .setValue("")
                }
            }.addOnFailureListener {
                databaseInt.child("users").child(currAuthInst.currentUser!!.phoneNumber.toString())
                    .setValue("")
            }.addOnCompleteListener {
                setUpChats()
            }
    }

    // initialization of all the views
    private fun initializeViewVariables(view: View) {
        // initializing all the views
        parentActivity = requireActivity(); currAuthInst = FirebaseAuth.getInstance()
        parentView = view; databaseInt = FirebaseDatabase.getInstance().reference
        chatsRV = parentView.findViewById(R.id.chats_rv); chatsRvAdapter = ChatHistoryAdapter()
        myShimmerFrameLayout = parentView.findViewById(R.id.layout_shimmerRv)
        chatsRV.apply {
            adapter = chatsRvAdapter
            layoutManager = LinearLayoutManager(parentActivity)
        }

        // setting up the new Chat Button and its listener
        // which will launch a bottom sheet for user to enter
        // a mobile number and start chatting
        val newChatButton: FloatingActionButton = parentView.findViewById(R.id.button_new_chat)
        newChatButton.setOnClickListener {
            val newBottomSheetDialog = BottomSheetDialog(parentActivity)
            newBottomSheetDialog.setContentView(R.layout.new_chat_bottom_dialog)

            val editTextMobileNum: EditText? = newBottomSheetDialog.findViewById(R.id.tv_new_chat)
            val createChatButton: MaterialButton? =
                newBottomSheetDialog.findViewById(R.id.button_createNewChat)
            createChatButton?.setOnClickListener {
                val enteredNum = editTextMobileNum?.text.toString()
                if (isValidIndianPhoneNumber(enteredNum)) {
                    newBottomSheetDialog.dismiss()
                    parentActivity.supportFragmentManager.beginTransaction().replace(
                        R.id.home_fragment, ChatFragment(editTextMobileNum?.text.toString())
                    ).commit()
                } else {
                    Toast.makeText(parentActivity,"Enter Valid Number",Toast.LENGTH_SHORT).show()
                }
            }

            newBottomSheetDialog.show()
        }
    }

    // checking whether the entered Phone Number is a valid
    // Indian Phone Number
    private fun isValidIndianPhoneNumber(givenPhoneNum: String): Boolean {
        return givenPhoneNum.isNotEmpty() && givenPhoneNum.length == 10 && (givenPhoneNum[0] == '9' || givenPhoneNum[0] == '8' || givenPhoneNum[0] == '7' || givenPhoneNum[0] == '6')
    }

}