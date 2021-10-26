package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity() {
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var signInButton: SignInButton? = null
    private var txtEmail: TextView? = null
    private var btnLogout: Button? = null
    private val RC_SIGN_IN = 1
    private val TAG = "Main screen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso: GoogleSignInOptions = Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        txtEmail = findViewById(R.id.txtEmail)
        btnLogout = findViewById(R.id.btnLogout)
        signInButton = findViewById(R.id.btnSignIn)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener { view -> signIn() }
        btnLogout.setOnClickListener(View.OnClickListener { view: View? -> signOut() })

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode())
            updateUI(null)
        }
    }

    private fun signIn() {
        val intent: Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(intent, RC_SIGN_IN)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, object : OnCompleteListener<Void?>() {
                override fun onComplete(task: Task<Void?>) {
                    updateUI(null)
                }
            })
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account == null) {
            signInButton.setEnabled(true)
            btnLogout!!.isEnabled = false
            txtEmail!!.text = "Chưa login!"
            return
        }
        signInButton.setEnabled(false)
        btnLogout!!.isEnabled = true
        txtEmail.setText(account.getEmail().toLowerCase())
    }
}