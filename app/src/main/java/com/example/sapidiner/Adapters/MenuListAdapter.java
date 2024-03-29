package com.example.sapidiner.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sapidiner.Classes.Food;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ListViewHolder> {
    private Context context;
    private ArrayList<Food> foodList;
    public static ArrayList<Food> selectedFoodItems = new ArrayList<>();
    public static double totalPrice = 0;

    public MenuListAdapter(Context context, ArrayList<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
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
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {
        holder.foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getBackground() == null){
                    //highlight selected item
                    v.setBackgroundResource(R.drawable.photo_highlight);
                    selectedFoodItems.add(foodList.get(position));
                    totalPrice = totalPrice + foodList.get(position).getPrice();
                } else {
                    //remove highlight
                    v.setBackgroundResource(0);
                    selectedFoodItems.remove(foodList.get(position));
                    totalPrice = totalPrice - foodList.get(position).getPrice();
                }
            }
        });

        loadImage(foodList.get(position).getName(), holder.foodImage);
        holder.foodName.setText(foodList.get(position).getName());
        holder.foodPrice.setText(String.valueOf(foodList.get(position).getPrice()));
    }

    private void loadImage(String foodName, final ImageView imageView){
        FirebaseDatabaseManager.Instance.getStorageReference().child(foodName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri.toString())
                        .apply(new RequestOptions().placeholder(R.drawable.image_placeholder))
                        .into(imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }


    public class ListViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodImage;
        private TextView foodName;
        private TextView foodPrice;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodItemImage);
            foodName = itemView.findViewById(R.id.foodItemName);
            foodPrice = itemView.findViewById(R.id.foodItemPrice);
        }
    }
}
