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
package jcodecollector.data.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

import javax.swing.filechooser.FileSystemView;

import jcodecollector.util.OS;

public class ApplicationSettings {

	/** Lo snippet selezionato. */
	private String selectedSnippets;

	/** The dimension of main window. */
	private Dimension windowSize = null;

	/** The location of main window. */
	private Point windowLocation = null;

	/** The width of source list split panel. */
	private Integer sourceListWidth = null;

	/** The width of editor split panel. */
	private Integer editorWidth = null;

	/** The default dimension of main window. */
	public static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(750, 580);

	/** The default location of main window. */
	public static final Point DEFAULT_WINDOW_LOCATION = new Point(100, 50);

	/** The default width of source list. */
	public static final int DEFAULT_SOURCE_LIST_WIDTH = 250;

	/** The default width of editor panel. */
	public static final int DEFAULT_EDITOR_PANEL_WIDTH = 311;

	/** The path of database directory. */
	private String databasePath;

	/** The path of preferences file. */
	private String preferencesPath;

	private boolean searchInNameEnabled = true;
	private boolean searchInTagsEnabled = true;
	private boolean searchInCodeEnabled = true;
	private boolean searchInCommentEnabled = true;
	private boolean searchCaseSensitive = false;
	private boolean lineNumbersEnabled = false;
	private boolean commentPanelVisible = true;
	private boolean autoHideCommentEnabled = false;

	/** Il nome del database */
	public static final String DB_DIR_NAME = "JCODECOLLECTOR_DB";

	/** Default path of database. */
	public static final String DATABASE_PATH_DEFAULT;

	/** Default path of preferences file. */
	public static final String PREFERENCES_PATH_DEFAULT;

	/** Private instance of the class. */
	private static ApplicationSettings settings = new ApplicationSettings();

	static {
		String userDirectory = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
		if (OS.isMacOSX()) {
			DATABASE_PATH_DEFAULT = userDirectory + "/Library/Application Support/";
			PREFERENCES_PATH_DEFAULT = userDirectory + "/Library/Preferences/com.alessandro.jcodecollector";
		} else {
			DATABASE_PATH_DEFAULT = "jCodeCollector";
			PREFERENCES_PATH_DEFAULT = "jCodeCollector\\settings.dat";
		}

		System.out.println("DEFAULT DATABASE PATH = " + DATABASE_PATH_DEFAULT);
		System.out.println("DEFAULT PREFERENCES PATH = " + PREFERENCES_PATH_DEFAULT);
		System.out.println("CURRENT DATABASE PATH = " + settings.getDatabasePath());
	}

	private ApplicationSettings() {
		selectedSnippets = null;
		windowSize = new Dimension(DEFAULT_WINDOW_SIZE);
		windowLocation = new Point(DEFAULT_WINDOW_LOCATION);
		sourceListWidth = new Integer(DEFAULT_SOURCE_LIST_WIDTH);
		editorWidth = new Integer(DEFAULT_EDITOR_PANEL_WIDTH);
	}

	public String getSelectedSnippet() {
		return selectedSnippets;
	}

	public void setSelectedSnippet(String selectedSnippet) {
		if (selectedSnippet != null && selectedSnippet.equals("null")) {
			selectedSnippet = null;
		}

		this.selectedSnippets = selectedSnippet;
	}

	public Dimension getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Dimension windowSize) {
		this.windowSize = windowSize;
	}

	public Point getWindowLocation() {
		return windowLocation;
	}

	public void setWindowLocation(Point windowLocation) {
		this.windowLocation = windowLocation;
	}

	public Integer getSourceListWidth() {
		return sourceListWidth;
	}

	public void setSourceListWidth(Integer sourceListWidth) {
		this.sourceListWidth = sourceListWidth;
	}

	public Integer getEditorWidth() {
		return editorWidth;
	}

	public void setEditorWidth(Integer editorWidth) {
		this.editorWidth = editorWidth;
	}

	public String getDatabasePath() {
		if (databasePath != null && !databasePath.endsWith(File.separator)) {
			databasePath += File.separator;
		}

		return databasePath;
	}

	public void setDatabasePath(String databasePath) {
		this.databasePath = databasePath;
	}

	public String getPreferencesPath() {
		return preferencesPath;
	}

	public void setPreferencesPath(String preferencesPath) {
		this.preferencesPath = preferencesPath;
	}

	public boolean isSearchInNameEnabled() {
		return searchInNameEnabled;
	}

	public void setSearchInNameEnabled(boolean searchInNameEnabled) {
		this.searchInNameEnabled = searchInNameEnabled;
	}

	public boolean isSearchInTagsEnabled() {
		return searchInTagsEnabled;
	}

	public void setSearchInTagsEnabled(boolean searchInTagsEnabled) {
		this.searchInTagsEnabled = searchInTagsEnabled;
	}

	public boolean isSearchInCodeEnabled() {
		return searchInCodeEnabled;
	}

	public void setSearchInCodeEnabled(boolean searchInCodeEnabled) {
		this.searchInCodeEnabled = searchInCodeEnabled;
	}

	public boolean isSearchInCommentEnabled() {
		return searchInCommentEnabled;
	}

	public void setSearchInCommentEnabled(boolean searchInCommentEnabled) {
		this.searchInCommentEnabled = searchInCommentEnabled;
	}

	public boolean isSearchCaseSensitive() {
		return searchCaseSensitive;
	}

	public void setSearchCaseSensitive(boolean searchCaseSensitive) {
		this.searchCaseSensitive = searchCaseSensitive;
	}

	public boolean isLineNumbersEnabled() {
		return lineNumbersEnabled;
	}

	public void setLineNumbersEnabled(boolean showLineNumbersEnabled) {
		this.lineNumbersEnabled = showLineNumbersEnabled;
	}

	public boolean isCommentPanelVisible() {
		return commentPanelVisible;
	}

	public void setCommentPanelVisible(boolean commentPanelVisible) {
		this.commentPanelVisible = commentPanelVisible;
	}

	public String getUserHome() {
		return FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
	}

	/**
	 * Returns the instance of the class.
	 * 
	 * @return the instance of the class
	 */
	public static ApplicationSettings getInstance() {
		return settings;
	}

	public void setAutoHideCommentEnabled(boolean autoHideCommentEnabled) {
		this.autoHideCommentEnabled = autoHideCommentEnabled;
	}

	public boolean isAutoHideCommentEnabled() {
		return autoHideCommentEnabled;
	}

}
