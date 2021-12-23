package com.example.androidprojectsmb;

import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidprojectsmb.adapter.AddMemberAdapter;
import com.example.androidprojectsmb.adapter.GroupAdapter;
import com.example.androidprojectsmb.dto.RoomDTO;
import com.example.androidprojectsmb.dto.UserDTO;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddMemberActivity extends AppCompatActivity implements AddMemberAdapter.ItemChangeListener{
    ArrayList<UserDTO> listUser;
    AddMemberAdapter adapter;
    RecyclerView recyclerView;
    private long idAccount,idRoom;
    Gson mGson = new Gson();
    RoomDTO roomDTO = new RoomDTO();
    List<Long> listLong ;
    Button btnThem,btnTimBanBe;
    EditText editTimBanBe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_addmember);
        Intent intent = getIntent();
        idAccount = intent.getLongExtra("idAccount", 42);
        idRoom = intent.getLongExtra("idRoom", 69);
        initViews();


        eventHandle();
    }

    public void initViews(){
        listUser = new ArrayList<>();
        listLong = new ArrayList<>();
        btnThem =(Button) findViewById(R.id.btnThem);
        btnTimBanBe =(Button) findViewById(R.id.btnTimBanBe);
        editTimBanBe =(EditText) findViewById(R.id.editTimBanBe);
        getUserByContactOfAccountId(idAccount,idRoom);
        adapter = new AddMemberAdapter(this,listUser,idAccount);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.addItemChageListener(this);
    }

    public void eventHandle(){
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMembers(listLong,idRoom);
                finish();
            }
        });
        btnTimBanBe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = editTimBanBe.getText().toString().trim();
                if(key==null)
                    key="";
                getUserByKey(idAccount,key,idRoom);

            }
        });

    }
    public void addMembers( List<Long> listAccountId,long roomId) {
        String url = LOCALHOST+"/rooms/"+roomId+"/addMembers";
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
    public void getUserByKey(long id,String key,long roomId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LOCALHOST + "/users/"+id+"/rooms/"+roomId+"/byKey/"+key;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                listUser.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = (JSONObject) response.get(i);
                        Gson gson = new Gson();
                        listUser.add(gson.fromJson(String.valueOf(object), UserDTO.class));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, error -> Toast.makeText(AddMemberActivity.this, "Vui lòng nhập từ khóa muốn tìm", Toast.LENGTH_SHORT).show());
        queue.add(jsonArrayRequest);
    }

    public void getUserByContactOfAccountId(long id,long roomId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LOCALHOST + "/users/byContactOfAccountId/" + id+"/rooms/"+roomId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = (JSONObject) response.get(i);
                        Gson gson = new Gson();
                        listUser.add(gson.fromJson(String.valueOf(object), UserDTO.class));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, error -> Toast.makeText(AddMemberActivity.this, "Error with JSON Array Object", Toast.LENGTH_SHORT).show());
        queue.add(jsonArrayRequest);
    }


    @Override
    public void onItemCheck(int position, long id) {
        listLong.add(id);
        for(long l : listLong)
            System.out.println("id: "+l);
    }

    @Override
    public void onItemUncheck(int position, long id) {
        listLong.remove(id);
        for(long l : listLong)
            System.out.println("id: "+l);
    }
}