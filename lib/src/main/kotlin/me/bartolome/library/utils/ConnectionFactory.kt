package me.bartolome.library.utils

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import me.bartolome.library.CreateXlsx

fun connectionFactoryWithApiGoogle(): Drive? {
    val APPLICATION_NAME = "Google-Api-Connector"
    val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val CREDENTIALS_FILE_PATH = "/credentials-google-service-account.json"
    val credentialsFile = CreateXlsx::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
    val SCOPES: List<String> = listOf(
        DriveScopes.DRIVE_METADATA,
        DriveScopes.DRIVE,
        DriveScopes.DRIVE_APPDATA
    )
    val credentials = GoogleCredential.fromStream(credentialsFile).createScoped(SCOPES)
    return Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials).setApplicationName(APPLICATION_NAME)
        .build()
}
