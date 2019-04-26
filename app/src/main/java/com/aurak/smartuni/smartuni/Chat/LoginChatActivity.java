package com.aurak.smartuni.smartuni.Chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aurak.smartuni.smartuni.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;


public class LoginChatActivity extends AppCompatActivity {

    MaterialEditText email, password;
    Button btn_login;

    FirebaseAuth auth;
    TextView forgot_password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_chat);

        //Toolbar toolbar = findViewById(R.id.toolbarChat);
        //setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Login");
  //      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        //email = findViewById(R.id.emailChat);
        //password = findViewById(R.id.passwordChat);
        //btn_login = findViewById(R.id.btn_loginChat);
        //forgot_password = findViewById(R.id.forgot_passwordChat);

        progressDialog = new ProgressDialog(this);

//        forgot_password.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginChatActivity.this, ResetPasswordActivity.class));
//            }
//        });

        progressDialog.setMessage("Please wait while we check your details!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        auth.signInWithEmailAndPassword("aisha@gmail.com", "password")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginChatActivity.this, MainChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(LoginChatActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        btn_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String txt_email = email.getText().toString();
//                String txt_password = password.getText().toString();
//
//                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
//                    Toast.makeText(LoginChatActivity.this, "All fileds are required", Toast.LENGTH_SHORT).show();
//                } else {
//                    progressDialog.setMessage("Please wait while we check your details!");
//                    progressDialog.setCanceledOnTouchOutside(false);
//                    progressDialog.show();
//
//                    auth.signInWithEmailAndPassword(txt_email, txt_password)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()){
//                                        progressDialog.dismiss();
//                                        Intent intent = new Intent(LoginChatActivity.this, MainChatActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                        finish();
//                                    } else {
//                                        progressDialog.hide();
//                                        Toast.makeText(LoginChatActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
                //}
            //}
       // });
    }
}
