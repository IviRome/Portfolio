package com.gti.grupo3.mislugares;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class LoginActivity  extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    public static FirebaseUser usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();
    }

    private void login() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        this.usuario = usuario;

        if (usuario != null)
        {
            Usuarios.guardarUsuario(usuario);

            String nombre = usuario.getDisplayName();
            String correo = usuario.getEmail();
            String telefono = usuario.getPhoneNumber();
            Uri urlFoto = usuario.getPhotoUrl();
            String uid = usuario.getUid();
            String proveedor = usuario.getProviderId();

           /*

            Toast.makeText(this, "inicia sesión: " +
                    usuario.getDisplayName() + " - " + usuario.getEmail() + " - " +
                    usuario.getProviders().get(0), Toast.LENGTH_LONG).show();

            */

            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        } else {

            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setLogo(R.drawable.iconoautomotic)
                    .setTheme(R.style.AutomoticTheme)
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(true).build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.PhoneBuilder().build())).setIsSmartLockEnabled(true).build(), RC_SIGN_IN);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                login();
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Sin conexión a Internet",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(this, "Error desconocido",
                        Toast.LENGTH_LONG).show();
                Log.e("Autentificación", "Sign-in error: ", response.getError());
            }
        }
    }




}