package com.aurak.smartuni.smartuni.Chat.Fragments;

import com.aurak.smartuni.smartuni.Chat.Notifications.MyResponse;
import com.aurak.smartuni.smartuni.Chat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=ADD HERE YOUR KEY FROM FIREBASE PROJECT"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
