package com.example.androidprojectsmb;

import static com.example.androidprojectsmb.adapter.MessageAdapter.IPV4;
import static com.example.androidprojectsmb.LoginActivity.LOCALHOST;
import static com.example.androidprojectsmb.config.RestClient.ANDROID_EMULATOR_LOCALHOST;
import static com.example.androidprojectsmb.stomp.dto.ChatMessage.MessageType.CHAT;
import static com.example.androidprojectsmb.stomp.dto.ChatMessage.MessageType.JOIN;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidprojectsmb.adapter.MessageAdapter;
import com.example.androidprojectsmb.config.RestClient;
import com.example.androidprojectsmb.dto.AccountDTO;
import com.example.androidprojectsmb.dto.MessageDTO;
import com.example.androidprojectsmb.dto.RoomDTO;
import com.example.androidprojectsmb.multipart.AppHelper;
import com.example.androidprojectsmb.multipart.VolleyMultipartRequest;
import com.example.androidprojectsmb.stomp.Stomp;
import com.example.androidprojectsmb.stomp.StompClient;
import com.example.androidprojectsmb.stomp.dto.ChatMessage;
import com.example.androidprojectsmb.stomp.dto.StompHeader;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatGroupActivity extends AppCompatActivity {
    private ImageView imgBtnGui,imgBack,imgAddMembers,imgThanhVien,imgBtnGif;
    private EditText editNhap;
    private TextView txtRoomName;
    private static final String TAG = "ChatActivity";
    private Gson mGson = new GsonBuilder().create();
    private Disposable mRestPingDisposable;
    private CompositeDisposable compositeDisposable;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private StompClient mStompClient;
    public static final String LOGIN = "login";
    public static final String PASSCODE = "passcode";
    ArrayList<MessageDTO> listMessage;
    MessageAdapter adapter;
    RecyclerView recyclerView;
    private long idAccount,idRoom,idAdmin;
    private String topic=null,nameRoom;
    private static final int PICK_IMAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_chat_group);
        Intent intent = getIntent();
        idAccount = intent.getLongExtra("idAccount", 42);
        idRoom = intent.getLongExtra("idRoom", 37);
        idAdmin = intent.getLongExtra("idAdmin", 37);
        nameRoom = intent.getStringExtra("nameRoom");
        topic = "/app/chat/"+idRoom;


        resetSubscriptions();
        initViews();

        eventHandle();

    }
    public void eventHandle(){
        imgBtnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editNhap.getText().toString().trim();
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(message);
                chatMessage.setSender("son");
                chatMessage.setIdSender(idAccount);
                chatMessage.setRoomId(idRoom);
                chatMessage.setType(CHAT);
                chatMessage.setContentType("TEXT");
                sendEchoViaStomp(chatMessage);
                editNhap.setText("");
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatGroupActivity.this,GroupActivity.class);
                intent.putExtra("idAccount",idAccount);
                startActivity(intent);
            }
        });
        imgAddMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatGroupActivity.this,AddMemberActivity.class);
                intent.putExtra("idAccount",idAccount);
                intent.putExtra("idRoom",idRoom);
                startActivity(intent);
            }
        });
        imgThanhVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if(idAdmin==idAccount)
                    intent = new Intent(ChatGroupActivity.this,ThanhVienActivity.class);
                else
                    intent = new Intent(ChatGroupActivity.this,ThanhVienMemberActivity.class);
                intent.putExtra("idAdmin",idAdmin);
                intent.putExtra("idAccount",idAccount);
                intent.putExtra("idRoom",idRoom);
                startActivity(intent);
            }
        });
        imgBtnGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            File file = new File(data.getData().toString());

            try {
                //CREATE UI IMAGE
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Drawable yourDrawable = Drawable.createFromStream(inputStream, data.getData().toString() );
                String str = data.getData().toString();
                String fileName = System.currentTimeMillis()+".jpeg";
                uploadImage(yourDrawable,fileName);

                //CREATE CHAT MESSAGE STORE AT SERVER
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent("http://ec2-13-214-188-97.ap-southeast-1.compute.amazonaws.com:8080/file/download/" + fileName);
                chatMessage.setSender("son");
                chatMessage.setIdSender(idAccount);
                chatMessage.setRoomId(idRoom);
                chatMessage.setType(CHAT);
                chatMessage.setContentType("IMAGE");
                sendEchoViaStomp(chatMessage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


//            MessageDTO message = new MessageDTO();
//            message.setContentType("IMAGE");
//            message.setContent(data.getData().toString());
//            message.setCreateDate(new Date());
//            AccountDTO accountDTO = new AccountDTO();
//            accountDTO.setId(idAccount);
//            message.setFrom(accountDTO);
//            listMessage.add(message);
//            adapter.notifyDataSetChanged();
//            recyclerView.getLayoutManager().scrollToPosition(listMessage.size()-1);
        }
    }
    public void initViews(){
        listMessage = new ArrayList<>();
        imgBtnGui =  (ImageView) findViewById(R.id.imgBtnGui);
        imgBack =  (ImageView) findViewById(R.id.imgBack);
        imgThanhVien =  (ImageView) findViewById(R.id.imgThanhVien);
        imgAddMembers =  (ImageView) findViewById(R.id.imgAddMembers);
        imgBtnGif =  (ImageView) findViewById(R.id.imgBtnGif);
        editNhap =  (EditText) findViewById(R.id.editNhap);
        txtRoomName =  (TextView) findViewById(R.id.txtRoomName);
        txtRoomName.setText(nameRoom);
        getListMessageByRoomId(idRoom);
        adapter = new MessageAdapter(this,listMessage,idAccount);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + ANDROID_EMULATOR_LOCALHOST
                + ":" + RestClient.SERVER_PORT + "/ws/websocket");

        connectStomp();

       // recyclerView.smoothScrollToPosition(listMessage.size() - 1);
    }
    public void getListMessageByRoomId(long id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = LOCALHOST+"/messages/byRoomId/"+id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = (JSONObject) response.get(i);
                        Gson gson = new Gson();
                        MessageDTO messageDTO= gson.fromJson(String.valueOf(object), MessageDTO.class);
                        listMessage.add(messageDTO);
                       // System.out.println(messageDTO.toString());
                        adapter.notifyDataSetChanged();
                        recyclerView.getLayoutManager().scrollToPosition(listMessage.size()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, error -> Toast.makeText(ChatGroupActivity.this, "Error with JSON Array Object", Toast.LENGTH_SHORT).show());

        queue.add(jsonArrayRequest);

    }
    private void uploadImage(Drawable drawable,String fileName){
        String url = "http://"+IPV4+":8080/file/upload";


        JSONObject jsonBody = new JSONObject();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse  response) {
                        //System.out.println(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fileName", fileName);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("file", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), drawable), "image/jpeg"));
                //params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));

                return params;
            }

        };
        requestQueue.add(multipartRequest);

    }

    public void connectStomp() {
    //public void connectStomp(View view) {

            List<StompHeader> headers = new ArrayList<>();
            headers.add(new StompHeader(LOGIN, "guest"));
            headers.add(new StompHeader(PASSCODE, "guest"));

            //mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);

            resetSubscriptions();

            Disposable dispLifecycle = mStompClient.lifecycle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lifecycleEvent -> {
                        switch (lifecycleEvent.getType()) {
                            case OPENED:
                                toast("Stomp connection opened");
                                break;
                            case ERROR:
                                Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                                toast("Stomp connection error");
                                break;
                            case CLOSED:
                                toast("Stomp connection closed");
                                resetSubscriptions();
                                break;
                            case FAILED_SERVER_HEARTBEAT:
                                toast("Stomp failed server heartbeat");
                                break;
                        }
                    });

            compositeDisposable.add(dispLifecycle);


            // Receive greetings
            Disposable dispTopic = mStompClient.topic("/topic/"+idRoom)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {
                        Log.d(TAG, "Received " + topicMessage.getPayload());
                        addItem(mGson.fromJson(topicMessage.getPayload(), ChatMessage.class));

                    }, throwable -> {
                        Log.e(TAG, "Error on subscribe topic", throwable);
                    });

            compositeDisposable.add(dispTopic);

            mStompClient.connect(headers);

    }

    public void sendEchoViaStomp(ChatMessage chatMessage) {

        String destination  = topic+"/sendMessage";
        compositeDisposable.add(mStompClient.send(destination, mGson.toJson(chatMessage) )
                .compose(applySchedulers())
                .subscribe(() -> {
                    //addItem(chatMessage);
                    Log.d(TAG, "STOMP echo send successfully");
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                    toast(throwable.getMessage());
                }));
    }

    public void sendEchoViaRest(View v) {
        mRestPingDisposable = RestClient.getInstance().getExampleRepository()
                .sendRestEcho("Echo REST " + mTimeFormat.format(new Date()))
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "REST echo send successfully");
                }, throwable -> {
                    Log.e(TAG, "Error send REST echo", throwable);
                    toast(throwable.getMessage());
                });
    }

    private void addItem(ChatMessage chatMessage) {
//        mDataSet.add(echoModel.getEcho() + " - " + mTimeFormat.format(new Date()));
//        mAdapter.notifyDataSetChanged();
//        mRecyclerView.smoothScrollToPosition(mDataSet.size() - 1);
        if(!chatMessage.getType().equals(JOIN)){
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setContent(chatMessage.getContent());
            messageDTO.setCreateDate(new Date());
            AccountDTO from = new AccountDTO();
            from.setId(chatMessage.getIdSender());
            messageDTO.setFrom(from);
            messageDTO.setContentType(chatMessage.getContentType());
            listMessage.add(messageDTO);
            System.out.println(listMessage);
            // getListMessageByRoomId(chatMessage.getRoomId());

            adapter.notifyDataSetChanged();
            //recyclerView.smoothScrollToPosition(listMessage.size()-1);
            recyclerView.getLayoutManager().scrollToPosition(listMessage.size()-1);
        }

    }

    private void toast(String text) {
        Log.i(TAG, text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onDestroy() {
        mStompClient.disconnect();

        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
        super.onDestroy();
    }
    public void disconnectStomp(View view) {
        mStompClient.disconnect();
    }

}




