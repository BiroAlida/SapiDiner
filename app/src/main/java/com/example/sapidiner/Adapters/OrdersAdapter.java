package com.example.sapidiner.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapidiner.Classes.Orders;
import com.example.sapidiner.R;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<Orders> orders;

    public OrdersAdapter(Context context, ArrayList<Orders> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override


    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.view_orders,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        String userFirstName = orders.get(position).getUser().getFirstName();
        String userLastName = orders.get(position).getUser().getLastName();
        holder.userId.setText("Név: " + userFirstName + " " + userLastName);
        String o = "";
        String space = " ";
        if(orders.get(position).getOrders() != null)
        {
            for(String ord : orders.get(position).getOrders())
            {

                o = o + space + ord;
            }

        }

        holder.order.setText("Rendelés: " + o);
        holder.price.setText("Ár: " + orders.get(position).getPrice() + " RON");

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userId,order, price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userId = (TextView) itemView.findViewById(R.id.textViewId);
            order = (TextView) itemView.findViewById(R.id.textViewOrders);
            price = (TextView) itemView.findViewById(R.id.textViewPrice);
        }
    }
}