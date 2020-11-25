package com.gti.grupo3.mislugares;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Usuarios {
    static void guardarUsuario(final FirebaseUser user) {
        Usuario usuario = new Usuario(user.getDisplayName(),user.getEmail());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid()).set(usuario); }
}