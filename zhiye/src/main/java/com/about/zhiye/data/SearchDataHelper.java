package com.about.zhiye.data;

import android.content.Context;
import android.widget.Filter;

import com.about.zhiye.util.QueryPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyuefeng on 2017/6/6.
 * Contact me : mcxinyu@foxmail.com
 */
public class SearchDataHelper {

    public interface OnFindSuggestionsListener {
        void onResults(List<SearchNewsSuggestion> results);
    }

    public static List<SearchNewsSuggestion> getSearchSuggestion(Context context, int count) {
        List<String> history = QueryPreferences.getSearchHistory(context);

        List<SearchNewsSuggestion> suggestions = new ArrayList<>();

        for (int i = 0; i < history.size(); i++) {
            SearchNewsSuggestion suggestion = new SearchNewsSuggestion(history.get(i));
            suggestion.setIsHistory(true);
            suggestions.add(suggestion);
            if (suggestions.size() == count) {
                break;
            }
        }

        return suggestions;
    }

    public static void findSuggestions(final Context context,
                                       String query,
                                       final int limit,
                                       final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<SearchNewsSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {
                    List<SearchNewsSuggestion> searchNewsSuggestionsHistory = getSearchSuggestion(context, limit);

                    for (SearchNewsSuggestion suggestion : searchNewsSuggestionsHistory) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {
                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<SearchNewsSuggestion>) results.values);
                }
            }
        }.filter(query);
    }
}
