package com.example.android.workoutbuddy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.workoutbuddy.databinding.FragmentMainloginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.android.workoutbuddy.database.AppApplication
import com.example.android.workoutbuddy.database.AppViewModel
import com.example.android.workoutbuddy.database.AppViewModelFactory
import com.example.android.workoutbuddy.database.User
import com.google.firebase.auth.FirebaseAuth

class FragmentMain: Fragment() {

    companion object {
        const val TAG = "MainFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private lateinit var binding: FragmentMainloginBinding

    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((activity?.application as AppApplication).repository)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mainlogin, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
        binding.buttonLogin.setOnClickListener { launchSignInFlow() }
    }

    private fun observeAuthenticationState() {
        appViewModel.authenticationState.observe(viewLifecycleOwner, androidx.lifecycle.Observer { authenticationState ->
            when (authenticationState) {
                AppViewModel.AuthenticationState.AUTHENTICATED -> {
                    val username = FirebaseAuth.getInstance().currentUser?.displayName.toString()
                    val intent = Intent(activity, HomeActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                }
                else -> {
                    binding.buttonLogin.setOnClickListener {
                        launchSignInFlow()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                val user = User(FirebaseAuth.getInstance().currentUser?.displayName.toString(), 0)
                appViewModel.insert(user)
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }


    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email
        // If users choose to register with their email,
        // they will need to create a password as well
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
                //
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT_CODE
        )
    }
}