/*
 * Copyright 2006-2013 Alessandro Cocco.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jcodecollector.data;

import jcodecollector.data.settings.ApplicationSettings;

public class SearchFilter {
    public boolean isSearchInNameEnabled() {
        return ApplicationSettings.getInstance().isSearchInNameEnabled();
    }

    public boolean isSearchInTagsEnabled() {
        return ApplicationSettings.getInstance().isSearchInTagsEnabled();
    }

    public boolean isSearchInCodeEnabled() {
        return ApplicationSettings.getInstance().isSearchInCodeEnabled();
    }

    public boolean isSearchInCommentEnabled() {
        return ApplicationSettings.getInstance().isSearchInCommentEnabled();
    }

    public void setSearchInNameEnabled(boolean searchInNameEnabled) {
        ApplicationSettings.getInstance().setSearchInNameEnabled(searchInNameEnabled);
    }

    public void setSearchInTagsEnabled(boolean searchInTagsEnabled) {
        ApplicationSettings.getInstance().setSearchInTagsEnabled(searchInTagsEnabled);
    }

    public void setSearchInCodeEnabled(boolean searchInCodeEnabled) {
        ApplicationSettings.getInstance().setSearchInCodeEnabled(searchInCodeEnabled);
    }

    public void setSearchInCommentEnabled(boolean searchInCommentEnabled) {
        ApplicationSettings.getInstance().setSearchInCommentEnabled(searchInCommentEnabled);
    }

    public void setSearchCaseSensitive(boolean searchCaseSensitive) {
        ApplicationSettings.getInstance().setSearchCaseSensitive(searchCaseSensitive);
    }

    public boolean isSearchCaseSensitive() {
        return ApplicationSettings.getInstance().isSearchCaseSensitive();
    }

    public int countSearchTypeEnabled() {
        int sum = 0;
        if (ApplicationSettings.getInstance().isSearchInNameEnabled()) {
            sum++;
        }
        if (ApplicationSettings.getInstance().isSearchInCodeEnabled()) {
            sum++;
        }
        if (ApplicationSettings.getInstance().isSearchInTagsEnabled()) {
            sum++;
        }
        if (ApplicationSettings.getInstance().isSearchInCommentEnabled()) {
            sum++;
        }
        return sum;
    }

    private static SearchFilter filter = new SearchFilter();

    public static SearchFilter getInstance() {
        return filter;
    }

    private SearchFilter() {
        // do nothing
    }

}
