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

import javax.swing.JDialog;

import jcodecollector.gui.MainFrame;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.AppReOpenedEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.AppReOpenedListener;
import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

/**
 * Migliora l'integrazione dell'applicazione con Mac OS X usando le
 * funzionalita' offerte da Apple. Poiche' non sono portabili, questa classe
 * dovra' essere caricata dinamicamente solo su Mac OS X.
 * 
 * @author Alessandro Cocco
 */
public class MacUtilities {

//    private MainFrame mainFrame;

//    public void setMainFrame(MainFrame mainFrame) {
//        this.mainFrame = mainFrame;
//    }

    public MacUtilities() {
        // do nothing
    }

    public void installMacUtilities(final MainFrame mainFrame) {
        Application application = Application.getApplication();

        // open about window
        application.setAboutHandler(new AboutHandler() {
            public void handleAbout(AboutEvent e) {
                JDialog aboutWindow = mainFrame.getAboutWindow();
                aboutWindow.setLocationRelativeTo(mainFrame);
                aboutWindow.setVisible(true);
            }
        });

        // CMD+Q
        application.setQuitHandler(new QuitHandler() {
            public void handleQuitRequestWith(QuitEvent e, QuitResponse r) {
                mainFrame.prepareAndSaveSettings();
                System.exit(0);
            }
        });

        // re-open when main window is not visible
        application.addAppEventListener(new AppReOpenedListener() {
            public void appReOpened(AppReOpenedEvent e) {
                mainFrame.setVisible(true);
            }
        });
    }

}
