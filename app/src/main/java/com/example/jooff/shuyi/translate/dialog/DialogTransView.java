package com.example.jooff.shuyi.translate.dialog;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jooff.shuyi.R;
import com.example.jooff.shuyi.common.AboutFragment;
import com.example.jooff.shuyi.common.Constant;
import com.example.jooff.shuyi.common.MySnackBar;
import com.example.jooff.shuyi.main.MainActivity;
import com.example.jooff.shuyi.util.AnimationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DialogTransView extends AppCompatActivity implements DialogTransContract.View, Toolbar.OnMenuItemClickListener {
    private DialogTransContract.Presenter mPresenter;

    @BindView(R.id.share_result)
    TextView mResult;
    @BindView(R.id.share_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.share_appbar)
    AppBarLayout mAppBar;
    @BindView(R.id.share_speech)
    ImageView mShareSpeech;
    @BindView(R.id.share_et)
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        mPresenter = new DialogTransPresenter(getSharedPreferences(Constant.ARG_NAME, MODE_PRIVATE), getIntent(), this);
        mPresenter.initTheme();
        initView();

    }

    @Override
    public void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int) (dm.widthPixels * 0.8);
        getWindow().setAttributes(p);
        mToolbar.setTitle(R.string.app_name);
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(this);
        mShareSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.drawable.m_speech1);
                AnimationDrawable drawable = (AnimationDrawable) v.getBackground();
                drawable.stop();
                drawable.start();
                mPresenter.playSpeech();
            }
        });
        mResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DialogTransView.this, MainActivity.class);
                intent.putExtra("original", mEditText.getText().toString());
                startActivity(intent);
            }
        });
        mPresenter.loadData();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        AboutFragment fragment = new AboutFragment();
        fragment.show(getSupportFragmentManager(), "dialogAbout");
        return false;
    }

    @OnClick(R.id.share_original_delete)
    public void onDelete(ImageView delete) {
        mEditText.setText("");
        mShareSpeech.setVisibility(View.GONE);
        mEditText.startAnimation(AnimationUtil.getAlpha(this));
        delete.startAnimation(AnimationUtil.getScale(this));
    }

    @OnClick(R.id.share_original_send)
    public void onSend(ImageView send) {
        send.startAnimation(AnimationUtil.getScale(this));
        mPresenter.beginTrans(mEditText.getText().toString());
    }

    @Override
    public void showTrans(String original, String result) {
        mEditText.setText(original);
        mResult.setText(result);
        mResult.startAnimation(AnimationUtil.getAlpha(DialogTransView.this));
    }

    @Override
    public void showError() {
        MySnackBar.getSnack(mAppBar, R.string.invalid_translate).show();
    }

    @Override
    public void showSpeech() {
        mShareSpeech.startAnimation(AnimationUtil.getAlpha(this));
        mShareSpeech.setVisibility(View.VISIBLE);
    }

    @Override
    public void setAppTheme(int colorPrimary) {
        mAppBar.setBackgroundColor(colorPrimary);
    }

}


