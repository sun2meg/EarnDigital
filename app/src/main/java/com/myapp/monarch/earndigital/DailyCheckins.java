package com.myapp.monarch.earndigital;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class DailyCheckins extends AppCompatActivity {

    private Calendar calendar;
    private int weekday;
    private SharedPreferences coins;
    private Button sun, mon, tue, wed, thu, fri, sat;
    private String todayString;

    private RewardedInterstitialAd rewardedInterstitialAd;

    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_checkins);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadRewardedInterstitialAd();

//        loadRewardedVideoAd();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        } else {

        }
        ImageView imageView = findViewById(R.id.imageView8);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        coins = getSharedPreferences("Rewards", MODE_PRIVATE);
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        weekday = calendar.get(Calendar.DAY_OF_WEEK);
        todayString = year + "" + month + "" + day;
        sun = (Button) findViewById(R.id.btSun);
        mon = (Button) findViewById(R.id.btMon);
        tue = (Button) findViewById(R.id.btTue);
        wed = (Button) findViewById(R.id.btWed);
        thu = (Button) findViewById(R.id.btThu);
        fri = (Button) findViewById(R.id.btFri);
        sat = (Button) findViewById(R.id.btSat);
        sun.setEnabled(false);
        mon.setEnabled(false);
        tue.setEnabled(false);
        wed.setEnabled(false);
        thu.setEnabled(false);
        fri.setEnabled(false);
        sat.setEnabled(false);
        SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
        boolean currentDay = dailyChecks.getBoolean(todayString, false);
        if (weekday==1){
            if (currentDay){
                sun.setBackground(getResources().getDrawable(R.drawable.back11));
            }else {
            sun.setEnabled(true);
            sun.setAlpha(0f);
            sun.setBackground(getResources().getDrawable(R.drawable.back1now));
            sun.setTextColor(Color.WHITE);
        }}
        else if (weekday==2){
            if (currentDay){
                mon.setBackground(getResources().getDrawable(R.drawable.back11));
            }else {
            mon.setEnabled(true);
            mon.setAlpha(1f);
            mon.setBackground(getResources().getDrawable(R.drawable.back1now));
            mon.setTextColor(Color.WHITE);
        }}
        else if (weekday==3){
            if (currentDay){
                tue.setBackground(getResources().getDrawable(R.drawable.back11));
            }else {
            tue.setEnabled(true);
            tue.setAlpha(1f);
            tue.setBackground(getResources().getDrawable(R.drawable.back1now));
            tue.setTextColor(Color.WHITE);
        }}
        else if (weekday==4){
            if (currentDay){
                wed.setBackground(getResources().getDrawable(R.drawable.back11));
            }else {
            wed.setEnabled(true);
            wed.setAlpha(1f);
            wed.setBackground(getResources().getDrawable(R.drawable.back1now));
            wed.setTextColor(Color.WHITE);
        }}
        else if (weekday==5){
            if (currentDay){
                thu.setBackground(getResources().getDrawable(R.drawable.back1));
            }else {
            thu.setEnabled(true);
            thu.setAlpha(1f);
            thu.setBackground(getResources().getDrawable(R.drawable.back1now));
            thu.setTextColor(Color.WHITE);
        }}
        else if (weekday==6){
            if (currentDay){
                fri.setBackground(getResources().getDrawable(R.drawable.back11));
            }else {
            fri.setEnabled(true);
            fri.setAlpha(1f);
            fri.setBackground(getResources().getDrawable(R.drawable.back1now));
            fri.setTextColor(Color.WHITE);
        }}
        else if (weekday==7){
            if (currentDay){
                sat.setBackground(getResources().getDrawable(R.drawable.back11));
            }else {
            sat.setEnabled(true);
            sat.setAlpha(1f);
            sat.setBackground(getResources().getDrawable(R.drawable.back1now));
            sat.setTextColor(Color.WHITE);
        }}
    }
