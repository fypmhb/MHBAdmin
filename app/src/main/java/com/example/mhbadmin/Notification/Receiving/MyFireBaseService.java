package com.example.mhbadmin.Notification.Receiving;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFireBaseService extends FirebaseMessagingService {

    //instance id service
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String newToken = FirebaseInstanceId.getInstance().getToken();

        if (firebaseUser != null) {
            updateToken(newToken);
        }
    }

    private void updateToken(String newToken) {
        Token token = new Token(newToken);

        FirebaseFirestore.getInstance()
                .collection("Tokens")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(token);
    }
}