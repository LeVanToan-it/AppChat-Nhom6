package com.example.androidprojectsmb;

import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidprojectsmb.adapter.GroupAdapter;
import com.example.androidprojectsmb.adapter.UserAdapter;
import com.example.androidprojectsmb.dto.RoomDTO;
import com.example.androidprojectsmb.dto.UserDTO;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    ArrayList<RoomDTO> listRoom;
    GroupAdapter adapter;
    RecyclerView recyclerView;
    private long idAccount;
    EditText editTaoNhom;
    Button btnTaoNhom;
    Gson mGson = new Gson();
    RoomDTO roomDTO = new RoomDTO();
    ImageView imgBtnDanhBa,imgBtnCaNhan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_groupchat);
        Intent intent = getIntent();
        idAccount = intent.getLongExtra("idAccount", 42);
        initViews();


        eventHandle();
    }

    public void initViews(){
        editTaoNhom= (EditText) findViewById(R.id.editTaoNhom);
        btnTaoNhom= (Button) findViewById(R.id.btnTaoNhom);
        imgBtnDanhBa= (ImageView) findViewById(R.id.imgBtnDanhBa);
        imgBtnCaNhan= (ImageView) findViewById(R.id.imgBtnCaNhan);
        listRoom = new ArrayList<>();
        getListGroupByAccountId(idAccount);
        adapter = new GroupAdapter(this,listRoom,idAccount);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void eventHandle(){
        btnTaoNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomDTO room = new RoomDTO();
                room.setName(editTaoNhom.getText().toString().trim());
                room.setType("Group");
                room.setAdminId(idAccount);
                createGroup(room);
            }
        });
        imgBtnDanhBa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this,DanhBaActivity.class);
                intent.putExtra("idAccount",idAccount);
                startActivity(intent);
            }
        });
        imgBtnCaNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this,CaNhanActivity.class);
                intent.putExtra("idAccount",idAccount);
                startActivity(intent);
            }
        });
    }

    public void getListGroupByAccountId(long id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LOCALHOST+"/rooms/byAccountId/"+id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = (JSONObject) response.get(i);
                        Gson gson = new Gson();
                        listRoom.add(gson.fromJson(String.valueOf(object), RoomDTO.class));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, error -> Toast.makeText(GroupActivity.this, "Error with JSON Array Object", Toast.LENGTH_SHORT).show());
        queue.add(jsonArrayRequest);
    }

    private void createGroup(RoomDTO room){

        String url = LOCALHOST+"/rooms";
        try{
            JSONObject jsonBody = new JSONObject(mGson.toJson(room));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //System.out.println(response);
                            RoomDTO temp = mGson.fromJson(response.toString(),RoomDTO.class);
                            addMembers(temp.getId(),idAccount);
                            abc(temp);
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

    public void abc(RoomDTO dto){
        roomDTO = dto;
        System.out.println(roomDTO.toString());
        listRoom.add(dto);
        adapter.notifyDataSetChanged();
    }

    private void addMembers(long roomId,long idAccount){

            //ADD ADMIN INTO GROUP
            List<Long> listAccountId = new ArrayList<>();
            listAccountId.add(idAccount);


            String url = LOCALHOST+"/rooms/"+roomId+"/addMembers";
            try{
                String jsonString = mGson.toJson(listAccountId);
                //System.out.println(jsonString);
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
    public static String convertListLongToString(List<Long> listLong){
        String str = "[";
        for (Long l : listLong){
            str = str.concat(l+",");
        }
        str = deleteLastChar(str) + "]";
        return str;
    }
    public static String deleteLastChar(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}