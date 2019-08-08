package com.example.travelmantics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import utis.Deal;
import utis.Firebaseutil;

public class AdminActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private EditText edtTitle, edtPrice, edtDesription;
    private Button btnInsert;
    private ImageView imageDeal;
    private final String TAG = "AdminActivity";
    String imageUrl = "";
    private Deal deal;
    private int PICTURE_RESULT = 445;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        edtTitle = findViewById(R.id.edtTitle);
        edtPrice = findViewById(R.id.edtPrice);
        edtDesription = findViewById(R.id.edtDescription);
        imageDeal = findViewById(R.id.imgDeal);
        Firebaseutil.openFbReference("deals");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        btnInsert = findViewById(R.id.btnInsertImage);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert picture")
                        , PICTURE_RESULT);
            }
        });

        reference = Firebaseutil.databaseReference;

        Intent intent = getIntent();
        Deal newDeal = (Deal) intent.getSerializableExtra("Deal");
        if(newDeal==null)
            newDeal = new Deal();
        deal = newDeal;

        displayDeal();
        setEnable(Firebaseutil.isAdmin);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            uploadImage(uri);
        }
    }

    private void uploadImage(Uri Imageuri) {
        Firebaseutil.storageReference.child(Imageuri.getLastPathSegment()).putFile(Imageuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String pictureName = taskSnapshot.getStorage().getPath();
                deal.setImageName(pictureName);
                taskSnapshot.getMetadata().getReference().getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl = uri.toString();
                                Picasso.get().load(imageUrl).into(imageDeal);
                                progressDialog.dismiss();
                            }
                        });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage((int) progress + "%" + " completed" );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void setEnable(boolean isEnable) {
        edtTitle.setEnabled(isEnable);
        edtPrice.setEnabled(isEnable);
        edtDesription.setEnabled(isEnable);
        btnInsert.setEnabled(isEnable);
    }

    private void displayDeal() {
        edtTitle.setText(deal.getTitle());
        edtPrice.setText(String.valueOf(deal.getPrice()));
        edtDesription.setText(deal.getDescription());
        if(deal.getImgUrl()!=null && !deal.getImgUrl().isEmpty())
            Picasso.get().load(deal.getImgUrl()).into(imageDeal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin, menu);
        if(Firebaseutil.isAdmin){
            menu.findItem(R.id.mnu_delete).setVisible(true);
            menu.findItem(R.id.mnu_save).setVisible(true);
        }
        else{
            menu.findItem(R.id.mnu_delete).setVisible(false);
            menu.findItem(R.id.mnu_save).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnu_save:
                saveDeal();
                break;
            case R.id.mnu_delete:
                deleteDeal();
                break;
            default:
                finish();
                break;
        }
        return true;
    }

    private void deleteDeal() {
        if(deal.getImageName()!=null && !deal.getImageName().isEmpty())
            Firebaseutil.storageReference.child(deal.getImageName()).delete();
        if(deal.getId()==null||deal.getId().isEmpty()){
            Toast.makeText(this, "Save deal first before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        reference.child(deal.getId()).removeValue();
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();


    }

    private void saveDeal() {
        if(edtTitle.getText().toString().isEmpty()){
            edtTitle.setError("Enter title");
            return;
        }
        if(edtPrice.getText().toString().isEmpty()){
            edtPrice.setError("Enter price");
            return;
        }
        if(edtDesription.getText().toString().isEmpty()){
            edtDesription.setError("Set description");
            return;
        }

        final long price = Long.parseLong(edtPrice.getText().toString());
        deal.setTitle(edtTitle.getText().toString().trim());
        deal.setPrice(price);
        deal.setDescription(edtDesription.getText().toString().trim());
        deal.setImgUrl(imageUrl);

        if(deal.getId()==null|| deal.getId().isEmpty())
            reference.push().setValue(deal);
        else
            reference.child(deal.getId()).setValue(deal);
        Toast.makeText(AdminActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
