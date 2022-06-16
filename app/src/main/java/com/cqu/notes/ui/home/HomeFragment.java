package com.cqu.notes.ui.home;

import static com.cqu.notes.util.RequestUtil.doGet;
import static com.cqu.notes.util.RequestUtil.urlEncode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cqu.notes.R;
import com.cqu.notes.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    public static final int COMPLETED = -1;
    public static final int COMPLETED2 = -2;
    private FragmentHomeBinding binding;
    private RecyclerView historyList;
    private TextView WW;
    private TextView lunar;
    private TextView suit;
    private TextView avoid;
    private String toastMsg;
    private String info;
    private String history;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView yyyyMM = binding.yearMonth;
        TextView DD = binding.day;
        WW = binding.week;
        lunar = binding.lunar;
        suit = binding.suit;
        avoid = binding.avoid;
        historyList = binding.historyList;
        Calendar calendar = Calendar.getInstance();
        yyyyMM.setText(String.format(this.getString(R.string.year_month), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1));
        DD.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
        String current = formatter.format(calendar.getTime());
        formatter = new SimpleDateFormat("M/d", Locale.CHINA);
        String current2 = formatter.format(calendar.getTime());
        String date = getContext().getSharedPreferences("today", Context.MODE_PRIVATE).getString("date", "");
        if (date.equals(current)) {
            info = getContext().getSharedPreferences("today", Context.MODE_PRIVATE).getString("info", "");
            history = getContext().getSharedPreferences("today", Context.MODE_PRIVATE).getString("history", "");
            refresh();
        } else {
            new Thread(() -> queryWeather(current, current2)).start();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_SHORT).show();
            } else if (msg.what == COMPLETED2) {
                refresh();
            }
        }
    };

    /** 刷新页面当日信息 */
    private void refresh() {
        try {
            JSONObject data = new JSONObject(info);
            WW.setText(data.getString("weekday") + " " + data.getString("holiday"));
            lunar.setText(data.getString("lunarYear") + data.getString("lunar"));
            suit.setText(data.getString("suit"));
            avoid.setText(data.getString("avoid"));
            JSONArray historyLst = new JSONArray(history);
            JSONObject jsonObject;
            List<HistoryItem> hisLst = new ArrayList<>();
            for (int i = historyLst.length() - 1; i >= 0; i--) {
                jsonObject = historyLst.getJSONObject(i);
                hisLst.add(new HistoryItem(jsonObject.getString("date"), jsonObject.getString("title")));
            }
            historyList.setLayoutManager(new LinearLayoutManager(getContext()));
            historyList.setAdapter(new HistoryAdapter(getContext(), hisLst));
            LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.list_anim);
            historyList.setLayoutAnimation(layoutAnimationController);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /** 从网络请求当日信息 */
    private void queryWeather(String date, String date2) {
        Map<String, Object> params = new HashMap<>();//组合参数
        params.put("date", date);
        params.put("key", "3c597c217c6e7209041b5284b03a01ba");
        String response = doGet("http://v.juhe.cn/calendar/day", urlEncode(params));
        params = new HashMap<>();//组合参数
        params.put("date", date2);
        params.put("key", "db02e1d295dc308a0eba1223a5b3115f");
        String response2 = doGet("http://v.juhe.cn/todayOnhistory/queryEvent.php", urlEncode(params));
        try {
            JSONObject jsonObject = new JSONObject(response);
            int error_code = jsonObject.getInt("error_code");
            if (error_code == 0) {
                System.out.println("调用接口成功");
                JSONObject result = jsonObject.getJSONObject("result").getJSONObject("data");
                info = result.toString();
                history = new JSONObject(response2).getString("result");
                SharedPreferences sp = getContext().getSharedPreferences("today", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("date", date);
                editor.putString("info", info);
                editor.putString("history", history);
                editor.apply();
                toastMsg = "获取日期信息成功";
                Message msg = new Message();
                msg.what = COMPLETED2;
                handler.sendMessage(msg);
            } else {
                System.out.println("调用接口失败：" + jsonObject.getString("reason"));
                toastMsg = jsonObject.getString("reason");
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMsg = "无法连接网络";
        } finally {
            Message msg = new Message();
            msg.what = COMPLETED;
            handler.sendMessage(msg);
        }
    }
}