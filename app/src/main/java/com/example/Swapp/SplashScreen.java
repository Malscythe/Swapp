package com.example.Swapp;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.chrisbanes.photoview.PhotoView;

import Swapp.R;

public class SplashScreen extends AppCompatActivity {

    private ImageView logoPic;
    private TextView logoText;
    private TextView logoDesc;
    private RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        logoPic = findViewById(R.id.logo);
        logoText = findViewById(R.id.logoText);
        logoDesc = findViewById(R.id.logoDesc);
        rootLayout = findViewById(R.id.rootLayout);

        rootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.RotateIn)
                        .duration(1000)
                        .onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                YoYo.with(Techniques.FadeInDown)
                                        .duration(1000)
                                        .onStart(new YoYo.AnimatorCallback() {
                                            @Override
                                            public void call(Animator animator) {
                                                logoText.setVisibility(View.VISIBLE);

                                            }
                                        })
                                        .onEnd(new YoYo.AnimatorCallback() {
                                            @Override
                                            public void call(Animator animator) {
                                                YoYo.with(Techniques.FadeInUp)
                                                        .duration(1000)
                                                        .onStart(new YoYo.AnimatorCallback() {
                                                            @Override
                                                            public void call(Animator animator) {
                                                                logoDesc.setVisibility(View.VISIBLE);

                                                            }
                                                        })
                                                        .playOn(logoDesc);
                                            }
                                        })
                                        .playOn(logoText);
                            }
                        })
                        .playOn(logoPic);
            }
        },50);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent sharedIntent = new Intent(SplashScreen.this, login.class);

                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(logoPic, "imageTransition");
                pairs[1] = new Pair<View, String>(logoText, "textTransition1");
                pairs[2] = new Pair<View, String>(logoDesc, "textTransition2");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, pairs);
                startActivity(sharedIntent, options.toBundle());
            }
        }, 5000);
    }
}
