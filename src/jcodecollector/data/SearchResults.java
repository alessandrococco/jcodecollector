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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import jcodecollector.common.bean.Snippet;

public class SearchResults {
    
    /** La mappa ordinata che contiene gli snippet suddivisi per categoria. */
    private TreeMap<String, TreeSet<String>> data = null;

    private static SearchResults searchResults = new SearchResults();

    public static SearchResults getInstance() {
        return searchResults;
    }

    private SearchResults() {
        this.data = new TreeMap<String, TreeSet<String>>();
    }

    public ArrayList<String> getSnippets(String category) {
        ArrayList<String> names = new ArrayList<String>();
        TreeSet<String> set = data.get(category);
        if (set != null) {
            names.addAll(data.get(category));
        }

        return names;
    }

    public ArrayList<String> getCategories() {
        return new ArrayList<String>(data.keySet());
    }

    /**
     * Richiede al database la cancellazione di tutti gli snippet della
     * categoria indicata trovati con l'ultima ricerca.
     * 
     * @param category La categoria degli snippet da cancellare.
     */
    public boolean removeCategory(String category) {
        if (!data.containsKey(category)) {
            return false;
        }

        ArrayList<String> array = getSnippets(category);
        boolean success = DBMS.getInstance().removeSnippets(array);

        if (success) {
            data.remove(category);
        }

        return success;
    }

    public boolean renameCategory(String oldName, String newName) {
        if (!data.containsKey(oldName)) {
            return false;
        }

        // ottengo gli snippet della vecchia categoria
        TreeSet<String> oldValue = data.get(oldName);

        // rimuovo la vecchia categoria dalla mappa
        data.remove(oldName);

        // se la nuova categoria non e' presente la inserisco con tutti gli
        // snippet della vecchia categoria
        if (!data.containsKey(newName)) {
            data.put(newName, oldValue);
        } else {
            // altrimenti le aggiungo i vecchi snippet
            TreeSet<String> newValue = data.get(newName);
            newValue.addAll(oldValue);
            data.put(newName, newValue);
        }

        // fatto questo posso chiedere al dbms di effettuare l'aggiornamento
        return DBMS.getInstance().renameCategoryOf(
                new ArrayList<String>(data.get(newName)), newName);
    }

    public boolean removeSnippet(String name) {
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            TreeSet<String> value = data.get(key);
            if (value.contains(name)) {
                value.remove(name);
                return DBMS.getInstance().removeSnippet(name);
            }
        }

        return false;
    }

    public boolean updateSnippet(Snippet oldSnippet, Snippet newSnippet) {
        data.get(oldSnippet.getCategory()).remove(oldSnippet.getName());

        if (data.containsKey(newSnippet.getCategory())) {
            data.get(newSnippet.getCategory()).add(newSnippet.getName());
        } else {
            TreeSet<String> value = new TreeSet<String>();
            value.add(newSnippet.getName());
            data.put(newSnippet.getCategory(), value);
        }

        return DBMS.getInstance().updateSnippet(oldSnippet, newSnippet);
    }

    public void setData(TreeMap<String, TreeSet<String>> data) {
        this.data = data;
    }

    public int size() {
        return data.size();
    }

    public int countCategories() {
        return data.keySet().size();
    }

    public int countSnippets() {
        int n = 0;

        for (String s : data.keySet()) {
            n += data.get(s).size();
        }

        return n;
    }

    public void clear() {
        data.clear();
    }

    public boolean setSyntax(String newSyntax, String category, String selected) {
        if (!data.containsKey(category)) {
            return false;
        }
        
        ArrayList<String> snippets = new ArrayList<String>(data.get(category));
        snippets.remove(selected);
        
        return DBMS.getInstance().setSyntaxToSnippets(newSyntax, snippets);
    }
}
