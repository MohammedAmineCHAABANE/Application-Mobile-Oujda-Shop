package com.example.oujdashop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.oujdashop.Models.Category;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    ListView listViewCategories;
    ArrayList<Category> categoriesList;
    Database dbHelper;
    CategoryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewCategories = findViewById(R.id.listViewCategories);
        dbHelper = new Database(this);

        loadCategories();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Categories");
        }

        listViewCategories.setOnItemClickListener((parent, view, position, id) -> {
            Category category = categoriesList.get(position);
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });

        listViewCategories.setOnItemLongClickListener((parent, view, position, id) -> {
            Category selectedCategory = categoriesList.get(position);
            showCategoryOptionsDialog(selectedCategory);
            return true;
        });
    }

    private void loadCategories() {
        categoriesList = dbHelper.getAllCategories();
        adapter = new CategoryAdapter(this, categoriesList);
        listViewCategories.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_category) {
            showAddCategoryDialog();
            return true;
        } else if (item.getItemId() == R.id.action_user) {
            startActivity(new Intent(this, UserActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter une catégorie");
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            EditText etCategory = customLayout.findViewById(R.id.etCategoryName);
            EditText etCategoryDescription = customLayout.findViewById(R.id.etCategoryDescription);
            String categoryName = etCategory.getText().toString().trim();
            String categoryDescription = etCategoryDescription.getText().toString().trim();
            Category category = new Category(0, categoryName,categoryDescription);
            if (!categoryName.isEmpty() && !categoryDescription.isEmpty() && dbHelper.addCategory(category)) {
                Toast.makeText(this, "Catégorie ajoutée !", Toast.LENGTH_SHORT).show();
                loadCategories();
            } else {
                Toast.makeText(this, "Erreur d'ajout", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void showCategoryOptionsDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier ou Supprimer ?");
        builder.setItems(new String[]{"Modifier", "Supprimer"}, (dialog, which) -> {
            if (which == 0) {
                showEditCategoryDialog(category);
            } else {
                dbHelper.deleteCategory(category);
                Toast.makeText(this, "Catégorie supprimée", Toast.LENGTH_SHORT).show();
                loadCategories();
            }
        });
        builder.show();
    }

    private void showEditCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier la catégorie");
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        builder.setView(customLayout);
        EditText etCategory = customLayout.findViewById(R.id.etCategoryName);
        EditText etCategoryDescription = customLayout.findViewById(R.id.etCategoryDescription);
        etCategory.setText(category.getName());
        etCategoryDescription.setText(category.getDescription());


        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String newName = etCategory.getText().toString().trim();
            String newDescription = etCategoryDescription.getText().toString().trim();
            category.setName(newName);
            category.setDescription(newDescription);
            if (!newName.isEmpty() &&!newDescription.isEmpty() && dbHelper.updateCategory(category)) {
                Toast.makeText(this, "Catégorie mise à jour !", Toast.LENGTH_SHORT).show();
                loadCategories();
            } else {
                Toast.makeText(this, "Erreur de modification", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Déconnexion");
        builder.setMessage("Voulez-vous vraiment vous déconnecter ?");

        builder.setPositiveButton("Oui", (dialog, which) -> {
            SharedPreferences.Editor editor = getSharedPreferences("utilisateur", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("Non", null);
        builder.show();
    }

}
