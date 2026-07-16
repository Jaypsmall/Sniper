package com.example.sniper

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val isSystemDark = isSystemInDarkTheme()
                    var currentColor by remember { mutableStateOf(Color.White) }
                    var isDarkMode by remember { mutableStateOf(isSystemDark) }
                    var isGoldMode by remember { mutableStateOf(true) }

                    SniperMainScreen(
                        currentColor = currentColor,
                        isDarkMode = isDarkMode,
                        isGoldMode = isGoldMode,
                        uiAccentColor = Color(0xFF6200EE),
                        onColorCaptured = { currentColor = it },
                        onColorConfirmed = { color ->
                            val hex = ColorManager.colorToHex(color)
                            addToFavorites(hex)
                            copyToClipboard(hex)
                        }
                    )
                }
            }
        }
    }

    private fun addToFavorites(hex: String) {
        if (!favorites.contains(hex)) {
            favorites.add(hex)
            Toast.makeText(this, "Añadido a favoritos: $hex", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ya está en favoritos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Color Hex", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copiado: $text", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        baseContext, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    companion object {
        val favorites = mutableListOf<String>()
    }
}
