package com.about.zhiye.api;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.db.DBLab;
import com.about.zhiye.model.News;
import com.about.zhiye.model.NewsTimeLine;
import com.about.zhiye.model.Question;
import com.about.zhiye.model.Story;
import com.about.zhiye.model.Theme;
import com.about.zhiye.model.Themes;
import com.about.zhiye.model.TopStory;
import com.about.zhiye.support.Http;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by huangyuefeng on 2017/3/19.
 * Contact me : mcxinyu@foxmail.com
 */
public class ZhihuHelper {
    public static final int ZHIHU_HELPER_PERMISSION_REQUEST_CODE = 1024;
    private static final ZhihuApi ZHIHU_API = ApiFactory.getZhihuApiSingleton();

    public static Observable<List<Story>> getNewses(String date) {
        return ZHIHU_API.getBeforeNews(date)
                .map(new Func1<NewsTimeLine, List<Story>>() {
                    @Override
                    public List<Story> call(NewsTimeLine newsTimeLine) {
                        return newsTimeLine.getStories();
                    }
                });
    }

    /**
     * 按天获取 News 列表
     *
     * @param date
     * @return
     */
    public static Observable<List<News>> getNewsesOfDate(String date) {
        return getNewses(date)
                .flatMap(new Func1<List<Story>, Observable<List<News>>>() {
                    @Override
                    public Observable<List<News>> call(final List<Story> stories) {
                        return Observable.from(stories)
                                .flatMap(new Func1<Story, Observable<News>>() {
                                    @Override
                                    public Observable<News> call(final Story story) {
                                        return ZHIHU_API.getDetailNews(story.getId())
                                                .doOnNext(new Action1<News>() {
                                                    @Override
                                                    public void call(News news) {
                                                        if (null != story.getImages() &&
                                                                story.getImages().length > 0) {
                                                            news.setThumbnail(story.getImages()[0]);
                                                        }
                                                        if (null != story.getMultiPic()) {
                                                            news.setMultiPic(story.getMultiPic());
                                                        }
                                                    }
                                                });
                                    }
                                })
                                .toList();
                    }
                });
    }

    /**
     * 获取置顶 News
     *
     * @return
     */
    public static Observable<List<News>> getTopNews() {
        return ZHIHU_API.getLatestNews()
                .map(new Func1<NewsTimeLine, List<TopStory>>() {
                    @Override
                    public List<TopStory> call(NewsTimeLine newsTimeLine) {
                        return newsTimeLine.getTopStories();
                    }
                })
                .flatMap(new Func1<List<TopStory>, Observable<List<News>>>() {
                    @Override
                    public Observable<List<News>> call(List<TopStory> topStories) {
                        return Observable.from(topStories)
                                .flatMap(new Func1<TopStory, Observable<News>>() {
                                    @Override
                                    public Observable<News> call(TopStory topStory) {
                                        return ZHIHU_API.getDetailNews(topStory.getId());
                                    }
                                })
                                .toList();
                    }
                });
    }

    /**
     * 获取稍后阅读 News，存储的 ID 可能在服务器已经被删除，所有要做好检查
     *
     * @param context
     * @return
     */
    public static Observable<List<News>> getNewsesOfIds(final Context context) {
        final String[] currentId = new String[1];
        return Observable.from(DBLab.get(context).queryAllReadLater())
                .flatMap(new Func1<String, Observable<News>>() {
                    @Override
                    public Observable<News> call(String id) {
                        currentId[0] = id;
                        return ZHIHU_API.getDetailNews(id);
                    }
                })
                .onErrorReturn(new Func1<Throwable, News>() {
                    @Override
                    public News call(Throwable throwable) {
                        throwable.printStackTrace();
                        // 记录被服务器删除了，那么在稍后阅读列表中也删除，
                        // 以后可以考虑直接下载到sql或者尝试读取缓存
                        DBLab.get(context)
                                .deleteReadLaterNews(currentId[0]);
                        return null;
                    }
                })
                .filter(new Func1<News, Boolean>() {
                    @Override
                    public Boolean call(News news) {
                        return null != news;
                    }
                })
                .toList();
    }

    /**
     * 获取主题日报列表
     *
     * @return 各个主题日报分类
     */
    public static Observable<List<Themes.OthersBean>> getThemes() {
        return ZHIHU_API.getThemes()
                .map(new Func1<Themes, List<Themes.OthersBean>>() {
                    @Override
                    public List<Themes.OthersBean> call(Themes themes) {
                        return themes.getOthers();
                    }
                });
    }

    /**
     * 获取主题日报内容
     *
     * @param id
     * @return 主题日报的内容列表
     */
    public static Observable<Theme> getTheme(int id) {
        return ZHIHU_API.getTheme("" + id);
    }

    /**
     * 关键词搜索
     *
     * @param keyword
     * @return
     */
    public static Observable<List<News>> getNewsesWithKeyword(String keyword) {
        return toNewsListObservable(getHtml(ApiRetrofit.ZHIHU_SEARCH, "q", keyword));
    }

    /**
     * 关键词搜索
     *
     * @param keyword
     * @return
     */
    public static Observable<List<Question>> getQuestionsWithKeyword(String keyword) {
        return toQuestionListObservable(getHtml(ApiRetrofit.ZHIHU_SEARCH, "q", keyword), keyword);
    }

