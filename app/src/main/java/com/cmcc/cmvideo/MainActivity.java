package com.cmcc.cmvideo;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cmcc.cmvideo.search.PlayVideoActivity;
import com.cmcc.cmvideo.search.SearchByAIActivity;
import com.cmcc.cmvideo.search.aiui.AIUIService;
import com.cmcc.cmvideo.search.aiui.IAIUIService;
import com.cmcc.cmvideo.util.AppUtil;
import com.cmcc.cmvideo.util.ServiceUtils;
import com.cmcc.cmvideo.util.SharedPreferencesHelper;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIMessage;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tv_current_version)
    TextView tvCurrentVersion;
    @BindView(R.id.turn_to_ai_search)
    Button btTurnToAISearch;
    @BindView(R.id.open_aiui_helper)
    ToggleButton btOpenAIHelper;
    private static final String KEY_IS_AI_HELPER_OPEN = "key_is_ai_helper_open";
    private int downX, downY, btLeft, btRight, btBottom, btTop;
    private long downTime = 0;
    private int MAX_CLICK_TIME = 500;
    private int widthPixels, heightPixels;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {
        btTurnToAISearch.setOnTouchListener(btTouchListener);
        btOpenAIHelper.setOnCheckedChangeListener(btOpenAIHelperCheckedChangeListener);
    }

    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;

        service = new Intent(this, AIUIService.class);
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(MainActivity.this);
        sharedPreferencesHelper.setValue(KEY_IS_AI_HELPER_OPEN, ServiceUtils.isServiceRunning(MainActivity.this, AIUIService.AIUI_SERVICE_NAME));
        setViewVisible(sharedPreferencesHelper.getBoolean(KEY_IS_AI_HELPER_OPEN, false));

        int versionCode = AppUtil.getVersionCode(this);
        String versionName = AppUtil.getVersionName(this);
        if (!TextUtils.isEmpty(versionName)) {
            tvCurrentVersion.setVisibility(View.VISIBLE);
            tvCurrentVersion.setText("当前版本号:" + versionName);
        } else {
            tvCurrentVersion.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.turn_to_player)
    public void turnToPlayer() {
        Intent intent = new Intent(MainActivity.this, PlayVideoActivity.class);
        startActivity(intent);
    }

    private CompoundButton.OnCheckedChangeListener btOpenAIHelperCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            startAIService(isChecked);
        }
    };

    private View.OnTouchListener btTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (R.id.turn_to_ai_search == view.getId()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downTime = System.currentTimeMillis();
                        btLeft = view.getLeft();
                        btRight = view.getRight();
                        btTop = view.getTop();
                        btBottom = view.getBottom();
                        downX = (int) event.getRawX();
                        downY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        long moveTime = System.currentTimeMillis() - downTime;
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        showMoveDrag(moveTime > MAX_CLICK_TIME, moveX - downX, moveY - downY);
                        break;
                    case MotionEvent.ACTION_UP:
                        long upTime = System.currentTimeMillis() - downTime;
                        int upX = (int) event.getRawX();
                        int upY = (int) event.getRawY();
                        dragSearchBt(upTime <= MAX_CLICK_TIME, upX - downX, upY - downY);
                        break;
                    default:
                        break;
                }
                return true;
            }
            return false;

        }
    };

    private void dragSearchBt(boolean isClick, int dx, int dy) {
        if (isClick) {
            Intent intent = new Intent(MainActivity.this, SearchByAIActivity.class);
            startActivity(intent);
        } else {
            showUpDrag(dx, dy);
        }
    }

    private void showMoveDrag(boolean beginMove, int dx, int dy) {
        if (beginMove) {
            int l = btLeft + dx;
            int t = btTop + dy;
            int r = btRight + dx;
            int b = btBottom + dy;
            // 判断超出屏幕
            if (l < 0) {
                l = 0;
                r = l + btTurnToAISearch.getWidth();
            }
            if (t < 0) {
                t = 0;
                b = t + btTurnToAISearch.getHeight();
            }
            if (r > widthPixels) {
                r = widthPixels;
                l = r - btTurnToAISearch.getWidth();
            }
            if (b > heightPixels) {
                b = heightPixels;
                t = b - btTurnToAISearch.getHeight();
            }

            if (null != btTurnToAISearch) {
                btTurnToAISearch.layout(l, t, r, b);
                btTurnToAISearch.postInvalidate();
            }
        }
    }

    private void showUpDrag(int dx, int dy) {
        int left = btTurnToAISearch.getLeft();

        int l = btLeft + dx;
        int t = btTop + dy;
        int r = btRight + dx;
        int b = btBottom + dy;
        if (t < 0) {
            t = 0;
            b = t + btTurnToAISearch.getHeight();
        }
        if (b > heightPixels) {
            b = heightPixels;
            t = b - btTurnToAISearch.getHeight();
        }
        if (left * 2 < widthPixels) {
            l = 0;
            r = l + btTurnToAISearch.getWidth();
        } else {
            r = widthPixels;
            l = r - btTurnToAISearch.getWidth();
        }

        if (null != btTurnToAISearch) {
            btTurnToAISearch.layout(l, t, r, b);
            btTurnToAISearch.postInvalidate();
        }
    }

    private void setViewVisible(Boolean isOpen) {
        if (isOpen) {
            btTurnToAISearch.setVisibility(View.VISIBLE);
            btOpenAIHelper.setChecked(true);
        } else {
            btTurnToAISearch.setVisibility(View.GONE);
            btOpenAIHelper.setChecked(false);
        }
    }

    private void startAIService(boolean isChecked) {
        if (isChecked) {
            if (!ServiceUtils.isServiceRunning(MainActivity.this, AIUIService.AIUI_SERVICE_NAME)) {
                startService(service);
                bindService(new Intent(this, AIUIService.class), connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
                setViewVisible(isChecked);
                sharedPreferencesHelper.setValue(KEY_IS_AI_HELPER_OPEN, isChecked);
            }
        } else {
            if (ServiceUtils.isServiceRunning(MainActivity.this, AIUIService.AIUI_SERVICE_NAME)) {
                unbindService(connection);
                stopService(service);
                setViewVisible(isChecked);
                sharedPreferencesHelper.setValue(KEY_IS_AI_HELPER_OPEN, isChecked);
            }
        }
    }
    IAIUIService aiuiService = null;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Map<String, String> map = new HashMap<String, String>() {{
                put("msisdn", "13764279837");
                put("user_id", "553782460");
                put("client_id", "897ddadc222ec9c20651da355daee9cc");
            }};
            aiuiService = (IAIUIService) service;
            aiuiService.setUserParam(map);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
