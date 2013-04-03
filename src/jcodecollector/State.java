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
package jcodecollector;

import java.util.ArrayList;

import jcodecollector.common.bean.Snippet;
import jcodecollector.data.Controller;
import jcodecollector.data.DBMS;
import jcodecollector.listener.CategoryListener;
import jcodecollector.listener.CountListener;
import jcodecollector.listener.MenuListener;
import jcodecollector.listener.SearchListener;
import jcodecollector.listener.SnippetListener;
import jcodecollector.listener.WindowListener;

public class State implements SnippetListener, CategoryListener, CountListener, MenuListener, WindowListener, SearchListener {

	/**
	 * Gli oggetti che vogliono essere aggiornati su cio' che riguarda uno
	 * snippet (ad esempio: quando viene salvato, ecc).
	 */
	private ArrayList<SnippetListener> snippetListeners;

	/**
	 * Gli oggetti che vogliono essere aggiornati su cio' che riguarda una
	 * categoria (ad esempio se viene rinominata).
	 */
	private ArrayList<CategoryListener> categoryListeners;

	/**
	 * Gli oggetti che vogliono essere aggiornati quando il numero di categorie
	 * e/o snippet cambia.
	 */
	private ArrayList<CountListener> countListeners;

	/**
	 * Gli oggetti che vogliono essere aggiornati quando c'e' da modificare lo
	 * stato degli elementi dei menu.
	 */
	private ArrayList<MenuListener> menuListeners;

	/**
	 * Gli oggetti che vogliono essere aggiornati quando cambia lo stato della
	 * finestra (ad esempio quando viene settato il flag 'document modified'.
	 */
	private ArrayList<WindowListener> windowListeners;

	/**
	 * Gli oggetti che vogliono essere aggiornati quando viene effettuata una
	 * ricerca.
	 */
	private ArrayList<SearchListener> searchListeners;

	/** La categoria selezionata. */
	private String nameOfSelectedCategory;

	/** Lo snippet selezionato. */
	private String nameOfSelectedSnippet;

	/**
	 * Se <code>true</code> lo snippet corrente e' stato salvato e non ha subito
	 * ulteriori modifiche.
	 */
	private boolean snippetSaved;

	/** Se <code>true</code> lo snippet corrente e' stato validato. */
	private boolean snippetValidated;

	/** Se <code>true</code> lo snippet corrente e' stato bloccato. */
	private boolean snippetLocked;

	/** Lo snippet precedente. */
	private Snippet previousSnippet;

	/** Se <code>true</code> indica che la ricerca e' attiva. */
	private boolean searchActive;

	/** Istanzia i vari array di listener. */
	private State() {
		snippetListeners = new ArrayList<SnippetListener>();
		categoryListeners = new ArrayList<CategoryListener>();
		countListeners = new ArrayList<CountListener>();
		menuListeners = new ArrayList<MenuListener>();
		windowListeners = new ArrayList<WindowListener>();
		searchListeners = new ArrayList<SearchListener>();
	}

	/** L'unica istanza permessa di questa classe. */
	private static State state = new State();

	/**
	 * Restituisce l'unica instanza permessa di {@link State}
	 * 
	 * @return l'unica instanza permessa di {@link State}
	 */
	public static State getInstance() {
		return state;
	}

	public void addSnippetListener(SnippetListener listener) {
		snippetListeners.add(listener);
	}

	public boolean removeSnippetListener(SnippetListener listener) {
		return snippetListeners.remove(listener);
	}

	public void addCategoryListener(CategoryListener listener) {
		categoryListeners.add(listener);
	}

	public boolean removeCategoryListener(CategoryListener listener) {
		return categoryListeners.remove(listener);
	}

	public void addCountListener(CountListener listener) {
		countListeners.add(listener);
	}

	public boolean removeCountListener(CountListener listener) {
		return countListeners.remove(listener);
	}

	public void addMenuListener(MenuListener listener) {
		menuListeners.add(listener);
	}

	public boolean removeMenuListener(MenuListener listener) {
		return menuListeners.remove(listener);
	}

	public void addWindowListener(WindowListener listener) {
		windowListeners.add(listener);
	}

	public boolean removeWindowListener(WindowListener listener) {
		return windowListeners.remove(listener);
	}

	public void addSearchListener(SearchListener listener) {
		searchListeners.add(listener);
	}

	public boolean removeSearchListener(SearchListener listener) {
		return searchListeners.remove(listener);
	}

	/**
	 * @see jcodecollector.listener.CategoryListener#categoriesUpdated(java.
	 *      lang.String)
	 */
	public void categoriesUpdated(String selected) {
		for (CategoryListener listener : categoryListeners) {
			listener.categoriesUpdated(selected);
		}
	}

	/**
	 * @see jcodecollector.listener.CategoryListener#categoryRemoved(java.lang
	 *      .String)
	 */
	public void categoryRemoved(String name) {
		for (CategoryListener listener : categoryListeners) {
			listener.categoryRemoved(name);
		}
	}

	/**
	 * @see jcodecollector.listener.CategoryListener#categoryRenamed(java.lang
	 *      .String, java.lang.String)
	 */
	public void categoryRenamed(String oldName, String newName) {
		for (CategoryListener listener : categoryListeners) {
			listener.categoryRenamed(oldName, newName);
		}
	}

	/** @see jcodecollector.listener.SnippetListener#snippetRemoved(Snippet) */
	public void snippetRemoved(Snippet snippet) {
		for (SnippetListener listener : snippetListeners) {
			listener.snippetRemoved(snippet);
		}
	}

	/**
	 * @see jcodecollector.listener.SnippetListener#snippetRenamed(java.lang.String,
	 *      java.lang.String)
	 */
	public void snippetRenamed(String oldName, String newName) {
		for (SnippetListener listener : snippetListeners) {
			listener.snippetRenamed(oldName, newName);
		}
	}

