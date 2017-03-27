package com.app.rakez.dungatrial1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScrollingActivity extends AppCompatActivity {
    EditText userName,password;
    String uName=null,uPassword=null;
    int check = 0;
    TextView resultTv;
    UserInfo ui;


    String ipAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("Digital OS");
        SharedPreferences ipPref = getApplicationContext().getSharedPreferences("MyIP", 0);
        ipAddress = ipPref.getString("IPAddress"," ");
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        resultTv = (TextView) findViewById(R.id.result);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                uName= userName.getText().toString();
                uPassword = password.getText().toString();
                if(!uName.equals(null)&&!uPassword.equals(null)){
                    if(makeJsonArrayRequest()){
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("login","true");
                        editor.putString("login_id",ui.getId());
                        editor.commit();

                        Intent in = new Intent(getApplicationContext(),NavActivity.class);
                        startActivity(in);
                        finish();
                    }
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.action_settings){

        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.setIP) {
            Bundle source = new Bundle();
            Intent in = new Intent(getApplicationContext(),setIP.class);
            source.putString("requestFrom","login");
            in.putExtras(source);
            startActivity(in);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }
    private boolean makeJsonArrayRequest(){

      /*  final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading");
        pDialog.show();
     */

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, "http://"+ipAddress+"/orderapp/login.php?username="+uName+"&password="+uPassword, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                check = response.length();
                for(int i = 0;i<response.length();i++){
                    try {
                        JSONObject user = (JSONObject) response.get(i);
                        String id = user.getString("id");
                        String name = user.getString("name");
                        String password = user.getString("password");
                        String phoneno = user.getString("phoneno");
                        String address = user.getString("address");
                        String username = user.getString("username");
                        String role_id = user.getString("role_id");

                        ui = new UserInfo(address,id,name,phoneno,role_id,username);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //pDialog.hide();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //pDialog.hide();


            }
        });
        AppController.getInstance().addToRequestQueue(req);
        if(check==0){
            resultTv.setText("Login Failed");
            return false;

        }else{
            resultTv.setText("Login Success");
            return true;
        }

    }
}
