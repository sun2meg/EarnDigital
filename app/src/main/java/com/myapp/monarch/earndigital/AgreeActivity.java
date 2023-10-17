package com.myapp.monarch.earndigital;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;

public class AgreeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String AGREEMENT_ACCEPTED = "agreementAccepted";

    private CheckBox agreementCheckbox;
    private Button continueButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if the user has already accepted the agreement
        if (prefs.getBoolean(AGREEMENT_ACCEPTED, false)) {
            // User has already accepted, start EmailPasswordActivity (or your main activity)
            startEmailPasswordActivity();
        } else {
            // User hasn't accepted the agreement yet, show the agreement screen
            setContentView(R.layout.activity_agree);

            agreementCheckbox = findViewById(R.id.checkbox_agreement);
            continueButton = findViewById(R.id.button_continue);

            // Disable the "Continue" button until the user accepts the agreement
            continueButton.setEnabled(false);

            agreementCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Enable the "Continue" button if the user accepts the agreement
                continueButton.setEnabled(isChecked);
            });

            continueButton.setOnClickListener(view -> {
                // Set the flag in SharedPreferences to indicate the user has accepted the agreement
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(AGREEMENT_ACCEPTED, true);
                editor.apply();

                // Start EmailPasswordActivity (or your main activity)
                startEmailPasswordActivity();
            });
        }
    }

    private void startEmailPasswordActivity() {
        Intent intent = new Intent(this, EmailPasswordActivity.class);
        startActivity(intent);
        finish(); // Close the UserAgreementActivity
    }
}
