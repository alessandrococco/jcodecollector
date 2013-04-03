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

import jcodecollector.State;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * Implementazione di un {@link RSyntaxDocument} che aggiunge un limite al
 * numero massimo di caratteri inseribili.
 * 
 * @author Alessandro Cocco
 */
public class LimitedSyntaxDocument extends RSyntaxDocument {

	/** Numero massimo di caratteri inseribili nel documento. */
	private int maxSize;

	/**
	 * Crea un {@link LimitedSyntaxDocument} usando <code>syntax</code> come
	 * sintassi e che puo' contenere al massimo <code>maxSize</code> caratteri.
	 * 
	 * @param syntax La sintassi del documento.
	 * @param maxSize Il massimo numero di caratteri che possono essere inseriti
	 *        nel documento.
	 */
	public LimitedSyntaxDocument(String syntax, int maxSize) {
		super(syntax);
		this.maxSize = maxSize;
	}

	/**
	 * Crea un {@link LimitedSyntaxDocument} usando la sintassi predefinita e
	 * che puo' contenere al massimo <code>maxSize</code> caratteri.
	 * 
	 * @param maxSize Il massimo numero di caratteri che possono essere inseriti
	 *        nel documento.
	 */
	public LimitedSyntaxDocument(int maxSize) {
		this(SyntaxConstants.SYNTAX_STYLE_NONE, maxSize);
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

		// workaround line numbers
		if (str.contains("\n")) {
			State.getInstance().updateLineNumbers(true);
		}
		// end workaround line numbers
	}

	/** Serial Version UID. */
	private static final long serialVersionUID = 528011829911748389L;
}
