package com.app.rakez.dungatrial1;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RAKEZ on 4/27/2017.
 */
public class DeleteOrderAdapter extends RecyclerView.Adapter<DeleteOrderAdapter.MyViewHolder> {
    private Context mContext;
    private List<String> orderNo;
    private String tableNo,ipAddress;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public Button delete;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.deleteOrderOrderNo);
            delete = (Button) view.findViewById(R.id.deleteOrderOrderButton);
        }
    }

    public DeleteOrderAdapter(Context mContext, List<String> orderNo, String tableNo, String ipAddress) {
        this.mContext = mContext;
        this.orderNo = orderNo;
        this.tableNo=tableNo;
        this.ipAddress = ipAddress;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.delete_order_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String ordr = orderNo.get(position);
        holder.name.setText(ordr);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Trial","Button Clicked");
                makeDeleteOrderRequest(tableNo,ordr.substring(10));


            }
        });

    }

    private void makeDeleteOrderRequest(final String tableNo,final String orderNo) {

        StringRequest sr  = new StringRequest(Request.Method.POST, "http://"+ipAddress+"/orderapp/delete_order.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")){
                    Toast.makeText(mContext,"Order Deleted!!!",Toast.LENGTH_LONG);

                }else{
                    Toast.makeText(mContext,"Order Delete Failed!!!",Toast.LENGTH_LONG);
                }
                Toast.makeText(mContext,response,Toast.LENGTH_LONG);
                MenuActivity.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext,error.getMessage()+ipAddress+" "+tableNo+" "+orderNo,Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("table_no",tableNo);
                params.put("order_no",orderNo);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(sr);
    }

    @Override
    public int getItemCount() {
        return orderNo.size();
    }


}
