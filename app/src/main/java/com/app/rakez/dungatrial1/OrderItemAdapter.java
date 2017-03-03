package com.app.rakez.dungatrial1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by RAKEZ on 2/16/2017.
 */
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.MyViewHolder>{

    private Context mContext;
    private List<OrderItem> orderItem;

    public OrderItemAdapter(Context mContext, List<OrderItem> orderItem) {
        this.mContext = mContext;
        this.orderItem = orderItem;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        OrderItem order = orderItem.get(position);
        holder.itemName.setText(order.getItemName());
        holder.itemQty.setText(order.getItemQty());
        holder.addQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos =  holder.getAdapterPosition();
                String orderQty = holder.itemQty.getText().toString();
                String itemId = orderItem.get(pos).getItemId();
                int tempQty = Integer.parseInt(orderQty);
                String orderName = holder.itemName.getText().toString();
                orderItem.remove(pos);
                //notifyItemRemoved(pos);
                notifyItemRangeChanged(pos,orderItem.size());
                orderItem.add(pos,new OrderItem(orderName,String.valueOf(++tempQty), itemId));
                //notifyItemInserted(pos);
                notifyItemRangeChanged(pos,orderItem.size());
                notifyDataSetChanged();
            }
        });
        holder.minusQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = holder.itemQty.getText().toString();
                int tempval =  Integer.parseInt(val);
                holder.itemQty.setText(String.valueOf(--tempval));
                if(tempval==0){
                    int pos =  holder.getAdapterPosition();
                    orderItem.remove(pos);
                    notifyItemRemoved(pos);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItem.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName,itemQty;
        public Button addQty,minusQty;

        public MyViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.itemName);
            itemQty = (TextView) view.findViewById(R.id.itemQty);
            addQty = (Button) view.findViewById(R.id.addQty);
            minusQty = (Button) view.findViewById(R.id.minusQty);

        }

    }


}
