package com.example.androidprojectsmb;



import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;
import static com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidprojectsmb.dto.AccountDTO;
import com.example.androidprojectsmb.dto.UserDTO;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {
    private EditText mEditPhone_signup, mEditHoTen, mEditPass_signup, mEditRePass;
    private Button mBtnSignup_signup, mBtnHaveAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangky);

        initViews();
        mBtnSignup_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignupActivity.this, OTPActivity.class);
                intent.putExtra("user",mEditPhone_signup.getText().toString().trim());
                intent.putExtra("pass",mEditPass_signup.getText().toString().trim());
                intent.putExtra("hoten",mEditHoTen.getText().toString());
                startActivity(intent);
            }
        });

    }



    public void initViews(){
        mEditPhone_signup = (EditText) findViewById(R.id.editSoDienThoai_signup);
        mEditHoTen = (EditText) findViewById(R.id.editHoTen);
        mEditPass_signup = (EditText) findViewById(R.id.editMatKhau_signup);
        mEditRePass = (EditText) findViewById(R.id.editNhapLaiMatKhau);
        mBtnSignup_signup = (Button) findViewById(R.id.btnDangKy_signup);
        mBtnHaveAccount = (Button) findViewById(R.id.btnDaCoTaiKhoan);
    }
}