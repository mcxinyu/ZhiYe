package com.about.zhiye.api;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.about.zhiye.R;
import com.about.zhiye.model.News;
import com.about.zhiye.model.NewsTimeLine;
import com.about.zhiye.model.Question;
import com.about.zhiye.model.Story;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.about.zhiye.support.Constants.Information.ZHIHU_QUESTION_LINK_PREFIX;

/**
 * Created by huangyuefeng on 2017/3/19.
 * Contact me : mcxinyu@foxmail.com
 */
public class ZhihuHelper {
    private static final String QUESTION_SELECTOR = "div.question";
    private static final String QUESTION_TITLES_SELECTOR = "h2.question-title";
    private static final String QUESTION_LINKS_SELECTOR = "div.view-more a";

    private static final ZhihuApi ZHIHU_API = ApiFactory.getZhihuApiSingleton();

    private static Observable<NewsTimeLine> getZhihuOfDate(String date){
        return ZHIHU_API.getBeforeNews(date);
    }

    public static Observable<List<Story>> ofDate(String date){
        final List<Story> list = new ArrayList<>();

        return getZhihuOfDate(date).map(new Func1<NewsTimeLine, List<Story>>() {
            @Override
            public List<Story> call(NewsTimeLine newsTimeLine) {
                final List<Story> stories = newsTimeLine.getStories();

                Observable.from(stories)
                        // .subscribeOn(Schedulers.io())
                        // .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<Story, Observable<News>>() {
                            @Override
                            public Observable<News> call(Story story) {
                                return ZHIHU_API.getDetailNews(story.getId());
                            }
                        })
                        .map(new Func1<News, Observable<Story>>() {
                            @Override
                            public Observable<Story> call(final News news) {
                                return Observable.from(stories)
                                        .filter(new Func1<Story, Boolean>() {
                                            @Override
                                            public Boolean call(Story story) {
                                                return story.getId().equals(news.getId());
                                            }
                                        })
                                        .map(new Func1<Story, Story>() {
                                            @Override
                                            public Story call(Story story) {
                                                story.setQuestionTitle(getQuestions(news).get(0).getTitle());
                                                story.setShareUrl(news.getShareUrl());
                                                return story;
                                            }
                                        });
                            }
                        })
                        .subscribe(new Action1<Observable<Story>>() {
                            @Override
                            public void call(Observable<Story> storyObservable) {
                                storyObservable.subscribe(new Action1<Story>() {
                                    @Override
                                    public void call(Story story) {
                                        list.add(story);
                                    }
                                });
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
                return list;
            }
        });
    }

    public static List<Question> getQuestions(News news) {
        List<Question> list = new ArrayList<>();
        String body = news.getBody();
        Document document = Jsoup.parse(body);
        Elements questionElements = getQuestionElements(document);

        for (Element element : questionElements) {
            String questionTitle = getQuestionTitleFromQuestionElement(element);
            String questionUrl = getQuestionUrlFromQuestionElement(element);

            list.add(new Question(
                    TextUtils.isEmpty(questionTitle) ? news.getTitle() : questionTitle,
                    questionUrl));
        }
        return list;
    }

    private static Elements getQuestionElements(Document document) {
        return document.select(QUESTION_SELECTOR);
    }

    private static String getQuestionTitleFromQuestionElement(Element element) {
        Element questionTitleElement = element.select(QUESTION_TITLES_SELECTOR).first();

        if (questionTitleElement == null) {
            return null;
        } else {
            return questionTitleElement.text();
        }
    }

    private static String getQuestionUrlFromQuestionElement(Element element) {
        Element viewMoreElement = element.select(QUESTION_LINKS_SELECTOR).first();

        if (viewMoreElement == null){
            return null;
        } else {
            String url = viewMoreElement.attr("href");

            return (url != null && url.startsWith(ZHIHU_QUESTION_LINK_PREFIX))
                    ? url : null;
        }
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
