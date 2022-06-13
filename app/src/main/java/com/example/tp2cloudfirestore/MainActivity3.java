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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity3 extends AppCompatActivity {

    //variables golbales

    private static final String TAG = "MainActivity3";

    private EditText et_titre, et_note;
    private TextView tv_showNote;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Ajout de la reference à la collection comprenant toutes les notes : notebook
    private CollectionReference notebookRef = db.collection("Notebook");

    public void initUI(){
        et_titre=findViewById(R.id.et_titre3);
        et_note=findViewById(R.id.et_note3);
        tv_showNote=findViewById(R.id.tv_showNote3);
    }


    // La méthode pour ajouter des notes dans la base
    public void addNote(View view){
        String titre = et_titre.getText().toString();
        String note = et_note.getText().toString();
        // on utilise les constructeur de notre modele pour envoyer les notes vers la base
        Note contenuNote = new Note(titre, note);

        /** On appalle la reference a la collection notebook pour y ajouter
         * les valeurs de l'objet contenuNote
         */
        notebookRef.add(contenuNote)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity3.this, "Enregistrement de " + titre, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity3.this, "Erreur lors de l'ajout !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    // la méthode pour recuperer l'ensemble des données dans la collection
    public void loadNotes(View view){
        notebookRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    //On note l'utilisation de QuerySnapshot pour l'ensemble des valeurs de la bdd
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String notes = "";
                        // declaration d'un empty string pour remplir le textview plutot que de declarer  une liste et de remplir un recycler
                        // Utilisation d'une boucle for pour faire le tour de la base
                        // les : signifient qur l'on utilise un for each

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Note contenuNote = documentSnapshot.toObject(Note.class);
                            //récuperation de l'ID
                            contenuNote.setDocumentId(documentSnapshot.getId());

                            String documentId = contenuNote.getDocumentId();
                            String titre = contenuNote.getTitre();
                            String note = contenuNote.getNote();

                            notes += documentId + "\nTitre : " + titre + "\nNote : " + note + "\n\n";
                        }
                        tv_showNote.setText(notes);
                    }
                });
    }

    // Affichage automatiaue des notes


    @Override
    protected void onStart() {
        super.onStart();
        // Ne pas oublier d'ajouter this pour détacher le listener quand nous n'en avons plus besoin
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    return;
                }
                String notes="";
                for (QueryDocumentSnapshot documentSnapshot : value){
                    Note contenuNote = documentSnapshot.toObject(Note.class);
                    // Récupération de l'ID
                    contenuNote.setDocumentId(documentSnapshot.getId());

                    String documentId = contenuNote.getDocumentId();
                    String titre = contenuNote.getTitre();
                    String note = contenuNote.getNote();

                    notes += documentId + "\nTitre : " + titre + "\nNote : " + note + "\n\n";
                }
                tv_showNote.setText(notes);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initUI();
    }

}