package com.example.oujdashop.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oujdashop.Models.Product;
import com.example.oujdashop.R;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> productList;
    private LayoutInflater inflater;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.ivProductImage = convertView.findViewById(R.id.ivProductImage);
            holder.tvProductName = convertView.findViewById(R.id.tvProductName);
            holder.tvProductPrice = convertView.findViewById(R.id.tvProductPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);

        // Afficher les informations du produit
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(String.format("%.2f DH", product.getPrice()));
        if(product.getImage()!=null){
            holder.ivProductImage.setImageBitmap(getBitmap(product.getImage()));
        }else {
            holder.ivProductImage.setImageResource(R.mipmap.logo_app);
        }

        return convertView;
    }

    public Bitmap getBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    static class ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice;
    }
}
