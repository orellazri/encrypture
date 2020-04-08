package com.reaperberri.encrypture;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;

public class WelcomeActivity extends AppCompatActivity {

    private EncryptedPreferences encryptedPreferences;

    /* Elements */
    private EditText etPassword;
    private Button btnSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        encryptedPreferences = new EncryptedPreferences.Builder(this).withEncryptionPassword(androidId).build();

        /* Elements */
        etPassword = findViewById(R.id.welcome_et_password);
        btnSet = findViewById(R.id.welcome_btn_set_password);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetButton();
            }
        });
    }

    private void handleSetButton() {
        if (etPassword.getText().toString().equals("")) return;

        encryptedPreferences.edit().putString("password", etPassword.getText().toString()).apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
