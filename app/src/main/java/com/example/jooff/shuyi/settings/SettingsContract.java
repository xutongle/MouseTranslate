package com.example.jooff.shuyi.settings;

import com.example.jooff.shuyi.base.BasePresenter;
import com.example.jooff.shuyi.base.BaseView;

/**
 * Created by Jooff on 2017/1/24.
 */

public class SettingsContract {

    interface View extends BaseView{

        void showSettings(boolean[] settings);

    }

    interface Presenter extends BasePresenter {

        void saveSettings(boolean[] settings);

    }
}
