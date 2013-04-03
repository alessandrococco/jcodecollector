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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jcodecollector.data.DBMS;
import jcodecollector.data.settings.ApplicationSettingsManager;
import jcodecollector.gui.MainFrame;
import jcodecollector.util.OS;

public class Loader {

    public static void main(String[] args) {
        try {
            if (OS.isMacOSX()) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            } else if (OS.isWindows()) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else if (OS.isLinux()) {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception e) {
            System.err.println("error loading look and feel - using default.");
        }

        // carica i settaggi dell'applicazione
        ApplicationSettingsManager.readApplicationSettings();

        // controllo se e' necessario aggiornare il database in quando quello
        // nuovo non e' compatibile con quello della versione 2.0
        if (DBMS.getInstance().databaseMustBeUpdate()) {
            JOptionPane.showMessageDialog(null, "<html><font size=-1>jCodeCollector database <b>must be update</b>. " + "Clicking OK the operation will be performed.<br><br>"
                    + "<b>Warning</b>: Syntaxes from previous version are not compatible and will be removed.<br>" + "For this reason all snippets will be set to \"no syntax\". I'm sorry.<br>"
                    + "You can quickly fix your snippets right-clicking on a category and choosing " + "<i>Set Syntax->syntax</i></font></html>", "jCodeCollector - Migration Assistant", JOptionPane.INFORMATION_MESSAGE);

            if (!DBMS.getInstance().updateDatabase()) {
                System.err.println("error updating database");
                System.exit(1);
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);

                if (OS.isMacOSX()) {
                    // forzo il ridisegno dell'interfaccia: e' un piccolo fix
                    // per il problema della bottom bar che appare del colore
                    // sbagliato
                    mainFrame.repaint();
                }

                // carico il source list con gli snippet
                mainFrame.reloadSourceList();
                mainFrame.restoreSelectedSnippet();
            }
        });
    }

}
