package com.aurak.smartuni.smartuni.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aurak.smartuni.smartuni.HomeActivity;
import com.aurak.smartuni.smartuni.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        final StringEntity jsonEntity = null;
        final AsyncHttpClient client = new AsyncHttpClient(); //import the public server certificate into your default keystore
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.addHeader("Accept", "application/json");

        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Basic Og==");

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                synchronized(this) {
                attemptLogin(client, jsonEntity);
            }
            }
        });
    }
        private void attemptLogin(AsyncHttpClient client, StringEntity jsonEntity) {
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("username", "Ahmed@hotmail.com");
                jsonParams.put("password", "12345");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                jsonEntity = new StringEntity(jsonParams.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            client.post(null, "https://10.0.2.2:5001/api/users/authenticate", jsonEntity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject  response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Toast.makeText(LoginActivity.this, "صح :) " + response.getString("username") +statusCode, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    Toast.makeText(LoginActivity.this, "خلاص ! " , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }

                @Override
                public void onCancel() {
                    super.onCancel();
                    Toast.makeText(LoginActivity.this, "لغى ! " , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(LoginActivity.this, " لسه :( ", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }



