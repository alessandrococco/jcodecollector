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

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import jcodecollector.gui.images.Icons;
import jcodecollector.util.GeneralInfo;

/**
 * La finestra di about di jCodeCollector. Mostra la versione dell'applicazione
 * e le informazioni sull'autore.
 * 
 * @author Alessandro Cocco
 */
public class AboutWindow extends JDialog {

    /**
     * Crea la finestra di about di jCodeCollector.
     * 
     * @param mainFrame Il riferimento alla finestra principale
     *        dell'applicazione.
     */
    public AboutWindow(final MainFrame mainFrame) {
        setResizable(false);
        setTitle("About " + GeneralInfo.APPLICATION_NAME);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());
        getRootPane().setBorder(new EmptyBorder(30, 15, 30, 15));

        String text = String.format("<html><center>&nbsp; <b>%s</b><br>" +
                "&nbsp; Version %s<br><br>&nbsp; <b>Written by</b><br>" +
                "&nbsp; Alessandro Cocco<br><br>" +
                "<font size=-1>&nbsp; %s Alessandro Cocco &nbsp; " +
                "<br>&nbsp; All Rights Reserved. &nbsp;</font></center></html>",
                GeneralInfo.APPLICATION_NAME, GeneralInfo.APPLICATION_VERSION,
                GeneralInfo.COPYRIGHT_YEARS);

        add(new JLabel(text, JLabel.CENTER), BorderLayout.CENTER);
        add(new JLabel(Icons.APPLICATION_ICON_BIG), BorderLayout.NORTH);

        pack();
    }

    /** Serial Version UID. */
    private static final long serialVersionUID = 8750113543548319858L;

}
