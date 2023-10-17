package com.myapp.monarch.earndigital;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RedeemPayTm2 extends AppCompatActivity {
    private SharedPreferences coins, money;
    private String currentMoney;
    private String email;
    private String bankName;
    private String bankAcctNo;
    private String bankAcctName;
    private EditText emailEditText;
    private EditText bankNameEditText, bankAccountNameEditText, bankAccountNumberEditText;
    private RadioButton radioButtonBank, radioButtonPayPal;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef, mRefStatus;
    private float usermoney;
    private int usermoneyCoins, usercoins;
    private int selectedPaymentOption = 0; // 0 for bank, 1 for PayPal
    boolean isValid = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_pay_tm2);
        firebaseAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        bankNameEditText = findViewById(R.id.bankNameEditText);
        bankAccountNameEditText = findViewById(R.id.bankAccountNameEditText);
        bankAccountNumberEditText = findViewById(R.id.bankAccountNumberEditText);
        radioButtonBank = findViewById(R.id.radioButtonBank);
        radioButtonPayPal = findViewById(R.id.radioButtonPayPal);

        radioButtonBank.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPaymentOption = 0; // Bank account
                showBankFields();
            }
        });

        radioButtonPayPal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPaymentOption = 1; // PayPal
                showPayPalField();
            }
        });

        ImageView imageView = findViewById(R.id.imageView12);
        imageView.setOnClickListener(v -> onBackPressed());
        coins = getSharedPreferences("Rewards", MODE_PRIVATE);
        money = getSharedPreferences("Cointomoney", MODE_PRIVATE);
        currentMoney = money.getString("Money", "0");

        Button button = findViewById(R.id.button7);
        button.setOnClickListener(v -> sendMessage2());

//        EditText editText = findViewById(R.id.payTmmobile);
//        editText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (v == editText) {
//                if (hasFocus) {
//                    TextView textView1 = findViewById(R.id.Checkout);
//                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) textView1.getLayoutParams();
//                    params2.setMargins(0, 17, 0, 0);
//                    textView1.setLayoutParams(params2);
//                    ImageView imageView13 = findViewById(R.id.imageView13);
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView13.getLayoutParams();
//                    params.setMargins(0, 40, 0, 0);
//                    imageView13.setLayoutParams(params);
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
//                } else {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//                }
//            }
//        });
    }

    private void showBankFields() {
        bankNameEditText.setVisibility(View.VISIBLE);
        bankAccountNameEditText.setVisibility(View.VISIBLE);
        bankAccountNumberEditText.setVisibility(View.VISIBLE);
        emailEditText.setVisibility(View.GONE);
    }

    private void showPayPalField() {
        emailEditText.setVisibility(View.VISIBLE);
        bankNameEditText.setVisibility(View.GONE);
        bankAccountNameEditText.setVisibility(View.GONE);
        bankAccountNumberEditText.setVisibility(View.GONE);
    }

    private void captureBankPaymentInfo() {
        String bankAccountName = bankAccountNameEditText.getText().toString();
        String bankAccountNumber = bankAccountNumberEditText.getText().toString();
        String bankName = bankNameEditText.getText().toString();

        if (TextUtils.isEmpty(bankAccountName) || TextUtils.isEmpty(bankAccountNumber) || TextUtils.isEmpty(bankName)) {
            // Handle validation errors, e.g., show a Toast
            isValid = false;
            Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
//            return;
        } else
            isValid=true;
        // Save bank payment information to Firebase Database and handle the rest
    }

    private void capturePayPalPaymentInfo() {
        String paypalEmail = emailEditText.getText().toString();

        if (TextUtils.isEmpty(paypalEmail)) {
            // Handle validation errors, e.g., show a Toast
       isValid = false;
            Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
//            return;
        } else
            isValid=true;

        // Save PayPal email to Firebase Database and handle the rest
    }

    private void sendMessage2() {

        if (selectedPaymentOption == 0) {
            captureBankPaymentInfo();
        } else if (selectedPaymentOption == 1) {
            capturePayPalPaymentInfo();
        }
        if(isValid){
            showConfirmationDialog();
//            sendMessage();
        } else

            Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();

    }

    private void sendMessage() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user1 = mAuth.getCurrentUser();
        String userId = user1.getUid();
        mRef = database.getReference().child("Users").child(userId);
        mRef.child("RedeemUSD").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usermoney = Float.parseFloat(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mRef.child("RedeemCoins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usermoneyCoins = Integer.parseInt(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mRef.child("Coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usercoins = Integer.parseInt(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final ProgressDialog dialog = new ProgressDialog(RedeemPayTm2.this);
        dialog.setTitle("Withdrawal Request");
        dialog.setMessage("Processing");
        dialog.show();

        new Handler().postDelayed(() -> {
            Thread sender = new Thread(() -> {
                try {
                    email = emailEditText.getText().toString();
                   bankName = bankNameEditText.getText().toString();
                    bankAcctName = bankAccountNameEditText.getText().toString();
                    bankAcctNo = bankAccountNumberEditText.getText().toString();

                    dialog.dismiss();
                    // Show a successful message dialog
                    runOnUiThread(() -> showSuccessDialog());

                    int result = usercoins - usermoneyCoins;
                    mRef.child("RedeemCoins").removeValue();
                    mRef.child("RedeemUSD").removeValue();

                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId2 = user.getUid();
                    mRefStatus = database.getReference().child("Redeem").push();
                    mRefStatus.child("Status").setValue("Review");
                    mRefStatus.child("Email").setValue(email);
                    mRefStatus.child("BankName").setValue(bankName);
                    mRefStatus.child("BankAcctName").setValue(bankAcctName);
                    mRefStatus.child("BankAcctNo").setValue(bankAcctNo);
                    mRefStatus.child("MoneyUSD").setValue(String.valueOf(usermoney));


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId2).child("Redeem").push();
                    Map<String, Object> map = new HashMap<>();
                            map.put("id", databaseReference.getKey());
                            map.put("email", email);
                    map.put("bank name", bankName);
                    map.put("bank Acct NAme", bankAcctName);
                    map.put("bank Acct Number", bankAcctNo);

                    map.put("Redeem", usermoney);
                    Calendar c = Calendar.getInstance();

                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);
                    String date = day + ". " + month + ". " + year;
                    map.put("Date", date);
                    databaseReference.setValue(map);
//
//                     showMessage("Submitted");
                    SharedPreferences.Editor coinsEdit = coins.edit();
                    coinsEdit.putString("Coins", String.valueOf(result));
                    coinsEdit.apply();

                    Intent intent = new Intent(RedeemPayTm2.this, ChoiceSelection.class);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            });
            sender.start();
        }, 2500);
    }

    private void showSuccessDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("Your withdrawal request has been successfully submitted.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the user's action after seeing the success message
                Intent intent = new Intent(RedeemPayTm2.this, ChoiceSelection.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog successDialog = builder.create();
        successDialog.show();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to proceed with this withdrawal?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, proceed with the action
                performPaymentAction();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled the action
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performPaymentAction() {
        // This is where you should implement the action after confirmation
        // ...
        sendMessage();
        // After performing the action, you can navigate to the next activity or do other tasks
    }
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("Your withdrawal request has been successfully submitted.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the user's action after seeing the success message
                Intent intent = new Intent(RedeemPayTm2.this, ChoiceSelection.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog successDialog = builder.create();
        successDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Redeem.class);
        startActivity(intent);
    }
}
