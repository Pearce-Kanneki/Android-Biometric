package com.kanneki.biometric_xml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.kanneki.biometric_xml.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkBiometric()
        biometricPrompt = getBiometricPrompt()
        promptInfo = getPromptInfo()

        binding.button.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun checkBiometric() {
        BiometricManager.from(this).also { bm ->
            when(bm.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    binding.textView.text = "可以使用生物識別進行身份驗證"
                    binding.button.isEnabled = true
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    binding.textView.text = "此設備上沒有可用的生物識別功能"
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    binding.textView.text = "生物識別功能目前不可用"
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    binding.textView.text = "尚未接受憑證"
                }
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                    binding.textView.text = "與當前版本不兼容"
                    binding.button.isEnabled = true
                }
                else -> {
                    binding.textView.text = "未知狀況"
                }
            }
        }
    }
    
    private fun getBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)
        return BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext, 
                        "Authentication succeeded!", 
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext, 
                        "Authentication error: $errString", 
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext,
                        "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }
}