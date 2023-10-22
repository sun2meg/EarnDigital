package com.myapp.monarch.earndigital;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ScheduledExecutorService;

public class ChoiceSelection extends AppCompatActivity implements RewardedVideoAdListener {

    private TextView coins2;
    private boolean connected;
    public SharedPreferences coins;
//    private int currentCoins;
    private String currentCoins;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;

    //    private MaxInterstitialAd interstitialMaxAd;
    private int retryAttempt;
    //    StartAppAd start;
//    private MaxRewardedAd rewardedMaxAd;
    ScheduledExecutorService scheduler;
    private Handler handlerRetryAd;
    String dbCoin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_selection);
        dbCoin =null;
        coins = getSharedPreferences("Rewards", MODE_PRIVATE);

        handlerRetryAd = new Handler();

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        final Handler handler = new Handler();
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

                    Intent intent = new Intent(ChoiceSelection.this, NoInternetActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();

                }
                handler.postDelayed(this, 2000);
            }
        };
        mAuth = FirebaseAuth.getInstance();

//        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); // google

        MobileAds.initialize(this, "ca-app-pub-6799639509419386~6349245996");  //my id
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");  // google
        mInterstitialAd.setAdUnitId("ca-app-pub-6799639509419386/5164156835");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        ///////////////////////////////////////////////////////////////////////


        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

        final Handler handler2 = new Handler();
        final int delay = 1000; //milliseconds
        handler2.postDelayed(new Runnable(){
            public void run(){
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    connected = true;
                }
                else
                    connected = false;
                handler2.postDelayed(this, delay);
            }
        }, delay);



        ImageView settingbtn = (ImageView) findViewById(R.id.imageView9);
        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentActivity(v);
            }
        });



        coins2 = (TextView) findViewById(R.id.textViewCoins);
        FirebaseDatabase database =  FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();
//        Toast.makeText(ChoiceSelection.this, mAuth.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
        String userId = user.getUid();
        mRef =  database.getReference().child("Users").child(userId);

//        mRef.child("Coins").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
//                    try {
//                        currentCoins = Integer.parseInt(dataSnapshot.getValue().toString());
//                        // Now you can safely use 'intValue' as an integer.
//                    } catch (NumberFormatException e) {
//                        Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
//                        // Handle the case where the value is not a valid integer.
//                    }
//
////                if (dataSnapshot.exists()) {
////                    currentCoins = Integer.parseInt(dataSnapshot.getValue(String.class));
//////                        usercoin = dataSnapshot.getValue(Integer.class);
//                } else {
//                    Toast.makeText(getApplicationContext(), "No Coin yet", Toast.LENGTH_SHORT).show();
//                }
//
////                coinsEdit = coins.edit();
////                coinsEdit.putString("Coins", String.valueOf(usercoin));
////                coinsEdit.apply();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
//            }
//        });



        currentCoins = coins.getString("Coins", "0");


        coins2.setText(String.valueOf(currentCoins));
        mRef.child("Email").setValue(user.getEmail());


        CardView cardsmallads = findViewById(R.id.smallads);
        cardsmallads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChoiceSelection.this, "please wait", Toast.LENGTH_SHORT).show();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                else {
//                    Toast.makeText(ChoiceSelection.this, "The Ads wasn't loaded yet. Switching Ad channel", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

            }

        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                coinCount = coinCount + 10;
                SharedPreferences.Editor coinsEdit = coins.edit();
                coinsEdit.putString("Coins", String.valueOf(coinCount));
                coinsEdit.apply();
                coins2.setText(String.valueOf(coinCount));
                mRef.child("Coins").setValue(coinCount);
                Toast.makeText(ChoiceSelection.this, "10 coins received", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        final Handler handler1 = new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                coins2.setText(String.valueOf(coinCount));
                Log.d("Handlers", "Called on main thread");
                handler1.postDelayed(this, 2000);
            }
        };
        handler.post(runnableCode);
    } // end onCreate

    public void saveCoin(int cn) {
        mRef.child("RedeemCoins").setValue(String.valueOf(cn));
    }
    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SettingsActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(SettingsActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
    public void startVideo(View view) {
        Toast.makeText(ChoiceSelection.this, "please wait", Toast.LENGTH_SHORT).show();
        if(mRewardedVideoAd.isLoaded()){
            mRewardedVideoAd.show();
        }  else {
//            Toast.makeText(ChoiceSelection.this, "The Video wasn't loaded yet.Switching Ad Channel", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "The mRewardedVideoAd wasn't loaded yet.");
//            createRewardedAd();
//            rewardedMaxAd.loadAd();
//            showRewardAd();
        }
    }





    public void instruction(View view) {
        Intent openInstructions = new Intent(getApplicationContext(), Instructions.class);
        startActivity(openInstructions);
    }
    public void redeem(View view) {
        Intent openRedeem = new Intent(getApplicationContext(), Redeem.class);
        startActivity(openRedeem);
    }

    public void dailyCheck(View view) {
        Intent openDailyChecks = new Intent(getApplicationContext(), DailyCheckins.class);
        startActivity(openDailyChecks);
    }
    public void luckyWheel(View view) {
        Intent openLuckyWheel = new Intent(getApplicationContext(), LuckyWheel.class);
        startActivity(openLuckyWheel);
        //////////////////////////////////////////////////////////////////////////////
//        applvinterAd.showAd();
//        StartAppAd.showAd(this);
    }
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
    @Override
    public void onRewarded(RewardItem reward) {
        int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
        coinCount = coinCount + 20;
        SharedPreferences.Editor coinsEdit = coins.edit();
        coinsEdit.putString("Coins", String.valueOf(coinCount));
        coinsEdit.apply();
        coins2.setText(String.valueOf(coinCount));
        Toast.makeText(ChoiceSelection.this, "20 coins received", Toast.LENGTH_SHORT).show();
        mRef.child("Coins").setValue(coinCount);
    }
    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()){
//            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build()); //google
            mRewardedVideoAd.loadAd("ca-app-pub-6799639509419386/2130799386", new AdRequest.Builder().build());
        }
    }



    @Override
    public void onRewardedVideoAdLeftApplication() {}
    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }
    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {}
    @Override
    public void onRewardedVideoAdLoaded() {}
    @Override
    public void onRewardedVideoAdOpened() {}
    @Override
    public void onRewardedVideoStarted() {}
    @Override
    public void onRewardedVideoCompleted() {

        int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
        coinCount = coinCount + 20;
        SharedPreferences.Editor coinsEdit = coins.edit();
        coinsEdit.putString("Coins", String.valueOf(coinCount));
        coinsEdit.apply();
        coins2.setText(String.valueOf(coinCount));
        mRef.child("Coins").setValue(coinCount);
        Toast.makeText(ChoiceSelection.this, "20 coins received", Toast.LENGTH_SHORT).show();
        loadRewardedVideoAd();
    }
    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }
    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }



}
