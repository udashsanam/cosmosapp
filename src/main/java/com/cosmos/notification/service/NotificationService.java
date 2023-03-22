package com.cosmos.notification.service;

import com.cosmos.notification.model.Notification;
import com.cosmos.notification.model.NotificationDataPayload;
import com.cosmos.notification.model.NotificationReqBody;
import com.cosmos.notification.model.NotificationResponse;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
public class NotificationService {

    @Value("${app.notification.key}")
    private String notificationKey;

    @Value("${app.notification.url}")
    private String notificationUrl;

    public NotificationResponse sendPushNotification(String deviceToken, Notification notification, NotificationDataPayload notificationDataPayload){
        NotificationResponse notificationResponse = new NotificationResponse();

        NotificationReqBody reqBody = new NotificationReqBody();
        reqBody.setTo(deviceToken);
        reqBody.setCollapse_key("type_a");
        reqBody.setPriority("high");
        reqBody.setNotification(notification);
        reqBody.setData(notificationDataPayload);

        System.out.println(reqBody);

        Gson gson = new Gson();
        StringEntity postString = null;
        try {
            postString = new StringEntity(gson.toJson(reqBody));
        } catch (UnsupportedEncodingException e) {
            notificationResponse.setSuccess(false);
            notificationResponse.setFailureMsg(e.getLocalizedMessage());
            return notificationResponse;
        }

        // Initialize httpClient for sending request to server
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(notificationUrl);

        post.setHeader("Authorization", "key=" + notificationKey);
        post.setHeader("Content-type", "application/json");
        post.setEntity(postString);

        HttpResponse response = null;
        String responseBody = null;
        try {
            response = httpClient.execute(post);
            System.out.println(response);
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            notificationResponse.setSuccess(false);
            notificationResponse.setFailureMsg(e.getLocalizedMessage());
            return notificationResponse;
        }

        System.out.println(responseBody);
        JSONObject jsonObj = new JSONObject(responseBody);
        int success = jsonObj.getInt("success");
        int failure = jsonObj.getInt("failure");

        if (success == 1) {
            notificationResponse.setSuccess(true);
            /*need to change the message sent flag to true*/
            JSONArray errorParentNode = (JSONArray) jsonObj.get("results");
            for (int i = 0; i < errorParentNode.length(); i++) {
                JSONObject obj = (JSONObject) errorParentNode.get(0);
                String message_id = obj.getString("message_id");
                notificationResponse.setMessageId(message_id);
            }

        } else if (failure == 1) {
            notificationResponse.setSuccess(false);
            /*need to change the message sent flag to false and also store the failure reason*/
            JSONArray errorParentNode = (JSONArray) jsonObj.get("results");
            for (int i = 0; i < errorParentNode.length(); i++) {
                JSONObject obj = (JSONObject) errorParentNode.get(0);
                String error = obj.getString("error");
                notificationResponse.setFailureMsg(error);
            }
        }

        return notificationResponse;
    }
}
