package com.example.androidsync.data.remote.drive

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import android.content.Context

class DriveServiceProvider(private val context: Context) {

    private var driveService: Drive? = null

    fun getDriveService(accountEmail: String): Drive {
        driveService?.let { return it }

        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_FILE),
        )
        credential.selectedAccountName = accountEmail

        val service = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential,
        )
            .setApplicationName("AndroidSync")
            .build()

        driveService = service
        return service
    }

    fun clearService() {
        driveService = null
    }
}
