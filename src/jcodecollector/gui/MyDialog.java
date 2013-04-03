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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import jcodecollector.State;
import jcodecollector.common.bean.Snippet;
import jcodecollector.data.DBMS;
import jcodecollector.document.EditorValidator;
import jcodecollector.document.LimitedPlainDocument;
import jcodecollector.document.LimitedSyntaxDocument;
import jcodecollector.gui.images.Icons;
import jcodecollector.listener.CategoryListener;
import jcodecollector.listener.SnippetListener;
import jcodecollector.util.ApplicationConstants;
import jcodecollector.util.OS;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class MyDialog extends JDialog implements SnippetListener, CategoryListener {

    private static final long serialVersionUID = 4798877825989114217L;

    JComboBox categories;
    RSyntaxTextArea editor;
    JTextArea commentTextField;
    JTextField nameTextField;
    JComboBox syntaxBox;

    JTextField tagsTextField;
    JButton saveButton;
    JToggleButton lockButton;

    RTextScrollPane scrollPanel;

    JSplitPane split;

    /** Il validatore dei componenti di testo. */
    private EditorValidator editorValidator;

    /** Array di supporto per i componenti di testo. */
    private JTextComponent[] textComponents;

    private MainFrame mainframe;
    JPanel southPanel;
    JPanel mainPanel;

    public MyDialog(MainFrame mainframe) {
        super(mainframe);
        this.mainframe = mainframe;

        initComponents();
        initListeners();
        initSyntax();

        setLayout(new BorderLayout(1, 1));
        setSize(new Dimension(450, 600));
        setMinimumSize(new Dimension(450, 450));
        setBackground(ApplicationConstants.DEFAULT_MAC_COLOR);

        JPanel northPanel = buildNorthPanel();
        JPanel editorPanel = buildEditorPanel();
        southPanel = buildSouthPanel(); // lo creo ma non lo aggiungo subito

        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBorder(new EmptyBorder(5, 10, 2, 5));
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(editorPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        if (OS.isMacOSX()) {
            setDefaultBackgroundTo(this);
            setDefaultBackgroundTo(southPanel);
        }

        categoriesUpdated("");
    }

    /** {@link TreeMap} of syntaxes. */
    private TreeMap<String, String> syntaxMap = new TreeMap<String, String>();

    /** Popola il {@link JComboBox} delle sintassi. */
    private void initSyntax() {
        syntaxMap.put("", SyntaxConstants.SYNTAX_STYLE_NONE);
        syntaxMap.put("Assembler (X86)", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86);
        syntaxMap.put("AppleScript", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        syntaxMap.put("Unix Shell Script", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        syntaxMap.put("C", SyntaxConstants.SYNTAX_STYLE_C);
        syntaxMap.put("C++", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        syntaxMap.put("C#", SyntaxConstants.SYNTAX_STYLE_CSHARP);
        syntaxMap.put("CSS", SyntaxConstants.SYNTAX_STYLE_CSS);
        syntaxMap.put("Delphi", SyntaxConstants.SYNTAX_STYLE_DELPHI);
        syntaxMap.put("Lisp", SyntaxConstants.SYNTAX_STYLE_LISP);
        syntaxMap.put("Makefile", SyntaxConstants.SYNTAX_STYLE_MAKEFILE);
        syntaxMap.put("Perl", SyntaxConstants.SYNTAX_STYLE_PERL);
        syntaxMap.put("PHP", SyntaxConstants.SYNTAX_STYLE_PHP);
        syntaxMap.put("Python", SyntaxConstants.SYNTAX_STYLE_PYTHON);
        syntaxMap.put("Properties File", SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
        syntaxMap.put("Groovy", SyntaxConstants.SYNTAX_STYLE_GROOVY);
        syntaxMap.put("Java", SyntaxConstants.SYNTAX_STYLE_JAVA);
        syntaxMap.put("JavaScript", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        syntaxMap.put("JSP", SyntaxConstants.SYNTAX_STYLE_JSP);
        syntaxMap.put("Lua", SyntaxConstants.SYNTAX_STYLE_LUA);
        syntaxMap.put("Objective C", SyntaxConstants.SYNTAX_STYLE_C);
        syntaxMap.put("Ruby", SyntaxConstants.SYNTAX_STYLE_RUBY);
        syntaxMap.put("SQL", SyntaxConstants.SYNTAX_STYLE_SQL);
        syntaxMap.put("HTML", SyntaxConstants.SYNTAX_STYLE_HTML);
        syntaxMap.put("Tcl", SyntaxConstants.SYNTAX_STYLE_TCL);
        syntaxMap.put("Windows Batch", SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
        syntaxMap.put("XML", SyntaxConstants.SYNTAX_STYLE_XML);
        // syntaxMap.put("Fortran", SyntaxConstants.SYNTAX_STYLE_FORTRAN);
        // syntaxMap.put("Scala", SyntaxConstants.SYNTAX_STYLE_SCALA);
        // syntaxMap.put("SAS", SyntaxConstants.SYNTAX_STYLE_SAS);
        // syntaxMap.put("BBCode", SyntaxConstants.SYNTAX_STYLE_BBCODE);

        for (String name : syntaxMap.keySet()) {
            syntaxBox.addItem(name);
        }
    }

    /** Rileva quando dal {@link JComboBox} viene scelta una nuova sintassi. */
    private class MyItemListener implements ItemListener {

        boolean ignoreUpdate = false;

        public MyItemListener() {
            // TODO Auto-generated constructor stub
        }

        public void itemStateChanged(ItemEvent e) {
            // non mi importa sapere quando viene deselezionato
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                return;
            }

            // vedo che sintassi e' stata selezionata
            String syntax = syntaxMap.get(e.getItem());

            /* salvo il contenuto dell'editor e la posizione del cursore: questo
             * perche' quando imposto un nuovo Document l'editor viene pulito e
             * quindi il contenuto va perso. */
            String code = editor.getText();
            int caretPosition = editor.getCaretPosition();

            // aggiorno il Document dell'editor
            if (syntax != null) {
                editor.setDocument(new LimitedSyntaxDocument(syntax, ApplicationConstants.CODE_LENGTH));
            } else {
                editor.setDocument(new LimitedSyntaxDocument(ApplicationConstants.CODE_LENGTH));
            }

            // ripristino il testo e la posizione del cursore
            editor.setText(code);
            editor.setCaretPosition(caretPosition);

            // ripristino il validatore
            editor.getDocument().addDocumentListener(editorValidator);

            if (ignoreUpdate) {
                return;
            }

            State.getInstance().updateSnippetStatus(true, false, false);

            // evita lo "sfarfallamento" dell'indicatore di
            // "documento modificato"
            if (selectionFromUser) {
                State.getInstance().updateWindowStatus(true);
            }
        }
    };

    MyItemListener itemListener = new MyItemListener();

    /**
     * Piccolo accorgimento per evitare di chiamare inutilmente
     * {@link State#updateWindowStatus(boolean)}, cosa che causerebbe uno
     * "sfarfallamento" nell'indicatore di "documento modificato".
     */
    private boolean selectionFromUser = true;

    private void initComponents() {
        categories = new JComboBox();
        ((JTextField) categories.getEditor().getEditorComponent()).setDocument(new LimitedPlainDocument(ApplicationConstants.CATEGORY_LENGTH));
        categories.setEditable(true);
        categories.putClientProperty("JComboBox.isPopDown", Boolean.TRUE);

        nameTextField = new JTextField();
        nameTextField.setFont(nameTextField.getFont().deriveFont(Font.BOLD));
        nameTextField.setDocument(new LimitedPlainDocument(ApplicationConstants.SNIPPET_NAME_LENGTH));

        saveButton = new JButton("Save");
        saveButton.setFont(saveButton.getFont().deriveFont(saveButton.getFont().getSize() - 2f));
        saveButton.putClientProperty("JButton.buttonType", "textured");
        saveButton.setFocusable(false);
        saveButton.setEnabled(false);

        if (OS.isMacOSX()) {
            saveButton.setPreferredSize(new Dimension(50, 10));
        } else {
            saveButton.setPreferredSize(new Dimension(55, 10));
        }

        lockButton = new JToggleButton(Icons.UNLOCK_ICON);
        lockButton.setSelectedIcon(Icons.LOCK_ICON);
        lockButton.putClientProperty("JButton.buttonType", "textured");
        lockButton.setFocusable(false);
        lockButton.setPreferredSize(saveButton.getPreferredSize());
        lockButton.setEnabled(false);

        tagsTextField = new JTextField();

        syntaxBox = new JComboBox();
        ((JTextField) syntaxBox.getEditor().getEditorComponent()).setDocument(new LimitedPlainDocument(ApplicationConstants.SYNTAX_NAME_LENGTH));
        syntaxBox.setEditable(false);

        editor = new RSyntaxTextArea();
        editor.setDocument(new LimitedSyntaxDocument(ApplicationConstants.CODE_LENGTH));
        editor.setFadeCurrentLineHighlight(true);
        editor.setMarginLineEnabled(true);
        editor.setMarginLinePosition(80);
        editor.setRoundedSelectionEdges(true);
        editor.setPopupMenu(null);

        commentTextField = new JTextArea(4, 20);
        commentTextField.setDocument(new LimitedPlainDocument(ApplicationConstants.COMMENT_LENGTH));
        commentTextField.setWrapStyleWord(true);
        commentTextField.setLineWrap(true);
        commentTextField.setFont(commentTextField.getFont().deriveFont(commentTextField.getFont().getSize() - 1.5f));

        textComponents = new JTextComponent[] { (JTextComponent) categories.getEditor().getEditorComponent(), nameTextField, tagsTextField, editor, commentTextField };

        editorValidator = new EditorValidator(textComponents, new boolean[] { false, false, false, false, true });
        editorValidator.start();

        // Add a DocumentListener to all text components
        for (JTextComponent c : textComponents) {
            c.getDocument().addDocumentListener(editorValidator);
        }

        final JComponent[] components = { (JTextComponent) categories.getEditor().getEditorComponent(), nameTextField, tagsTextField, syntaxBox, editor };

        /* Add a KeyListener to some components. In this way the user can move
         * focus from a component to its next using ENTER. */
        for (int i = 0; i < components.length; i++) {
            if (i == components.length - 1) {
                break;
            }

            final int J = i + 1;

            JComponent c = components[i];
            c.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        components[J].requestFocusInWindow();
                    }
                }
            });
        }
    }

    /**
     * Inizializza gli ascoltatori dei {@link JButton} dell'editor e del
     * {@link JComboBox} della sintassi.
     */
    private void initListeners() {
        // registra l'azione di salvataggio dello snippet
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainframe.SAVE_SNIPPET_ACTION.actionPerformed(e);
            }
        });

        // registra l'azione di bloccaggio/sbloccaggio dello snippet
        lockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (State.getInstance().isSnippetSaved()) {
                    State.getInstance().updateSnippetStatus(true, true, lockButton.isSelected());
                } else {
                    lockButton.setSelected(false);
                    State.getInstance().updateSnippetStatus(State.getInstance().isSnippetValidated(), false, false);
                }

                State.getInstance().updateSnippetStatus(true, true, lockButton.isSelected());
                State.getInstance().updateMenu(true, false);
            }
        });

        // registra l'azione di cambiamento dello sintassi
        syntaxBox.addItemListener(itemListener);
    }

    /**
     * Sets a default background color to all <code>component</code>'s children.
     * 
     * @param component The root component.
     */
    private void setDefaultBackgroundTo(Component component) {
        if (component instanceof JTextComponent) {
            return;
        }

        if (component instanceof Container) {
            Component[] components = ((Container) component).getComponents();

            for (Component c : components) {
                setDefaultBackgroundTo(c);
            }
        }

        // set the background color
        component.setBackground(ApplicationConstants.DEFAULT_MAC_COLOR);
    }

    private JPanel buildNorthPanel() {
        JLabel categoryLabel = new JLabel(" Category:  ", JLabel.RIGHT);
        JLabel nameLabel = new JLabel(" Name:  ", JLabel.RIGHT);
        JLabel tagsLabel = new JLabel(" Tags:  ", JLabel.RIGHT);
        JLabel syntaxLabel = new JLabel(" Syntax:  ", JLabel.RIGHT);

        // coppie (label, input component)
        JComponent[][] components = { { categoryLabel, categories }, { nameLabel, nameTextField }, { tagsLabel, tagsTextField }, { syntaxLabel, syntaxBox } };

        int horizontalGap = OS.isMacOSX() ? 0 : 3;
        int verticalGap = OS.isMacOSX() ? 0 : 3;

        JPanel left = new JPanel(new GridLayout(components.length, 1, 0, verticalGap));
        JPanel right = new JPanel(new GridLayout(components.length, 1, 0, verticalGap));

        for (int i = 0; i < components.length; i++) {
            JPanel p = new JPanel(new BorderLayout(horizontalGap, 0));
            p.setOpaque(false);

            if (i == 0) {
                p.add(saveButton, BorderLayout.EAST);
            } else if (i == 1) {
                p.add(lockButton, BorderLayout.EAST);
            }

            p.add(components[i][1], BorderLayout.CENTER);

            left.add(components[i][0]);
            right.add(p);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildEditorPanel() {
        scrollPanel = new RTextScrollPane(editor, false);

        // pannello dell'editor completo
        JPanel editorPanel = new JPanel(new BorderLayout(1, 5));

        JLabel codeLabel = new JLabel(" Code:  ", JLabel.RIGHT);
        codeLabel.setVerticalAlignment(JLabel.TOP);

        if (OS.isMacOSX()) {
            codeLabel.setBorder(new EmptyBorder(0, 24, 0, 2));
        } else {
            codeLabel.setBorder(new EmptyBorder(0, 21, 0, -2));
        }

        editorPanel.add(scrollPanel, BorderLayout.CENTER);
        editorPanel.add(codeLabel, BorderLayout.WEST);
        scrollPanel.setBorder(new CompoundBorder(new EmptyBorder(
                    OS.isMacOSX() ? 0 : 3, // top
                0, // left
                OS.isMacOSX() ? 2 : 0, // bottom
                OS.isMacOSX() ? 2 : 0), // right
                scrollPanel.getBorder()));

        return editorPanel;
    }

    private JPanel buildSouthPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JScrollPane scrollPanel = new JScrollPane(commentTextField, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // scrollPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0),
        // new EtchedBorder()));
        panel.add(scrollPanel, BorderLayout.CENTER);

        // pannello per l'area di testo di commento
        JPanel commentPanel = new JPanel(new BorderLayout());
        commentPanel.setOpaque(false);
        commentPanel.add(panel, BorderLayout.CENTER);

        // etichetta dell'area di testo del commento
        JLabel commentLabel = new JLabel((!OS.isMacOSX() ? " " : "") + "Comment: ", JLabel.RIGHT);
        commentLabel.setVerticalAlignment(JLabel.TOP);

        // pannello dell'etichetta dell'area di commenti
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(commentLabel, BorderLayout.CENTER);

        // metto insieme i pezzi
        JPanel southPanel = new JPanel(new BorderLayout(OS.isMacOSX() ? 6 : 3, 0));
        southPanel.setBorder(new EmptyBorder(3, 0, 2, OS.isMacOSX() ? 3 : 0));
        southPanel.add(commentPanel, BorderLayout.CENTER);
        southPanel.add(labelPanel, BorderLayout.WEST);
        southPanel.setOpaque(false);

        return southPanel;
    }

    /**
     * Mostra nell'editor lo snippet indicato. Lo snippet non deve essere
     * <code>null</code>.
     * 
     * @param snippet Lo snippet da mostrare.
     */
    public void setSnippet(Snippet snippet) {
        if (snippet == null) {
            return;
        }

        /* Prima di iniziare ad inserire i dati dello snippet nell'editor fermo
         * il controllo di validazione: in questo modo non si perde tempo a
         * validare uno snippet che e' gia' validato ed inoltre si evitano
         * alcune collisioni tra listener. Dopo i vari inserimenti il validatore
         * viene riattivato. */
        editorValidator.stop();

        String s = snippet.getCategory().trim();
        if (s != null && !s.equals(categories.getEditor().getItem())) {
            categories.setSelectedItem(s);
        }

        s = snippet.getName().trim();
        if (s != null && !s.equals(nameTextField.getText().trim())) {
            nameTextField.setText(s);
            nameTextField.setCaretPosition(0);
        }

        s = snippet.getTagsAsString().trim();
        if (s != null && !s.equals(tagsTextField.getText().trim())) {
            tagsTextField.setText(s);
            tagsTextField.setCaretPosition(0);
        }

        s = snippet.getSyntax().trim();
        selectionFromUser = false;
        if (s != null && s.length() > 0) {
            syntaxBox.setSelectedItem(s);
        } else {
            syntaxBox.setSelectedIndex(0);
        }
        selectionFromUser = true;

        s = snippet.getCode().trim();
        if (s != null && !s.equals(editor.getText().trim())) {
            editor.setText(s);
            editor.setCaretPosition(0);
        }

        s = ("" + snippet.getComment()).trim();
        if (s != null && !s.equals(commentTextField.getText().trim())) {
            commentTextField.setText(s);
            commentTextField.setCaretPosition(0);
        }

        // resetto l'undo manager
        editor.discardAllEdits();

        // riattivo il validatore
        editorValidator.start();

        lockButton.setSelected(snippet.isLocked());
        lockButton.setEnabled(true);
        lock(snippet.isLocked());
    }

    /** Svuota l'editor e pulisce tutti i campi. */
    public void clear() {
        editorValidator.stop();

        categories.setSelectedIndex(-1);
        nameTextField.setText(null);
        tagsTextField.setText(null);
        editor.setText(null);
        commentTextField.setText(null);
        // syntaxBox.setSelectedItem("-- no syntax highlighting");
        syntaxBox.setSelectedIndex(-1);

        lockButton.setEnabled(false);
        lockButton.setSelected(false);

        editorValidator.start();

        lock(false);
    }

    /** Crea un nuovo snippet vuoto. */
    public void createNewSnippet() {
        lockButton.setSelected(false);
        clear();

        // resetto l'undo manager
        editor.discardAllEdits();

        categories.requestFocusInWindow();
    }

    /** Incolla nell'editor il contenuto della clipboard. */
    public void pasteFromClipboard() {
        clear();

        editor.paste();
        editor.setCaretPosition(0);

        categories.requestFocusInWindow();
    }

    /** Copia lo snippet corrente nella clipboard. */
    public void copyToClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(editor.getText()), null);
    }

    /** @see CategoryListener#categoryRemoved(String) */
    public void categoryRemoved(String name) {
        // rimuove la categoria dall'elenco a discesa
        categories.removeItem(name);

        /* Ottengo la categoria dello snippet attuale e pulisco l'editor se si
         * tratta della categoria cancellata */
        String workingCategory = "" + categories.getModel().getSelectedItem();
        if (workingCategory.equals(name)) {
            // syntaxBox.removeItemListener(itemListener);
            clear();
            // syntaxBox.addItemListener(itemListener);
        }
    }

    /**
     * @see SnippetListener#snippetRenamed(String, String)
     */
    public void categoryRenamed(String oldName, String newName) {
        JTextField textField = (JTextField) categories.getEditor().getEditorComponent();

        if (textField.getText().trim().equals(oldName)) {
            categories.addItem(newName);
            categories.setSelectedItem(newName);
            categories.removeItem(oldName);
        }
    }

    /** @see jcodecollector.listener.SnippetListener#snippetRemoved(Snippet) */
    public void snippetRemoved(Snippet snippet) {
        if (nameTextField.getText().trim().equals(snippet.getName())) {
            clear();
        }
    }

    /**
     * @see jcodecollector.listener.SnippetListener#snippetRenamed(String,
     *      String)
     */
    public void snippetRenamed(String oldName, String newName) {
        if (!oldName.equals(newName)) {
            nameTextField.setText(newName);
        }
    }

    /** @see jcodecollector.listener.CategoryListener#categoriesUpdated(String) */
    public void categoriesUpdated(String selected) {
        ArrayList<String> array = DBMS.getInstance().getCategories();
        Collections.sort(array);

        categories.removeAllItems();

        for (String s : array) {
            categories.addItem(s);
        }

        if (array.contains(selected)) {
            categories.setSelectedItem(selected);
        } else {
            categories.setSelectedIndex(-1);
        }
    }

    /**
     * Restituisce lo snippet contenuto nell'editor.
     * 
     * @return lo snippet contenuto nell'editor
     */
    public Snippet getSnippet() {
        String name = nameTextField.getText().trim();
        String category = ((String) categories.getEditor().getItem()).trim();
        String code = editor.getText().trim();
        String comment = commentTextField.getText().trim();
        String syntax = (String) syntaxBox.getSelectedItem();
        boolean locked = lockButton.isSelected();

        StringTokenizer tokenizer = new StringTokenizer(tagsTextField.getText().trim(), ",");
        String[] tags = new String[tokenizer.countTokens()];

        for (int i = 0; i < tags.length; i++) {
            tags[i] = tokenizer.nextToken().trim();
        }

        int id = DBMS.getInstance().getSnippetId(name);
        return new Snippet(id, category, name, tags, code, comment, syntax, locked);
    }

    /** @see jcodecollector.listener.SnippetListener#snippetEdited(Snippet) */
    public void snippetEdited(Snippet snippet) {
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < categories.getModel().getSize(); i++) {
            items.add("" + categories.getModel().getElementAt(i));
        }

        if (!items.contains(snippet.getCategory())) {
            items.add(snippet.getCategory());
            categories.setModel(new DefaultComboBoxModel(items.toArray()));
            categories.setSelectedItem(snippet.getCategory());
        }
    }

    private void lock(boolean value) {
        value = !value;

        categories.setEnabled(value);
        nameTextField.setEditable(value);
        tagsTextField.setEnabled(value);
        syntaxBox.setEnabled(value);
        editor.setEditable(value);
        commentTextField.setEnabled(value);
    }

    /**
     * @see jcodecollector.listener.SnippetListener#updateSnippetStatus(boolean,
     *      boolean, boolean)
     */
    public void updateSnippetStatus(boolean validated, boolean saved, boolean locked) {
        lockButton.setEnabled(saved && validated);
        saveButton.setEnabled(validated && !locked);

        // lock/unlock the snippet
        lock(lockButton.isSelected());

        if (DBMS.getInstance().lockSnippet(nameTextField.getText().trim(), lockButton.isSelected())) {
            // TODO cosa si fa qui? boh
        }
    }

    public void syntaxRenamed(String newName, String category) {
        if (!State.getInstance().isSnippetSaved()) {
            return;
        }

        if (category.equals(categories.getSelectedItem())) {
            itemListener.ignoreUpdate = true;
            syntaxBox.setSelectedItem(newName);
            itemListener.ignoreUpdate = false;
        }
    }

    /**
     * Restuisce un riferimento al pannello scorrevole che contiene l'editor.
     * 
     * @return un riferimento al pannello scorrevole che contiene l'editor
     */
    public RTextScrollPane getScrollPanel() {
        return scrollPanel;
    }

    public String[] getSyntaxes() {
        return new ArrayList<String>(syntaxMap.keySet()).toArray(new String[] {});
    }

}
