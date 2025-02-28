package com.example.oujdashop;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oujdashop.Models.Category;
import com.example.oujdashop.Models.Product;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    GridView gridViewProducts;
    ArrayList<Product> productList;
    ProductAdapter adapter;
    Database dbHelper;
    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        gridViewProducts = findViewById(R.id.gridViewProducts);
        dbHelper = new Database(this);

        category = (Category) getIntent().getSerializableExtra("category");
        loadProducts();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Produits");
        }

        gridViewProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product product = productList.get(position);
            Intent intent = new Intent(ProductActivity.this, DetailsActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        gridViewProducts.setOnItemLongClickListener((parent, view, position, id) -> {
            Product product = productList.get(position);
            showProductOptionsDialog(product);
            return true;
        });
    }

    private void loadProducts() {
        productList = dbHelper.getProductsByCategory(category);
        adapter = new ProductAdapter(this, productList);
        gridViewProducts.setAdapter( adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_product) {
            showAddProductDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showProductOptionsDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier ou Supprimer ?");
        builder.setItems(new String[]{"Modifier", "Supprimer"}, (dialog, which) -> {
            if (which == 0) {
                showEditProductDialog(product);
            } else {
                dbHelper.deleteProduct(product.getId());
                Toast.makeText(this, "Produit supprimé", Toast.LENGTH_SHORT).show();
                loadProducts();
            }
        });
        builder.show();
    }

    private void showEditProductDialog(Product product) {
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter un produit");

        // Charger le layout personnalisé
        View view = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(view);

        // Récupérer les champs du layout
        EditText etProductName = view.findViewById(R.id.etProductName);
        EditText etProductPrice = view.findViewById(R.id.etProductPrice);
        EditText etProductDesc = view.findViewById(R.id.etProductDesc);

        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String name = etProductName.getText().toString().trim();
            String priceStr = etProductPrice.getText().toString().trim();
            String desc = etProductDesc.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);

            boolean success = dbHelper.addProduct(name, price, desc, category);
            if (success) {
                Toast.makeText(this, "Produit ajouté avec succès !", Toast.LENGTH_SHORT).show();
                loadProducts(); // Recharger la liste des produits
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        loadProducts();
    }

}
