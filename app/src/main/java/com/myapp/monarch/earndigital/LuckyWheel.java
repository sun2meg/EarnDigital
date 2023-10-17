package com.myapp.monarch.earndigital;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class LuckyWheel extends AppCompatActivity {
    private AdView adView;
    private Calendar calendar;
    private int weekday;
    private String todayString;
    List<LuckyItem> data = new ArrayList<>();
    private int coin;
    private InterstitialAd mInterstitialAd;
    private FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private int retryAttempt;
    AdRequest adRequest;
    private InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lucky_wheel);

        final Handler handler = new Handler();
        adRequest = new AdRequest.Builder().build();
        adView = findViewById(R.id.adView);
        mAuth = FirebaseAuth.getInstance();
         Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (null != activeNetwork) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) { }
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) { }
                } else {

                    Intent intent = new Intent(LuckyWheel.this, NoInternetActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();

                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

        FirebaseDatabase database =  FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();
//        Toast.makeText(ChoiceSelection.this, mAuth.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
        String userId = user.getUid();
        mRef =  database.getReference().child("Users").child(userId);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView.loadAd(adRequest);
       loadInter();


//        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); // google id
        adView.loadAd(adRequest);
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-6799639509419386/5164156835");
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //google unit id
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {}
//            @Override
//            public void onAdFailedToLoad(int errorCode) {}
//            @Override
//            public void onAdOpened() {
//                final SharedPreferences coins = getSharedPreferences("Rewards", MODE_PRIVATE);
//
//                Toast.makeText(getApplicationContext(), String.valueOf("+ " + coin +" Coins"), Toast.LENGTH_SHORT).show();
//                int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
//                coinCount = coinCount + (coin);
//                SharedPreferences.Editor coinsEdit = coins.edit();
//                coinsEdit.putString("Coins", String.valueOf(coinCount));
//                coinsEdit.apply();
//                mRef.child("Coins").setValue(coinCount);
//            }
//            @Override
//            public void onAdClicked() {}
//            @Override
//            public void onAdLeftApplication() {}
//            @Override
//            public void onAdClosed() {}
//        });

        ImageView imageView = findViewById(R.id.imageView11);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final SharedPreferences coins = getSharedPreferences("Rewards", MODE_PRIVATE);
        final LuckyWheelView luckyWheelView = (LuckyWheelView) findViewById(R.id.luckyWheel);
        findViewById(R.id.play).setEnabled(true);
        findViewById(R.id.play).setAlpha(1f);
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        weekday = calendar.get(Calendar.DAY_OF_WEEK);
        todayString = year + "" + month + "" + day;
        final SharedPreferences spinChecks = getSharedPreferences("SPINCHECK", 0);
        final boolean currentDay = spinChecks.getBoolean(todayString, false);

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.text = "0";
        luckyItem1.color = Color.parseColor("#8574F1");
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.text = "10";
        luckyItem2.color = Color.parseColor("#8E84FF");
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.text = "20";
        luckyItem3.color = Color.parseColor("#752BEF");
        data.add(luckyItem3);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.text = "30";
        luckyItem4.color = ContextCompat.getColor(getApplicationContext(), R.color.Spinwell140);
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.text = "40";
        luckyItem5.color = Color.parseColor("#8574F1");
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.text = "50";
        luckyItem6.color = Color.parseColor("#8E84FF");
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.text = "60";
        luckyItem7.color = Color.parseColor("#752BEF");
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.text = "70";
        luckyItem8.color = ContextCompat.getColor(getApplicationContext(), R.color.Spinwell140);
        data.add(luckyItem8);

        luckyWheelView.setData(data);
        luckyWheelView.setRound(getRandomRound());

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadInter();
                    int index = getRandomIndex();
                    luckyWheelView.startLuckyWheelWithTargetIndex(index);
                    SharedPreferences.Editor spins = spinChecks.edit();
                    spins.putBoolean(todayString, true);
                    spins.apply();
                    findViewById(R.id.play).setEnabled(false);
                    findViewById(R.id.play).setAlpha(.5f);
            }
        });

        luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                if (index ==1 ){
                     coin = 0;
                } if (index ==2 ){
                    coin = 10;
                } if (index ==3 ){
                    coin = 20;
                } if (index ==4 ){
                    coin = 30;
                } if (index ==5){
                    coin = 40;
                } if (index ==6 ){
                    coin = 50;
                } if (index ==7 ){
                    coin = 60;
                } if (index ==8 ){
                    coin = 70; }

                findViewById(R.id.play).setEnabled(true);
                findViewById(R.id.play).setAlpha(1f);
              showInterstitialAd();
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                } else {
//                    Log.d("TAG", "The interstitial wasn't loaded yet. switching ad");
//
//                }

            }
        });
    }
    private void loadInter() {
//        InterstitialAd.load(this, "ca-app-pub-5836526993277102/4593647902", adRequest, new InterstitialAdLoadCallback() {
//        InterstitialAd.load(this,    "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
        InterstitialAd.load(this, "ca-app-pub-5836526993277102/4593647902", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                interstitialAd = ad;
                Toast.makeText(LuckyWheel.this, "Loaded", Toast.LENGTH_LONG).show();
                showInterstitialAd();
//                showAdBtn.setEnabled(true);
                // You can now show the ad when it's loaded
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                interstitialAd = null;
                Toast.makeText(LuckyWheel.this, loadAdError.getMessage(), Toast.LENGTH_LONG).show();
//                showAdBtn.setEnabled(false);
//                showInter();
            }
        });
    }

    private void showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.show(LuckyWheel.this);
        }
    }


    private int getRandomIndex() {
        int[] ind = new int[] {1,2,3,4,5,6,7,8};
        int rand = new Random().nextInt(ind.length);
        return ind[rand];
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }

    @Override
    public void onBackPressed() {
       Intent intent = new Intent(this, ChoiceSelection.class);
       startActivity(intent);
    }

}
