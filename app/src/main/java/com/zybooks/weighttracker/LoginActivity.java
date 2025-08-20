package com.zybooks.weighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnSignIn, btnCreateAccount;
    private DbHelper db;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        db = new DbHelper(this);

        TextWatcher enabler = new SimpleTextWatcher(() -> {
            boolean ok = !etUsername.getText().toString().trim().isEmpty()
                    && !etPassword.getText().toString().trim().isEmpty();
            btnSignIn.setEnabled(ok);
        });
        etUsername.addTextChangedListener(enabler);
        etPassword.addTextChangedListener(enabler);

        btnSignIn.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();
            if (db.validateUser(u, p)) {
                startActivity(new Intent(this, DataGridActivity.class));
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateAccount.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean created = db.createUserIfNotExists(u, p);
            Toast.makeText(this, created ? "Account created" : "User already exists", Toast.LENGTH_SHORT).show();
        });
    }

    static class SimpleTextWatcher implements TextWatcher {
        private final Runnable onChange;
        SimpleTextWatcher(Runnable r){ onChange = r; }
        public void beforeTextChanged(CharSequence s,int st,int c,int a) {}
        public void onTextChanged(CharSequence s,int st,int b,int c){ onChange.run(); }
        public void afterTextChanged(Editable s) {}
    }
}