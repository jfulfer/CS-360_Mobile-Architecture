package com.zybooks.weighttracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SmsActivity extends AppCompatActivity {
    private static final int REQ_SMS = 101;
    private EditText etPhone;
    private TextView tvStatus;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_sms);
        etPhone = findViewById(R.id.etPhone);
        tvStatus = findViewById(R.id.tvStatus);

        findViewById(R.id.btnEnableSms).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, REQ_SMS);
            } else {
                sendTestSms();
            }
        });
    }

    @Override public void onRequestPermissionsResult(int code, @NonNull String[] perms, @NonNull int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        if (code == REQ_SMS) {
            if (res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED) {
                tvStatus.setText("Status: SMS permission granted");
                sendTestSms();
            } else {
                tvStatus.setText("Status: Permission denied (app will continue without SMS)");
                Toast.makeText(this, "SMS disabled. You can still use the app.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendTestSms() {
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            Toast.makeText(this, "Enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone, null, "Test: Goal notifications are enabled.", null, null);
            tvStatus.setText("Status: Test SMS sent");
        } catch (Exception ex) {
            tvStatus.setText("Status: Failed to send SMS (" + ex.getMessage() + ")");
        }
    }
}