//    private void loadRewardedVideoAd() {
//        if (!mRewardedVideoAd.isLoaded()){
////            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build()); //google
//            mRewardedVideoAd.loadAd("ca-app-pub-6799639509419386/2130799386", new AdRequest.Builder().build());
//        }
//    }
    public void monCheck(View view) {
        SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
        boolean currentDay = dailyChecks.getBoolean(todayString, false);
        if (!currentDay){

            if(rewardedInterstitialAd != null){
               showRewardedIntAd();
            }  else {
                Log.d("TAG", "The mRewardedVideoAd wasn't loaded yet.");

            }
        } else {
            Toast.makeText(this, "Reward already recieved", Toast.LENGTH_SHORT).show();
        }}
    public void tueCheck(View view) {
        SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
        boolean currentDay = dailyChecks.getBoolean(todayString, false);
        if (!currentDay){

            showRewardedIntAd();

          } else {
            Toast.makeText(this, "Reward already recieved", Toast.LENGTH_SHORT).show();
        }}
    public void wedCheck(View view) {
        SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
        boolean currentDay = dailyChecks.getBoolean(todayString, false);
        if (!currentDay){

            showRewardedIntAd();

           } else {
            Toast.makeText(this, "Reward already recieved", Toast.LENGTH_SHORT).show();
        }}

    public void thuCheck(View view) {
        SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
        boolean currentDay = dailyChecks.getBoolean(todayString, false);
        if (!currentDay){

            showRewardedIntAd();

        } else {
            Toast.makeText(this, "Reward already recieved", Toast.LENGTH_SHORT).show();
        }}

    public void friCheck(View view) {
        SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
        boolean currentDay = dailyChecks.getBoolean(todayString, false);
        if (!currentDay){
            showRewardedIntAd();

          } else {
            Toast.makeText(this, "Reward already recieved", Toast.LENGTH_SHORT).show();
        }}

    public void satCheck(View view) {
        SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
        boolean currentDay = dailyChecks.getBoolean(todayString, false);
        if (!currentDay){
            showRewardedIntAd();

         }else {
            Toast.makeText(this, "Reward already recieved", Toast.LENGTH_SHORT).show();
        }}

    public void sunCheck(View view) {
        SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
        boolean currentDay = dailyChecks.getBoolean(todayString, false);
        if (!currentDay){
            showRewardedIntAd();

        } else {
            Toast.makeText(this, "Reward already recieved", Toast.LENGTH_SHORT).show();
        }}

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,ChoiceSelection.class);
        startActivity(intent);
    }
//    @Override
//    public void onRewarded(RewardItem reward) {
//        SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
//        Toast.makeText(this, "50 Coins Recieved!", Toast.LENGTH_SHORT).show();
//        SharedPreferences.Editor daily = dailyChecks.edit();
//        daily.putBoolean(todayString, true);
//        daily.apply();
//        int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
//        coinCount = coinCount + 50;
//        SharedPreferences.Editor coinsEdit = coins.edit();
//        coinsEdit.putString("Coins", String.valueOf(coinCount));
//        coinsEdit.apply();
//    }

    public void loadRewardedInterstitialAd(){
        RewardedInterstitialAd.load(DailyCheckins.this, "ca-app-pub-5836526993277102/1225248532",
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                rewardedInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                rewardedInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                loadRewardedInterstitialAd();
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                    }
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
//                        rewardedInterstitialAd = null;
                        Toast.makeText (DailyCheckins.this, loadAdError.getMessage(), Toast.LENGTH_LONG).show();
                        loadRewardedInterstitialAd();

                    }
                });
//        showRewardedIntAd();
    }

    private void showRewardedIntAd() {

        if (rewardedInterstitialAd != null) {
            Activity activityContext = DailyCheckins.this;
            rewardedInterstitialAd.show(
                    activityContext,
                    new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            SharedPreferences dailyChecks = getSharedPreferences("DAILYCHECKS", 0);
                            Toast.makeText(DailyCheckins.this, "50 Coins Recieved!", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor daily = dailyChecks.edit();
                            daily.putBoolean(todayString, true);
                            daily.apply();
                            int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                            coinCount = coinCount + 50;
                            SharedPreferences.Editor coinsEdit = coins.edit();
                            coinsEdit.putString("Coins", String.valueOf(coinCount));
                            coinsEdit.apply();

//                            int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
//                            coinCount = coinCount + 50;
//                            SharedPreferences.Editor coinsEdit = coins.edit();
//                            coinsEdit.putString("Coins", String.valueOf(coinCount));
//                            coinsEdit.apply();

//        mRef.child("Coins").setValue(coinCount);
//                            Toast.makeText(DailyCheckins.this, "50 coins received", Toast.LENGTH_SHORT).show();
                         loadRewardedInterstitialAd();

//                            int rewardAmount = rewardItem.getAmount();
//                            Toast.makeText(getApplicationContext(), String.valueOf(rewardAmount), Toast.LENGTH_SHORT).show();
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
//                      loadRewardedInterstitialAd();//continous ads
                        }
                    });
        }
    }

}
