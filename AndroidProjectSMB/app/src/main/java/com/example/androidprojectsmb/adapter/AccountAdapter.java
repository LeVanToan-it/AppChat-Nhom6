package com.example.androidprojectsmb.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidprojectsmb.R;
import com.example.androidprojectsmb.dto.AccountDTO;
import com.example.androidprojectsmb.dto.UserDTO;

import java.util.ArrayList;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>  {
    Context context;
    ArrayList<AccountDTO> listAccout;
    private long idAccount;
    private ItemChangeListener mItemChangeListener;
    private long idAdmin;
    public AccountAdapter(Context context, ArrayList<AccountDTO> listAccout, long idAccount,long idAdmin) {
        this.context = context;
        this.listAccout = listAccout;
        this.idAccount = idAccount;
        this.idAdmin= idAdmin;

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
        AccountDTO account = listAccout.get(position);
        holder.txtId.setText(account.getId()+"");
        holder.txtName.setText(account.getUsername());
        if(idAdmin==account.getId()) {
            holder.txtSDT.setText("Chức vụ: Quản trị viên");
            holder.checkbox_account.setVisibility(View.INVISIBLE);
        }
        else{
            holder.txtSDT.setText("Chức vụ: Thành viên");
        }
        holder.checkbox_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mItemChangeListener != null) {
                    long id = account.getId();
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
        return listAccout.size();
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
