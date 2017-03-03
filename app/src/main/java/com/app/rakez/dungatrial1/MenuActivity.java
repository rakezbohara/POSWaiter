package com.app.rakez.dungatrial1;

import android.app.ProgressDialog;
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

        }
        return super.onOptionsItemSelected(item);
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
                //prepareData(tableNo,status);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();

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
        Log.d("xyz","This is data "+searchMenuName.get(i)+searchMenuId.get(i));
        orderId.add(searchMenuId.get(i));
        orderQty.add("1");
        prepareData(Integer.parseInt(searchMenuId.get(i)));
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
    public void onClick(View view) {

        if (view.getId()==R.id.orderUpdate){
            String menuId=null;
            String menuQty=null;
            for(int i = 0 ; i < orderList.size() ; i++){
                Log.d("This","This is sample order"+orderList.get(i).getItemName()+orderList.get(i).getItemQty()+orderList.get(i).getItemId());
                if(i==0){
                    menuId = orderList.get(i).getItemId();
                    menuQty = orderList.get(i).getItemQty();
                }else{
                    menuId = menuId+","+orderList.get(i).getItemId();
                    menuQty = menuQty+","+orderList.get(i).getItemQty();
                }
            }
            if(!menuId.equals(null)){
                makePostRequest(tableNo,menuId,menuQty,"2");
            }
        }
        if(view.getId()==R.id.orderCheckout){
            makePostCheckout(tableNo);
            Intent in = new Intent(getApplicationContext(),NavActivity.class);
            startActivity(in);
            finish();
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
}
