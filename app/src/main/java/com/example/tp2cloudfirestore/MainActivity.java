package com.example.tp2cloudfirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /** Attribut TAG en autocompletion avec logt **/
    private static final String TAG = "MainActivity";

    /** variables globales des cles de notre base **/
    private static final String KEY_TITRE = "titre";
    private static final String KEY_NOTE = "note";

    /** Attribute globaux **/
    private EditText et_titre, et_note;
    private TextView tv_saveNote, tv_showNote;

    /** reference de la db de Firebase **/
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // on fait appelle à notre base de donnees dans laquelle on va ajouter
    // une collection appelee liste de message et un document comprenant nos donnees
    // si ce document est vide alors Firebase ajoutera un id automatiquement

    private DocumentReference noteRef = db.document("listeDeNotes/Ma premiere note");

    public void initUI(){
        et_titre=(EditText) findViewById(R.id.et_titre);
        et_note=(EditText) findViewById(R.id.et_note);
        tv_saveNote=(TextView) findViewById(R.id.tv_saveNote);
        tv_showNote=findViewById(R.id.tv_showNote);
    }

    public void saveNote(View view){
        String titre = et_titre.getText().toString();
        String note = et_note.getText().toString();

        /** Containeur pour transmettre ces donnees à Firestore
         * Map fonctionne sur un modele cle valeurs
         * ici on donne le type de donnée puis un objet, comme ca on peut tout passer
         */

        Map<String, Object> contenuNote = new HashMap<>();
        contenuNote.put(KEY_TITRE, titre);
        contenuNote.put(KEY_NOTE, note);

        /** Envoi des données dans FirStore **/
        noteRef.set(contenuNote)
                // ajout du addOnSuccessListener pour verifier que tout c'est bien passé
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Note enregistrée", Toast.LENGTH_SHORT).show();
                    }
                })
                // Ajout du addOnFailureListener qui affichera l'erreur dans le log et d'un Toast pour l'UX
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( MainActivity.this, "Erreur lors de l'envoi !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void showNote(View view){
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        /** document Snapshot contient toutes les donnéées auquelles nous voulons acceder
                         * tant qu'il en existe bien sur
                         */
                        if (documentSnapshot.exists()){
                            String titre = documentSnapshot.getString(KEY_TITRE);
                            String note = documentSnapshot.getString(KEY_NOTE);
                        }
                    }
                })
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }
}