package com.puzzle.pzsdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.puzzle.sdk.Listener.FriendListener;
import com.puzzle.sdk.Listener.SurveyListener;
import com.puzzle.sdk.PZFriend;
import com.puzzle.sdk.PZPlatform;
import com.puzzle.sdk.PZPlayer;
import com.puzzle.sdk.PZSDKListener;
import com.puzzle.sdk.account.PZUserInfo;
import com.puzzle.sdk.PZType;
import com.puzzle.sdk.Listener.ShareLinkListener;
import com.puzzle.sdk.Listener.SharePhotoListener;
import com.puzzle.sdk.payment.PZOrder;
import com.puzzle.sdk.payment.PZProduct;
import com.puzzle.sdk.payment.google.db.OperateDatabase;
import com.puzzle.sdk.survey.PZSurveyWrapper;
import com.puzzle.sdk.utils.Logger;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "demo";

    private EditText mShowInfo;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(this.getResources().getIdentifier
                ("activity_main", "layout", getPackageName()), null);
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        // setImmersionStatusBar();

        // Get user consent
//        FacebookSdk.setAutoInitEnabled(true);
//        FacebookSdk.fullyInitialize();
//        Logger.d("aaaaaaaaaaaaaaaaaaaaaaa");
//        AppLinkData.fetchDeferredAppLinkData(MainActivity.this,
//                new AppLinkData.CompletionHandler() {
//                    @Override
//                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
//                        // Process app link data
//                        Logger.d("appLinkData=" + appLinkData);
//                        if (appLinkData != null) {
//                            Logger.d("onDeferredAppLinkDataFetched isAutoAppLink=" + appLinkData.isAutoAppLink()
//                                    + "，getTargetUri=" + appLinkData.getTargetUri()
//                                    + ", getAppLinkData=" + appLinkData.getAppLinkData()
//                                    + ", getRefererData" + appLinkData.getRefererData()
//                                    + ", getPromotionCode= " + appLinkData.getPromotionCode()
//                                    + ", getArgumentBundle=" + appLinkData.getArgumentBundle()
//                                    + ", getRef=" + appLinkData.getRef());
//                        }
//                    }
//                }
//        );

        // 初始化界面组件
        initView(view);
        initSDK();
    }

    /**
     * 设置沉浸式状态栏，方便测试刘海屏手机头部内容填充
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void setImmersionStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        getWindow().setAttributes(lp);
    }

    private void initView(View view) {
        view.findViewWithTag("login").setOnClickListener(this);
        view.findViewWithTag("createNewAccount").setOnClickListener(this);
        view.findViewWithTag("switchAccount").setOnClickListener(this);
        view.findViewWithTag("bind").setOnClickListener(this);
        view.findViewWithTag("unbind").setOnClickListener(this);
        view.findViewWithTag("socialLogin").setOnClickListener(this);
        view.findViewWithTag("products").setOnClickListener(this);
        view.findViewWithTag("update_bind").setOnClickListener(this);
        view.findViewWithTag("pay").setOnClickListener(this);
        view.findViewWithTag("logout").setOnClickListener(this);
        view.findViewWithTag("bound").setOnClickListener(this);
        view.findViewWithTag("notifyEnable").setOnClickListener(this);
        view.findViewWithTag("notify_setting").setOnClickListener(this);
        view.findViewWithTag("pushGeneral").setOnClickListener(this);
        view.findViewWithTag("pushSpecial").setOnClickListener(this);
        view.findViewWithTag("storeView").setOnClickListener(this);
        view.findViewWithTag("showSurveys").setOnClickListener(this);
        view.findViewWithTag("showFAQs").setOnClickListener(this);
        view.findViewWithTag("connected").setOnClickListener(this);

        view.findViewWithTag("friends").setOnClickListener(this);
        view.findViewWithTag("shareLink").setOnClickListener(this);
        view.findViewWithTag("sharePhoto").setOnClickListener(this);

        view.findViewWithTag("invite").setOnClickListener(this);
        view.findViewWithTag("requestNone").setOnClickListener(this);
        view.findViewWithTag("requestUsers").setOnClickListener(this);
        view.findViewWithTag("requestNonUsers").setOnClickListener(this);

        mShowInfo = view.findViewWithTag("showInfo");
    }

    private void test() {
        Logger.d("=======================================================================");
        Logger.d("BOARD=" + Build.BOARD + "\n" +
                "BRAND=" + Build.BRAND + "\n" +
                "CPU_ABI=" + Build.CPU_ABI + "\n" +
                "DEVICE=" + Build.DEVICE + "\n" +
                "DISPLAY=" + Build.DISPLAY + "\n" +
                "HOST=" + Build.HOST + "\n" +
                "ID=" + Build.ID + "\n" +
                "MANUFACTURER=" + Build.MANUFACTURER + "\n" +
                "HARDWARE=" + Build.HARDWARE + "\n" +
                "MODEL=" + Build.MODEL + "\n" +
                "PRODUCT=" + Build.PRODUCT + "\n" +
                "TAGS=" + Build.TAGS + "\n" +
                "TYPE=" + Build.TYPE + "\n" +
                "SERIAL=" + Build.SERIAL + "\n" +
                "USER=" + Build.USER);
        Logger.d("=======================================================================");
    }

    /**
     * 测试粉丝页面
     */
    private void testFanPage() {
//        String twitterPageId = "homescapes";
//        String youTuBePageId = "UCQidjUAqs1C4JdP_TuylBGQ";
//        String insPageId = "homescapes_mobile";
//        String fbPageId = "designislandgame";
//
//        new FanPage.Builder()
//                .context(this)
//                .setPageId(youTuBePageId)
//                .setFanPageType(FanPageType.PAGE_TYPE_YOUTUBE)
//                .build()
//                .openPage();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals("login")) {
            // testDB();
            // test();
            // testFBPage();
        } else if (tag.equals("createNewAccount")) {
            PZPlatform.getInstance().resetAccount();
        } else if (tag.equals("products")) {
            List<String> productIds = new ArrayList<>();

            productIds.add("com.kingsgroup.diamond1");
            productIds.add("com.kingsgroup.diamond2");
            productIds.add("com.kingsgroup.diamond3");
            productIds.add("com.kingsgroup.diamond0");
            PZPlatform.getInstance().localizedProducts(productIds);
        } else if (tag.equals("pay")) {
            PZProduct product = new PZProduct();
            product.setProductId("com.kingsgroup.diamond1");
            product.setPayload("Payload");
            PZPlatform.getInstance().pay(MainActivity.this, product);
        } else if (tag.equals("notifyEnable")) {
            Message msg = new Message();
            msg.obj = "notifyEnable ==》message: " + PZPlatform.getInstance().notificationEnabled();
            mHandler.sendMessage(msg);
        } else if (tag.equals("notify_setting")) {
            PZPlatform.getInstance().openNotificationSetting();
        } else if (tag.equals("pushGeneral")) {
            PZPlatform.getInstance().displayLocalNotification("push title", "","push message2", "extra data2", "push_sound_1.mp3", 2);
            PZPlatform.getInstance().displayLocalNotification("push title", "","push message4", "extra data4", "push_sound_1.mp3", 4);
            PZPlatform.getInstance().displayLocalNotification("push title", "","push message6", "extra data6", "push_sound_1.mp3", 6);
        } else if (tag.equals("pushSpecial")) {

            PZPlatform.getInstance().displayLocalCustomNotification("Animatch", "All sdfa push message", "push data", "push_sound_1.mp3", 1);
        } else if (tag.equals("friends")) {
            PZPlatform.getInstance().friends(PZType.TYPE_FACEBOOK ,new FriendListener() {
                @Override
                public void onFriends(int code, String message, List<PZFriend> list) {
                    Log.d(TAG, "onFriends code=" + code + ", message=" + message + ", list=" + list);
                    Message msg = new Message();
                    if (code == 0 && list != null) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < list.size(); i++) {
                            sb.append(list.get(i).toString());
                        }
                        msg.obj = "onFriends ==》list: " + sb.toString();

                    } else {
                        msg.obj = "onFriends ==》message: " + msg;
                    }
                    mHandler.sendMessage(msg);
                }
            });
        } else if (tag.equals("shareLink")) {
            PZPlatform.getInstance().shareLink(PZType.TYPE_FACEBOOK, "http://www.baidu.com", "share link", new ShareLinkListener() {
                @Override
                public void shareLinkCallback(int code, String s) {
                    Log.d(TAG, "shareLinkCallback");
                    Message message = new Message();
                    message.obj = "shareLinkCallback ==》message: " + s;
                    mHandler.sendMessage(message);
                }
            });

        } else if (tag.equals("sharePhoto")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://pic1.sc.chinaz.com/files/pic/pic9/201905/zzpic18314.jpg");
                        InputStream is = url.openStream();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int length = -1;

                        while ((length = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, length);
                        }
                        baos.flush();

                        byte[] data = baos.toByteArray();
                        is.close();
                        baos.close();

                        PZPlatform.getInstance().sharePhoto(PZType.TYPE_FACEBOOK, data, data.length, "", "share photo", "", new SharePhotoListener() {
                            @Override
                            public void sharePhotoCallback(int code, String s) {
                                Log.d(TAG, "sharePhotoCallback");
                                Message message = new Message();
                                message.obj = "sharePhotoCallback ==》message: " + s;
                                mHandler.sendMessage(message);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (tag.equals("storeView")) {
            PZPlatform.getInstance().storeReview();
        } else if (tag.equals("showSurveys")) { // "https://www.surveymonkey.com/r/ZYDKVX7?UID=%s"
            PZSurveyWrapper.getInstance().showSurvey(MainActivity.this, "RW9CP2C", new JSONObject(), new SurveyListener() {
                @Override
                public void onSurveyFinish(int code, String message) {
                    Log.d(TAG, "onSurveyFinish code=" + code + ", message=" + message);
                    Message msg = new Message();
                    msg.obj = "sharePhotoCallback ==》code: " + code + ",  message: " + message;
                    mHandler.sendMessage(msg);
                }
            });

            // "file:///android_asset/TestShareLink.html"
            /*PZPlatform.getInstance().showWebView("https://auror-cdn-web.puzzleplusgames.net/summer/sharePages/en/step1/share1/index.html#/firstPage", false, "#3FE2C5", new WebViewListener() {
                @Override
                public void onWebViewClose(int code, String message) {
                    Message msg = new Message();
                    msg.obj = "onWebViewClose ==》message: " + message;
                    mHandler.sendMessage(msg);
                }
            });*/
        } else if (tag.equals("invite")) {
            PZPlatform.getInstance().invite("invite title", "invite message", null);
        } else if (tag.equals("requestNone")) {
            PZPlatform.getInstance().request("title", "message", "data", null, 0, null);
        } else if (tag.equals("requestUsers")) {
            PZPlatform.getInstance().request("title", "message", "data", null, 1, null);
        } else if (tag.equals("requestNonUsers")) {
            PZPlatform.getInstance().request("title", "message", "data", null, 2, null);
        }
    }

    private void initSDK() {
        PZPlatform.getInstance().onCreate(this);
        PZPlatform.getInstance().SDKInit(mSDKListener);
    }

    private void testDB() {
        OperateDatabase.getInstance().createDbHelper(this);
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "-------------------");
                    OperateDatabase.getInstance().queryValidOrders();
                }
            }).start();
        }
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            mShowInfo.setText(msg.obj.toString());
        }
    };

    PZSDKListener mSDKListener = new PZSDKListener() {

        @Override
        public void onSDKInitFinish(int code, String message) {
            Log.d(TAG, "sdk init finish, message=" + message);
            Message msg = new Message();
            msg.obj = "onSDKInitFinish ===>>> code: " + code + ", message: " + message;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onPushDeviceToken(String pushDeviceToken) {
            Log.d(TAG, "onPushDeviceToken pushDeviceToken=" + pushDeviceToken);
            Message msg = new Message();
            msg.obj = "onPushDeviceToken: " + pushDeviceToken;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onNotificationClicked(String payload) {
            Log.d(TAG, "onNotificationClicked, payload=" + payload);
            Message msg = new Message();
            msg.obj = "onNotificationClicked ===>>> payload: " + payload;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onDeepLinkData(String data) {
            Log.d(TAG, "onDeepLinkData, data=" + data);
            Message msg = new Message();
            msg.obj = "onDeepLinkData ===>>> data: " + data;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onLoginFinish(int code, String message, PZUserInfo userInfo) {
            Log.d(TAG, "onLoginFinish code=" + code + ", message=" + message + ", userInfo=" + userInfo);
            Message msg = new Message();
            if (userInfo == null) {
                msg.obj = "onLoginFinish ===>>> code: " + code + ", message: " + message;
            } else {
                msg.obj = "onLoginFinish ===>>> code: " + code + ", message: " + message + "\n userInfo: " + userInfo.toString();
            }
            mHandler.sendMessage(msg);
            PZPlayer player = new PZPlayer();
            player.setPlayerId("1000002");
            player.setServerId("1");
            PZPlatform.getInstance().enterGame(player);
        }

        @Override
        public void onResetFinish(int code, String message, PZUserInfo userInfo) {
            Log.d(TAG, "onResetFinish code=" + code + ", message=" + message + ", userInfo=" + userInfo);
            Message msg = new Message();
            if (userInfo == null) {
                msg.obj = "onResetFinish ===>>> code: " + code + ", message: " + message;
            } else {
                msg.obj = "onResetFinish ===>>> code: " + code + ", message: " + message + "\n userInfo: " + userInfo.toString();
            }
            mHandler.sendMessage(msg);
        }

        @Override
        public void onSwitchAccountFinish(int code, String message, PZUserInfo userInfo) {
            Log.d(TAG, "onSwitchAccountFinish code=" + code + ", message=" + message + ", userInfo=" + userInfo);
            Message msg = new Message();
            if (userInfo == null) {
                msg.obj = "onSwitchAccountFinish ===>>> code: " + code + ", message: " + message;
            } else {
                msg.obj = "onSwitchAccountFinish ===>>> code: " + code + ", message: " + message + "\n userInfo: " + userInfo.toString();
            }
            mHandler.sendMessage(msg);
        }

        @Override
        public void onBindFinish(int code, String message, PZUserInfo userInfo) {
            Log.d(TAG, "onBindFinish code=" + code + ", message=" + message + ", userInfo=" + userInfo);
            Message msg = new Message();
            if (userInfo == null) {
                msg.obj = "onBindFinish ===>>> code: " + code + ", message: " + message;
            } else {
                msg.obj = "onBindFinish ===>>> code: " + code + ", message: " + message + "\n userInfo: " + userInfo.toString();
            }
            mHandler.sendMessage(msg);
        }

        @Override
        public void onUnbindFinish(int code, String message, PZUserInfo userInfo) {
            Log.d(TAG, "onUnbindFinish code=" + code + ", message=" + message + ", userInfo=" + userInfo);
            Message msg = new Message();
            if (userInfo == null) {
                msg.obj = "onUnbindFinish ===>>> code: " + code + ", message: " + message;
            } else {
                msg.obj = "onUnbindFinish ===>>> code: " + code + ", message: " + message + "\n userInfo: " + userInfo.toString();
            }
            mHandler.sendMessage(msg);
        }

        @Override
        public void onLogoutFinish(int code, String message) {
            Log.d(TAG, "onLogoutFinish finish");
            Message msg = new Message();
            msg.obj = "onLogoutFinish ===>>> code: " + code + ", message: " + message;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onExitFinish(int code, String message) {
            Log.d(TAG, "This method only use china SDK.");
        }

        @Override
        public void onLocalizedProducts(int code, String message, List<PZProduct> products) {
            Log.d(TAG, "onLocalizedProducts code=" + code + ", message=" + message + ", products=" + products);
            Message msg = new Message();
            if (products == null) {
                msg.obj = "onLocalizedProducts ===>>> code: " + code + ", message: " + message;
            } else {
                msg.obj = "onLocalizedProducts ===>>> code: " + code + ", message: " + message + "\n products: " + products.toString();
            }
            mHandler.sendMessage(msg);
        }

        @Override
        public void onPayFinish(int code, String message, PZOrder order) {
            Log.d(TAG, "onPayFinish code=" + code + ", message=" + message + ", order=" + order);
            Message msg = new Message();
            if (order == null) {
                msg.obj = "onPayFinish ===>>> code: " + code + ", message: " + message;
            } else {
                msg.obj = "onPayFinish ===>>> code: " + code + ", message: " + message + "\n order: " + order.toString();
            }
            mHandler.sendMessage(msg);
        }

        @Override
        public void onRepairOrder(int code, String message, PZOrder order) {
            Log.d(TAG, "onRepairOrder code=" + code + ", message=" + message + ", order=" + order);
            Message msg = new Message();
            if (order == null) {
                msg.obj = "onRepairOrder ===>>> code: " + code + ", message: " + message;
            } else {
                msg.obj = "onRepairOrder ===>>> code: " + code + ", message: " + message + "\n order: " + order.toString();
            }
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        PZPlatform.getInstance().onStart(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PZPlatform.getInstance().onRestart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PZPlatform.getInstance().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PZPlatform.getInstance().onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PZPlatform.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PZPlatform.getInstance().onNewIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PZPlatform.getInstance().onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PZPlatform.getInstance().onDestroy(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PZPlatform.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        PZPlatform.getInstance().onConfigurationChanged(configuration);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PZPlatform.getInstance().onBackPressed();
    }

    private void showSocialChoiceDialog(final OnSocialCLickedListener onSocialChoicedListener, boolean isSocial) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择对应的功能");


        List<String> loginTypes = new ArrayList<>();
        loginTypes.add("FACEBOOK");
        loginTypes.add("TWITTER");
        loginTypes.add("GOOGLE");
        if (!isSocial) {
            loginTypes.add("AUTO");
            loginTypes.add("GUEST");
        }

        String[] dataType = new String[loginTypes.size()];
        final String[] items = loginTypes.toArray(dataType);


        // -1代表没有条目被选中
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // [1]把选择的条目给取出来
                String item = items[which];
                switch (item) {
                    case "GUEST":
                        if (onSocialChoicedListener != null)
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_GUEST, "GUEST");
                        break;
                    case "FACEBOOK":
                        if (onSocialChoicedListener != null)
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_FACEBOOK, "FACEBOOK");
                        break;
                    case "TWITTER":
                        if (onSocialChoicedListener != null)
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_TWITTER, "TWITTER");
                        break;
                    case "AUTO":
                        if (onSocialChoicedListener != null)
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_AUTO, "AUTO");
                        break;
                    case "GOOGLE":
                        if (onSocialChoicedListener != null) {
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_GOOGLE, "GOOGLE");
                        }
                        break;
                    default:
                        break;
                }

                // [2]把对话框关闭
                dialog.dismiss();

            }
        });

        // 最后一步 一定要记得 和Toast 一样 show出来
        builder.show();
    }

    private void showShareChoiceDialog(final OnSocialCLickedListener onSocialChoicedListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择对应的分享功能");


        List<String> shareTypes = new ArrayList<>();
        shareTypes.add("FACEBOOK_LINK");
        shareTypes.add("FACEBOOK_PHOTO");
        shareTypes.add("TWITTER_LINK");
        shareTypes.add("TWITTER_PHOTO");
        shareTypes.add("INSTAGRAM");

        String[] dataType = new String[shareTypes.size()];
        final String[] items = shareTypes.toArray(dataType);

        // -1代表没有条目被选中
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // [1]把选择的条目给取出来
                String item = items[which];
                switch (item) {
                    case "FACEBOOK_LINK":
                        if (onSocialChoicedListener != null)
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_FACEBOOK, "LINK");
                        break;
                    case "FACEBOOK_PHOTO":
                        if (onSocialChoicedListener != null)
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_FACEBOOK, "PHOTO");
                        break;
                    case "TWITTER_LINK":
                        if (onSocialChoicedListener != null)
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_TWITTER, "LINK");
                        break;
                    case "TWITTER_PHOTO":
                        if (onSocialChoicedListener != null)
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_TWITTER, "PHOTO");
                        break;
                    case "INSTAGRAM":
                        if (onSocialChoicedListener != null)
                            onSocialChoicedListener.onSocialClicked(PZType.TYPE_INSTAGRAM, "PHOTO");
                        break;
                    default:
                        break;
                }

                // [2]把对话框关闭
                dialog.dismiss();

            }
        });

        // 最后一步 一定要记得 和Toast 一样 show出来
        builder.show();
    }

    interface OnSocialCLickedListener {
        void onSocialClicked(int socialType, String socialName);
    }

}
