package com.liyuliang.mytime.activity.settings;

import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.adapter.LanguageAdapter;
import com.liyuliang.mytime.bean.Language;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.LanguageUtil;
import com.liyuliang.mytime.widget.MyToolbar;
import com.liyuliang.mytime.widget.RecyclerViewDivider;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择语言页面
 * Created at 2018/11/28 13:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class SetLanguageActivity extends SwipeBackActivity {

    private List<Language> languageList;
    private LanguageAdapter languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.ChooseLanguage, R.drawable.back_white, -1, -1, -1, onClickListener);
        RecyclerView recyclerViewLanguage = findViewById(R.id.recyclerView_language);
        languageList = new ArrayList<>();
        addLanguages();
        //垂直线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewLanguage.setLayoutManager(linearLayoutManager);
        recyclerViewLanguage.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(this, R.color.gray_slight)));
        languageAdapter = new LanguageAdapter(this, languageList);
        recyclerViewLanguage.setAdapter(languageAdapter);
        languageAdapter.setOnItemClickListener((view, position) -> {
            SPHelper.save(getString(R.string.language), languageList.get(position).getLanguageCode());
            LanguageUtil.changeAppLanguage(this);
            // 因为切换了语言，所以需要重新刷新列表，加载新的语言
            languageAdapter.notifyDataSetChanged();
            // 通过EventBus通知其他页面更改语言
            EventBus.getDefault().post("CHANGE_LANGUAGE");
        });
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        }
    };

    /**
     * 添加语言种类
     */
    private void addLanguages() {
        Language general = new Language();
        general.setLanguageName(R.string.lan_default);
        general.setLanguageCode("");
        languageList.add(general);

        Language chinese = new Language();
        chinese.setLanguageName(R.string.lan_chinese);
        chinese.setLanguageCode("zh");
        languageList.add(chinese);

        Language chinese_taiwan = new Language();
        chinese_taiwan.setLanguageName(R.string.lan_chinese_taiwan);
        chinese_taiwan.setLanguageCode("zh-rtw");
        languageList.add(chinese_taiwan);

        Language english = new Language();
        english.setLanguageName(R.string.lan_en);
        english.setLanguageCode("en");
        languageList.add(english);

        Language japanese = new Language();
        japanese.setLanguageName(R.string.lan_ja);
        japanese.setLanguageCode("ja");
        languageList.add(japanese);
    }

}
