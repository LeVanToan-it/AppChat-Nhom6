package com.example.androidprojectsmb.adapter;

import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidprojectsmb.ChatActivity;
import com.example.androidprojectsmb.R;
import com.example.androidprojectsmb.dto.RoomDTO;
import com.example.androidprojectsmb.dto.UserDTO;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class LoiMoiKetBanAdapter extends RecyclerView.Adapter<LoiMoiKetBanAdapter.ViewHolder> {
    Context context;
    ArrayList<UserDTO> listUser;
    private long idAccount;
    private ItemClickListener mItemClickListener;
    public LoiMoiKetBanAdapter(Context context, ArrayList<UserDTO> listUser, long idAccount) {
        this.context = context;
        this.listUser = listUser;
        this.idAccount = idAccount;
    }

    //Define your Interface method here
    public interface ItemClickListener {
        void onItemClick(int position,long id);
    }
    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_loimoiketban,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        UserDTO user = listUser.get(position);
        holder.txtId.setText(user.getId()+"");
        holder.txtName.setText(user.getAccount().getUsername());
        holder.txtSDT.setText("SĐT: " +user.getSoDienThoai());
        holder.imgAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicking");
                if(mItemClickListener!=null){
                    mItemClickListener.onItemClick(position,user.getAccount().getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtId;
        private TextView txtName;
        private TextView txtSDT;
        private ImageView imgAccept,imgXoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtName = itemView.findViewById(R.id.txtName);
            txtSDT = itemView.findViewById(R.id.txtSDT);
            imgAccept = itemView.findViewById(R.id.imgAccept);
            imgXoa = itemView.findViewById(R.id.imgXoa);
        }
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
