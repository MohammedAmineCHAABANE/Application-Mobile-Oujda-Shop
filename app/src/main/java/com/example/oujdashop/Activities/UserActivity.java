package com.example.oujdashop.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oujdashop.Database.Database;
import com.example.oujdashop.Models.User;
import com.example.oujdashop.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class UserActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText etUserName, etUserEmail;
    Button btnSave, btnChangePassword;
    ImageView ivProfilePicture;
    Database dbHelper;
    SharedPreferences sharedPreferences;
    int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        dbHelper = new Database(this);
        sharedPreferences = getSharedPreferences("utilisateur", MODE_PRIVATE);
        userId = sharedPreferences.getInt("Id",0);

        loadUserInfo();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("PROFIL");
        }

        ivProfilePicture.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> saveUserInfo());

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

    }

    private void loadUserInfo() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT nom, prenom, email FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0)+" "+cursor.getString(1);
            etUserName.setText(name);
            etUserEmail.setText(cursor.getString(2));
        }
        cursor.close();
        String photoBase64 = dbHelper.getUserPhoto(userId);
        if (photoBase64 != null) {
            ivProfilePicture.setImageBitmap(getBitmap(photoBase64));
        }

    }

    private void saveUserInfo() {
        String newName = etUserName.getText().toString().trim();


        if (!newName.isEmpty() && dbHelper.updateUserName(userId, newName)) {
            Toast.makeText(this, "Informations mises à jour !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
        }
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                if(bitmap != null){
                    String photoBase64 = save(bitmap);
                    if(dbHelper.updateUserPhoto(userId,photoBase64)){
                        Toast.makeText(this, "Photo de profil mise à jour avec succès", Toast.LENGTH_SHORT).show();
                        ivProfilePicture.setImageBitmap(bitmap);
                    }else{
                        Toast.makeText(this, "Erreur lors de la mise à jour de la photo de profil", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public String save(Bitmap bitmap) {
        File directory = getApplicationContext().getFilesDir();
        File file = new File(directory, new Date().toString() + ".jpg");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Changer le mot de passe");
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        User user;
        if (cursor.moveToFirst()) {
            user = new User(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4));
        } else {
            user = null;
        }
        cursor.close();
        if(user == null){
            return;
        }

        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(view);

        EditText etOldPassword = view.findViewById(R.id.etOldPassword);
        EditText etNewPassword = view.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String oldPass = etOldPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Les nouveaux mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!user.getPassword().equals(oldPass)){
                Toast.makeText(this, "Ancien mot de passe incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.updatePassword(userId, newPass);
            if (success) {
                Toast.makeText(this, "Mot de passe mis à jour avec succès", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ancien mot de passe incorrect", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

}
