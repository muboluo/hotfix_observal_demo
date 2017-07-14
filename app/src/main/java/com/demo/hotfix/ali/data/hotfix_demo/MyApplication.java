package com.demo.hotfix.ali.data.hotfix_demo;

import android.app.Application;
import android.content.pm.PackageManager;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Created by Henry on 2017/6/27.
 */

public class MyApplication extends Application {

    //这个string 可以在主页面中，进行显示，如果listener为null 的话， 可以通过该
    //static 变量传递数据，在mainActivity 中接收。
    public static StringBuilder cacheMsg = new StringBuilder();


    public interface MsgDisplayListener {
        void handle(int codeLoadStatus);
    }

    public static MsgDisplayListener msgDisplayListener = null;


    @Override
    public void onCreate() {
        super.onCreate();
        initHotfix();
    }

    private void initHotfix() {
        String appVersion;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "1.0";
        }
        SophixManager.getInstance().setContext(this).setAppVersion(appVersion)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {

                        // 补丁加载回调通知，这里依据code进行事件的分发
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            SophixManager.getInstance().cleanPatches();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }

                        if (msgDisplayListener != null) {
                            msgDisplayListener.handle(code);
                        } else {
                            cacheMsg.append("\n").append(code);
                        }

                    }
                }).initialize();

        //用于查询服务器是否有新的可用补丁，为了测试写在主界面当中
        //SophixManager.getInstance().queryAndLoadNewPatch();
    }
}
