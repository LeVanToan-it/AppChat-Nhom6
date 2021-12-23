package com.example.androidprojectsmb.adapter;

import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidprojectsmb.ChatActivity;
import com.example.androidprojectsmb.ChatGroupActivity;
import com.example.androidprojectsmb.R;
import com.example.androidprojectsmb.ThanhVienActivity;
import com.example.androidprojectsmb.dto.AccountDTO;
import com.example.androidprojectsmb.dto.RoomDTO;
import com.example.androidprojectsmb.dto.UserDTO;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    Context context;
    ArrayList<RoomDTO> listRoom;
    private long idAccount;

    public GroupAdapter(Context context, ArrayList<RoomDTO> listRoom, long idAccount) {
        this.context = context;
        this.listRoom = listRoom;
        this.idAccount = idAccount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_groupchat,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomDTO room = listRoom.get(position);
        holder.txtId.setText(room.getId()+"");
        holder.txtName.setText(room.getName());
        holder.txtCount.setText("Members: 1");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getRoom(idAccount,room);
                getAdminInRoomById(room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRoom.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtId;
        private TextView txtName;
        private TextView txtCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtName = itemView.findViewById(R.id.txtName);
            txtCount = itemView.findViewById(R.id.txtCount);
        }
    }
    public void getRoom(long idAccount,RoomDTO room,long idAdmin) {
                Intent intent= new Intent(context, ChatGroupActivity.class);
                intent.putExtra("idAccount",idAccount);
                intent.putExtra("idRoom",room.getId());
                intent.putExtra("idAdmin",idAdmin);
                intent.putExtra("nameRoom",room.getName());
                context.startActivity(intent);

    }
    public void getAdminInRoomById(RoomDTO room) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = LOCALHOST + "/rooms/"+room.getId()+"/getAdmin";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject object = (JSONObject) response;
                Gson gson = new Gson();
                AccountDTO a= gson.fromJson(String.valueOf(object), AccountDTO.class);
                getRoom(idAccount,room,a.getId());
            }
        }, error -> Toast.makeText(context, "Error JSOn OBJECT", Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
