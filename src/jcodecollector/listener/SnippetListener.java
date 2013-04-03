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

import jcodecollector.common.bean.Snippet;

/**
 * @author Alessandro Cocco
 */
public interface SnippetListener {
    /**
     * Invocato quando lo snippet indicato viene modificato.
     * 
     * @param snippet Lo snippet che e' stato modificato.
     */
    public void snippetEdited(Snippet snippet);

    /**
     * Invocato quando uno snippet viene rinominato.
     * 
     * @param oldName Il vecchio nome dello snippet.
     * @param newName Il nuovo nome dello snippet.
     */
    public void snippetRenamed(String oldName, String newName);

    /**
     * Invocato quando uno snippet viene rimosso.
     * 
     * @param snippet Lo snippet rimosso.
     */
    public void snippetRemoved(Snippet snippet);

    /**
     * Invocato quando lo stato di uno snippet cambia.
     * 
     * @param validated
     * @param saved
     * @param locked
     */
    public void updateSnippetStatus(boolean validated, boolean saved, boolean locked);

    public void syntaxRenamed(String newName, String category);
}
