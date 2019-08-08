package com.example.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import utis.DealsAdapter;
import utis.Firebaseutil;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainAct";
    private RecyclerView listDeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebaseutil.openFbReference("deals", this);
        listDeals = findViewById(R.id.listDeals);
        listDeals.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Firebaseutil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Firebaseutil.attachListener();
        DealsAdapter adapter = new DealsAdapter(MainActivity.this);
        listDeals.setAdapter(adapter);
        Log.i(TAG, "onResume: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuAdd = menu.findItem(R.id.mnu_add);

        if(Firebaseutil.isAdmin)
            menuAdd.setVisible(true);
        else
            menuAdd.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnu_logout:
                logout();
                break;
            case R.id.mnu_add:
                startActivity(new Intent(this, AdminActivity.class));
                break;
        }
        return true;
    }

    private void logout() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "logout onComplete: ");
                        Firebaseutil.attachListener();
                    }
                });
        Firebaseutil.detachListener();
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}
