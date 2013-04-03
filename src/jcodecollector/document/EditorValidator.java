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
package jcodecollector.document;

import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import jcodecollector.State;

/**
 * Validatore di componenti di testo come {@link JTextArea}, {@link JTextField},
 * {@link JTextPane}, {@link JEditorPane} ecc.
 * 
 * @author Alessandro Cocco
 */
public class EditorValidator implements DocumentListener {

    /** I componenti di testo da validare. */
    private JTextComponent[] components;

    /** Gli eventuali componenti opzionali. */
    private boolean[] optional;

    /**
     * Se <code>true</code> indica che il validatore e' in funzione, se
     * <code>false</code> il validatore e' disattivato.
     */
    private boolean running;

    /**
     * Costruisce il validatore dei componenti indicati. Il secondo parametro
     * indica se ci sono componenti opzionali.
     * 
     * @param components I componenti ({@link JTextArea}, {@link JTextField},
     *        {@link JEditorPane} ecc. da validare.
     * @param optional Un array della stessa dimensione del precedente. In ogni
     *        posizione, <code>true</code> indica che il corrispondente
     *        componente e' opzionale, <code>false</code> che e' obbligatorio.
     */
    public EditorValidator(JTextComponent[] components, boolean[] optional) {
        if (components.length != optional.length) {
            throw new IllegalArgumentException("components.length != optional.length");
        }

        this.components = components;
        this.optional = optional;
    }

    /** Attiva il validatore. */
    public void start() {
        running = true;
    }

    /** Disattiva il validatore. */
    public void stop() {
        running = false;
    }

    /**
     * Valida i componenti di testo. Un componente di testo e' considerato
     * valido se contiene almeno un carattere, a meno che non sia opzionale.
     */
    private void validate() {
        if (!running) {
            State.getInstance().updateWindowStatus(false);
            return;
        } else {
            State.getInstance().updateWindowStatus(true);
        }

        State.getInstance().updateSnippetStatus(false, false, false);
        boolean validated = true;
        for (int i = 0; i < components.length; i++) {
            if (optional[i]) {
                continue;
            }

            validated &= components[i].getText().trim().length() > 0;
        }
        State.getInstance().updateSnippetStatus(validated, false, false);
        State.getInstance().updateMenu(true, false);
    }

    /** @see DocumentListener#changedUpdate(DocumentEvent) */
    public void changedUpdate(DocumentEvent e) {
        validate();
    }

    /** @see DocumentListener#insertUpdate(DocumentEvent) */
    public void insertUpdate(DocumentEvent e) {
        validate();
    }

    /** @see DocumentListener#removeUpdate(DocumentEvent) */
    public void removeUpdate(DocumentEvent e) {
        validate();
    }

}
