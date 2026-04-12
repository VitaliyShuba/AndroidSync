package com.example.androidsync.data.remote.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

data class GoogleAccount(
    val email: String,
    val displayName: String?,
    val idToken: String,
)

class GoogleAuthManager(
    private val context: Context,
) {
    private val credentialManager = CredentialManager.create(context)

    private var cachedAccount: GoogleAccount? = null

    val currentAccount: GoogleAccount? get() = cachedAccount

    val isSignedIn: Boolean get() = cachedAccount != null

    suspend fun signIn(activityContext: Context, serverClientId: String): GoogleAccount {
        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        val result = credentialManager.getCredential(activityContext, request)

        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
            val account = GoogleAccount(
                email = googleIdToken.id,
                displayName = googleIdToken.displayName,
                idToken = googleIdToken.idToken,
            )
            cachedAccount = account
            return account
        }

        throw IllegalStateException("Unexpected credential type: ${credential.type}")
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        cachedAccount = null
    }
}
