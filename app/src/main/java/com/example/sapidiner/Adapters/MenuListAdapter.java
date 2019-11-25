package com.example.sapidiner.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
    public static ArrayList<String> selectedFoodItems = new ArrayList<>();

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
    public void onBindViewHolder(@NonNull ListViewHolder holder, final int position) {
        //todo image

        holder.foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getBackground() == null){
                    v.setBackgroundResource(R.drawable.photo_highlight);
                    selectedFoodItems.add(foods.get(position));
                } else {
                    v.setBackgroundResource(0);
                    selectedFoodItems.remove(foods.get(position));
                }

            }
        });

        holder.foodName.setText(foods.get(position));
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }


    public class ListViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodImage;
        private TextView foodName;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodItemImage);
            foodName = itemView.findViewById(R.id.foodItemName);
        }
    }
}
