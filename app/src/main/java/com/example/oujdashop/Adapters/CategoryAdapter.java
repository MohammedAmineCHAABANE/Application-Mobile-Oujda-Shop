package com.example.oujdashop.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.oujdashop.Models.Category;
import com.example.oujdashop.R;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Category> categoryList;
    private LayoutInflater inflater;

    public CategoryAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_category, parent, false);
            holder = new ViewHolder();
            holder.tvCategoryName = convertView.findViewById(R.id.tvCategoryName);
            holder.tvCategoryDescription = convertView.findViewById(R.id.tvCategoryDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryDescription.setText(category.getDescription());

        return convertView;
    }

    static class ViewHolder {
        TextView tvCategoryName, tvCategoryDescription;
    }
}
