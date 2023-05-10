package cat.udl.hyperion.appmobils.kingdomcollector.other.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import cat.udl.hyperion.appmobils.kingdomcollector.R;

public class RegisterActivity extends AppCompatActivity {
    protected String myClassTag = this.getClass().getSimpleName();
    private FirebaseAuth mAuth;

    private FirebaseUser user;
    EditText editText_email;
    EditText editText_password;
    EditText editText_username;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editText_email = findViewById(R.id.email_input);
        editText_password = findViewById(R.id.password_input);
        editText_username = findViewById(R.id.name_input);
        findViewById(R.id.register_button).setOnClickListener(v -> register());


    }
    // En la parte superior de tu clase RegisterActivity


    // Reemplaza tu método register() con este
    private void register() {
        String email = editText_email.getText().toString().trim();
        String password = editText_password.getText().toString().trim();
        String username = editText_username.getText().toString().trim();

        // Verificar que se han ingresado valores válidos
        if (email.isEmpty()) {
            editText_email.setError("Email is required");
            Log.d(myClassTag, "Falta introducir el correo electrónico.");
            editText_email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editText_password.setError("Password is required");
            Log.d(myClassTag, "Falta introducir la contraseña.");
            editText_password.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            editText_username.setError("Username is required");
            Log.d(myClassTag, "Falta introducir el nombre de usuario.");
            editText_username.requestFocus();
            return;
        }

        // Registrar al usuario en Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El registro fue exitoso, enviar correo de verificación
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.sendEmailVerification()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Se ha enviado un correo de verificación a su cuenta de correo electrónico. Por favor, confirme su correo electrónico antes de iniciar sesión.", Toast.LENGTH_LONG).show();
                                    }
                                });
                        // Actualizar el nombre de usuario del usuario
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task12 -> {
                                    if (task12.isSuccessful()) {
                                        Log.d(myClassTag, "Perfil actualizado correctamente con el username.");
                                        assignRandomCardsToUser(user.getUid());
                                        finish();
                                    }
                                });
                    } else {
                        // El registro falló, mostrar un mensaje de error
                        Toast.makeText(RegisterActivity.this, "No se pudo crear la cuenta. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_LONG).show();
                        Log.d(myClassTag, "Error al crear la cuenta.", task.getException());
                    }
                });
    }

    // Este es el nuevo método para asignar 5 cartas aleatorias al usuario
    private void assignRandomCardsToUser(String userId) {
        db.collection("general_cards")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> allCards = task.getResult().getDocuments();
                        Collections.shuffle(allCards); // Mezcla las tarjetas
                        List<DocumentSnapshot> selectedCards = allCards.subList(0, 5); // Selecciona las primeras 5 tarjetas
                        // Continuación del método assignRandomCardsToUser
                        for (DocumentSnapshot card : selectedCards) {
                            Map<String, Object> cardData = card.getData();
                            if (cardData != null) {
                                db.collection("users/" + userId + "/user_cards")
                                        .document(card.getId())
                                        .set(cardData)
                                        .addOnSuccessListener(aVoid -> Log.d(myClassTag, "Card added successfully for user: " + userId))
                                        .addOnFailureListener(e -> Log.d(myClassTag, "Error adding card for user: " + userId, e));
                            }
                        }
                    }
                });
    }

    private void goToLoginPage(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}