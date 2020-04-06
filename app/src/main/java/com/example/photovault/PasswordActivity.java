package com.example.photovault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;

public class PasswordActivity extends AppCompatActivity {

    /* Elements */
    private EditText etPassword;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        /* Elements */
        etPassword = findViewById(R.id.password_et_password);

        btnStart = findViewById(R.id.password_btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStartButton();
            }
        });

        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        EncryptedPreferences encryptedPreferences = new EncryptedPreferences.Builder(this).withEncryptionPassword(androidId).build();
        Toast.makeText(this, ""+encryptedPreferences.getString("password", ""), Toast.LENGTH_SHORT).show();
    }

    private void handleStartButton() {
        if (etPassword.length() == 0) return;

        // Check password
        if (etPassword.getText().toString().equals("666")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // Start messages app
            PackageManager pm = getApplicationContext().getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(Telephony.Sms.getDefaultSmsPackage(getApplicationContext()));
            if (intent != null) {
                startActivity(intent);
            }
        }
    }
}
