package com.example.config;

import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

//Firebase 
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class ConfigFirebase {

    @PostConstruct
    public void init() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                //.setDatabaseUrl(" ") For the moment this line is desactivated until we learn if the database should be in real tiem or not.
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}