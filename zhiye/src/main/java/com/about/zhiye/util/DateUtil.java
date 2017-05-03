package com.about.zhiye.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huangyuefeng on 2017/4/11.
 * Contact me : mcxinyu@foxmail.com
 */
public class DateUtil {
    @SuppressWarnings("deprecation")
    public static final Date ZHIHU_DAILY_BIRTHDAY = new Date(113, 4, 19); // May 19th, 2013
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.US);
}
