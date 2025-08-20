package com.zybooks.weighttracker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import androidx.core.app.ActivityCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DataGridActivity extends AppCompatActivity {
    private EditText etPhone;
    private EditText etGoal;
    private TableLayout table;
    private EditText etDate, etWeight;
    private DbHelper db;
    private long currentUserId = 1;
    private boolean isFormattingDate = false;

    @Override protected void onCreate(Bundle b) {

        // Request SMS permission at runtime if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        super.onCreate(b);
        setContentView(R.layout.activity_data_grid);
        etPhone = findViewById(R.id.etPhone);
        table = findViewById(R.id.table);
        etDate = findViewById(R.id.etDate);
        etWeight = findViewById(R.id.etWeight);
        etGoal = findViewById(R.id.etGoal);
        db = new DbHelper(this);

        Double existingGoal = db.getGoal(currentUserId);
        if (existingGoal != null) {
            etGoal.setText(String.valueOf(existingGoal));
        }

        etDate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (isFormattingDate) return;
                isFormattingDate = true;

                String input = s.toString();
                if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    isFormattingDate = false;
                    return;
                }
                String digits = input.replaceAll("[^0-9]", "");
                if (digits.length() >= 8) {
                    String year  = digits.substring(0, 4);
                    String month = digits.substring(4, 6);
                    String day   = digits.substring(6, 8);
                    String formatted = year + "-" + month + "-" + day;
                    s.replace(0, s.length(), formatted);
                }
                isFormattingDate = false;
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(v -> {

            String goalInput = etGoal.getText().toString().trim();
            if (!goalInput.isEmpty()) {
                double goalVal = Double.parseDouble(goalInput);
                db.updateGoal(currentUserId, goalVal); // We'll implement this next
            }


            String date = etDate.getText().toString().trim();
            String w = etWeight.getText().toString().trim();
            if (date.isEmpty() || w.isEmpty()) {
                Toast.makeText(this, "Enter date and weight", Toast.LENGTH_SHORT).show();
                return;
            }
            double val = Double.parseDouble(w);
            long id = db.insertWeight(currentUserId, date, val);
            if (id > 0) {
                Double goal = db.getGoal(currentUserId);

                if (goal != null && val <= goal) {
                    Toast.makeText(this, "Goal reached!", Toast.LENGTH_LONG).show();

                    String phoneNumber = etPhone.getText().toString().trim();
                    if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
                        Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String message = "Congratulations! You've reached your weight goal of " + goal + " lbs.";

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(phoneNumber, null, message, null, null);
                    } else {
                        Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
                    }
                }

                etDate.setText("");
                etWeight.setText("");
                reloadTable();
            }
        });

        reloadTable();
    }

    private String normalizeDate(String input) {
        if (input == null) return null;
        input = input.trim();
        if (input.matches("\\d{4}-\\d{2}-\\d{2}")) return input;

        String digits = input.replaceAll("[^0-9]", "");
        if (digits.length() == 8) {
            String y = digits.substring(0, 4);
            String m = digits.substring(4, 6);
            String d = digits.substring(6, 8);
            int mi = Integer.parseInt(m), di = Integer.parseInt(d);
            if (mi >= 1 && mi <= 12 && di >= 1 && di <= 31) {
                return y + "-" + m + "-" + d;
            }
        }
        return null;
    }

    private void reloadTable() {
        while (table.getChildCount() > 1) table.removeViewAt(1);

        List<WeightRow> rows = db.getWeights(currentUserId); // id, date, value
        for (WeightRow r : rows) {
            TableRow tr = new TableRow(this);

            TextView tvDate = new TextView(this);
            tvDate.setText(r.date);

            TextView tvVal = new TextView(this);
            tvVal.setText(String.valueOf(r.value));

            // Delete Action
            Button btnDel = new Button(this);
            btnDel.setText("Delete");
            btnDel.setOnClickListener(v -> {
                db.deleteWeight(r.id);
                reloadTable();
            });

            // Edit Action
            Button btnEdit = new Button(this);
            btnEdit.setText("Edit");
            btnEdit.setOnClickListener(v -> {

                // inflate teh edit layout
                View form = getLayoutInflater().inflate(R.layout.dialog_edit_weight, null);
                EditText etDate = form.findViewById(R.id.etDateEdit);
                EditText etWeight = form.findViewById(R.id.etWeightEdit);

                // Prefill with current data so user can edit as needed
                etDate.setText(r.date);
                etWeight.setText(String.valueOf(r.value));

                // Create dialog used for editing values
                new AlertDialog.Builder(this)
                        .setTitle("Edit Entry")
                        .setView(form)
                        .setPositiveButton("Save", (d, which) -> {

                            // Use normalize method to ensure date format
                            String nd = normalizeDate(etDate.getText().toString());
                            if (nd == null) {
                                Toast.makeText(this, "Enter date as YYYY-MM-DD",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            double nv;

                            // Verify valid input for weight
                            try {
                                nv = Double.parseDouble(etWeight.getText().toString().trim());
                            } catch (NumberFormatException e) {
                                Toast.makeText(this, "Enter a valid number for weight",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // If checks above passed, update database and return rows affected
                            int rows_updated = db.updateWeight(r.id, nd, nv);
                            if (rows_updated > 0) {
                                reloadTable();
                            } else {
                                Toast.makeText(this, "No updates were made (Check ID)",
                                        Toast.LENGTH_SHORT).show();
                            }

                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            // Add buttons to view
            tr.addView(tvDate);
            tr.addView(tvVal);
            tr.addView(btnEdit);
            tr.addView(btnDel);
            table.addView(tr);
        }
    }

    static class WeightRow {
        long id; String date; double value;
        WeightRow(long id, String date, double v){ this.id=id; this.date=date; this.value=v; }
    }
}