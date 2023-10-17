package com.myapp.monarch.earndigital;


import static android.content.ContentValues.TAG;

import android.app.Activity;
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

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ScheduledExecutorService;

public class ChoiceSelection extends AppCompatActivity  {

    private TextView coins2;
    private boolean connected;
    public SharedPreferences coins;
    private String currentCoins;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private RewardedInterstitialAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;
    private RewardedAd rewardedAd;
    //    private MaxInterstitialAd interstitialMaxAd;
    private int retryAttempt;
    //    StartAppAd start;
//    private MaxRewardedAd rewardedMaxAd;
    ScheduledExecutorService scheduler;
    private Handler handlerRetryAd;
    String dbCoin;
    private AdRequest adRequest;

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

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
//        adRequest = new AdRequest.Builder().build();
        loadInter();
        loadRewardedAd();

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
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mRef = database.getReference().child("Users").child(userId);
            currentCoins = coins.getString("Coins", "0");
            // Rest of your code that relies on the user being authenticated
        } else {
            Toast.makeText(ChoiceSelection.this, "Not Authenticated", Toast.LENGTH_SHORT).show();
            // Handle the case where the user is not authenticated
            // You may want to redirect the user to the login screen or take appropriate action.
            Intent openInstructions = new Intent(getApplicationContext(), AgreeActivity.class);
            startActivity(openInstructions);
        }


//        FirebaseUser user =  mAuth.getCurrentUser();
////        Toast.makeText(ChoiceSelection.this, mAuth.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
//        String userId = user.getUid();
//        mRef =  database.getReference().child("Users").child(userId);
//        currentCoins = coins.getString("Coins", "0");


        coins2.setText(currentCoins);
        mRef.child("Email").setValue(user.getEmail());
        mRef.child("Coins").setValue(currentCoins);
        coins2 = (TextView) findViewById(R.id.textViewCoins);

        CardView cardsmallads = findViewById(R.id.smallads);
        cardsmallads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChoiceSelection.this, "please wait", Toast.LENGTH_SHORT).show();
                showInterstitialAd();

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


    private void loadInter() {
//        InterstitialAd.load(this, "ca-app-pub-5836526993277102/4593647902", adRequest, new InterstitialAdLoadCallback() {
//        InterstitialAd.load(this,    "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
        adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-5836526993277102/4593647902", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                mInterstitialAd = ad;
//                showInterstitialAd();
                int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                coinCount = coinCount + 20;
                SharedPreferences.Editor coinsEdit = coins.edit();
                coinsEdit.putString("Coins", String.valueOf(coinCount));
                coinsEdit.apply();
                coins2.setText(String.valueOf(coinCount));
                mRef.child("Coins").setValue(coinCount);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
                Toast.makeText(ChoiceSelection.this, loadAdError.getMessage(), Toast.LENGTH_LONG).show();
//                showAdBtn.setEnabled(false);
//                showInter();
            }

        });

    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(ChoiceSelection.this);
        }
    }

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
        showRewardedAd();
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

    private void loadRewardedAd() {
        adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-5836526993277102/3280566233", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedAd ad) {
                rewardedAd = ad;
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
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
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.e(TAG, "Ad failed to show fullscreen content.");
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d(TAG, "Ad recorded an impression.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad showed fullscreen content.");
                    }
                });
//                showRewardedAd();

            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                rewardedAd = null;
                Toast.makeText (ChoiceSelection.this, loadAdError.getMessage(), Toast.LENGTH_LONG).show();

            }

        });
    }

    private void showRewardedAd() {

        if (rewardedAd != null) {
            Activity activityContext = ChoiceSelection.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");

                    int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                    coinCount = coinCount + 50;
                    SharedPreferences.Editor coinsEdit = coins.edit();
                    coinsEdit.putString("Coins", String.valueOf(coinCount));
                    coinsEdit.apply();
                    coins2.setText(String.valueOf(coinCount));
                    mRef.child("Coins").setValue(coinCount);
                    Toast.makeText(ChoiceSelection.this, "50 coins received", Toast.LENGTH_SHORT).show();
                    loadRewardedAd();

//                    int rewardAmount = rewardItem.getAmount();
//                    String rewardType = rewardItem.getType();
//                    Toast.makeText(getApplicationContext(), String.valueOf(rewardAmount), Toast.LENGTH_SHORT).show();
//                    loadRewardedAd();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }

    }




}
