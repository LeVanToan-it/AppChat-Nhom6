package com.example.androidprojectsmb.adapter;

import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.List;

public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.ViewHolder>  {
    Context context;
    ArrayList<UserDTO> listUser;
    private long idAccount;
    private ItemChangeListener mItemChangeListener;

    public AddMemberAdapter(Context context, ArrayList<UserDTO> listUser, long idAccount) {
        this.context = context;
        this.listUser = listUser;
        this.idAccount = idAccount;

    }
    //Define your Interface method here
    public interface ItemChangeListener {
        void onItemCheck(int position,long id);
        void onItemUncheck(int position,long id);
    }
    public void addItemChageListener(ItemChangeListener listener) {
        mItemChangeListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_checkbox_account,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        UserDTO user = listUser.get(position);
        holder.txtId.setText(user.getAccount().getId()+"");
        holder.txtName.setText(user.getAccount().getUsername());
        holder.txtSDT.setText("SĐT: " +user.getSoDienThoai());
        holder.checkbox_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mItemChangeListener != null) {
                    long id = user.getAccount().getId();
                    if(isChecked)
                        mItemChangeListener.onItemCheck(position,id);
                    else mItemChangeListener.onItemUncheck(position,id);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getRoomDualByTwoAccountId(idAccount,user);
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
        private CheckBox checkbox_account;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtName = itemView.findViewById(R.id.txtName);
            txtSDT = itemView.findViewById(R.id.txtSDT);
            checkbox_account = itemView.findViewById(R.id.checkbox_account);
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
