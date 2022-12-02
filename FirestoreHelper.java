package com.example.mynotes.firestore;

import com.example.mynotes.TaskModel;
import com.example.mynotes.auth.AuthenticationHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public Task<Void> addNewDocument(TaskModel taskModel , String uuid) {
        return firestore.collection(new AuthenticationHelper().getCurrentUser().getUid()).document(uuid).set(taskModel);
    }

    public CollectionReference getAllNotes()  {
        FirebaseUser user = new AuthenticationHelper().getCurrentUser();
        if (user == null) {
            return null;
        }
        return firestore.collection(user.getUid());
    }

    public  Task<Void> deleteNotes(String uuid)  {
        FirebaseUser user = new AuthenticationHelper().getCurrentUser();
        if (user == null) {
            return null;
        }

        return firestore.collection(user.getUid()).document(uuid).delete();
    }


}
