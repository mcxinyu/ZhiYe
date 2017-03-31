package com.about.zhiye.api;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.model.News;
import com.about.zhiye.model.NewsTimeLine;
import com.about.zhiye.model.Story;
import com.about.zhiye.model.TopStory;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by huangyuefeng on 2017/3/19.
 * Contact me : mcxinyu@foxmail.com
 */
public class ZhihuHelper {

    private static final ZhihuApi ZHIHU_API = ApiFactory.getZhihuApiSingleton();

    /**
     * 按天获取 News 列表
     * @param date
     * @return
     */
    public static Observable<List<News>> getNewsesOfDate(String date) {
        return ZHIHU_API.getBeforeNews(date)
                .map(new Func1<NewsTimeLine, List<Story>>() {
                    @Override
                    public List<Story> call(NewsTimeLine newsTimeLine) {
                        return newsTimeLine.getStories();
                    }
                })
                .flatMap(new Func1<List<Story>, Observable<List<News>>>() {
                    @Override
                    public Observable<List<News>> call(List<Story> stories) {
                        return Observable.from(stories)
                                .flatMap(new Func1<Story, Observable<News>>() {
                                    @Override
                                    public Observable<News> call(Story story) {
                                        return ZHIHU_API.getDetailNews(story.getId());
                                    }
                                })
                                .toList();
                    }
                });
    }

    /**
     * 获取置顶 News
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
     * 获取稍后阅读 News
     * @param ids 要获取的新闻 id
     * @return
     */
    public static Observable<List<News>> getNewsesOfIds(List<String> ids) {
        return Observable.from(ids)
                .flatMap(new Func1<String, Observable<News>>() {
                    @Override
                    public Observable<News> call(String id) {
                        return ZHIHU_API.getDetailNews(id);
                    }
                })
                .toList();
    }

    /**
     * 用浏览器打开
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

    private static boolean isIntentSafe(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

    /*
    // 分享到客户端相关代码
    private void shareToZhihu() {
        final List<Question> questions = ZhihuHelper.getQuestions(mNews);
        String[] titlesArray = getQuestionTitlesAsStringArray(questions);

        if (titlesArray.length > 1){
            new AlertDialog.Builder(getContext(), R.style.dialog)
                    .setTitle("用知乎打开一个你感兴趣的问题")
                    .setItems(titlesArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shareToZhihuClient(questions.get(which).getUrl());
                        }
                    })
                    .create()
                    .show();
        } else {
            shareToZhihuClient(questions.get(0).getUrl());
        }
    }

    private void shareToZhihuClient(String questionUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(questionUrl));
        intent.setPackage(Constants.Information.ZHIHU_PACKAGE_ID);
        if (isZhihuClientInstalled()) {
            getActivity().startActivity(intent);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_zhihu_client), Toast.LENGTH_SHORT).show();
        }
    }

    private String[] getQuestionTitlesAsStringArray(List<Question> questions) {
        String[] titles = new String[questions.size()];

        for (int i = 0; i < titles.length; i++) {
            titles[i] = questions.get(i).getTitle();
        }

        return titles;
    }

    private boolean isZhihuClientInstalled() {
        try {
            return getActivity()
                    .getPackageManager()
                    .getPackageInfo(Constants.Information.ZHIHU_PACKAGE_ID,
                            PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }
    */
}
