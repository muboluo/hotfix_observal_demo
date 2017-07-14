package com.demo.hotfix.ali.data.hotfix_demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.demo.hotfix.ali.data.hotfix_demo.msg.MsgCenter;
import com.demo.hotfix.ali.data.hotfix_demo.msg.MsgID;
import com.demo.hotfix.ali.data.hotfix_demo.msg.MsgListener;
import com.taobao.sophix.SophixManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txt_message;

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_download).setOnClickListener(this);
        findViewById(R.id.btn_clean_patch).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        txt_message = (TextView) findViewById(R.id.txt_message);

        if (MyApplication.msgDisplayListener == null) {

            MyApplication.msgDisplayListener = new MyApplication.MsgDisplayListener() {
                @Override
                public void handle(final int codeLoadStatus) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateConsole(codeLoadStatus);
                        }
                    });
                }
            };
        }

        if (Build.VERSION.SDK_INT >= 23) {
            requestExternalStoragePermission();
        }

        initObservers();


    }


    private void initObservers() {
        // 具体主题角色通常用具体自来来实现
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (args != null) {
                    String temp = (String) args[0];
                    txt_message.setText(temp);
                }


            }
        }, MsgID.HIDEKEYBOARD);

    }

    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 0;

    /**
     * 如果本地补丁放在了外部存储卡中, 6.0以上需要申请读外部存储卡权限才能够使用. 应用内部存储则不受影响
     */
    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED) {
            //多个权限申请，可以添加到 [] 中，进行统一申请，并在回调方法中统一进行设置。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSION);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION) {

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean shouldShowRequestPermissionRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(permission);
                    }
                    if (!shouldShowRequestPermissionRationale) {
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (Manifest.permission.WRITE_CONTACTS.equals(permission)) {
                        //展示信息
                        // user did NOT check "never ask again"
                        // this is a good place to explain the user
                        // why you need the permission and ask if he wants
                        // to accept it (the rationale)
                    } else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {

                        /* possibly check more permissions...*/

                    }
                    //....... 这里依次判断我们申请的权限，进行响应的处理。
                    /**
                     *
                     * 或者为用户提供一个按钮，直接去设置页面
                     Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                     Uri uri = Uri.fromParts("package", getPackageName(), null);
                     intent.setData(uri);
                     startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                     */

                }

            }
        }

    }

    private String mStatusStr = "";


    /**
     * 更新监控台的输出信息
     *
     * @param content 更新内容
     */
    private void updateConsole(int content) {
        mStatusStr += content + "\n";
        if (txt_message != null) {
            txt_message.setText(mStatusStr);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_download:
                SophixManager.getInstance().queryAndLoadNewPatch();
                break;

            case R.id.btn_clean_patch:
                SophixManager.getInstance().cleanPatches();
                break;

            case R.id.btn_3:
                MsgCenter.fireNull(MsgID.HIDEKEYBOARD, "MsgTest");

                break;

            case R.id.btn_4:
                break;

        }
    }
}