	/** @see SnippetListener#snippetEdited(Snippet) */
	public void snippetEdited(Snippet snippet) {
		for (SnippetListener listener : snippetListeners) {
			listener.snippetEdited(snippet);
		}
	}

	/** @see SnippetListener#syntaxRenamed(String, String) */
	public void syntaxRenamed(String newName, String category) {
		for (SnippetListener listener : snippetListeners) {
			listener.syntaxRenamed(newName, category);
		}
	}

	/**
	 * Conta quante categorie e quanti snippet sono presenti nel database o come
	 * risultato della ricerca.
	 */
	public void countUpdate() {
		int categories;
		int snippets;

		if (searchActive) {
			categories = Controller.getInstance().countCategories();
			snippets = Controller.getInstance().countSnippets();
		} else {
			categories = DBMS.getInstance().countCategories();
			snippets = DBMS.getInstance().countSnippets();
		}

		countUpdate(categories, snippets);
	}

	/** @see CountListener#countUpdate(int, int) */
	public void countUpdate(int categories, int snippets) {
		for (CountListener listener : countListeners) {
			listener.countUpdate(categories, snippets);
		}
	}

	public void updateSnippetStatus(boolean validated, boolean saved, boolean locked) {
		this.snippetValidated = validated;
		this.snippetSaved = saved;
		this.snippetLocked = locked;

		for (SnippetListener listener : snippetListeners) {
			listener.updateSnippetStatus(validated, saved, locked);
		}
	}

	public void updateMenu(boolean enabled, boolean resetExportSubMenu) {
		for (MenuListener listener : menuListeners) {
			listener.updateMenu(enabled, resetExportSubMenu);
		}
	}

	/** @see WindowListener#updateWindowStatus(boolean) */
	public void updateWindowStatus(boolean documentModified) {
		for (WindowListener listener : windowListeners) {
			listener.updateWindowStatus(documentModified);
		}
	}

	/** @see WindowListener#updateLineNumbers(boolean) */
	public void updateLineNumbers(boolean enabled) {
		for (WindowListener listener : windowListeners) {
			listener.updateLineNumbers(enabled);
		}
	}

	/**
	 * Imposta il nome della categoria selezionata.
	 * 
	 * @param selectedCategory La categoria selezionata.
	 */
	public void setNameOfSelectedCategory(String selectedCategory) {
		this.nameOfSelectedCategory = selectedCategory;
	}

	/**
	 * Imposta il nome dello snippet selezionato.
	 * 
	 * @param selectedSnippet Lo snippet selezionato.
	 */
	public void setNameOfSelectedSnippet(String selectedSnippet) {
		this.nameOfSelectedSnippet = selectedSnippet;
	}

	/**
	 * Restuisce il nome della categoria selezionata.
	 * 
	 * @return il nome della categoria selezionata
	 */
	public String getNameOfSelectedCategory() {
		return this.nameOfSelectedCategory;
	}

	/**
	 * Restuisce il nome dello snippet selezionato.
	 * 
	 * @return il nome dello snippet selezionato
	 */
	public String getNameOfSelectedSnippet() {
		return this.nameOfSelectedSnippet;
	}

	/**
	 * Blocca o sblocca lo snippet corrente
	 * 
	 * @param snippetLocked Se <code>true</code> lo snippet viene bloccato, con
	 *        <code>false</code> viene sbloccato.
	 */
	public void setSnippetLocked(boolean snippetLocked) {
		this.snippetLocked = snippetLocked;
	}

	/**
	 * Indica se lo snippet corrente e' validato o meno.
	 * 
	 * @return <code>true</code> se lo snippet e' stato validato,
	 *         <code>false</code> altrimenti
	 */
	public boolean isSnippetValidated() {
		return snippetValidated;
	}

	/**
	 * Indica se lo snippet corrente e' stato salvato e se da allora non ha piu'
	 * subito modifiche.
	 * 
	 * @return <code>true</code> se lo snippet e' stato salvato.
	 */
	public boolean isSnippetSaved() {
		return snippetSaved;
	}

	/**
	 * Indica se lo snippet e ' bloccato o meno.
	 * 
	 * @return <code>true</code> se lo snippet e' bloccato, <code>false</code>
	 *         altrimenti
	 */
	public boolean isSnippetLocked() {
		return snippetLocked;
	}

	/**
	 * Imposta lo snippet precedentemente selezionato.
	 * 
	 * @param previousSnippet Lo snippet precedentemente selezionato.
	 */
	public void setPreviousSnippet(Snippet previousSnippet) {
		this.previousSnippet = previousSnippet;
	}

	/**
	 * Restituisce lo snippet precedentemente selezionato.
	 * 
	 * @return lo snippet precedentemente selezionato
	 */
	public Snippet getPreviousSnippet() {
		return previousSnippet;
	}

	/** @see SearchListener#updateSearch(boolean) */
	public void updateSearch(boolean active) {
		searchActive = active;

		for (SearchListener listener : searchListeners) {
			listener.updateSearch(!active);
		}
	}

	/**
	 * Indica se la ricerca e' attiva.
	 * 
	 * @return <code>true</code> se la ricerca attiva, <code>false</code>
	 *         altrimenti
	 */
	public boolean isSearchActive() {
		return searchActive;
	}

	/** Ferma la ricerca (o meglio: entra in modalita' 'ricerca') */
	public void startSearch() {
		searchActive = true;
	}

	/** Ferma la ricerca (o meglio: esce dalla modalita' 'ricerca') */
	public void stopSearch() {
		searchActive = false;
	}

	public boolean isDatabaseEmpty() {
		return DBMS.getInstance().countCategories() == 0;
	}

}
