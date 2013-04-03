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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import jcodecollector.util.GeneralInfo;

public class ApplicationSettingsManager {

	public static void readApplicationSettings() {
		readApplicationSettings(new File(ApplicationSettings.PREFERENCES_PATH_DEFAULT));
	}

	public static void saveApplicationSettings() { 
		saveApplicationSettings(new File(ApplicationSettings.PREFERENCES_PATH_DEFAULT));
	}

	private static void readApplicationSettings(File file) {
		Properties properties = new Properties();
		ApplicationSettings settings = ApplicationSettings.getInstance();

		try {
			properties.load(new FileReader(file));
		} catch (IOException ex) {
			System.err.println("error loading preferences - using default");
		}

		// recupero il path del database
		settings.setDatabasePath(properties.getProperty("database_path", ApplicationSettings.DATABASE_PATH_DEFAULT));

		// recupero lo snippet selezionato
		settings.setSelectedSnippet(properties.getProperty("selected_snippet", "null"));

		// recupero le dimensioni della finestra
		String windowWidth = properties.getProperty("window_width", "" + ApplicationSettings.DEFAULT_WINDOW_SIZE.width);
		String windowHeight = properties.getProperty("window_height", "" + ApplicationSettings.DEFAULT_WINDOW_SIZE.height);

		try {
			settings.setWindowSize(new Dimension(Integer.parseInt(windowWidth), Integer.parseInt(windowHeight)));
		} catch (NumberFormatException ex) {
			settings.setWindowSize(ApplicationSettings.DEFAULT_WINDOW_SIZE);
		}

		// recupero la posizione della finestra
		String windowX = properties.getProperty("window_x", "" + ApplicationSettings.DEFAULT_WINDOW_LOCATION.x);
		String windowY = properties.getProperty("window_y", "" + ApplicationSettings.DEFAULT_WINDOW_LOCATION.x);

		try {
			settings.setWindowLocation(new Point(Integer.parseInt(windowX), Integer.parseInt(windowY)));
		} catch (NumberFormatException ex) {
			settings.setWindowLocation(ApplicationSettings.DEFAULT_WINDOW_LOCATION);
		}

		try {
			// recupero la larghezza del source list
			settings.setSourceListWidth(Integer.parseInt(properties.getProperty("source_list_width")));
		} catch (NumberFormatException ex) {
			settings.setSourceListWidth(ApplicationSettings.DEFAULT_SOURCE_LIST_WIDTH);
		}

		try {
			// recupero la larghezza del source list
			settings.setEditorWidth(Integer.parseInt(properties.getProperty("editor_width")));
		} catch (NumberFormatException ex) {
			settings.setEditorWidth(ApplicationSettings.DEFAULT_EDITOR_PANEL_WIDTH);
		}

		// recupero i vari valori booleani 
		settings.setSearchInNameEnabled(Boolean.parseBoolean(properties.getProperty("search_name", "true")));
		settings.setSearchInTagsEnabled(Boolean.parseBoolean(properties.getProperty("search_tags", "true")));
		settings.setSearchInCodeEnabled(Boolean.parseBoolean(properties.getProperty("search_code", "true")));
		settings.setSearchInCommentEnabled(Boolean.parseBoolean(properties.getProperty("search_comment", "false")));
		settings.setSearchCaseSensitive(Boolean.parseBoolean(properties.getProperty("search_case_sensitive", "false")));
		settings.setLineNumbersEnabled(Boolean.parseBoolean(properties.getProperty("show_line_numbers", "true")));
		settings.setCommentPanelVisible(Boolean.parseBoolean(properties.getProperty("show_comment_panel", "true")));
		settings.setAutoHideCommentEnabled(Boolean.parseBoolean(properties.getProperty("auto_hide_comment_panel", "false")));
	}

	private static void saveApplicationSettings(File file) {
		Properties properties = new Properties();
		ApplicationSettings settings = ApplicationSettings.getInstance();

		properties.put("database_path", settings.getDatabasePath());
		properties.put("selected_snippet", settings.getSelectedSnippet() == null ? "null" : settings.getSelectedSnippet());
		properties.put("window_width", new Integer(settings.getWindowSize().width).toString());
		properties.put("window_height", new Integer(settings.getWindowSize().height).toString());
		properties.put("window_x", new Integer(settings.getWindowLocation().x).toString());
		properties.put("window_y", new Integer(settings.getWindowLocation().y).toString());
		properties.put("source_list_width", new Integer(settings.getSourceListWidth()).toString());
		properties.put("editor_width", new Integer(settings.getEditorWidth()).toString());

		properties.put("search_name", new Boolean(settings.isSearchInNameEnabled()).toString());
		properties.put("search_tags", new Boolean(settings.isSearchInTagsEnabled()).toString());
		properties.put("search_code", new Boolean(settings.isSearchInCodeEnabled()).toString());
		properties.put("search_comment", new Boolean(settings.isSearchInCommentEnabled()).toString());
		properties.put("search_case_sensitive", new Boolean(settings.isSearchCaseSensitive()).toString());
		properties.put("show_line_numbers", new Boolean(settings.isLineNumbersEnabled()).toString());
		properties.put("show_comment_panel", new Boolean(settings.isCommentPanelVisible()).toString());
		properties.put("auto_hide_comment_panel", new Boolean(settings.isAutoHideCommentEnabled()).toString());

		try {
			properties.store(new FileWriter(file), "jCodeCollector " + GeneralInfo.APPLICATION_VERSION);
		} catch (IOException e) {
			System.err.println("cannot save data"); // OMFG!
		}
	}

}
