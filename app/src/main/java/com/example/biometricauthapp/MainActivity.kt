package com.example.biometricauthapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import com.example.biometricauthapp.ui.theme.BiometricAuthAppTheme

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricManager(this)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiometricAuthAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val biometricResult by promptManager.promptResults.collectAsState(initial = null)

                    val enrollLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            println("Activity Result : $it")
                        }
                    )
                    LaunchedEffect(biometricResult) {
                        if (biometricResult is BiometricManager.BiometricResult.AuthenticationNotSet){
                            if (Build.VERSION.SDK_INT >= 30) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                    )
                                }

                                enrollLauncher.launch(enrollIntent)

                            }
                        }

                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            promptManager.showBiometricPrompt(
                                "Sameple Prompt",
                                "Sample Prompt Description"
                            )
                        }) {
                            Text(text = "Authenticate")
                        }

                        biometricResult?.let { result ->
                            Text(
                                text = when(result) {
                                    is BiometricManager.BiometricResult.AuthenticationError -> {
                                        "Authentication error: ${result.error}"
                                    }
                                    BiometricManager.BiometricResult.AuthenticationFailed -> {
                                        "Authentication failed"
                                    }
                                    BiometricManager.BiometricResult.AuthenticationNotSet -> {
                                        "No biometric credentials enrolled"
                                    }
                                    BiometricManager.BiometricResult.AuthenticationSuccess -> {
                                        "Authentication succeeded"
                                    }
                                    BiometricManager.BiometricResult.FeatureUnavailable -> {
                                        "Biometric features are not available on this device"
                                    }
                                    BiometricManager.BiometricResult.HardwareUnavailable -> {
                                        "Biometric hardware is unavailable"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BiometricAuthAppTheme {
        Greeting("Android")
    }
}