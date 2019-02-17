package com.aurak.smartuni.smartuni.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.aurak.smartuni.smartuni.HomeActivity;
import com.aurak.smartuni.smartuni.R;

public class MainActivity extends AppCompatActivity {
    Button btn;
    Animation fromBottom;
    Animation Scale;
    Animation Scale2;
    TextView textView;
    com.github.florent37.materialtextfield.MaterialTextField formView2;
    com.github.florent37.materialtextfield.MaterialTextField formView3;
    com.github.florent37.materialtextfield.MaterialTextField formView4;
    com.github.florent37.materialtextfield.MaterialTextField formView5;
    View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.email_sign_up_button);
        textView = findViewById(R.id.textView7);
        formView2 = findViewById(R.id.firstNa);
        formView3 = findViewById(R.id.lastNa);
        formView4 = findViewById(R.id.EmailNa);
        formView5 = findViewById(R.id.passNa);
        Scale = AnimationUtils.loadAnimation(this, R.anim.textanim);
        Scale2 = AnimationUtils.loadAnimation(this, R.anim.animx2);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.anim);
        btn.setAnimation(fromBottom);
        textView.setAnimation(Scale);
        formView2.setAnimation(Scale);
        formView3.setAnimation(Scale);
        formView4.setAnimation(Scale);
        formView5.setAnimation(Scale);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
        });
    }
}
