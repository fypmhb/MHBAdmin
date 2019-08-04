package com.example.mhbadmin.Notification;

import com.example.mhbadmin.Notification.Sending.MyResponse;
import com.example.mhbadmin.Notification.Sending.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAbekEgZU:APA91bGhIAsJJvNmXXnvaDYOoFNhRD2GT1vJlaE6PusTHwiLuf9OL2U4wK2dRwNCOUG18kudHH6Tuh18FUvZ9X4UmrrcDfI15ANgOc1YyxEy0WJxr6t3OPAkPoCqWHPus9M1PBOkmm4V"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}