package com.example.services;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import java.util.List;

//New dependencies
import jakarta.annotation.PostConstruct;
import com.google.firebase.cloud.FirestoreClient;
import com.example.model.PokeEntity;


@Service
//Listening logic is going to be here.
public class PokeService {
    
    @Autowired
    private FirebaseApp firebaseApp;  // inyectamos FirebaseApp con @Autowired
    private PokeEntity ultimaBatalla = new PokeEntity();
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    @PostConstruct
    public void escucharCambios() {
        Firestore db = FirestoreClient.getFirestore(firebaseApp);

        DocumentReference docRef = db.collection("pokebattles").document("battle1");

        docRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                error.printStackTrace();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                ultimaBatalla = snapshot.toObject(PokeEntity.class);

                System.out.println("Cambio detectado:");
                System.out.println("Vida Pokemon 1: " + ultimaBatalla.getPokemon1Vida());
                System.out.println("Vida Pokemon 2: " + ultimaBatalla.getPokemon2Vida());

                enviarActualizacion();
            }
        });
    }

    public PokeEntity getUltimaBatalla() {
        return ultimaBatalla;
    }

    public SseEmitter agregarCliente() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    private void enviarActualizacion() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(ultimaBatalla);
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }
}
