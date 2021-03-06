package com.example.jooff.shuyi.main;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.example.jooff.shuyi.R;
import com.example.jooff.shuyi.api.BaiDuTransAPI;
import com.example.jooff.shuyi.api.JinShanTransApi;
import com.example.jooff.shuyi.api.ShanBeiTransApi;
import com.example.jooff.shuyi.api.YiYunTransApi;
import com.example.jooff.shuyi.api.YouDaoTransAPI;
import com.example.jooff.shuyi.common.Constant;
import com.example.jooff.shuyi.util.MD5Format;
import com.example.jooff.shuyi.util.UTF8Format;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jooff on 2017/1/17.
 */

public class MainPresenter implements MainContract.Presenter {
    private static final String TAG = "MainPresenter";
    private MainContract.View mView;
    private SharedPreferences mPref;
    private Boolean isDoubleClick;
    private Boolean isCopyTrans;
    private Boolean isTransMode;
    private Boolean isNightMode;
    private Boolean isNoteMode;
    private String mResultLan;
    private int transFrom;
    private int colorPrimary;
    private int colorPrimaryDark;
    private int themeId;

    public MainPresenter(SharedPreferences sharedPreferences, MainContract.View mainView) {
        mPref = sharedPreferences;
        mView = mainView;
        isCopyTrans = mPref.getBoolean(Constant.ARG_COPY, false);
        isTransMode = mPref.getBoolean(Constant.ARG_TRANS, false);
        isNightMode = mPref.getBoolean(Constant.ARG_NIGHT, false);
        isNoteMode = mPref.getBoolean(Constant.ARG_NOTE, false);
        colorPrimary = mPref.getInt(Constant.ARG_PRIMARY, Color.parseColor("#F44336"));
        colorPrimaryDark = mPref.getInt(Constant.ARG_DARK, Color.parseColor("#D32f2f"));
        themeId = mPref.getInt(Constant.ARG_THEME, 0);
        transFrom = mPref.getInt(Constant.ARG_FROM, R.id.source_youdao);
        mResultLan = mPref.getString(Constant.ARG_LAN, "en");
        isDoubleClick = false;
    }

    @Override
    public void initTheme() {
        Constant.sIsNightMode = isNightMode;
        if (isNightMode) {
            themeId = 1024;
            colorPrimary = Color.parseColor("#35464e");
            mView.openNightMode();
        }
        mView.initTheme(themeId, colorPrimary);
    }

    @Override
    public void initSettings() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            mView.initLayout();
        }
        if (isCopyTrans) {
            mView.startService();
        }
        if (!isNightMode) {
            mView.setAppTheme(colorPrimary, colorPrimaryDark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isTransMode) {
                    mView.setTransparent(colorPrimary);
                } else {
                    mView.setMaterial(colorPrimaryDark);
                }
            }
        }
        if (transFrom == R.id.source_baidu) {
            mView.showSpinner(mResultLan);
        }
        if (isNoteMode) {
            mView.showNotification();
        }
    }

    @Override
    public void updateSetting(int position, boolean isChecked) {
        switch (position) {
            case 0:
                if (isChecked) {
                    mView.startService();
                } else {
                    mView.stopService();
                }
                break;
            case 1:
                if (!isNightMode) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (isChecked) {
                            mView.setTransparent(colorPrimary);
                        } else {
                            mView.setMaterial(colorPrimaryDark);
                        }
                    }
                }
                break;
            case 2:
                if (isChecked) {
                    mView.openNightMode();
                    mView.startIntent();
                } else {
                    mView.closeNightMode();
                    mView.startIntent();
                }
                break;
            case 3:
                if (isChecked) {
                    mView.showNotification();
                } else {
                    mView.cancelNotification();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void beginTrans(String original) {
        String transUrl = "";
        if (TextUtils.isEmpty(original)) {
            mView.showEmptyInput();
        } else {
            switch (transFrom) {
                case R.id.source_youdao:
                    transUrl = YouDaoTransAPI.YOUDAO_URL
                            + YouDaoTransAPI.YOUDAO_ORIGINAL + UTF8Format.encode(original).replace("\n", "");
                    break;
                case R.id.source_jinshan:
                    transUrl = JinShanTransApi.JINSHAN_URL
                            + JinShanTransApi.JINSHAN_ORIGINAL + UTF8Format.encode(original).replace("\n", "")
                            + JinShanTransApi.JINSHAN_KEY;
                    break;
                case R.id.source_baidu:
                    transUrl = BaiDuTransAPI.BAIDU_URL
                            + BaiDuTransAPI.BAIDU_ORIGINAL + UTF8Format.encode(original).replace("\n", "")
                            + BaiDuTransAPI.BAIDU_ORIGINAL_LAN
                            + BaiDuTransAPI.BAIDU_RESULT_LAN + mResultLan
                            + BaiDuTransAPI.BAIDU_ID
                            + BaiDuTransAPI.BAIDU_SALT + String.valueOf(1234567899)
                            + BaiDuTransAPI.BAIDU_SIGN + MD5Format.getMd5(BaiDuTransAPI.BAIDU_ID.substring(7) + original + 1234567899 + BaiDuTransAPI.BAIDU_KEY);
                    Log.d(TAG, "beginTrans: " + transUrl);
                    break;
                case R.id.source_yiyun:
                    String originalLan, resultLan;
                    Pattern p = Pattern.compile("[a-zA-Z]+");
                    Matcher m = p.matcher(original);
                    if (m.matches()) {
                        originalLan = "en";
                        resultLan = "zh";
                    } else {
                        originalLan = "zh";
                        resultLan = "en";
                    }
                    transUrl = YiYunTransApi.YIYUN_URL
                            + YiYunTransApi.YIYUN_ORIGINAL + UTF8Format.encode(original).replace("\n", "")
                            + YiYunTransApi.YIYUN_ORIGINAL_LAN + originalLan
                            + YiYunTransApi.YIYUN_RESULT_LAN + resultLan
                            + YiYunTransApi.YIYUN_ID
                            + YiYunTransApi.YIYUN_KEY;
                    Log.d(TAG, "beginTrans: " + transUrl);
                    break;
                case R.id.source_shanbei:
                    transUrl = ShanBeiTransApi.SHANBEI_SEARCH_URL + UTF8Format.encode(original).replace("\n", "");
                    Log.d(TAG, "beginTrans: " + transUrl);
                    break;
                default:
                    break;
            }
            mView.showTrans(transFrom, transUrl);
        }
    }

    @Override
    public void loadData() {
        mView.showHistory();
    }

    @Override
    public void refreshSource(int source) {
        transFrom = source;
        if (transFrom == R.id.source_baidu) {
            mView.showSpinner(mResultLan);
        } else {
            mView.hideSpinner();
        }
    }

    @Override
    public void refreshResultLan(String lan) {
        mResultLan = lan;
        mPref.edit().putString(Constant.ARG_LAN, mResultLan).apply();
    }

    @Override
    public void handleClick() {
        Timer mTimer = new Timer();
        if (!isDoubleClick) {
            isDoubleClick = true;
            mView.showConfirmFinish();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isDoubleClick = false;
                }
            }, 2000);
        } else mView.doFinish();
    }

}
