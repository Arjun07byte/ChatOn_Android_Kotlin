package com.example.chaton.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.chaton.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {
    private lateinit var sendOtpButton: MaterialButton
    private lateinit var submitOtpButton: MaterialButton
    private lateinit var editPhoneNumButton: ImageButton
    private lateinit var phoneNumLayout: TextInputLayout
    private lateinit var otpLayout: TextInputLayout
    private lateinit var editTextPhoneNum: TextInputEditText
    private lateinit var editTextOtp: TextInputEditText
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var parentActivity: FragmentActivity
    private lateinit var verificationCode: String
    private lateinit var resendCodeID: PhoneAuthProvider.ForceResendingToken
    private lateinit var animationView: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewVariables(view)

        sendOtpButton.setOnClickListener {
            var enteredNumber = editTextPhoneNum.text.toString()
            if (isValidIndianPhoneNumber(enteredNumber)) {
                enteredNumber = "+91$enteredNumber"
                sendOtpToNumber(enteredNumber)
            } else {
                Snackbar.make(view, "Please Enter a Valid Number", Snackbar.LENGTH_LONG).show()
            }
        }

        editPhoneNumButton.setOnClickListener {
            disableOtpViews()
        }

        submitOtpButton.setOnClickListener {
            val enteredCred =
                PhoneAuthProvider.getCredential(verificationCode, editTextOtp.text.toString())
            verifyUserWithOtpAndNumber(enteredCred)
        }
    }

    private fun setUpViewVariables(view: View) {
        sendOtpButton = view.findViewById(R.id.button_sendOtp)
        submitOtpButton = view.findViewById(R.id.button_submitOtp)
        editPhoneNumButton = view.findViewById(R.id.button_editPhoneNum)

        phoneNumLayout = view.findViewById(R.id.layout_phoneNumber)
        otpLayout = view.findViewById(R.id.layout_otp)
        editTextPhoneNum = view.findViewById(R.id.editText_phoneNum)
        editTextOtp = view.findViewById(R.id.editText_otp)
        parentActivity = requireActivity()
        animationView = view.findViewById(R.id.loading_anim)
    }

    private fun isValidIndianPhoneNumber(givenPhoneNum: String): Boolean {
        return givenPhoneNum.isNotEmpty() && givenPhoneNum.length == 10 && (givenPhoneNum[0] == '9' || givenPhoneNum[0] == '8' || givenPhoneNum[0] == '7' || givenPhoneNum[0] == '6')
    }

    private fun disableOtpViews() {
        editTextPhoneNum.text?.clear()
        editTextOtp.text?.clear()
        editTextPhoneNum.isEnabled = true

        otpLayout.visibility = View.INVISIBLE
        submitOtpButton.visibility = View.INVISIBLE
        editPhoneNumButton.visibility = View.INVISIBLE
        editPhoneNumButton.visibility = View.INVISIBLE
        sendOtpButton.visibility = View.VISIBLE
    }

    private fun enableOtpViews() {
        editTextPhoneNum.isEnabled = false

        sendOtpButton.visibility = View.INVISIBLE
        otpLayout.visibility = View.VISIBLE
        submitOtpButton.visibility = View.VISIBLE
        editPhoneNumButton.visibility = View.VISIBLE
    }

    // verify user with a OTP and verification ID which is passed
    // as credential for the function
    private fun verifyUserWithOtpAndNumber(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    parentActivity.supportFragmentManager.beginTransaction().replace(
                        R.id.home_fragment, HomeFragment()
                    ).commit()
                } else {
                    animationView.visibility = View.INVISIBLE
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(requireView(), "Weak Auth", Snackbar.LENGTH_LONG)
                            .show()
                    }
                    Snackbar.make(requireView(), "Invalid OTP Entered", Snackbar.LENGTH_LONG).show()
                }
            }
    }

    // Send OTP to a given mobile number
    private fun sendOtpToNumber(enteredNumber: String) {
        animationView.visibility = View.VISIBLE

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(enteredNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(authCallbacks)          // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Authentication CallBacks which are called when we have sent our OTP
    private val authCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            verifyUserWithOtpAndNumber(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            animationView.visibility = View.INVISIBLE
            if (e is FirebaseAuthInvalidCredentialsException) {
                Snackbar.make(requireView(), "Some Error Occurred", Snackbar.LENGTH_LONG).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Snackbar.make(requireView(), "Server Issue", Snackbar.LENGTH_LONG).show()
            }

            Snackbar.make(requireView(), "Server Issue", Snackbar.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            verificationCode = verificationId
            resendCodeID = token

            animationView.visibility = View.INVISIBLE
            enableOtpViews()
            Snackbar.make(requireView(), "OTP Sent", Snackbar.LENGTH_LONG).show()
        }

    }

}