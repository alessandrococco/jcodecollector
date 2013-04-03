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

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class PromptTextField extends JTextField {
    static FocusListener focusListener = new FocusListener() {
	public void focusGained(FocusEvent e) {
	    PromptTextField src = (PromptTextField) e.getSource();
	    if (src.state == STATE_PROMPT) {
		src.setState(STATE_NORMAL);
	    }
	}

	public void focusLost(FocusEvent e) {
	    PromptTextField src = (PromptTextField) e.getSource();
	    if (src.getText().length() == 0)
		src.setState(STATE_PROMPT);
	}
    };
    
    private static final int STATE_UNDEFINED = 0;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_PROMPT = 2;

    private Color normalColor = SystemColor.textText;
    protected Color promptColor = Color.gray;
    protected String promptText;
    private int state = STATE_UNDEFINED;

    public PromptTextField(String text, String prompt, int columns) {
	super(text, columns);
	promptText = prompt;
	normalColor = getForeground();
	addFocusListener(focusListener);
	if (text.length() == 0) {
	    setState(STATE_PROMPT);
	} else {
	    setState(STATE_NORMAL);
	}
    }

    public String getText() {
	if (state == STATE_PROMPT) {
	    return "";
	}
	return super.getText();
    }

    private void setState(int s) {
	if (s == state)
	    return;
	state = s;
	if (s == STATE_PROMPT) {
	    if (state == STATE_NORMAL && getText().length() > 0)
		throw new IllegalArgumentException("the state should not be set to STATE_PROMPT if there is text already in the text field (\"" + getText() + "\")");
	    super.setForeground(promptColor);
	    super.setText(promptText);
	} else if (s == STATE_NORMAL) {
	    super.setForeground(normalColor);
	    super.setText("");
	}
    }

    public void setText(String s) {
	if (state == STATE_UNDEFINED) {
	    super.setText(s);
	    return;
	}
	if (s == null)
	    s = "";
	if (hasFocus() || s.length() > 0) {
	    state = STATE_NORMAL;
	    super.setForeground(normalColor);
	    super.setText(s);
	} else if (s.length() == 0) {
	    state = STATE_PROMPT;
	    super.setForeground(promptColor);
	    super.setText(promptText);
	}
    }

    private static final long serialVersionUID = 8860111447676423480L;

}
