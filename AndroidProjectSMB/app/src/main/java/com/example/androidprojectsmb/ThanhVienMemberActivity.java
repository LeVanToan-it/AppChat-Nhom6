package com.example.androidprojectsmb;

import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidprojectsmb.adapter.AccountAdapter;
import com.example.androidprojectsmb.dto.AccountDTO;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ThanhVienMemberActivity extends AppCompatActivity implements AccountAdapter.ItemChangeListener{
    ArrayList<AccountDTO> listAccount;
    AccountAdapter adapter;
    RecyclerView recyclerView;
    private long idAccount,idRoom,idAdmin;
    Gson mGson = new Gson();
    List<Long> listLong ;
    Button btnDoiTenNhom,btnRoiNhom;
    EditText editTenNhom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_thanhvien_member);
        Intent intent = getIntent();
        idAccount = intent.getLongExtra("idAccount", 42);
        idAdmin = intent.getLongExtra("idAdmin", 42);
        idRoom = intent.getLongExtra("idRoom", 69);
        initViews();
        eventHandle();
    }

    public void initViews(){

        listAccount = new ArrayList<>();
        listLong = new ArrayList<>();
        btnDoiTenNhom =(Button) findViewById(R.id.btnDoiTenNhom);
        btnRoiNhom =(Button) findViewById(R.id.btnRoiNhom);
        editTenNhom =(EditText) findViewById(R.id.editTenNhom);
        getListAccountInRoomById(idRoom);
        adapter = new AccountAdapter(this,listAccount,idAccount,idAdmin);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.addItemChageListener(this);

    }



    public void eventHandle(){
        btnDoiTenNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = editTenNhom.getText().toString().trim();
                updateRoomName(roomName,idRoom);
                Intent intent = new Intent(ThanhVienMemberActivity.this,ChatGroupActivity.class);
                intent.putExtra("idAccount",idAccount);
                intent.putExtra("nameRoom",editTenNhom.getText().toString().trim());
                intent.putExtra("idRoom",idRoom);
                startActivity(intent);
            }
        });

        btnRoiNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveRoom(idRoom,idAccount);
                Intent intent = new Intent(ThanhVienMemberActivity.this,GroupActivity.class);
                intent.putExtra("idAccount",idAccount);
                startActivity(intent);
            }
        });

    }

//    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//    public void leaveRoom(long roomId,long idAccount){
//        String url = LOCALHOST+"/rooms/"+roomId+"/leave2" ;
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(idAccount));
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//
//        try {
//            okhttp3.Response response = client.newCall(request).execute();
//
//        } catch (IOException e) {
//            // handle error
//        }
//    }

    public void leaveRoom(long roomId,long idAccount) {
        String url = LOCALHOST+"/rooms/"+roomId+"/leave/"+idAccount ;
        RequestQueue requestQueue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) ;

        requestQueue.add(stringRequest);
    }
    public void removeMembers(List<Long> listAccountId, long roomId) {
        String url = LOCALHOST+"/rooms/"+roomId+"/removeMembers";
        try{
            String jsonString = mGson.toJson(listAccountId);
            System.out.println(jsonString);
            JSONArray jsonBody = new JSONArray(jsonString);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.POST,url,jsonBody,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //System.out.println("add member success");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
            requestQueue.add(jsObjRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void updateRoomName(String roomName, long roomId) {
        String url = LOCALHOST+"/rooms/"+roomId+"/updateRoomName";

            //String jsonString = mGson.toJson(roomName);
            //System.out.println(jsonString);
            String requestBody = roomName;

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);

    }
    public void getListAccountInRoomById(long roomId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LOCALHOST + "/accounts/rooms/" + roomId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = (JSONObject) response.get(i);
                        Gson gson = new Gson();
                        listAccount.add(gson.fromJson(String.valueOf(object), AccountDTO.class));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, error -> Toast.makeText(ThanhVienMemberActivity.this, "Error with JSON Array Object", Toast.LENGTH_SHORT).show());
        queue.add(jsonArrayRequest);
    }

    @Override
    public void onItemCheck(int position, long id) {
        listLong.add(id);

    }

    @Override
    public void onItemUncheck(int position, long id) {
        listLong.remove(id);

    }
}