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
import com.example.androidprojectsmb.adapter.GroupAdapter;
import com.example.androidprojectsmb.adapter.LoiMoiKetBanAdapter;
import com.example.androidprojectsmb.dto.ContactDTO;
import com.example.androidprojectsmb.dto.RoomDTO;
import com.example.androidprojectsmb.dto.UserDTO;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoiMoiKetBanActivity extends AppCompatActivity implements LoiMoiKetBanAdapter.ItemClickListener {
    ArrayList<UserDTO> listUser;
    LoiMoiKetBanAdapter adapter;
    RecyclerView recyclerView;
    private long idAccount;
    Gson mGson = new Gson();
    ImageView imgBtnDanhBa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_loimoiketban);
        Intent intent = getIntent();
        idAccount = intent.getLongExtra("idAccount", 42);
        initViews();


        eventHandle();
    }

    public void initViews(){
        imgBtnDanhBa= (ImageView) findViewById(R.id.imgBtnDanhBa);
        listUser = new ArrayList<>();
        getListContactByAccountId(idAccount);
        adapter = new LoiMoiKetBanAdapter(this,listUser,idAccount);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.addItemClickListener(this);
    }

    public void eventHandle(){

        imgBtnDanhBa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoiMoiKetBanActivity.this,DanhBaActivity.class);
                intent.putExtra("idAccount",idAccount);
                startActivity(intent);
            }
        });

    }

    public void getListContactByAccountId(Long id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LOCALHOST+"/users/byContactOfAccountIdNotAccept/"+id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response!=null)
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
        }, error -> Toast.makeText(LoiMoiKetBanActivity.this, "Error with JSON Array Object", Toast.LENGTH_SHORT).show());
        queue.add(jsonArrayRequest);
    }


    @Override
    public void onItemClick(int position, long id) {

        //CREATE ROOM
        RoomDTO newRoom = new RoomDTO();
        newRoom.setAdminId(idAccount);
        newRoom.setDeleted(false);
        newRoom.setType("Dual");
        createRoom(newRoom,idAccount,id);

        //CREATE CONTACTS
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LOCALHOST+"/contacts/accept/"+idAccount+"/"+id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

            }
        }, error -> System.out.println(error));
        queue.add(jsonArrayRequest);
        for(UserDTO u : listUser){
            if(u.getAccount().getId()==id){
                listUser.remove(u);
                adapter.notifyDataSetChanged();
            }
        }
    }
    public void createRoom( RoomDTO room,long accountId, long friendId)  {
        try {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LOCALHOST+"/rooms/byTwoAccountId/"+accountId+"/"+friendId;
        JSONObject jsonObject = null;

            jsonObject = new JSONObject(mGson.toJson(room));

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(url, jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, error -> System.out.println(error));
            queue.add(jsonArrayRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}