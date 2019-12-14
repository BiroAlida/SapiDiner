package com.example.sapidiner.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapidiner.Classes.Order;
import com.example.sapidiner.R;
import com.example.sapidiner.Utilities;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {
    private MyViewHolder.OrderClickListener orderClickListener;
    private ArrayList<Order> orders;

    public OrdersAdapter(ArrayList<Order> orders, MyViewHolder.OrderClickListener orderClickListener) {
        this.orders = orders;
        this.orderClickListener = orderClickListener;
    }

    @NonNull
    @Override


    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.view_orders, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(listItem, orderClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.userId.setText(orders.get(position).getUser().getName());
        holder.order.setText(Utilities.concatFoodListItems(orders.get(position).getFoodList()));
        holder.price.setText(String.valueOf(orders.get(position).getTotalPrice()));

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView userId,order, price;
        private OrderClickListener orderClickListener;

        public MyViewHolder(@NonNull View itemView, OrderClickListener orderClickListener) {
            super(itemView);
            userId = (TextView) itemView.findViewById(R.id.textViewId);
            order = (TextView) itemView.findViewById(R.id.textViewOrders);
            price = (TextView) itemView.findViewById(R.id.textViewPrice);
            this.orderClickListener = orderClickListener;
            if (orderClickListener != null){
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            orderClickListener.onOrderClick(getAdapterPosition());
        }

        public interface OrderClickListener{
            void onOrderClick(int position);
        }
    }
}