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
package jcodecollector.listener;

public interface CategoryListener {

    /**
     * Invocato quando una categoria viene rinominata.
     * 
     * @param oldName Il vecchio nome della categoria.
     * @param newName Il nuovo nome della categoria.
     */
    public void categoryRenamed(String oldName, String newName);

    /**
     * Invocato quando una categoria viene rimossa.
     * 
     * @param name Il nome della categoria rimossa.
     */
    public void categoryRemoved(String name);

    /**
     * Invocato quando l'elenco delle categorie viene aggiornato.
     * 
     * @param selected La categoria che risulta selezionata.
     */
    public void categoriesUpdated(String selected);

}
