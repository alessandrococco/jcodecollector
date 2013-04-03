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
import java.util.TreeMap;
import java.util.TreeSet;

import jcodecollector.State;
import jcodecollector.common.bean.Snippet;

public class Controller {
    private static Controller controller = new Controller();
    private static DBMS dbms = DBMS.getInstance();
    private static SearchResults searchManager = SearchResults.getInstance();
    private static SearchFilter filters = SearchFilter.getInstance();

    private Controller() {
        // do nothing
    }

    public static Controller getInstance() {
        return controller;
    }

    public boolean removeSnippet(String name) {
        return State.getInstance().isSearchActive() ? searchManager.removeSnippet(name) : dbms.removeSnippet(name);
    }

    public boolean updateSnippet(Snippet oldSnippet, Snippet newSnippet) {
        return State.getInstance().isSearchActive() ? searchManager.updateSnippet(oldSnippet, newSnippet) : dbms.updateSnippet(oldSnippet, newSnippet);
    }

    public boolean removeCategory(String text) {
        return State.getInstance().isSearchActive() ? searchManager.removeCategory(text)
                : dbms.removeCategory(text);
    }

    public boolean renameCategory(String oldName, String newName) {
        return State.getInstance().isSearchActive() ? searchManager.renameCategory(oldName, newName) : dbms.renameCategory(oldName, newName);
    }

    public boolean updateSyntax(String newSyntax, String category, String selectedSnippet) {
        return State.getInstance().isSearchActive() ? searchManager.setSyntax(newSyntax, category, selectedSnippet) : dbms.setSyntaxToCategory(newSyntax, category, selectedSnippet);
    }

    /**
     * Restituisce l'elenco delle categorie presenti nel database.
     * 
     * @return l'elenco delle categorie presenti nel database
     */
    public ArrayList<String> getAllCategories() {
        return dbms.getCategories();
    }

    public ArrayList<String> getCategories() {
        return State.getInstance().isSearchActive() ? searchManager.getCategories() : dbms.getCategories();
    }

    public String getCategoryOf(String snippet) {
        return dbms.getCategoryOf(snippet);
    }

    public Snippet getSnippet(String name) {
        return dbms.getSnippet(name);
    }

    public ArrayList<String> getSnippetsName(String category) {
        return State.getInstance().isSearchActive() ? searchManager.getSnippets(category) : dbms.getSnippetsNames(category);
    }

    public boolean insertNewSnippet(Snippet newSnippet) {
        return dbms.insertNewSnippet(newSnippet);
    }

    public boolean lockSnippet(String name, boolean locked) {
        return dbms.lockSnippet(name, locked);
    }

    public boolean isSearchActive() {
        return State.getInstance().isSearchActive();
    }

    public void setData(TreeMap<String, TreeSet<String>> data) {
        searchManager.setData(data);
    }

    public int countCategories() {
        return State.getInstance().isSearchActive() ? searchManager.countCategories() : dbms.countCategories();
    }

    public int countSnippets() {
        return State.getInstance().isSearchActive() ? searchManager.countSnippets() : dbms.countSnippets();
    }

    public int size() {
        return countSnippets();
    }

    public int getValue() {
        return State.getInstance().isSearchActive() ? filters.countSearchTypeEnabled() : -1;
    }
}
