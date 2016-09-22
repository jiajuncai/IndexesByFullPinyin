package com.junecai.indexesforfullpinyin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.junecai.indexesforfullpinyin.utils.CnToQuanpin;
import com.junecai.indexesforfullpinyin.view.SlideBar;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private SlideBar mSlideBar;
    private TextView mTvSlideBarHint;
    private ListView mLvSample;
    private List<String> mDatas = new ArrayList<String>();
    private HashMap<String, Integer> alphaIndexer = new HashMap<String, Integer>();
    private List<Map<String, Object>> mDatasBySort = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        initData();
        setListener();
    }

    private void initview() {
        mSlideBar = (SlideBar) findViewById(R.id.slidebar);
        mTvSlideBarHint = (TextView) findViewById(R.id.tv_slidebar_hint);
        mLvSample = (ListView) findViewById(R.id.lv_sample);
    }

    /**
     * 数据
     */
    private void initData() {
        setTestData();
        // 设置数据，添加首字母
        for (int i = 0; i < mDatas.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", mDatas.get(i));
            String contactSort = CnToQuanpin.getFullSpell(mDatas.get(i))
                    .toUpperCase().substring(0, 1);
            if (!isLetter(contactSort)) {
                contactSort = "#";
            }
            map.put("Sort", contactSort);
            mDatasBySort.add(map);
        }
        Comparator comp = new Mycomparator();
        Collections.sort(mDatasBySort, comp);
        for (int i = 0; i < mDatasBySort.size(); i++) {
            // 当前汉语拼音首字母
            // getAlpha(list.get(i));
            String currentStr = mDatasBySort.get(i).get("Sort").toString();
            // 上一个汉语拼音首字母，如果不存在为“ ”
            String previewStr = (i - 1) >= 0 ? mDatasBySort.get(i - 1).get("Sort")
                    .toString() : " ";
            if (!previewStr.equals(currentStr)) {
                String name = mDatasBySort.get(i).get("Sort").toString();
                alphaIndexer.put(name, i);
//                sections[i] = name;
            }
        }
        //创建adapter
        SimpleAdapter adapter = new SimpleAdapter(this, mDatasBySort, android.R.layout
                .simple_list_item_activated_1, new String[]{"name"}, new int[]{android.R.id.text1});
        mLvSample.setAdapter(adapter);
    }

    private void setListener() {
        mSlideBar.setOnTouchLetterChangeListenner(new SlideBar.OnTouchLetterChangeListenner() {
            @Override
            public void onTouchLetterChange(MotionEvent event, String s) {
//                Log.i(TAG, "索引：" + s);
                if (alphaIndexer.get(s) != null) {
                    mTvSlideBarHint.setText(s);
                    mTvSlideBarHint.setVisibility(View.VISIBLE);
                    // 设置textview在一秒后不可见
                    mTvSlideBarHint.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTvSlideBarHint.setVisibility(View.GONE);
                        }
                    }, 1000);
                    // 设置跳转到listview的某个位置
                    mLvSample.setSelection(alphaIndexer.get(s));
                }
            }
        });
    }

    /**
     * 按中文拼音排序
     * compare（a,b）方法:根据第一个参数小于、等于或大于第二个参数分别返回负整数、零或正整数。
     */
    public class Mycomparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Map<String, Object> c1 = (Map<String, Object>) o1;
            Map<String, Object> c2 = (Map<String, Object>) o2;
            // 控制#标识的始终在最后
            if (c1.get("Sort").equals("#")) {
                return 1;
            } else if (c2.get("Sort").equals("#")) {
                return -1;
            } else {
                Comparator cmp = Collator.getInstance(Locale.ENGLISH);
                return cmp.compare(c1.get("Sort"), c2.get("Sort"));
            }
        }
    }

    /**
     * 判断是否为字母
     *
     * @param str 需要判断的字符串
     * @return true：为字母  false：不是字母
     */
    public static boolean isLetter(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[a-zA-Z]+");
        java.util.regex.Matcher m = pattern.matcher(str);
        return m.matches();
    }

    /**
     * 设置测试数据
     */
    private void setTestData() {
        mDatas.add("12222aaa");
        mDatas.add("啊");
        mDatas.add("吧");
        mDatas.add("12222v");
        mDatas.add("出");
        mDatas.add("去");
        mDatas.add("我");
        mDatas.add("饿");
        mDatas.add("#");
        mDatas.add("他");
        mDatas.add("有");
        mDatas.add("u");
        mDatas.add("怕");
        mDatas.add("出错");
        mDatas.add("*");
        mDatas.add("个");
        mDatas.add("^");
        mDatas.add("就");
        mDatas.add("好");
        mDatas.add("看");
        mDatas.add("来");
        mDatas.add("12222");
    }
}
