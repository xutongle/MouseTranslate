package com.example.jooff.shuyi.translate.main;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jooff.shuyi.R;
import com.example.jooff.shuyi.common.Constant;
import com.example.jooff.shuyi.common.MySnackBar;
import com.example.jooff.shuyi.common.OnAppStatusListener;
import com.example.jooff.shuyi.common.SourceFragment;
import com.example.jooff.shuyi.data.AppDbRepository;
import com.example.jooff.shuyi.util.AnimationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Jooff on 2017/1/18.
 */

public class MainTranslateView extends Fragment implements MainTranslateContract.View {
    private Context mContext;
    private MainTranslateContract.Presenter mPresenter;
    private OnAppStatusListener mListener;

    @BindView(R.id.content_trans)
    CardView mTransCard;
    @BindView(R.id.content_dic)
    CardView mDicCard;
    @BindView(R.id.trans_result)
    TextView mResult;
    @BindView(R.id.phonetic)
    LinearLayout mPhonetic;
    @BindView(R.id.uk_phonetic)
    TextView mUkPhonetic;
    @BindView(R.id.us_phonetic)
    TextView mUsPhonetic;
    @BindView(R.id.dic_explainEN)
    TextView mExplainEn;
    @BindView(R.id.dic_explain)
    TextView mExplain;
    @BindView(R.id.dic_web)
    TextView mWeb;

    public static MainTranslateView newInstance(int transSource, String original) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.ARG_FROM, transSource);
        bundle.putString(Constant.ARG_ORIGINAL, original);
        MainTranslateView fragment = new MainTranslateView();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mListener = (OnAppStatusListener) mContext;
        mPresenter = new MainTranslatePresenter(getArguments()
                , AppDbRepository.getInstance(getContext())
                , this);
        initView();
        return view;
    }

    @OnClick(R.id.result_copy)
    public void setCopy(ImageView mCopy) {
        mCopy.startAnimation(AnimationUtil.getScale(getContext()));
        mPresenter.setTextToClip(getContext(), mResult.getText().toString());
    }

    @OnClick(R.id.us_speech)
    public void setUsSpeech(ImageView usSpeech) {
        startAnim(usSpeech);
        mPresenter.playSpeech(0);
    }

    @OnClick(R.id.uk_speech)
    public void setUkSpeech(ImageView ukSpeech) {
        startAnim(ukSpeech);
        mPresenter.playSpeech(1);
    }

    private void startAnim(ImageView imageView) {
        imageView.setBackgroundResource(R.drawable.m_speech);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
        drawable.stop();
        drawable.start();
    }

    /*
    翻译卡片结果
     */
    @Override
    public void showResult(String result) {
        mTransCard.setVisibility(View.VISIBLE);
        mTransCard.startAnimation(AnimationUtil.getAlpha(mContext));
        mResult.setText(result);
    }

    /*
    音标
     */
    @Override
    public void showPhonetic(String usPhonetic, String ukPhonetic) {
        mPhonetic.setVisibility(View.VISIBLE);
        mUsPhonetic.setText(usPhonetic);
        mUkPhonetic.setText(ukPhonetic);
    }

    /*
    英文释义
     */
    @Override
    public void showExplainEn(String explainEn) {
        mExplainEn.setVisibility(View.VISIBLE);
        mExplainEn.setText(explainEn);
    }

    /*
    中文释义
     */
    @Override
    public void showExplain(String explain) {
        mExplain.setVisibility(View.VISIBLE);
        mExplain.setText(explain);
    }

    /*
    网络例句
     */
    @Override
    public void showWeb(String web) {
        mDicCard.setVisibility(View.VISIBLE);
        mDicCard.startAnimation(AnimationUtil.getAlpha(mContext));
        mWeb.setText(web);
    }

    /*
    翻译可能出现了错误：原因可能是 ① API 服务器不稳定；② 免费次数用光了；③ 没有网络；④ 不支持的文本或者文本过长
     */
    @Override
    public void showError() {
        MySnackBar.getSnack(mTransCard, R.string.invalid_translate).show();
        mListener.onSnackBarShow();
    }

    /*
    在使用金山扇贝等只能查词的源时输入的是句子时提示
     */
    @Override
    public void showNotSupport() {
        MySnackBar.getSnack(mTransCard, R.string.only_support_dic).setAction("换源", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourceFragment sourceFragment = new SourceFragment();
                sourceFragment.show(getActivity().getSupportFragmentManager(), "sourceFragment");
            }
        }).show();
        mListener.onSnackBarShow();
    }

    /*
    通知主 activity 翻译完成，以便隐藏 progressbar 与显示原文
     */
    @Override
    public void showCompletedTrans(String original) {
        if (mListener != null) {
            mListener.onSuccess(original);
        }
    }

    @Override
    public void showCopySuccess() {
        MySnackBar.getSnack(mTransCard, R.string.copy_success).show();
        mListener.onSnackBarShow();
    }

    @Override
    public void setAppTheme(int color) {
        mTransCard.setCardBackgroundColor(color);
    }

    @Override
    public void initView() {
        mPresenter.initTheme();
        mPresenter.loadData();
    }

}
