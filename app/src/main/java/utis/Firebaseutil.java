package utis;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.travelmantics.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Firebaseutil {
    private static final int RC_SIGN_IN = 321;
    private static FirebaseAuth auth;
    private static FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseDatabase database;
    public static DatabaseReference databaseReference;
    public static StorageReference storageReference;
    private static ArrayList<Deal> deals;
    private static Firebaseutil firebaseutil;
    private static MainActivity caller;
    public static boolean isAdmin;

    private Firebaseutil(){}

    public static void openFbReference(String ref, final MainActivity callerActivity){
        caller = callerActivity;
        openFbReference(ref);

    }

    public static void openFbReference(String ref){
        if(firebaseutil == null){
            firebaseutil = new Firebaseutil();
            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference().child("deals_images");
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(auth.getCurrentUser()==null){
                        singIn();
                    }
                    else{
                        Log.i("FirebaseUtil", "onAuthStateChanged: "+ auth.getCurrentUser().getUid());
                        checkAdmin(auth.getCurrentUser().getUid());
                    }
                }
            };
        }
        deals = new ArrayList<Deal>();
        databaseReference = database.getReference().child(ref);
    }

    private static void checkAdmin(String uid) {
        isAdmin=false;
        database.getReference().child("admin").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("Util", "onChildAdded: ");
                isAdmin=true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  static void attachListener(){
        auth.addAuthStateListener(authStateListener);
    }

    public static void detachListener(){
        auth.removeAuthStateListener(authStateListener);
    }

    private static void singIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }
}
