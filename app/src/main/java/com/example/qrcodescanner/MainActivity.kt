package com.example.qrcodescanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {
    private lateinit var scanButton: Button
    private lateinit var scannedText: TextView
    private var scannedUrl: String? = null // Store scanned URL for clicking

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.let { handleScanResult(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanButton = findViewById(R.id.scan_button)
        scannedText = findViewById(R.id.scanned_text)

        requestCameraPermission() // Request camera permission

        scanButton.setOnClickListener {
            val options = ScanOptions().setPrompt("Scan a QR code")
                .setCameraId(0)
                .setOrientationLocked(false)
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(true)
                .setTorchEnabled(false)
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)


            barcodeLauncher.launch(options)
        }

        scannedText.setOnClickListener {
            scannedUrl?.let { url ->
                openLink(url)
            }
        }
    }

    private fun handleScanResult(result: String) {
        scannedText.text = result
        scannedUrl = result // Store the scanned URL

        when {
            result.startsWith("http") -> {
                // You could also directly open here if you want
            }
            result.startsWith("mailto:") -> sendEmail(result)
            result.contains("phonepe") -> handlePhonePePayment(result)
            else -> showToast("Scanned text: $result")
        }
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(email))
        startActivity(intent)
    }

    private fun handlePhonePePayment(url: String) {
        openLink(url) // Open the PhonePe payment link
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            val CAMERA_REQUEST_CODE = 0
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