    public static void downloadZhihuImageToAlbum(final Context context, String url) {
        final String destFileDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath();

        if (context.getApplicationInfo().targetSdkVersion >= 23) {
            if (Build.VERSION.SDK_INT >= 23) {
                int permissionStatus = ContextCompat
                        .checkSelfPermission(context, android.Manifest.permission_group.STORAGE);
                if (permissionStatus != 0) {
                    ActivityCompat.requestPermissions(
                            (AppCompatActivity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            ZHIHU_HELPER_PERMISSION_REQUEST_CODE);
                }
            } else {
                downloadZhihuImage(context, url, destFileDir);
            }
        } else {
            downloadZhihuImage(context, url, destFileDir);
        }
    }

    private static void downloadZhihuImage(final Context context, String url, final String destFileDir) {
        ZHIHU_API.downloadZhihuImage(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        try {
                            is = responseBody.byteStream();
                            File file = new File(destFileDir);
                            //如果file不存在,就创建这个file
                            if (!file.exists()) {
                                file.mkdir();
                            }

                            final File imageFile = new File(destFileDir, new Date().getTime() + ".jpg");
                            fos = new FileOutputStream(imageFile);
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                            }
                            fos.flush();
                            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                            //更新相册
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(file);
                            intent.setData(uri);
                            context.sendBroadcast(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
                        } finally {
                            try {
                                if (is != null) is.close();
                            } catch (IOException e) {
                            }
                            try {
                                if (fos != null) fos.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                });
    }

    private static Observable<String> getHtml(final String baseUrl, final String key, final String value) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(Http.get(baseUrl, key, value));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static Observable<List<News>> toNewsListObservable(Observable<String> htmlObservable) {
        return htmlObservable
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return decodeHtml(s);
                    }
                })
                .flatMap(new Func1<String, Observable<JSONObject>>() {
                    @Override
                    public Observable<JSONObject> call(String s) {
                        return toJSONObject(s);
                    }
                })
                .flatMap(new Func1<JSONObject, Observable<JSONArray>>() {
                    @Override
                    public Observable<JSONArray> call(JSONObject jsonObject) {
                        return getDailyNewsJSONArray(jsonObject);
                    }
                })
                .map(new Func1<JSONArray, List<News>>() {
                    @Override
                    public List<News> call(JSONArray jsonArray) {
                        return reflectNewsListFromJSON(jsonArray);
                    }
                });
    }

    private static Observable<List<Question>> toQuestionListObservable(Observable<String> htmlObservable, final String keyword) {
        return htmlObservable
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return decodeHtml(s);
                    }
                })
                .flatMap(new Func1<String, Observable<JSONObject>>() {
                    @Override
                    public Observable<JSONObject> call(String s) {
                        return toJSONObject(s);
                    }
                })
                .flatMap(new Func1<JSONObject, Observable<JSONArray>>() {
                    @Override
                    public Observable<JSONArray> call(JSONObject jsonObject) {
                        return getDailyNewsJSONArray(jsonObject);
                    }
                })
                .map(new Func1<JSONArray, List<News>>() {
                    @Override
                    public List<News> call(JSONArray jsonArray) {
                        return reflectNewsListFromJSON(jsonArray);
                    }
                })
                .map(new Func1<List<News>, List<Question>>() {
                    @Override
                    public List<Question> call(List<News> newses) {
                        return reflectQuestionListFromNews(newses, keyword);
                    }
                });
    }

    private static List<Question> reflectQuestionListFromNews(final List<News> newses, String keyword) {
        List<Question> list = new ArrayList<>();
        for (News news : newses) {
            for (Question question : news.getQuestions()) {
                if (question.getTitle().toUpperCase().contains(keyword.toUpperCase()))
                    list.add(question);
            }
        }
        return list;
    }

    private static List<News> reflectNewsListFromJSON(JSONArray newsListJsonArray) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(newsListJsonArray.toString(), new TypeToken<List<News>>() {
        }.getType());
    }

    private static Observable<JSONArray> getDailyNewsJSONArray(final JSONObject dailyNewsJsonObject) {
        return Observable.create(new Observable.OnSubscribe<JSONArray>() {
            @Override
            public void call(Subscriber<? super JSONArray> subscriber) {
                try {
                    subscriber.onNext(dailyNewsJsonObject.getJSONArray("news"));
                    subscriber.onCompleted();
                } catch (JSONException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static Observable<JSONObject> toJSONObject(final String data) {
        return Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                try {
                    subscriber.onNext(new JSONObject(data));
                    subscriber.onCompleted();
                } catch (JSONException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private static String decodeHtml(String in) {
        return Html.fromHtml(Html.fromHtml(in).toString()).toString();
    }

    /**
     * 用浏览器打开
     *
     * @param context
     * @param url
     */
    public static void shareToBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        if (isIntentSafe(context, intent)) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context,
                    context.getString(R.string.no_browser),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享新闻
     *
     * @param context
     * @param newsTitle
     * @param newsUrl
     */
    public static void shareNews(Context context, String newsTitle, String newsUrl) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //noinspection deprecation
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.the_left_brace) +
                newsTitle +
                context.getString(R.string.the_right_brace) + " " +
                newsUrl + " " +
                context.getString(R.string.share_from_zhihu));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_to)));
    }

    /**
     * 分享新闻
     *
     * @param context
     * @param newsTitle
     * @param newsUrl
     */
    public static void shareImage(Context context, String newsTitle, String newsUrl) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //noinspection deprecation
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.the_left_brace) +
                newsTitle +
                context.getString(R.string.the_right_brace) + " " +
                newsUrl + " " +
                context.getString(R.string.share_from_zhihu));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_to)));
    }

    private static boolean isIntentSafe(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

}
