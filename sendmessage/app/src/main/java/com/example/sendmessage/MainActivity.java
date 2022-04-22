package com.example.sendmessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button callList;
    private Button call;
    private Button sendmsg;
    //private Button photos;


    private TextView userName;
    private TextView phoneNum;
   

    String phoneName;
    String callNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callList=this.findViewById(R.id.btn_call_list);
        call=this.findViewById(R.id.btn_call);
        sendmsg=this.findViewById(R.id.btn_send_msg);
        //photos=this.
        userName=this.findViewById(R.id.user_name);
        phoneNum=this.findViewById(R.id.phone_name);

        callList.setOnClickListener(this);
        call.setOnClickListener(this);
        sendmsg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_call_list:
                getPermission();
                Toast.makeText(MainActivity.this, "你点击了联系人列表", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_send_msg:
                sendMsg(phoneNum.getText().toString());
                Toast.makeText(MainActivity.this, "你点击了发送短信", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_call:
                callPhone(phoneNum.getText(),toString());
                Toast.makeText(MainActivity.this, "你点击了打电话", Toast.LENGTH_SHORT).show();
                break;
        }

    }


    private void getPermission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},202);
            }else {
                Intent intent =new Intent();
                intent.setAction("android.intent.action.PICK");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setType("vnd.android.cursor.dir/phone_v2");
                startActivityForResult(intent,0x30);
            }
        }else {
            Intent intent =new Intent();
            intent.setAction("android.intent.action.PICK");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setType("vnd.android.cursor.dir/phone_v2");
            startActivityForResult(intent,0x30);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 202) {
            if (grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                getPermission();
            }else {
                Toast.makeText(MainActivity.this, "授权被禁止！", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x30){
            if (data != null){
                Uri uri = data.getData();

                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = null;
                if (uri != null){
                    cursor = contentResolver.query(uri,new String[]{"display_name","data1"},null,null,null);
                }
                while (cursor.moveToNext()){
                    phoneName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    callNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                cursor.close();

                if (callNum != null) {
                    callNum = callNum.replaceAll("-"," ");
                    callNum = callNum.replaceAll(" ","");
                }

                userName.setText(phoneName);
                phoneNum.setText(callNum);
            }
        }
    }

    private void callPhone(CharSequence text, String phoneNum){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:"+phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    private void sendMsg(String phoneNum){
        Uri uri = Uri.parse("smsto:"+phoneNum);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);

    }
}