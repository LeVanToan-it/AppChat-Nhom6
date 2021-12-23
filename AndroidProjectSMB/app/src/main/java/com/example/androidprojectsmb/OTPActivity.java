package com.example.androidprojectsmb;

import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.androidprojectsmb.adapter.GroupAdapter;
import com.example.androidprojectsmb.dto.AccountDTO;
import com.example.androidprojectsmb.dto.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    Button btnVerify;
    Button btnGenerateOTP, btnSignIn;

    EditText etPhoneNumber, etOTP;

    String phoneNumber, otp;

    FirebaseAuth auth;
    String user,pass,hoten;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        pass = intent.getStringExtra("pass");
        hoten = intent.getStringExtra("hoten");
        etOTP = (EditText) findViewById(R.id.etOTP);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        StartFirebaseLogin();
        generateOTP(user);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp=etOTP.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);

                SigninWithPhone(credential);


            }
        });


    }
    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(OTPActivity.this,LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(OTPActivity.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void generateOTP(String phoneNumber){
        String tempPhone = "+84".concat(phoneNumber.substring(1));
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                tempPhone,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                OTPActivity.this,        // Activity (for callback binding)
                mCallback);                      // OnVerificationStateChangedCallbacks
    }
    private void StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                Toast.makeText(OTPActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
                UserDTO userDTO = new UserDTO();
                userDTO.setSoDienThoai(user);
                userDTO.setPassword(pass);
                userDTO.setEnable(true);
                AccountDTO a = new AccountDTO();
                a.setUsername(hoten);
                userDTO.setAccount(a);
                sendRegister(userDTO);
                Toast toast = Toast.makeText(OTPActivity.this,"Đăng ký thành công!!!",Toast.LENGTH_SHORT);
                toast.show();
                startActivity(new Intent(OTPActivity.this, LoginActivity.class));
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(OTPActivity.this,"verification fialed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(OTPActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
    }
    // private void PostApi(String name, String age){
    private void sendRegister(UserDTO user){
        try {
            String url = LOCALHOST+"/users";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("soDienThoai", user.getSoDienThoai());
            jsonBody.put("password", user.getPassword());
            jsonBody.put("enable", user.isEnable());
            //    jsonBody.put("roles", user.getRoles());
            JSONObject accountJson = new JSONObject();
            accountJson.put("username",user.getAccount().getUsername());

            jsonBody.put("account", accountJson);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response+"ERR1");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString()+"ERR2");
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
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}