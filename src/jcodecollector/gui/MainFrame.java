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
package jcodecollector.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;

import jcodecollector.MacUtilities;
import jcodecollector.State;
import jcodecollector.common.bean.Snippet;
import jcodecollector.common.bean.Syntax;
import jcodecollector.data.Controller;
import jcodecollector.data.DBMS;
import jcodecollector.data.SearchFilter;
import jcodecollector.data.settings.ApplicationSettings;
import jcodecollector.data.settings.ApplicationSettingsManager;
import jcodecollector.io.FileManager;
import jcodecollector.io.PackageManager;
import jcodecollector.listener.CategoryListener;
import jcodecollector.listener.CountListener;
import jcodecollector.listener.MenuListener;
import jcodecollector.listener.SearchListener;
import jcodecollector.listener.SnippetListener;
import jcodecollector.listener.WindowListener;
import jcodecollector.util.ApplicationConstants;
import jcodecollector.util.GeneralInfo;
import jcodecollector.util.OS;
import jcodecollector.util.Utility;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacIcons;
import com.explodingpixels.macwidgets.MacUtils;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListContextMenuProvider;
import com.explodingpixels.macwidgets.SourceListControlBar;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModelListener;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.explodingpixels.macwidgets.UnifiedToolBar;

/**
 * La finestra principale dell'applicazione.
 * 
 * @author Alessandro Cocco *
 */
public class MainFrame extends JFrame implements CountListener, SnippetListener, CategoryListener, WindowListener, SearchListener, MenuListener {

    /** Il componente che mostra graficamente il contenuto del database. */
    public SourceList sourceList;

    /** Il pannello splittato. */
    private JSplitPane split = new JSplitPane();

    /** Il pannello contenente l'editor degli snippet. */
    public MyDialog mainPanel = new MyDialog(this);

    /** L'etichetta con le statistiche sul database. */
    private JLabel statusLabel;

    /** La casella di testo usata per inserire le parole chiave della ricerca. */
    private JTextField searchTextField;

    /** Il pulsante di reset della ricerca (per Windows e Linux). */
    private JButton turnOffButton;

    /** Il pulsante per nasconde il pannello della ricerca */
    private JButton hideButton;

    /** La finestra di About. */
    private AboutWindow aboutWindow = new AboutWindow(this);

    /**
     * Il 'controller' MVC, si occupa di dare i dati all'interfaccia,
     * indipendentemente dalla provenienza degli stessi (database o ricerca).
     */
    private Controller controller = Controller.getInstance();

    /** Il riferimento allo stato dell'applicazione. */
    private State state = State.getInstance();

    private JPanel sourcePanel;

