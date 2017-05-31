package com.app.rakez.dungatrial1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,ListView.OnItemClickListener, View.OnClickListener {

    private ArrayList<String> menuId = new ArrayList<>();
    private ArrayList<String> menuName = new ArrayList<>();
    private ArrayList<String> menuPrice = new ArrayList<>();
    private Button orderUpdate,orderCheckout;
    private SearchView searchView;
    private ListView searchResult;
    private RecyclerView orderRV;
    private OrderItemAdapter orderItemAdapter;
    private List<OrderItem> orderList = new ArrayList<>();
    private ArrayAdapter<String> menuAdapter;
    private ArrayList<String> searchMenuName = new ArrayList<>();
    private ArrayList<String> searchMenuId = new ArrayList<>();
    private ArrayList<String> orderId = new ArrayList<>();
    private ArrayList<String> orderQty = new ArrayList<>();
    private String tableNo;
    private String tableState;

    //variables for previous order
    private List<String> previousOrderMenuName = new ArrayList<>();
    private List<String> previousOrderMenuQty = new ArrayList<>();
    private String previousOrder;


    //variable for delete order
    static Dialog dialog;
    RecyclerView deleteOrderRV;


    String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        SharedPreferences ipPref = getApplicationContext().getSharedPreferences("MyIP", 0);
        ipAddress = ipPref.getString("IPAddress"," ");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle b = getIntent().getExtras();
        tableNo = b.getString("tableNo");
        tableState = b.getString("state");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        makeJsonArrayRequest();
        menuAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.search_layout,searchMenuName);
        orderUpdate = (Button) findViewById(R.id.orderUpdate);
        orderCheckout = (Button) findViewById(R.id.orderCheckout);
        searchResult = (ListView) findViewById(R.id.menuResult);
        searchView = (SearchView) findViewById(R.id.searchMenu);
        searchView.setIconifiedByDefault(false);
        orderRV = (RecyclerView) findViewById(R.id.orderRecylerView);
        searchView.setOnQueryTextListener(this);
        searchResult.setOnItemClickListener(this);
        orderUpdate.setOnClickListener(this);
        orderCheckout.setOnClickListener(this);
        searchResult.setAdapter(menuAdapter);
        orderItemAdapter = new OrderItemAdapter(getApplicationContext(),orderList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderRV.setLayoutManager(linearLayoutManager);
        orderRV.setItemAnimator(new DefaultItemAnimator());
        orderRV.setAdapter(orderItemAdapter);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchMenuName.clear();
                searchMenuId.clear();
                menuAdapter.notifyDataSetChanged();
                return false;
            }
        });

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            //Intent in = new Intent(getApplicationContext(),ScrollingActivity.class);
            //startActivity(in);
            finish();
        }
        if(item.getItemId() == R.id.history){
            makePreviousOrderRequest();

        }
        if(item.getItemId() == R.id.deleteOrder){

            //delete order dialog box
            dialog = new Dialog(MenuActivity.this);
            dialog.setTitle("Remove Order");
            dialog.setContentView(R.layout.dialogdelete);
            deleteOrderRV = (RecyclerView) dialog.findViewById(R.id.deleteOrderRV);


            ArrayList<String> dt = new ArrayList<>();
            requestDeletableOrder(dt);




        }
        return super.onOptionsItemSelected(item);
    }

    private void requestDeletableOrder(final ArrayList<String> dt) {

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, "http://"+ipAddress+"/orderapp/deletable_order.php?tableNo="+tableNo, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d("size of the","Size is response "+response.length());
                for(int i = 0;i<response.length();i++){
                    try {
                        JSONObject table = (JSONObject) response.get(i);
                        String orderNo = table.getString("order_no");
                        dt.add("Order No.  "+orderNo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                prepareDeleteOrder(dt);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        AppController.getInstance().addToRequestQueue(req);

    }

    private void prepareDeleteOrder(ArrayList<String> dt) {
        DeleteOrderAdapter deleteOrderAdapter  = new DeleteOrderAdapter(getApplicationContext(),dt,tableNo,ipAddress);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(dialog.getContext());
        deleteOrderRV.setLayoutManager(linearLayoutManager);
        deleteOrderRV.setItemAnimator(new DefaultItemAnimator());
        deleteOrderRV.setAdapter(deleteOrderAdapter);
        dialog.show();
    }

    private void makeJsonArrayRequest(){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, "http://"+ipAddress+"/orderapp/menuJson.php", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d("size of the","Size is response "+response.length());
                for(int i = 0;i<response.length();i++){
                    try {
                        JSONObject table = (JSONObject) response.get(i);
                        String id = table.getString("id");
                        String name = table.getString("name");
                        String price = table.getString("price");

                        //Log.d("size of the","Data is "+num + sts);
                        menuId.add(id);
                        menuName.add(name);
                        menuPrice.add(price);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //Log.d("size of the","Data is "+tableNo.size());
                pDialog.hide();
                pDialog.dismiss();
                //prepareData(tableNo,status);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                pDialog.dismiss();

            }
        });
        AppController.getInstance().addToRequestQueue(req);


    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        searchMenuName.clear();
        searchMenuId.clear();
        for(int i = 0 ; i < menuName.size() ; i++){
            if(menuName.get(i).toLowerCase().contains(s.toLowerCase())){
                searchMenuName.add(menuName.get(i));
                searchMenuId.add(menuId.get(i));
            }
        }
        menuAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(!orderId.contains(searchMenuId.get(i))){
            Log.d("xyz","This is data "+searchMenuName.get(i)+searchMenuId.get(i));
            orderId.add(searchMenuId.get(i));
            orderQty.add("1");
            prepareData(Integer.parseInt(searchMenuId.get(i)));
        }
        searchView.setQuery("",true);
        searchMenuName.clear();
        searchMenuId.clear();
        menuAdapter.notifyDataSetChanged();


    }
    public void prepareData(int position){
        orderList.add(new OrderItem(menuName.get(position-1),"1", String.valueOf(position)));
        orderItemAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(getApplicationContext(),NavActivity.class);
        startActivity(in);
        finish();
    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.orderUpdate){
            AlertDialog.Builder adb = new AlertDialog.Builder(MenuActivity.this);
            adb.setTitle("Confirm");
            adb.setMessage("Are you sure about sending order?");
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String menuId=null;
                    String menuQty=null;
                    for(int j = 0 ; j < orderList.size() ; j++){
                        Log.d("This","This is sample order"+orderList.get(j).getItemName()+orderList.get(j).getItemQty()+orderList.get(j).getItemId());
                        if(j==0){
                            menuId = orderList.get(j).getItemId();
                            menuQty = orderList.get(j).getItemQty();
                        }else{
                            menuId = menuId+","+orderList.get(j).getItemId();
                            menuQty = menuQty+","+orderList.get(j).getItemQty();
                        }
                    }
                    if(!menuId.equals(null)){
                        makePostRequest(tableNo,menuId,menuQty,"2");
                    }
                }
            });
            adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            adb.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            adb.show();
        }
        if(view.getId()==R.id.orderCheckout){
            AlertDialog.Builder adb = new AlertDialog.Builder(MenuActivity.this);
            adb.setTitle("Confirm");
            adb.setMessage("Are you sure about checkout?");
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    makePostCheckout(tableNo);
                    Intent in = new Intent(getApplicationContext(),NavActivity.class);
                    startActivity(in);
                    finish();
                }
            });
            adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            adb.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            adb.show();

        }

    }

    private void makePostCheckout(final String tableNo) {
        StringRequest sr  = new StringRequest(Request.Method.POST, "http://"+ipAddress+"/orderapp/orderCheckout.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("fsds","Sample post "+response);
                if(response.contains("success")){
                    Log.d("fsds","Reached here");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                    builder.setTitle("Success");
                    builder.setMessage("Checkout Success");
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dg = builder.create();
                    dg.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("tableNo",tableNo);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(sr);

    }

    private void makePostRequest(final String tableNo, final String menuId, final String menuQty, final String waiterId){
        StringRequest sr  = new StringRequest(Request.Method.POST, "http://"+ipAddress+"/orderapp/orderHandler.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("fsds","Sample post "+response);
                if(response.contains("success")){
                    Log.d("fsds","Reached here");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                    builder.setTitle("Success");
                    builder.setMessage("Order Successful");
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dg = builder.create();
                    dg.show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("tableNo",tableNo);
                params.put("menuId",menuId);
                params.put("menuQty",menuQty);
                params.put("waiterId",waiterId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(sr);
    }
    private void makePreviousOrderRequest(){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading");
        pDialog.show();


        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, "http://"+ipAddress+"/orderapp/previous_order.php?tableNo="+tableNo, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                previousOrderMenuName.clear();
                previousOrderMenuQty.clear();
                Log.d("size of the","Sizw is rakjsdifns response "+response.length());
                for(int i = 0;i<response.length();i++){
                    try {
                        JSONObject table = (JSONObject) response.get(i);
                        String qty = table.getString("qty");
                        String name = table.getString("name");
                        previousOrderMenuQty.add(qty);
                        previousOrderMenuName.add(name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                pDialog.hide();
                pDialog.dismiss();
                preparePreviousOrder();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                pDialog.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(req);
    }

    public void preparePreviousOrder(){
        previousOrder = "";
        for(int i = 0; i< previousOrderMenuName.size(); i++){
            previousOrder = previousOrder + previousOrderMenuName.get(i)+"  "+previousOrderMenuQty.get(i)+"\n";
        }
        AlertDialog.Builder adb = new AlertDialog.Builder(MenuActivity.this);
        adb.setTitle("Confirm");
        adb.setMessage(previousOrder);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        adb.show();
    }

    public  static void dismissDialog(){
        dialog.dismiss();
    }

}
