package com.example.oujdashop.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.oujdashop.Models.Category;
import com.example.oujdashop.Models.Product;
import com.example.oujdashop.Models.User;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "OujdaShop.db";
    private static final int DATABASE_VERSION = 1;

    // Table Utilisateurs
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOM = "nom";
    private static final String COLUMN_PRENOM = "prenom";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String COLUMN_PHOTO = "photo";


    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOM + " TEXT, " +
                    COLUMN_PRENOM + " TEXT, " +
                    COLUMN_EMAIL + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT," +
                    COLUMN_PHOTO + " TEXT)";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CAT_ID = "id";
    private static final String COLUMN_CAT_NAME = "name";

    private static final String COLUMN_CAT_DESCRIPTION = "description";


    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PROD_ID = "id";
    private static final String COLUMN_PROD_NAME = "name";
    private static final String COLUMN_PROD_PRICE = "price";
    private static final String COLUMN_PROD_DESC = "description";
    private static final String COLUMN_PROD_IMG = "image";
    private static final String COLUMN_PROD_CATEGORY_ID = "category_id";

    private static final String CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    COLUMN_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PROD_NAME + " TEXT, " +
                    COLUMN_PROD_PRICE + " REAL, " +
                    COLUMN_PROD_DESC + " TEXT, " +
                    COLUMN_PROD_IMG + " TEXT, " +
                    COLUMN_PROD_CATEGORY_ID + " INTEGER , " +
                    "FOREIGN KEY(" + COLUMN_PROD_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CAT_ID + "))";

    private static final String CREATE_CATEGORIES_TABLE =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    COLUMN_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CAT_NAME + " TEXT ," +
                    COLUMN_CAT_DESCRIPTION + " TEXT )";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public boolean addUser(String nom, String prenom, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM, nom);
        values.put(COLUMN_PRENOM, prenom);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);


        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public User checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password});

        User user = null;
        if (cursor.moveToNext()) {
            user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        }
        cursor.close();
        db.close();
        return user;
    }

    public boolean addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAT_NAME, category.getName());
        values.put(COLUMN_CAT_DESCRIPTION, category.getDescription());
        long result = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
        while (cursor.moveToNext()) {
            categories.add(new Category(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        db.close();
        return categories;
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, COLUMN_CAT_ID + "=?", new String[]{String.valueOf(category.getId())});
        db.close();
    }

    public boolean updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAT_NAME, category.getName());
        values.put(COLUMN_CAT_DESCRIPTION, category.getDescription());
        int rows = db.update(TABLE_CATEGORIES, values, COLUMN_CAT_ID + "=?", new String[]{String.valueOf(category.getId())});
        db.close();
        return rows > 0;
    }

    public boolean addProduct(String name, double price, String desc, Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_NAME, name);
        values.put(COLUMN_PROD_PRICE, price);
        values.put(COLUMN_PROD_DESC, desc);
        values.put(COLUMN_PROD_CATEGORY_ID, category.getId());
        long result = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return result != -1;
    }

    public Product getProduct(int Id) {
        Product product = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PROD_ID + "=?", new String[]{String.valueOf(Id)});
        while (cursor.moveToNext()) {
            product = new Product(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getString(3),cursor.getString(4), null);
        }
        cursor.close();
        db.close();
        return product;
    }

    public ArrayList<Product> getProductsByCategory(Category category) {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PROD_CATEGORY_ID + "=?", new String[]{String.valueOf(category.getId())});
        while (cursor.moveToNext()) {
            products.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getString(3),cursor.getString(4), category));
        }
        cursor.close();
        db.close();
        return products;
    }

    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_PROD_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean updateProduct(int id, String name, double price, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_NAME, name);
        values.put(COLUMN_PROD_PRICE, price);
        values.put(COLUMN_PROD_DESC, desc);
        int rows = db.update(TABLE_PRODUCTS, values, COLUMN_PROD_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean updatePassword(int Id, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ? ",
                new String[]{String.valueOf(Id)});

        if (cursor.getCount() > 0) {
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("password", newPassword);
            int rows = db.update("users", values, "id = ?", new String[]{String.valueOf(Id)});
            db.close();
            return rows > 0;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

    public boolean updateUserName(int Id, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String nom = newName.split(" ")[0];
        String prenom = newName.split(" ")[1];
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM, nom);
        values.put(COLUMN_PRENOM, prenom);

        int rows = db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(Id)});
        db.close();
        return rows > 0;
    }

    public boolean updateUserPhoto(int Id, String photoBase64) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHOTO, photoBase64);

        int rows = db.update("users", values, "id = ?", new String[]{String.valueOf(Id)});
        db.close();
        return rows > 0;
    }

    public String getUserPhoto(int Id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT photo FROM users WHERE id = ?", new String[]{String.valueOf(Id)});

        String photoBase64 = null;
        if (cursor.moveToFirst()) {
            photoBase64 = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return photoBase64;
    }

    public String getProductPhoto(int Id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT image FROM products WHERE id = ?", new String[]{String.valueOf(Id)});

        String photoBase64 = null;
        if (cursor.moveToFirst()) {
            photoBase64 = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return photoBase64;
    }
    public boolean updateProductPhoto(int Id, String photoBase64) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_IMG, photoBase64);

        int rows = db.update("products", values, "id = ?", new String[]{String.valueOf(Id)});
        db.close();
        return rows > 0;
    }

}