    public MainFrame() {
        setTitle(GeneralInfo.APPLICATION_NAME);
        if (!OS.isMacOSX()) {
            setTitle(GeneralInfo.APPLICATION_NAME + " - " + new File(ApplicationSettings.getInstance().getDatabasePath()).getAbsolutePath());
        }

        if (OS.isMacOSX()) {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            MacUtils.makeWindowLeopardStyle(getRootPane());
        } else {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    prepareAndSaveSettings();
                    System.exit(0);
                }
            });
        }

        setSize(ApplicationSettings.getInstance().getWindowSize());
        setMinimumSize(OS.isMacOSX() ? new Dimension(750, 393) : new Dimension(750, 383));

        initSourceList();
        initSearchComponents();

        sourcePanel = new JPanel(new BorderLayout());
        sourcePanel.add(sourceList.getComponent(), BorderLayout.CENTER);

        split.setBorder(null);
        split.setDividerSize(1);
        split.setContinuousLayout(true);
        split.setDividerLocation(ApplicationSettings.getInstance().getSourceListWidth());
        split.setLeftComponent(sourcePanel);
        split.getLeftComponent().setMinimumSize(new Dimension(250, 50));
        split.setRightComponent(mainPanel.getContentPane());
        setPreferredSize(ApplicationSettings.getInstance().getWindowSize());

        JButton newSnippet = new JButton("New Snippet");
        newSnippet.setFocusable(false);
        newSnippet.putClientProperty("JButton.buttonType", "textured");
        newSnippet.addActionListener(NEW_EMPTY_SNIPPET_ACTION);

        if (OS.isMacOSX()) {
            statusLabel = MacWidgetFactory.makeEmphasizedLabel(new JLabel("", JLabel.CENTER));
            // statusLabel.setFont(statusLabel.getFont().deriveFont(statusLabel.getFont().getSize()
            // - 1.8f));
        } else {
            statusLabel = new JLabel("", JLabel.CENTER);
        }

        UnifiedToolBar unifiedToolBar = new UnifiedToolBar();

        // workaround
        Border currentToolBarBorder = unifiedToolBar.getComponent().getBorder();
        Border newToolBarBorder = BorderFactory.createCompoundBorder(new EmptyBorder(-5, 0, 0, 0), currentToolBarBorder);
        unifiedToolBar.getComponent().setBorder(newToolBarBorder);
        // end workaround

        unifiedToolBar.addComponentToLeft(newSnippet);
        unifiedToolBar.disableBackgroundPainter();
        unifiedToolBar.installWindowDraggerOnWindow(this);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        if (OS.isMacOSX()) {
            unifiedToolBar.addComponentToRight(searchTextField);
            container.add(unifiedToolBar.getComponent(), BorderLayout.NORTH);
        }
        container.add(split, BorderLayout.CENTER);

        if (OS.isMacOSX()) {
            BottomBar bottomBar = new BottomBar(BottomBarSize.LARGE);
            bottomBar.addComponentToCenter(statusLabel);
            bottomBar.installWindowDraggerOnWindow(this);
            container.add(bottomBar.getComponent(), BorderLayout.SOUTH);
        } else {
            JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
            searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            searchPanel.add(searchTextField, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
            controlPanel.setBorder(new EmptyBorder(5, 2, 5, 5));
            if (!OS.isMacOSX()) {
                controlPanel.add(turnOffButton);
            }

            hidePanel.setLayout(new BorderLayout());
            hidePanel.setBorder(new CompoundBorder(new EmptyBorder(-2, -2, -1, -2), new EtchedBorder()));
            hidePanel.add(searchPanel, BorderLayout.CENTER);
            if (!OS.isMacOSX()) {
                JLabel searchLabel = new JLabel("<html>Press <b>ENTER</b> to start search, <b>CTRL+K</b> to cancel</html>");
                searchLabel.setBorder(new EmptyBorder(5, 5, 0, 5));
                hidePanel.add(searchLabel, BorderLayout.NORTH);
            }
        }

        pack();

        // creo e aggiungo la barra menu
        buildMenuBar();

        try {
            setLocation(ApplicationSettings.getInstance().getWindowLocation());
        } catch (Exception ex) {
            setLocationRelativeTo(null);
        }

        // registro i vari ascoltatori personalizzati
        state.addSnippetListener(this);
        state.addSnippetListener(mainPanel);

        state.addCategoryListener(this);
        state.addCategoryListener(mainPanel);
        state.addCountListener(this);

        state.addMenuListener(this);
        state.addWindowListener(this);
        state.addSearchListener(this);

        // effettuo subito alcuni aggiornamenti
        state.countUpdate();
        state.updateMenu(true, true);
        state.updateLineNumbers(true);

        if (OS.isMacOSX()) {
            initMacUtilities();
        }
    }

    JPanel hidePanel = new JPanel();

    private void initMacUtilities() {
        new MacUtilities().installMacUtilities(this);
        // try {
        // ClassLoader loader = ClassLoader.getSystemClassLoader();
        // Class<?> clazz = loader.loadClass("jcodecollector.MacUtilities");
        // Object macUtilitiesInstance = clazz.newInstance();
        // clazz.getMethod("installMacUtilities",
        // MainFrame.class).invoke(macUtilitiesInstance, this);
        // } catch (Exception ex) {
        // System.err.println("cannot load MacUtilities class :-/");
        // ex.printStackTrace();
        // }
    }

    /**
     * Seleziona lo snippet che era "corrente" al momento della chiusura
     * dell'applicazione
     */
    public void restoreSelectedSnippet() {
        String selectedSnippet = ApplicationSettings.getInstance().getSelectedSnippet();
        if (selectedSnippet != null) {
            manuallySelectItem(selectedSnippet);
        }
    }

    private void initSourceList() {
        sourceList = new SourceList();
        // TODO non fare ricreare tutto ogni volta
        sourceList.setSourceListContextMenuProvider(new SourceListContextMenuProvider() {
            public JPopupMenu createContextMenu(SourceListCategory category) {
                CATEGORY_POPUP_MANAGER_ACTION.setText(category.getText());

                JMenuItem renameItem = new JMenuItem(CATEGORY_POPUP_MANAGER_ACTION);
                renameItem.setText("Rename...");

                JMenuItem removeItem = new JMenuItem(CATEGORY_POPUP_MANAGER_ACTION);
                removeItem.setText("Remove");

                JMenuItem createPackageItem = new JMenuItem(CATEGORY_POPUP_MANAGER_ACTION);
                createPackageItem.setText("Export...");

                JMenu moveAllItem = new JMenu("Merge In...");

                JMenuItem separator = new JMenuItem("");
                separator.setEnabled(false);

                JMenu syntaxItem = new JMenu(CATEGORY_POPUP_MANAGER_ACTION);
                syntaxItem.setText("Set Syntax...");
                for (String s : mainPanel.getSyntaxes()) {
                    final JMenuItem item = new JMenuItem(s);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (controller.updateSyntax(item.getText(), CATEGORY_POPUP_MANAGER_ACTION.getText(), !state.isSnippetSaved() ? state.getNameOfSelectedSnippet() : null)) {
                                state.syntaxRenamed(item.getText(), CATEGORY_POPUP_MANAGER_ACTION.getText());
                            }
                        }
                    });
                    syntaxItem.add(item);
                }

                ArrayList<String> categories = controller.getAllCategories();
                categories.remove(category.getText());

                JPopupMenu adapter = new JPopupMenu();
                adapter.add(renameItem);
                adapter.add(removeItem);
                adapter.add(syntaxItem);
                adapter.add(createPackageItem);
                adapter.addSeparator();
                adapter.add(moveAllItem);
                for (String s : categories) {
                    JMenuItem temp = new JMenuItem(CATEGORY_POPUP_MANAGER_ACTION);
                    temp.setText(s);
                    moveAllItem.add(temp);
                }

                return adapter;
            }

            public JPopupMenu createContextMenu(SourceListItem item) {
                ITEM_POPUP_MANAGER_ACTION.setText(item.getText());
                JMenu moveItem = new JMenu("Move To...");

                JMenuItem showInEditorItem = new JMenuItem(ITEM_POPUP_MANAGER_ACTION);
                showInEditorItem.setText("Open In Editor...");

                JMenuItem showInOtherWindowItem = new JMenuItem(ITEM_POPUP_MANAGER_ACTION);
                showInOtherWindowItem.setText("Open In A New Window...");

                JMenuItem removeItem = new JMenuItem(ITEM_POPUP_MANAGER_ACTION);
                removeItem.setText("Remove");

                JMenuItem separatorItem = new JMenuItem("");
                separatorItem.setEnabled(false);

                JMenuItem newCategoryItem = new JMenuItem(ITEM_POPUP_MANAGER_ACTION);
                newCategoryItem.setText("New Category...");

                ArrayList<String> categories = controller.getAllCategories();
                categories.remove(controller.getCategoryOf(item.getText()));

                moveItem.add(newCategoryItem);
                moveItem.addSeparator();
                for (String category : categories) {
                    moveItem.add(new JMenuItem(ITEM_POPUP_MANAGER_ACTION)).setText(category);
                }

                JPopupMenu popupMenuAdapter = new JPopupMenu();
                popupMenuAdapter.add(moveItem);
                popupMenuAdapter.addSeparator();
                popupMenuAdapter.add(removeItem);

                return popupMenuAdapter;
            }

            public JPopupMenu createContextMenu() {
                return null;
            }
        });

        SourceListControlBar controlBar = new SourceListControlBar();
        controlBar.installDraggableWidgetOnSplitPane(split);
        controlBar.createAndAddButton(MacIcons.PLUS, NEW_EMPTY_SNIPPET_ACTION);
        controlBar.createAndAddButton(MacIcons.MINUS, REMOVE_SNIPPET_ACTION);

        sourceList.installSourceListControlBar(controlBar);
        sourceList.addSourceListSelectionListener(new SourceListSelectionListener() {
            public void sourceListItemSelected(SourceListItem item) {
                if (item == null) {
                    state.setNameOfSelectedCategory(null);
                    state.setNameOfSelectedSnippet(null);
                    state.setPreviousSnippet(null);
                    state.updateSnippetStatus(false, false, false);

                    mainPanel.clear();
                } else {
                    // leggo lo snippet corrispondente al nome selezionato
                    Snippet snippet = controller.getSnippet(item.getText().trim());

                    // lo imposto come lo snippet di lavoro
                    state.setPreviousSnippet(snippet);
                    state.setNameOfSelectedCategory(snippet.getCategory());
                    state.setNameOfSelectedSnippet(snippet.getName());

                    // aggiorno l'editor
                    mainPanel.setSnippet(snippet);

                    // scrolla anche quando ci si sposta tra gli snippet
                    // usando le frecce direzionali
                    sourceList.scrollItemToVisible(item);

                    state.updateSnippetStatus(true, true, snippet.isLocked());
                    state.updateMenu(true, false);
                    state.updateWindowStatus(false);

                    checkAutoHideCommentPanel(snippet, false);
                }
            }
        });

        sourceList.getModel().addSourceListModelListener(new SourceListModelListener() {
            public void categoryAdded(SourceListCategory sourceListCategory, int i) {
                state.countUpdate();
            }

            public void itemChanged(SourceListItem item) {
            }

            public void categoryRemoved(SourceListCategory sourceListCategory) {
                state.countUpdate();
            }

            public void itemAddedToCategory(SourceListItem sourceListItem, SourceListCategory sourceListCategory, int i) {
                state.countUpdate();
            }

            public void itemAddedToItem(SourceListItem sourceListItem, SourceListItem parentSourceListItem, int i) {
                state.countUpdate();
            }

            public void itemRemovedFromCategory(SourceListItem sourceListItem, SourceListCategory sourceListCategory) {
                state.countUpdate();
            }

            public void itemRemovedFromItem(SourceListItem sourceListItem, SourceListItem parentsoSourceListItem) {
                state.countUpdate();
            }
        });
    }

    /**
     * <code>true</code> indica che lo snippet precedentemente selezionato era
     * senza commento.
     */
    private boolean previousCommentWasEmpty;

    private void checkAutoHideCommentPanel(Snippet snippet, boolean menuItemClicked) {
        int length = snippet != null ? snippet.getComment().trim().length() : 0;
        if (autoHideCommentPanelMenuItem.isSelected()) {
            if (previousCommentWasEmpty && length == 0) {
                /* Commento precedente vuoto e attuale vuoto: nascondo il
                 * pannello. Questo perche' puo' capitare che l'utente commenti
                 * uno snippet senza commento, per poi spostarsi verso un altro
                 * snippet senza commento. In questo caso devo nascondere il
                 * pannello. */
                hideCommentPanel();
            } else if (!previousCommentWasEmpty && length == 0) {
                // commento precedente non vuoto e attuale vuoto: nascondo il
                // pannello
                hideCommentPanel();
            } else if (previousCommentWasEmpty && length > 0) {
                // commento precedente vuoto e attuale non vuoto: mostro il
                // pannello
                showCommentPanel();
            } else {
                // precedente non vuoto, attuale non vuoto: non faccio nulla
            }
        }

        /* Arrivo qui in seguito al click diretto sul menu. Se l'auto hide e'
         * stato attivato e mi trovo in uno snippet senza commento nascondo il
         * pannello. Se mi trovo in uno snippet con commento e il pannello e'
         * nascosto lo rendo visibile. */
        if (menuItemClicked) {
            if (autoHideCommentPanelMenuItem.isSelected()) {
                if (length == 0 && showCommentPanelMenuItem.isSelected()) {
                    hideCommentPanel();
                } else if (length > 0 && !showCommentPanelMenuItem.isSelected()) {
                    showCommentPanel();
                }
            }
        }

        previousCommentWasEmpty = snippet.getComment().trim().length() == 0;
    }

    void showCommentPanel() {
        if (showCommentPanelMenuItem.isSelected()) {
            // gia' visibile, no faccio nulla
            return;
        } else {
            showCommentPanelMenuItem.doClick();
        }
    }

    void hideCommentPanel() {
        if (showCommentPanelMenuItem.isSelected()) {
            showCommentPanelMenuItem.doClick();
        } else {
            // gia' visibile, no faccio nulla
            return;
        }
    }

    private void initSearchComponents() {
        searchTextField = new PromptTextField("", OS.isMacOSX() ? "Search" : "", 15);
        searchTextField.addActionListener(START_SEARCH_ACTION);
        searchTextField.putClientProperty("JTextField.variant", "search");
        searchTextField.putClientProperty("JTextField.Search.CancelAction", CANCEL_BUTTON_SEARCH_ACTION);

        if (OS.isMacOSX()) {
            getRootPane().registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    searchTextField.requestFocusInWindow();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), JComponent.WHEN_IN_FOCUSED_WINDOW);
        }

        getRootPane().registerKeyboardAction(CANCEL_BUTTON_SEARCH_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_K, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), JComponent.WHEN_IN_FOCUSED_WINDOW);

        if (!OS.isMacOSX()) {
            getRootPane().registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    searchTextField.requestFocusInWindow();
                    searchTextField.selectAll();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), JComponent.WHEN_IN_FOCUSED_WINDOW);
        }

        turnOffButton = new JButton("Clear");
        turnOffButton.addActionListener(CANCEL_BUTTON_SEARCH_ACTION);
        turnOffButton.setPreferredSize(new Dimension(mainPanel.saveButton.getPreferredSize().width + 10, turnOffButton.getPreferredSize().height));

        hideButton = new JButton("Hide");
        hideButton.addActionListener(HIDE_SEARCH_PANEL_ACTION);
    }

    /** Raccoglie e salva i settaggi del programma. */
    public void prepareAndSaveSettings() {
        ApplicationSettings.getInstance().setWindowSize(getSize());
        ApplicationSettings.getInstance().setWindowLocation(getLocation());
        ApplicationSettings.getInstance().setSourceListWidth(split.getDividerLocation());
        // ApplicationSettings.getInstance().setEditorWidth(mainPanel.split.getDividerLocation());
        ApplicationSettings.getInstance().setLineNumbersEnabled(mainPanel.scrollPanel.getLineNumbersEnabled());
        ApplicationSettings.getInstance().setCommentPanelVisible(showCommentPanelMenuItem.isSelected());
        ApplicationSettings.getInstance().setSelectedSnippet(state.getNameOfSelectedSnippet());
        ApplicationSettings.getInstance().setAutoHideCommentEnabled(autoHideCommentPanelMenuItem.isSelected());

        ApplicationSettingsManager.saveApplicationSettings();
    }

    /** Azione di creazione di un nuovo snippet. */
    final ActionListener NEW_EMPTY_SNIPPET_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            sourceList.clearSelection();
            mainPanel.createNewSnippet();

            state.setPreviousSnippet(null);
            state.updateSnippetStatus(false, false, false);

            state.updateWindowStatus(true);
            showCommentPanel();
        }
    };

    /** Azione di creazione di un nuovo snippet a partire dalla clipboard. */
    final ActionListener NEW_SNIPPET_FROM_CLIPBOARD_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            sourceList.clearSelection();
            mainPanel.pasteFromClipboard();

            state.setPreviousSnippet(null);
            state.updateSnippetStatus(false, false, false);

            state.updateWindowStatus(true);
            showCommentPanel();
        }
    };

    /** Azione di rimozione dello snippet selezionato. */
    final ActionListener REMOVE_SNIPPET_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (sourceList.getSelectedItem() == null) {
                return;
            }

            String name = sourceList.getSelectedItem().getText();
            Snippet snippet = controller.getSnippet(name);
            String category = snippet.getCategory();

            if (category == null) {
                return;
            }

            // rimuovo lo snippet
            if (controller.removeSnippet(name)) {
                actionsAfterRemovingSnippet(snippet);
            }
        }
    };

    private void actionsAfterRemovingSnippet(Snippet snippet) {
        state.snippetRemoved(snippet);
        state.updateSnippetStatus(false, false, false);
        state.updateWindowStatus(false);
        state.updateMenu(true, true);
    }

    final ActionListener COPY_TO_CLIPBOARD_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            mainPanel.copyToClipboard();
        }
    };

    final ActionListener LOCK_SNIPPET_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            mainPanel.lockButton.doClick();
        }
    };

    /** Effettua il salvataggio dello snippet corrente. */
    final ActionListener SAVE_SNIPPET_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Snippet newSnippet = mainPanel.getSnippet();
            Snippet oldSnippet = state.getPreviousSnippet();

            if (newSnippet == null) {
                System.out.println("new snippet is null");
                return;
            }

            // lo snippet precedente e' null: si tratta di un nuovo inserimento
            if (oldSnippet == null) {
                if (controller.insertNewSnippet(newSnippet)) {
                    insertSnippet(newSnippet);
                } else {
                    error(newSnippet.getName());
                    return;
                }
            } else {
                // se le modifiche vengono effettuate correttamente aggiorno
                // anche il SourceList
                if (controller.updateSnippet(oldSnippet, newSnippet)) {
                    updateSnippetInSourceList(oldSnippet, newSnippet);
                } else {
                    error(newSnippet.getName());
                    return;
                }
            }

            state.updateSnippetStatus(true, true, false);
            state.updateWindowStatus(false);
            state.updateMenu(true, true);

            checkAutoHideCommentPanel(newSnippet, false);
        }

        /**
         * Visualizza un messaggio di errore.
         * 
         * @param name Lo snippet che ha causato l'errore.
         */
        private void error(String name) {
            JOptionPane.showMessageDialog(MainFrame.this, "<html><b>A snippet named \"" + name + "\" already exists!</b><br><br><font size=3>" + "The name of a snippet is a <i>primary key</i> " + "and <b>must</b> be unique.</font></html>",
                    "Houston, we have a problem...", JOptionPane.ERROR_MESSAGE);
        }
    };

    final ActionListener RELOAD_SOURCE_LIST_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            mainPanel.clear();
            reloadSourceList();
        }
    };

    final ActionListener SHOW_SEARCH_PANEL_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (!OS.isMacOSX()) {
                if (hidePanel.getParent() == null) {
                    sourcePanel.add(hidePanel, BorderLayout.NORTH);
                } else {
                    sourcePanel.remove(hidePanel);
                }
                sourcePanel.validate();
            }

            updateMenu(true, false);
            searchTextField.requestFocusInWindow();
        }
    };

    final ActionListener CANCEL_BUTTON_SEARCH_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            searchTextField.setText("");
            if (state.isSearchActive()) {
                searchEnabledMenuItem.setEnabled(false);
                state.updateSearch(false);
            }
            updateMenu(true, false);
        }
    };

    final ActionListener HIDE_SEARCH_PANEL_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (!OS.isMacOSX() && hidePanel.getParent() != null) {
                getContentPane().remove(hidePanel);
                getContentPane().validate();
            }
            updateMenu(true, false);
        }
    };

    final ActionListener START_SEARCH_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // interrompo se l'utente ha inserito una stringa vuota
            String text = searchTextField.getText().trim();
            if (text.length() == 0) {
                if (e != null) {
                    Toolkit.getDefaultToolkit().beep();
                }

                // se la ricerca e' attiva la disattivo
                if (state.isSearchActive()) {
                    CANCEL_BUTTON_SEARCH_ACTION.actionPerformed(e);
                }

                return;
            }

            // la stringa inserita *dovrebbe* essere valida: separo le
            // varie parole chiave e tolgo gli spazi prima e dopo il
            // testo, poi effettuo la ricerca
            String[] keywords = text.split(",");
            for (int i = 0; i < keywords.length; i++) {
                keywords[i] = keywords[i].trim();
            }

            // avvio la ricerca
            state.startSearch();

            // risultato della ricerca
            TreeMap<String, TreeSet<String>> data = DBMS.getInstance().search(keywords, controller.getValue());
            controller.setData(data);

            // la ricerca non ha dato risultati: emetto un effetto
            // sonoro, fermo la ricerca e restituisco il controllo
            if (data.size() == 0) {
                Toolkit.getDefaultToolkit().beep();
                state.stopSearch();
            }

            // attivo la possibilita' di disattivare la ricerca
            searchEnabledMenuItem.setEnabled(true);

            // la ricerca ha prodotto risultati: lo segnalo ai vari ascoltatori
            state.updateSearch(true);
        }
    };

    final ActionListener IMPORT_PACKAGE_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            importPackage();
        }
    };

    final ActionListener EXPORT_ALL_SNIPPETS_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            exportPackage(null);
        }
    };

    final ActionListener EXPORT_CATEGORY_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            exportPackage(e.getActionCommand());
        }
    };

    final ActionListener MINIMIZE_WINDOW_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (MainFrame.this.getExtendedState() == JFrame.NORMAL) {
                setExtendedState(JFrame.ICONIFIED);
            }
        }
    };

    final ActionListener CLOSE_WINDOW_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            MainFrame.this.setVisible(false);
        }
    };

    final ActionListener ZOOM_WINDOW_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (MainFrame.this.getExtendedState() == JFrame.NORMAL) {
                MainFrame.this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                MainFrame.this.setExtendedState(JFrame.NORMAL);
            }
        }
    };

    final ActionListener CHANGE_LOCATION_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            File newLocation = null;
            File oldLocation = new File(ApplicationSettings.getInstance().getDatabasePath() + "/jCodeCollector");

            if (OS.isMacOSX()) {
                // i file dialog possono accettare solo directory (modifica il
                // comportamento di default)
                System.setProperty("apple.awt.fileDialogForDirectories", "true");

                FileDialog dialog = new FileDialog(MainFrame.this);
                dialog.setTitle("Choose New Location...");
                dialog.setModal(true);
                dialog.setMode(FileDialog.LOAD);
                dialog.setDirectory(ApplicationSettings.getInstance().getUserHome());

                setJMenuBar(new JMenuBar());
                dialog.setVisible(true);
                setJMenuBar(menuBar);

                // ripristina il filtro di default del file dialog
                System.setProperty("apple.awt.fileDialogForDirectories", "false");

                if (dialog.getFile() == null) {
                    return;
                }

                newLocation = new File(dialog.getDirectory() + dialog.getFile() + "/");
            } else {
                JFileChooser chooser = new JFileChooser();
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setDialogTitle("Choose New Location...");
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public String getDescription() {
                        return "Folder Only";
                    }

                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory();
                    }
                });

                if (chooser.showOpenDialog(MainFrame.this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                newLocation = chooser.getSelectedFile();
            }

            // aggiorno i settaggi e forzo il salvataggio su file
            ApplicationSettings.getInstance().setDatabasePath(newLocation.getAbsolutePath());
            ApplicationSettingsManager.saveApplicationSettings();

            // faccio una copia del database nella nuova posizione: se va tutto
            // ok reimposto la connessione verso il nuovo database, cancello
            // quello vecchio
            if (DBMS.getInstance().copyDatabase(newLocation.getAbsolutePath())) {
                if (DBMS.getInstance().resetConnection()) {
                    if (!FileManager.deleteDirectory(oldLocation)) {
                        JOptionPane.showMessageDialog(MainFrame.this, "<html><b>An error occured while moving the database</b>.<br><br>"
                                + "All your data has been copied successfully in the new location but the old database folder cannot be removed.<br>" + "You should manually remove the folder <b>" + oldLocation.getAbsolutePath() + "</b>.</html>", "",
                                JOptionPane.WARNING_MESSAGE);
                    }

                    if (!OS.isMacOSX()) {
                        setTitle(GeneralInfo.APPLICATION_NAME + " - " + ApplicationSettings.getInstance().getDatabasePath());
                    }
                }
            }
        }
    };

    /**
     * Inserisce nel SourceList un nuovo elemento rappresentante lo snippet
     * appena inserito nel database.
     * 
     * @param newSnippet Lo snippet appena inserito nel database.
     */
    private void insertSnippet(Snippet newSnippet) {
        String category = newSnippet.getCategory();
        String name = newSnippet.getName();
        SourceListCategory sourceListCategory = getSourceListCategoryOf(category);

        if (sourceListCategory == null) {
            sourceListCategory = new SourceListCategory(category);
            sourceList.getModel().addCategory(sourceListCategory);
        }

        SourceListItem sourceListItem = new SourceListItem(name);
        sourceList.getModel().addItemToCategory(sourceListItem, sourceListCategory);

        state.setPreviousSnippet(newSnippet);
        sourceList.setSelectedItem(sourceListItem);
        sourceList.scrollItemToVisible(sourceListItem);
    }

    /**
     * Rimuove dal SourceList lo snippet indicato.
     * 
     * @param snippet Lo snippet da rimuovere.
     */
    private void removeSnippetFromSourceList(Snippet snippet) {
        String category = snippet.getCategory();
        String name = snippet.getName();

        SourceListCategory sourceListCategory = getSourceListCategoryOf(category);
        SourceListItem sourceListItem = getSourceListItemOf(name);

        // se nella categoria e' rimasto un unico snippet cancello direttamente
        // tutta la categoria
        if (sourceListCategory.getItemCount() == 1) {
            sourceList.getModel().removeCategory(sourceListCategory);
            return;
        }

        // altrimenti cancello solo lo snippet
        sourceList.getModel().removeItemFromCategory(sourceListItem, sourceListCategory);
    }

    /**
     * Aggiorna lo snippet spostandolo da una categoria all'altra del
     * SourceList.
     * 
     * @param oldSnippet Lo snippet modificato.
     * @param newSnippet Lo snippet privo di modifiche.
     */
    private void updateSnippetInSourceList(Snippet oldSnippet, Snippet newSnippet) {
        String newCategory = newSnippet.getCategory();
        String oldCategory = oldSnippet.getCategory();
        String newName = newSnippet.getName();
        String oldName = oldSnippet.getName();

        // SourceListItem selectedItem = sourceList.getSelectedItem();
        SourceListItem clickedItem = getSourceListItemOf(oldName);

        // se la categoria non cambia passo a controllare i nomi
        if (newCategory.equalsIgnoreCase(oldCategory)) {
            // se i nomi sono diversi tengo quello nuovo, altrimenti non
            // devo fare nulla
            if (!newName.equals(oldName)) {
                clickedItem.setText(newName);
            }
        } else {
            // la vecchia categoria esiste per forza
            SourceListCategory oldSourceListCategory = getSourceListCategoryOf(oldCategory);

            // la nuova categoria potrebbe non esistere, eventualmente
            // la creo al momento e la aggiungo al source list
            SourceListCategory newSourceListCategory = getSourceListCategoryOf(newCategory);
            if (newSourceListCategory == null) {
                newSourceListCategory = new SourceListCategory(newCategory);
                sourceList.getModel().addCategory(newSourceListCategory);
            }

            // se nella vecchia categoria c'e' un solo elemento (quello
            // su cui sto lavorando) la cancello, altrimenti cancello
            // solo l'item
            if (oldSourceListCategory.getItemCount() == 1) {
                sourceList.getModel().removeCategory(oldSourceListCategory);
            } else {
                sourceList.getModel().removeItemFromCategory(clickedItem, oldSourceListCategory);
            }

            // inserico l'item nella giusta posizione
            sourceList.getModel().addItemToCategory(clickedItem, newSourceListCategory);
            clickedItem.setText(newName);
        }

        sourceList.setSelectedItem(clickedItem);
        sourceList.scrollItemToVisible(clickedItem);

        state.setPreviousSnippet(newSnippet);
    }

    /**
     * Permette di rinominare una categoria. Se la nuova categoria non esiste si
     * rinomina in loco altrimenti vengono spostati tutti gli snippet della
     * vecchia in quella nuova (gia' esistente) e si cancella quella di
     * partenza.
     * 
     * @param sourceName Il nome di partenza.
     * @param destinationName Il nome di destinazione.
     */
    private void moveCategoryInSourceList(String sourceName, String destinationName) {
        SourceListCategory oldCategory = getSourceListCategoryOf(sourceName);
        if (sourceName.equalsIgnoreCase(destinationName)) {
            oldCategory.setText(destinationName);
            return;
        }

        SourceListCategory newCategory = getSourceListCategoryOf(destinationName);
        SourceListItem selectedItem = sourceList.getSelectedItem();

        // newCategory e' null --> basta rinominare l'attuale
        if (newCategory == null) {
            oldCategory.setText(destinationName);
        } else {
            // newCategory non e' null -> devo spostare tutti gli item degli
            // snippet nella nuova categoria
            List<SourceListItem> items = oldCategory.getItems();
            Iterator<SourceListItem> iterator = items.iterator();
            while (iterator.hasNext()) {
                SourceListItem item = iterator.next();
                sourceList.getModel().addItemToCategory(item, newCategory);
            }
            sourceList.getModel().removeCategory(oldCategory);
        }

        if (selectedItem != null) {
            sourceList.setSelectedItem(selectedItem);
            sourceList.scrollItemToVisible(selectedItem);
        }
    }

    /**
     * Rimuove la categoria indicata dal SourceList. Il database non viene
     * toccato.
     * 
     * @param name Il nome della categoria da rimuovere dal SourceList.
     */
    private void removeCategoryFromSourceList(String name) {
        SourceListCategory sourceListCategory = getSourceListCategoryOf(name);
        sourceList.getModel().removeCategory(sourceListCategory);
    }

    private abstract class MyAction extends AbstractAction {
        private static final long serialVersionUID = 3994175409007864807L;
        protected String menuItemClicked;
        protected String text;

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    final MyAction ITEM_POPUP_MANAGER_ACTION = new MyAction() {
        private static final long serialVersionUID = 6864009534779492593L;

        public void actionPerformed(ActionEvent e) {
            // leggo la voce di menu cliccata dall'utente
            menuItemClicked = e.getActionCommand();

            // l'utente ha cliccato su "Remove"
            if (menuItemClicked.equals("Remove")) {
                // ottengo lo snippet presente nel punto cui l'utente ha
                // cliccato il tasto destro del mouse
                Snippet snippet = controller.getSnippet(text);

                // rimuove lo snippet dal database e chiama in cascata i vari
                // listener interessati all'evento
                if (controller.removeSnippet(text)) {
                    actionsAfterRemovingSnippet(snippet);
                }

                return;
            }

            // se l'esecuzione arriva qui significa che l'utente ha cliccato su
            // una categoria esistente o sulla voce per crearne una nuova

            // ottengo lo snippet presente nel punto cui l'utente ha cliccato il
            // tasto destro del mouse
            Snippet oldSnippet = controller.getSnippet(text);

            // destinazione dello snippet
            String category = new String();

            // l'utente vuole spostare lo snippet in una nuova categoria quindi
            // gli chiedo di inserirmela
            if (menuItemClicked.equals("New Category...")) {
                category = JOptionPane.showInputDialog(MainFrame.this,
                        "<html><b>Please insert the name of a new category</b>" + "<br><font size=-1>The name must be shorter than " + ApplicationConstants.CATEGORY_LENGTH + " characters.</font></html>", "Move Into A New Category...",
                        JOptionPane.QUESTION_MESSAGE);

                // l'utente ha premuto ESC, ha chiuso la finestra di dialogo
                // oppure ha inserito una stringa vuota
                if (category == null || category.trim().length() == 0) {
                    return;
                }

                // verifico se il nome che ha inserito l'utente e' ammesso
                if (!Utility.nameIsValid(category.trim()) || category.trim().length() > ApplicationConstants.CATEGORY_LENGTH) {
                    String message = String.format("<html>%s is not a valid name!</html>", category.trim());
                    JOptionPane.showMessageDialog(MainFrame.this, message, "", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                // in questo caso ha scelto una delle categorie gia' esistenti
                category = menuItemClicked;
            }

            Snippet newSnippet;

            try {
                newSnippet = (Snippet) oldSnippet.clone();
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
                return;
            }

            newSnippet.setCategory(category);

            if (controller.updateSnippet(oldSnippet, newSnippet)) {
                boolean locked = state.isSnippetLocked();
                updateSnippetInSourceList(oldSnippet, newSnippet);
                state.updateSnippetStatus(true, true, locked);
                state.updateMenu(true, true);
            }
        }
    };

    /**
     * Gestore del menu di popup che si apre al click destro sul nome di una
     * categoria presente nel SourceList, permette di cancellare o rinominare
     * una categoria. In quest'ultimo caso se si inserisce un nome nuovo la
     * categoria viene rinominata in loco altrimenti tutti gli snippet presenti
     * vengono spostati e la categoria corrente cancellata.
     */
    final MyAction CATEGORY_POPUP_MANAGER_ACTION = new MyAction() {
        private static final long serialVersionUID = -8767979402119895694L;

        public void actionPerformed(ActionEvent e) {
            // leggo la voce di menu cliccata dall'utente
            menuItemClicked = e.getActionCommand();

            String newName = new String();

            // l'utente ha fatto click su "Remove"
            if (menuItemClicked.equals("Remove")) {
                // Rimuovo dal database la categoria e tutti i suoi snippet. In
                // caso di successo rimuovo la categoria anche dal SourceList.
                if (controller.removeCategory(text)) {
                    state.categoryRemoved(text);
                    state.updateSnippetStatus(state.isSnippetValidated(), state.isSnippetSaved(), state.isSnippetLocked());
                    state.updateMenu(true, true);
                }

                // restituisce subito il controllo
                return;
            }

            // l'utente ha fatto click su "Export..."
            if (menuItemClicked.equals("Export...")) {
                exportPackage(text);
                return;
            }

            if (menuItemClicked.equals("Set Syntax...")) {
                String[] syntaxes = mainPanel.getSyntaxes();
                syntaxes[0] = "-- no syntax highlighting"; // workaround
                String newSyntax = (String) JOptionPane.showInputDialog(MainFrame.this, "<html><b>Please choose a syntax for snippets in \"" + text + "\".</b><br>"
                        + "<font size=-1>The new syntax will be set to all snippets in the category.</font></html>", "Set A Syntax For All Snippets...", JOptionPane.QUESTION_MESSAGE, null, syntaxes, syntaxes[0]);

                if (newSyntax == null) {
                    return;
                }

                if (newSyntax.equalsIgnoreCase("-- no syntax highlighting")) {
                    newSyntax = "";
                }

                if (controller.updateSyntax(newSyntax, text, !state.isSnippetSaved() ? state.getNameOfSelectedSnippet() : null)) {
                    state.syntaxRenamed(newSyntax, text);
                }

                // restituisce subito il controllo
                return;
            }

            // l'utente ha fatto click su "Rename..."
            if (menuItemClicked.equals("Rename...")) {
                newName = JOptionPane.showInputDialog(MainFrame.this, "<html><b>Please insert a new name for " + text + ".</b><br><font size=-1>The name must be shorter than " + ApplicationConstants.CATEGORY_LENGTH + " characters.</font></html>",
                        "Rename...", JOptionPane.QUESTION_MESSAGE);

                // l'utente ha premuto ESC, ha chiuso la finestra di dialogo
                // oppure ha inserito una stringa vuota
                if (newName == null || newName.trim().length() == 0) {
                    return;
                }

                // verifico se il nome che ha inserito l'utente e' ammesso
                if (!Utility.nameIsValid(newName.trim()) || newName.trim().length() > ApplicationConstants.CATEGORY_LENGTH) {
                    String message = String.format("<html>%s is not a valid name!</html>", newName.trim());
                    JOptionPane.showMessageDialog(MainFrame.this, message, "", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                // l'utente ha fatto click sul nome di una categoria
                newName = menuItemClicked;
            }

            // dopo aver ottenuto il nuovo nome e verificato se e' valido
            // aggiorno il database e il SourceList
            if (controller.renameCategory(text, newName)) {
                state.categoryRenamed(text, newName);
                state.updateSnippetStatus(false, false, false);
                state.updateWindowStatus(false);
                state.updateMenu(true, true);
            }
        }
    };

    private void importPackage() {
        File path;
        if (OS.isMacOSX()) {
            FileDialog dialog = new FileDialog(MainFrame.this);
            dialog.setTitle("Import Snippets From A Package...");
            dialog.setModal(true);
            dialog.setFilenameFilter(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(ApplicationConstants.JCC_EXTENSION);
                }
            });
            dialog.setMode(FileDialog.LOAD);

            setJMenuBar(new JMenuBar());
            dialog.setVisible(true);
            setJMenuBar(menuBar);

            String directory = dialog.getDirectory();
            String file = dialog.getFile();

            if (file == null) {
                return;
            }
            path = new File(directory + file);
        } else {
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setDialogTitle("Import Snippets Package...");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public String getDescription() {
                    return "jCodeCollector Package (.jccp)";
                }

                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(ApplicationConstants.JCC_EXTENSION) | f.isDirectory();
                }
            });

            if (chooser.showOpenDialog(MainFrame.this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            path = chooser.getSelectedFile();
        }

        // leggo il contenuto del file indicato dall'utente
        ArrayList<Snippet> snippets = PackageManager.readPackage(path);

        if (snippets == null) {
            JOptionPane.showMessageDialog(MainFrame.this, "The selected file is not valid.", "Error!", JOptionPane.ERROR_MESSAGE, null);
            return;
        }

        if (snippets.size() == 0) {
            JOptionPane.showMessageDialog(MainFrame.this, "The selected file does not contain any snippets!", "Warning!", JOptionPane.INFORMATION_MESSAGE, null);
            return;
        }

        for (Snippet s : snippets) {
            DBMS.getInstance().insertNewSnippet(s);
        }

        // se va tutto bene ricarico il SourceList
        reloadSourceList();

        // forzo l'aggiornamento del sub-menu con l'elenco delle categorie
        state.updateMenu(true, true);
    }

    public void exportPackage(String name) {
        File path;

        if (OS.isMacOSX()) {
            FileDialog dialog = new FileDialog(MainFrame.this);
            dialog.setTitle("Create A Package For " + (name == null ? "All Snippets" : name) + "...");
            dialog.setModal(true);
            dialog.setFilenameFilter(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return false;
                }
            });
            dialog.setMode(FileDialog.SAVE);

            setJMenuBar(new JMenuBar());
            dialog.setVisible(true);
            setJMenuBar(menuBar);

            String directory = dialog.getDirectory();
            String file = dialog.getFile();

            if (file == null) {
                return;
            }

            path = new File(directory + file);
        } else {
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setDialogTitle("Export Snippets Package...");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public String getDescription() {
                    return "jCodeCollector Package (.jccp)";
                }

                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(ApplicationConstants.JCC_EXTENSION) | f.isDirectory();
                }
            });

            if (chooser.showSaveDialog(MainFrame.this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            path = chooser.getSelectedFile();
        }

        if (!path.getAbsolutePath().endsWith(ApplicationConstants.JCC_EXTENSION)) {
            path = new File(path.getAbsolutePath() + ApplicationConstants.JCC_EXTENSION);
        }

        if (!PackageManager.exportSnippets(path, name)) {
            JOptionPane.showMessageDialog(MainFrame.this, "An error occurred while exporting the snippets. See log file for details.", "", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Svuota il {@link SourceList} */
    private void resetSourceList() {
        SourceListCategory[] categories = sourceList.getModel().getCategories().toArray(new SourceListCategory[] {});
        for (int i = 0; i < categories.length; i++) {
            sourceList.getModel().removeCategory(categories[i]);
        }
    }

    /** Legge il contenuto del database e popola il {@link SourceList}. */
    public void reloadSourceList() {
        // svuoto il SourceList
        resetSourceList();

        // ottengo l'elenco delle categorie
        ArrayList<String> categories = controller.getCategories();

        for (String category : categories) {
            /* Il nome di una categoria e' case-insensitive: verifico se la
             * categoria corrente e' gia' nel SourceList. In caso affermativo
             * la riutilizzo, altrimenti ne creo una nuova. */
            SourceListCategory categoryItem = getSourceListCategoryOf(category);
            if (categoryItem == null) {
                categoryItem = new SourceListCategory(category);
                sourceList.getModel().addCategory(categoryItem);
            }

            // ottengo l'elenco degli snippet e lo ordino alfabeticamente
            ArrayList<String> snippets = controller.getSnippetsName(category);
            Collections.sort(snippets);

            for (String snippet : snippets) {
                SourceListItem snippetItem = new SourceListItem(snippet);
                sourceList.getModel().addItemToCategory(snippetItem, categoryItem);
            }
        }

        state.updateWindowStatus(false);
    }

    /**
     * Restituisce il {@link SourceListCategory} relativo alla categoria
     * indicata.
     * 
     * @param category La categoria di cui si vuole ottenere il
     *        {@link SourceListCategory}.
     * @return il {@link SourceListCategory} della categoria indicata (se
     *         esiste), <code>null</code> altrimenti
     */
    private SourceListCategory getSourceListCategoryOf(String category) {
        if (category == null) {
            throw new IllegalArgumentException("\"category\" must not be null");
        }

        List<SourceListCategory> categories = sourceList.getModel().getCategories();
        Iterator<SourceListCategory> iterator = categories.iterator();
        while (iterator.hasNext()) {
            SourceListCategory sourceListCategory = iterator.next();
            if (category.equalsIgnoreCase(sourceListCategory.getText())) {
                return sourceListCategory;
            }
        }

        return null;
    }

    /**
     * Permette di ottenere il <code>SourceListItem</code> relativo allo snippet
     * indicato.
     * 
     * @param snippet Lo snippet della categoria di cui si vuole ottenere il
     *        <code>SourceListItem</code>.
     * @return il <code>SourceListItem</code> dello snippet indicato (se
     *         esiste), <code>null</code> altrimenti
     */
    private SourceListItem getSourceListItemOf(String snippet) {
        List<SourceListCategory> categories = sourceList.getModel().getCategories();
        Iterator<SourceListCategory> iterator = categories.iterator();

        while (iterator.hasNext()) {
            SourceListCategory sourceListCategory = iterator.next();
            List<SourceListItem> items = sourceListCategory.getItems();
            Iterator<SourceListItem> innerIterator = items.iterator();
            while (innerIterator.hasNext()) {
                SourceListItem sourceListItem = innerIterator.next();
                if (sourceListItem.getText().equals(snippet)) {
                    return sourceListItem;
                }
            }
        }

        return null;
    }

    public AboutWindow getAboutWindow() {
        return aboutWindow;
    }

    public void countUpdate(int categories, int snippets) {
        String temp = categories + " categor";
        temp += (categories == 1) ? "y" : "ies";
        temp += " / " + snippets + " snippet";
        temp += (snippets != 1) ? "s" : "";

        statusLabel.setText(temp);
    }

    public void categoriesUpdated(String selected) {
    }

    public void categoryRemoved(String name) {
        removeCategoryFromSourceList(name);
    }

    public void categoryRenamed(String oldName, String newName) {
        moveCategoryInSourceList(oldName, newName);
    }

    public void snippetEdited(Snippet snippet) {
    }

    /**
     * Rimuove dal SourceList il nodo che rappresenta lo snippet indicato.
     * Questo metodo viene chiamato <b>dopo</b> che il corrispondente snippet e'
     * stato rimosso dal database.
     * 
     * @param snippet Lo snippet rimosso.
     */
    public void snippetRemoved(Snippet snippet) {
        removeSnippetFromSourceList(snippet);
    }

    public void snippetRenamed(String oldName, String newName) {
    }

    public void syntaxUpdated(String syntax, String category) {
    }

    public void syntaxUpdated(ArrayList<Syntax> syntaxes) {
    }

    public void updateSnippetStatus(boolean validated, boolean saved, boolean locked) {
    }

    /**
     * Update window status.
     * 
     * @param documentModified <code>true</code> if there are snippets unsaved,
     *        <code>false</code> otherwise.
     */
    public void updateWindowStatus(boolean documentModified) {
        if (OS.isMacOSX()) {
            getRootPane().putClientProperty("Window.documentModified", documentModified);
        } else {
            String title = getTitle();
            if (documentModified) {
                if (!title.startsWith("*")) {
                    title = "*" + title;
                }
            } else {
                if (title.startsWith("*")) {
                    title = title.substring(1);
                }
            }
            setTitle(title);
        }
    }

    public void updateLineNumbers(boolean enabled) {
        mainPanel.getScrollPanel().setLineNumbersEnabled(!mainPanel.getScrollPanel().getLineNumbersEnabled());
        mainPanel.getScrollPanel().setLineNumbersEnabled(!mainPanel.getScrollPanel().getLineNumbersEnabled());
    }

    /** @see jcodecollector.listener.SearchListener#updateSearch(boolean) */
    public void updateSearch(boolean oldStatus) {
        boolean newStatus = state.isSearchActive();

        if (newStatus) {
            if (!OS.isMacOSX()) {
                if (!getTitle().endsWith(" - search enabled")) {
                    setTitle(getTitle() + " - search enabled");
                }
            }
        } else {
            if (!OS.isMacOSX()) {
                setTitle(getTitle().substring(0, getTitle().lastIndexOf(" - search enabled")));
            }
        }

        // se c'e' discordanza tra lo stato attuale e lo stato precendente della
        // ricerca significa che devo aggiornare il SourceList
        if ((!oldStatus && newStatus) || (oldStatus && !newStatus)) {
            String selected = null;

            if (sourceList.getSelectedItem() != null) {
                selected = sourceList.getSelectedItem().getText();
            }

            if (!newStatus) {
                searchTextField.setText(null);
            }

            reloadSourceList();

            if (oldStatus && !newStatus) {
                manuallySelectItem(selected);
            }
        }
    }

    public void syntaxRenamed(String newName, String category) {
    }

    /**
     * Seleziona il {@link SourceListItem} che contiene il testo indicato.
     * 
     * @param text Il testo contenuto nel {@link SourceListItem} da selezionare.
     */
    private void manuallySelectItem(String text) {
        Iterator<SourceListCategory> iterator = sourceList.getModel().getCategories().iterator();
        while (iterator.hasNext()) {
            Iterator<SourceListItem> innerIterator = iterator.next().getItems().iterator();
            while (innerIterator.hasNext()) {
                SourceListItem item;
                if ((item = innerIterator.next()).getText().equals(text)) {
                    sourceList.setSelectedItem(item);
                    sourceList.scrollItemToVisible(item);
                    return;
                }
            }
        }
    }

    // top level menu
    private JMenu fileMenu;
    private JMenu snippetsMenu;
    private JMenu searchMenu;
    private JMenu viewMenu;
    private JMenu windowMenu;

    // file menu
    private JMenuItem reloadSourceListMenuItem;
    private JMenuItem importFromPackageMenuItem;

    private JMenu exportSubMenu;
    private JMenuItem exportAllMenuItem;
    private JMenu exportSnippetsInCategorySubMenu;
    private JMenuItem changeDirectoryMenuItem;

    // view menu
    private JCheckBoxMenuItem showLineNumbersMenuItem;
    private JCheckBoxMenuItem showCommentPanelMenuItem;
    private JCheckBoxMenuItem autoHideCommentPanelMenuItem;

    // search menu
    private JMenuItem searchInLabelMenuItem;
    private JCheckBoxMenuItem showSearchPanelMenuItem;
    private JCheckBoxMenuItem namesMenuItem;
    private JCheckBoxMenuItem tagsMenuItem;
    private JCheckBoxMenuItem codeMenuItem;
    private JCheckBoxMenuItem commentsMenuItem;
    private JCheckBoxMenuItem caseMenuItem;
    private JMenuItem searchEnabledMenuItem;

    // snippet menu
    private JMenuItem newSnippetMenuItem;
    private JMenuItem newSnippetFromClipboardMenuItem;
    private JMenuItem saveSnippetMenuItem;
    private JMenuItem removeSnippetMenuItem;
    private JMenuItem lockSnippetMenuItem;
    private JMenuItem copyToClipboardMenuItem;

    // window menu
    private JMenuItem minimizeWindowMenuItem;
    private JMenuItem zoomWindowMenuItem;
    private JMenuItem closeWindowMenuItem;

    // menu bar
    private JMenuBar menuBar;

    private void buildMenuBar() {
        final int MENU_SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        // top level menu
        fileMenu = new JMenu("File");
        viewMenu = new JMenu("View");
        snippetsMenu = new JMenu("Snippets");
        windowMenu = new JMenu("Window");

        // file menu
        reloadSourceListMenuItem = new JMenuItem("Reload Snippets");
        reloadSourceListMenuItem.addActionListener(RELOAD_SOURCE_LIST_ACTION);

        importFromPackageMenuItem = new JMenuItem("Import From A Package...");
        importFromPackageMenuItem.addActionListener(IMPORT_PACKAGE_ACTION);
        importFromPackageMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, MENU_SHORTCUT_KEY_MASK | InputEvent.ALT_DOWN_MASK));

        // export sub-menu
        exportSubMenu = new JMenu("Export");

        exportAllMenuItem = new JMenuItem("All Snippets...");
        exportAllMenuItem.addActionListener(EXPORT_ALL_SNIPPETS_ACTION);
        exportAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MENU_SHORTCUT_KEY_MASK | InputEvent.ALT_DOWN_MASK));
        exportSubMenu.add(exportAllMenuItem);

        exportSnippetsInCategorySubMenu = new JMenu("Only Snippets In");
        exportSubMenu.add(exportSnippetsInCategorySubMenu);
        // end export sub-menu

        changeDirectoryMenuItem = new JMenuItem("Change Database Location...");
        changeDirectoryMenuItem.addActionListener(CHANGE_LOCATION_ACTION);

        fileMenu.add(reloadSourceListMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(importFromPackageMenuItem);
        fileMenu.add(exportSubMenu);
        fileMenu.addSeparator();
        fileMenu.add(changeDirectoryMenuItem);

        if (!OS.isMacOSX()) {
            JMenuItem aboutItem = new JMenuItem("About jCodeCollector");
            aboutItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MainFrame.this.aboutWindow.setLocationRelativeTo(null);
                    MainFrame.this.aboutWindow.setVisible(true);
                }
            });

            JMenuItem quitApplication = new JMenuItem("Quit jCodeCollector");
            quitApplication.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
            quitApplication.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    prepareAndSaveSettings();
                    System.exit(0);
                }
            });

            fileMenu.addSeparator();
            fileMenu.add(aboutItem);
            fileMenu.add(quitApplication);
        }
        // end file menu

        // view menu
        showLineNumbersMenuItem = new JCheckBoxMenuItem("Show Line Numbers");
        showLineNumbersMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK));
        showLineNumbersMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.scrollPanel.setLineNumbersEnabled(showLineNumbersMenuItem.isSelected());
            }
        });
        if (ApplicationSettings.getInstance().isLineNumbersEnabled()) {
            showLineNumbersMenuItem.doClick();
        }

        showCommentPanelMenuItem = new JCheckBoxMenuItem("Show Comment");
        showCommentPanelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK));
        showCommentPanelMenuItem.setSelected(ApplicationSettings.getInstance().isCommentPanelVisible());
        showCommentPanelMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (showCommentPanelMenuItem.isSelected()) {
                    // mainPanel.arrowDownSplitButton.doClick();
                    mainPanel.add(mainPanel.southPanel, BorderLayout.SOUTH);
                    mainPanel.mainPanel.setBorder(new EmptyBorder(5, 10, 3, 5));
                    mainPanel.southPanel.setBorder(new EmptyBorder(0, 10, 7, OS.isMacOSX() ? 7 : 5));
                } else {
                    if (mainPanel.southPanel.getParent() != null) {
                        mainPanel.remove(mainPanel.southPanel);
                        mainPanel.mainPanel.setBorder(new EmptyBorder(5, 10, OS.isMacOSX() ? 5 : 7, 5));
                    }
                    // mainPanel.arrowUpSplitButton.doClick();
                }
                mainPanel.getParent().validate();
            }
        });

        if (showCommentPanelMenuItem.isSelected()) {
            mainPanel.add(mainPanel.southPanel, BorderLayout.SOUTH);
            mainPanel.mainPanel.setBorder(new EmptyBorder(5, 10, 3, 5));
            mainPanel.southPanel.setBorder(new EmptyBorder(0, 10, 7, OS.isMacOSX() ? 7 : 5));
            mainPanel.getParent().validate();
        }

        autoHideCommentPanelMenuItem = new JCheckBoxMenuItem("Auto Hide Comment If Empty");
        autoHideCommentPanelMenuItem.setSelected(ApplicationSettings.getInstance().isAutoHideCommentEnabled());
        autoHideCommentPanelMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkAutoHideCommentPanel(DBMS.getInstance().getSnippet(state.getNameOfSelectedSnippet()), true);
            }
        });

        viewMenu.add(showLineNumbersMenuItem);
        viewMenu.add(showCommentPanelMenuItem);
        viewMenu.add(autoHideCommentPanelMenuItem);
        // end view menu

        // search menu
        searchMenu = new JMenu("Search");

        showSearchPanelMenuItem = new JCheckBoxMenuItem("Show Search Panel");
        showSearchPanelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, MENU_SHORTCUT_KEY_MASK));
        showSearchPanelMenuItem.addActionListener(SHOW_SEARCH_PANEL_ACTION);

        searchInLabelMenuItem = new JMenuItem("Search In...");
        searchInLabelMenuItem.setEnabled(false);

        namesMenuItem = new JCheckBoxMenuItem("Names", SearchFilter.getInstance().isSearchInNameEnabled());
        namesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
        tagsMenuItem = new JCheckBoxMenuItem("Tags", SearchFilter.getInstance().isSearchInTagsEnabled());
        tagsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
        codeMenuItem = new JCheckBoxMenuItem("Code", SearchFilter.getInstance().isSearchInCodeEnabled());
        codeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
        commentsMenuItem = new JCheckBoxMenuItem("Comments", SearchFilter.getInstance().isSearchInCommentEnabled());
        commentsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
        caseMenuItem = new JCheckBoxMenuItem("Case Sensitive", SearchFilter.getInstance().isSearchCaseSensitive());
        caseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK));

        final JCheckBoxMenuItem[] searchItems = { namesMenuItem, tagsMenuItem, codeMenuItem, commentsMenuItem };
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int selected = 0; // number of different search enabled
                for (JCheckBoxMenuItem item : searchItems) {
                    selected += item.isSelected() ? 1 : 0;
                }

                JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();

                // almeno un elemento DEVE essere selezionato!
                if (selected < 1) {
                    source.setSelected(true);
                }

                SearchFilter filter = SearchFilter.getInstance();
                if (source == namesMenuItem) {
                    filter.setSearchInNameEnabled(namesMenuItem.isSelected());
                } else if (source == tagsMenuItem) {
                    filter.setSearchInTagsEnabled(tagsMenuItem.isSelected());
                } else if (source == codeMenuItem) {
                    filter.setSearchInCodeEnabled(codeMenuItem.isSelected());
                } else if (source == commentsMenuItem) {
                    filter.setSearchInCommentEnabled(commentsMenuItem.isSelected());
                }

                // ad ogni cambiamento del filtro rifaccio la ricerca
                START_SEARCH_ACTION.actionPerformed(null);
            }
        };

        for (JCheckBoxMenuItem item : searchItems) {
            item.addItemListener(itemListener);
        }

        caseMenuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                SearchFilter.getInstance().setSearchCaseSensitive(caseMenuItem.isSelected());
            }
        });

        searchEnabledMenuItem = new JMenuItem("Clear Search");
        searchEnabledMenuItem.setEnabled(false);
        searchEnabledMenuItem.addActionListener(CANCEL_BUTTON_SEARCH_ACTION);

        if (!OS.isMacOSX()) {
            searchMenu.add(showSearchPanelMenuItem);
            searchMenu.addSeparator();
        }
        searchMenu.add(searchInLabelMenuItem);
        for (JCheckBoxMenuItem item : searchItems) {
            searchMenu.add(item);
        }
        searchMenu.addSeparator();
        searchMenu.add(caseMenuItem);
        // end search menu

        // snippet menu
        newSnippetMenuItem = new JMenuItem("New Empty Snippet...");
        newSnippetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, MENU_SHORTCUT_KEY_MASK));
        newSnippetMenuItem.addActionListener(NEW_EMPTY_SNIPPET_ACTION);

        newSnippetFromClipboardMenuItem = new JMenuItem("New Snippet From Clipboard...");
        newSnippetFromClipboardMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK));
        newSnippetFromClipboardMenuItem.addActionListener(NEW_SNIPPET_FROM_CLIPBOARD_ACTION);

        saveSnippetMenuItem = new JMenuItem("Save");
        saveSnippetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_SHORTCUT_KEY_MASK));
        saveSnippetMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.saveButton.doClick();
            }
        });

        removeSnippetMenuItem = new JMenuItem("Remove");
        removeSnippetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, MENU_SHORTCUT_KEY_MASK));
        removeSnippetMenuItem.addActionListener(REMOVE_SNIPPET_ACTION);

        lockSnippetMenuItem = new JMenuItem("Lock");
        lockSnippetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK));
        lockSnippetMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.lockButton.doClick();
            }
        });
        copyToClipboardMenuItem = new JMenuItem("Copy Code To Clipboard");
        copyToClipboardMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK));
        copyToClipboardMenuItem.addActionListener(COPY_TO_CLIPBOARD_ACTION);

        snippetsMenu.add(newSnippetMenuItem);
        snippetsMenu.add(newSnippetFromClipboardMenuItem);
        snippetsMenu.addSeparator();
        snippetsMenu.add(saveSnippetMenuItem);
        snippetsMenu.add(removeSnippetMenuItem);
        snippetsMenu.addSeparator();
        snippetsMenu.add(lockSnippetMenuItem);
        snippetsMenu.addSeparator();
        snippetsMenu.add(copyToClipboardMenuItem);
        // end snippet menu

        // window menu
        minimizeWindowMenuItem = new JMenuItem("Minimize");
        minimizeWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, MENU_SHORTCUT_KEY_MASK));
        minimizeWindowMenuItem.addActionListener(MINIMIZE_WINDOW_ACTION);

        zoomWindowMenuItem = new JMenuItem("Zoom");
        zoomWindowMenuItem.addActionListener(ZOOM_WINDOW_ACTION);

        closeWindowMenuItem = new JMenuItem("Close Window");
        closeWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, MENU_SHORTCUT_KEY_MASK));
        closeWindowMenuItem.addActionListener(CLOSE_WINDOW_ACTION);

        windowMenu.add(minimizeWindowMenuItem);
        windowMenu.add(zoomWindowMenuItem);
        windowMenu.add(closeWindowMenuItem);
        // end window menu

        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(snippetsMenu);
        menuBar.add(searchMenu);
        if (OS.isMacOSX()) {
            menuBar.add(windowMenu);
        }

        setJMenuBar(menuBar);
    }

    public void updateMenu(boolean enabled, boolean resetExportSubMenu) {
        String name = state.getNameOfSelectedSnippet();

        // file menu
        reloadSourceListMenuItem.setEnabled(enabled);

        importFromPackageMenuItem.setEnabled(enabled);
        exportAllMenuItem.setEnabled(!state.isDatabaseEmpty() && enabled);

        // aggiorno il sub menu con le categorie di destinazione
        updateExportCategorySubMenu(enabled, resetExportSubMenu);

        changeDirectoryMenuItem.setEnabled(enabled);

        // view menu
        showLineNumbersMenuItem.setEnabled(enabled);
        showCommentPanelMenuItem.setEnabled(enabled);
        autoHideCommentPanelMenuItem.setEnabled(enabled);

        // search menu
        showSearchPanelMenuItem.setEnabled(enabled);
        namesMenuItem.setEnabled(enabled);
        tagsMenuItem.setEnabled(enabled);
        codeMenuItem.setEnabled(enabled);
        commentsMenuItem.setEnabled(enabled);
        caseMenuItem.setEnabled(enabled);
        searchEnabledMenuItem.setEnabled(state.isSearchActive() && enabled);

        // snippet menu
        newSnippetMenuItem.setEnabled(enabled);
        newSnippetFromClipboardMenuItem.setEnabled(enabled);
        saveSnippetMenuItem.setEnabled(state.isSnippetValidated() && !state.isSnippetLocked() && enabled);
        removeSnippetMenuItem.setEnabled(name != null && enabled);
        lockSnippetMenuItem.setEnabled((name != null) && state.isSnippetSaved() && state.isSnippetValidated() && enabled);
        lockSnippetMenuItem.setText((state.isSnippetLocked() ? "Unlock" : "Lock"));
        copyToClipboardMenuItem.setEnabled(name != null && enabled);

        // window menu
        minimizeWindowMenuItem.setEnabled(enabled);
        zoomWindowMenuItem.setEnabled(enabled);
        closeWindowMenuItem.setEnabled(enabled);
    }

    private void updateExportCategorySubMenu(boolean enabled, boolean resetExportSubMenu) {
        if (!resetExportSubMenu) {
            return;
        }

        exportSnippetsInCategorySubMenu.removeAll();

        ArrayList<String> categories = DBMS.getInstance().getCategories();
        for (int i = 0; i < categories.size(); i++) {
            JMenuItem categoryMenuItem = new JMenuItem(categories.get(i));
            categoryMenuItem.addActionListener(EXPORT_CATEGORY_ACTION);
            categoryMenuItem.setEnabled(enabled);
            exportSnippetsInCategorySubMenu.add(categoryMenuItem);
        }

        if (categories.size() == 0) {
            JMenuItem emptyMenuItem = new JMenuItem("No Categories Available");
            emptyMenuItem.setEnabled(false);
            exportSnippetsInCategorySubMenu.add(emptyMenuItem);
        }
    }

    /** Serial Version UID. */
    private static final long serialVersionUID = 2875157835785998264L;

}
