package com.example.tp2cloudfirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

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

        Note contenuNote = new Note(titre, note);

        /** Envoi des données dans FirStore **/
        noteRef.set(contenuNote)
                // ajout du addOnSuccessListener pour verifier que tout c'est bien passé
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity2.this, "Note enregistrée", Toast.LENGTH_SHORT).show();
                    }
                })
                // Ajout du addOnFailureListener qui affichera l'erreur dans le log et d'un Toast pour l'UX
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( MainActivity2.this, "Erreur lors de l'envoi !", Toast.LENGTH_SHORT).show();
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
                            Note contenuNote = documentSnapshot.toObject(Note.class);
                            String titre = contenuNote.getTitre();
                            String note = contenuNote.getNote();
                            tv_saveNote.setText("Titre de la note : " + titre + "\n" + "Note : " + note);
                        }else{
                            Toast.makeText(MainActivity2.this, "Le document n'existe pas !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, "Erreur de lecture !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        /** Ce listener tourne en tache de fond il faut donc l'arreter lorsque l'on change d'activite
         * ou de fragment. il existe deux méthodes : la 1ere consiste à declarer une variable de type
         * ListenerRegistration, encapsuler le addSnapshotListener avec, puis utiliser remove dans la
         * méthode onStop pour l'arreter. La seconde consiste à ajouter this (le context) pour detacher
         * notre Listener au moment approprié
         */
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                // on verifie qu'il n'y ai pas d'erreur
                if (error!=null){
                    Toast.makeText(MainActivity2.this, "Erreur de chargement !", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + error.toString());
                    return; // pour quitter la méthode s'il y a une erreur
                }
                if (value.exists()){
                    Note contenuNote = value.toObject(Note.class);
                    String titre = contenuNote.getTitre();
                    String note = contenuNote.getNote();
                    tv_showNote.setText("Titre de la note : " + titre + "\n" + "Note : " + note);
                } // partie ajoutée pour ne pas afficher null dans le champ
                else{
                    tv_showNote.setText(" ");

                }
            }
        });
    }

    // Modification de la note mais pas du titre
    public void updateNote(View view){
        String textNote = et_note.getText().toString();
        noteRef.update(KEY_NOTE, textNote);
    }

    // Suppression de la note mais pas du titre
    public void deleteNote(View view){
        noteRef.update(KEY_NOTE, FieldValue.delete())
                // on peut ajouter un onSuccessListener pour valider notre action
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity2.this, "La note est bien supprimée !", Toast.LENGTH_SHORT).show();
                    }
                })
                // et bien sur un onFailureListener pour afficher l'erreur dans un log
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, "Erreur lors de la suppression !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }


    //méthode pour supprimer le contenu de la note et le titre
    public void deleteAll(View view){
        noteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity2.this, "Toute la note est bien supprimée !", Toast.LENGTH_SHORT).show();
                    }
                })
                // et bien sur onFailureListener pour afficher l'erreur dans un log
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initUI();
    }
}