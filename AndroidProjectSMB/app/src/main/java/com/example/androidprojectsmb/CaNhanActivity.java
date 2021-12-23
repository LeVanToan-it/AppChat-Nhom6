package com.example.androidprojectsmb;

import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidprojectsmb.adapter.AddMemberAdapter;
import com.example.androidprojectsmb.dto.AccountDTO;
import com.example.androidprojectsmb.dto.RoomDTO;
import com.example.androidprojectsmb.dto.UserDTO;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class CaNhanActivity extends AppCompatActivity {
    long idAccount;
    TextView tvTenNguoiDung,tvSoDienThoai;
    Button btnLoiMoiKetBan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_canhan);

        Intent intent = getIntent();
        idAccount = intent.getLongExtra("idAccount", 42);
        initViews();


        eventHandle();

    }

    public void initViews(){
        tvTenNguoiDung = (TextView) findViewById(R.id.tvTenNguoiDung);
        tvSoDienThoai = (TextView) findViewById(R.id.tvSoDienThoai);
        btnLoiMoiKetBan = (Button) findViewById(R.id.btnLoiMoiKetBan);
        getUserByAccoudId();
    }

    public void eventHandle(){
        btnLoiMoiKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CaNhanActivity.this,LoiMoiKetBanActivity.class);
                intent.putExtra("idAccount",idAccount);
                startActivity(intent);
            }
        });

    }

    public void getUserByAccoudId() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LOCALHOST + "/users/accounts/"+idAccount;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject object = (JSONObject) response;
                Gson gson = new Gson();
                UserDTO u= gson.fromJson(String.valueOf(object), UserDTO.class);
                tvSoDienThoai.setText(u.getSoDienThoai());
                tvTenNguoiDung.setText(u.getAccount().getUsername());
            }
        }, error -> Toast.makeText(this, "Error JSOn OBJECT", Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }
}