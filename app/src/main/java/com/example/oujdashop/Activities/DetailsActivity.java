package com.example.oujdashop.Activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oujdashop.Database.Database;
import com.example.oujdashop.Models.Product;
import com.example.oujdashop.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductPrice, tvProductDesc;
    private ImageView ivProductImage;
    private int productId;
    private Product product;
    private Database dbHelper;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        productId = getIntent().getIntExtra("product_id", -1);
        dbHelper = new Database(this);

        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDesc = findViewById(R.id.tvProductDesc);
        ivProductImage = findViewById(R.id.ivProductImage);

        loadProductDetails();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(product.getName());
        }
        ivProductImage.setOnClickListener(v -> openGallery());

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
                    if(dbHelper.updateProductPhoto(productId,photoBase64)){
                        Toast.makeText(this, "Photo de profil mise à jour avec succès", Toast.LENGTH_SHORT).show();
                        ivProductImage.setImageBitmap(bitmap);
                    }else{
                        Toast.makeText(this, "Erreur lors de la mise à jour de la photo de profil", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public Bitmap getBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    private void loadProductDetails() {
        product = dbHelper.getProduct(productId);

        if (product != null) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(String.format("%.2f DH", product.getPrice()));
            tvProductDesc.setText(product.getDescription());
            ivProductImage.setImageResource(R.mipmap.logo_app);
            String photoBase64 = dbHelper.getProductPhoto(productId);
            if (photoBase64 != null) {
                ivProductImage.setImageBitmap(getBitmap(photoBase64));
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            showEditProductDialog();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            confirmDeleteProduct();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier le produit");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(view);

        EditText etProductName = view.findViewById(R.id.etProductName);
        EditText etProductPrice = view.findViewById(R.id.etProductPrice);
        EditText etProductDesc = view.findViewById(R.id.etProductDesc);

        etProductName.setText(product.getName());
        etProductPrice.setText(String.valueOf(product.getPrice()));
        etProductDesc.setText(product.getDescription());

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String newName = etProductName.getText().toString().trim();
            String priceStr = etProductPrice.getText().toString().trim();
            String newDesc = etProductDesc.getText().toString().trim();

            if (!newName.isEmpty() && !priceStr.isEmpty() && !newDesc.isEmpty()) {
                double newPrice = Double.parseDouble(priceStr);
                boolean success = dbHelper.updateProduct(product.getId(), newName, newPrice, newDesc);

                if (success) {
                    Toast.makeText(this, "Produit modifié avec succès", Toast.LENGTH_SHORT).show();
                    loadProductDetails(); // Recharger les infos
                } else {
                    Toast.makeText(this, "Erreur de modification", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void confirmDeleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Supprimer le produit");
        builder.setMessage("Voulez-vous vraiment supprimer ce produit ?");
        builder.setPositiveButton("Oui", (dialog, which) -> {
            dbHelper.deleteProduct(product.getId());
            Toast.makeText(this, "Produit supprimé", Toast.LENGTH_SHORT).show();
            finish(); // Fermer l'activité
        });
        builder.setNegativeButton("Non", null);
        builder.show();
    }

}
