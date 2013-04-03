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
package jcodecollector.util;

/**
 * Classe di utilita', permette di capire su che sistema operativo e' in
 * esecuzione il programma.
 * 
 * @author Alessandro Cocco
 */
public class OS {
    /**
     * Indica se il programma e' in esecuzione su Mac OS X.
     * 
     * @return <code>true</code> se il programma e' in esecuzione su Mac OS X,
     *         <code>false</code> altrimenti
     */
    public static boolean isMacOSX() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    /**
     * Indica se il programma e' in esecuzione su Windows.
     * 
     * @return <code>true</code> se il programma e' in esecuzione su Windows,
     *         <code>false</code> altrimenti
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    private OS() {
        // do nothing
    }
}
