package com.example.sapidiner.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapidiner.R;

import java.util.ArrayList;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ListViewHolder> {
    private Context context;
    private ArrayList<String> foods;

    public MenuListAdapter(Context context, ArrayList<String> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.food_list_item, parent, false);
        ListViewHolder viewHolder = new ListViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        //todo image
        holder.foodName.setText(foods.get(position));
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView foodName;
        private ImageView foodImage;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodImage = itemView.findViewById(R.id.foodImage);
        }
    }
}
