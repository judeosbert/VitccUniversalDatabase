package com.example.kleptomaniac.vitccuniversaldatabase;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by kleptomaniac on 6/7/17.
 */

public class RecentSearchSuggestionsProvider extends SearchRecentSuggestionsProvider {


    public static final String AUTHORITY = RecentSearchSuggestionsProvider.class.getName();
    public static final int MODE = DATABASE_MODE_QUERIES;
    public RecentSearchSuggestionsProvider()
    {
        setupSuggestions(AUTHORITY,MODE);
    }
}
