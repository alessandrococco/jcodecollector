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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Sotto classe di <code>PlainDocument</code>, permette di specificare il numero
 * massimo di caratteri accettati dal documento.
 * 
 * @author Alessandro Cocco me@alessandrococco.com
 */
public class LimitedPlainDocument extends PlainDocument {
    private static final long serialVersionUID = 450773819997253578L;

    /** Numero massimo di caratteri inseribili nel documento. */
    private int maxSize;

    /**
     * Crea un <code>LimitedPlainDocument</code> che permette di inserire al
     * massimo <code>maxSize</code> caratteri.
     * 
     * @param maxSize Il massimo numero di caratteri che possono essere inseriti
     *        nel documento.
     */
    public LimitedPlainDocument(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * @see javax.swing.text.PlainDocument#insertString(int, String,
     *      AttributeSet)
     */
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) {
            return;
        }

        if (getLength() + str.length() > maxSize) {
            str = str.substring(0, maxSize - getLength());
        }

        super.insertString(offs, str, a);
    }
}
