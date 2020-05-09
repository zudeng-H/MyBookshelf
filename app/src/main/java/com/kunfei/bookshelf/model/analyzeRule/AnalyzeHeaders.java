package com.kunfei.bookshelf.model.analyzeRule;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.kunfei.bookshelf.DbHelper;
import com.kunfei.bookshelf.MApplication;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.bean.BookSourceBean;
import com.kunfei.bookshelf.bean.CookieBean;
import com.kunfei.bookshelf.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;
import static com.kunfei.bookshelf.constant.AppConstant.DEFAULT_USER_AGENT;
import static com.kunfei.bookshelf.constant.AppConstant.MAP_STRING;

/**
 * Created by GKF on 2018/3/2.
 * 解析Headers
 */

public class AnalyzeHeaders {
    private static SharedPreferences preferences = MApplication.getConfigPreferences();

    public static Map<String, String> getMap(BookSourceBean bookSourceBean) {
        Map<String, String> headerMap = new HashMap<>();
        if (bookSourceBean != null) {
            String headers = bookSourceBean.getHttpUserAgent();
            if (!isEmpty(headers)) {
                if (StringUtils.isJsonObject(headers)) {
                    try {
                        Map<String, String> map = new Gson().fromJson(headers, MAP_STRING);
                        headerMap.putAll(map);
                    } catch (Exception e) {
                        headerMap.put("User-Agent", headers);
                    }
                } else {
                    headerMap.put("User-Agent", headers);
                }
            } else {
                headerMap.put("User-Agent", getDefaultUserAgent());
            }
            CookieBean cookie = DbHelper.getDaoSession().getCookieBeanDao().load(bookSourceBean.getBookSourceUrl());
            if (cookie != null) {
                headerMap.put("Cookie", cookie.getCookie());
            }
        } else {
            headerMap.put("User-Agent", getDefaultUserAgent());
        }
        return headerMap;
    }

    private static String getDefaultUserAgent() {
        return preferences.getString(MApplication.getInstance().getString(R.string.pk_user_agent), DEFAULT_USER_AGENT);
    }
}
