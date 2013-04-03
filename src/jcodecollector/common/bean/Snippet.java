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
package jcodecollector.common.bean;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Incapsula il concetto di "snippet". Ogni snippet e' composto dal codice, una
 * categoria, una serie di tag ed un commento addizionale oltre che da un nome
 * (univoco).
 * 
 * @author Alessandro Cocco me@alessandrococco.com
 */
public class Snippet implements Cloneable {
    /** La categoria dello snippet. */
    private String category;

    /** Il nome dello snippet. */
    private String name;

    /** I tag dello snippet. */
    private ArrayList<String> tags;

    /** Il codice relativo allo snippet. */
    private String code;

    /** Un commento relativo allo snippet. */
    private String comment;

    /** Lo stile da usare per colorare il codice. */
    private String syntax;

    /** Stato dello snippet. */
    private boolean locked;

    /** Id dello snippet. */
    private int id;

    /**
     * Instanzia uno snippet completo dei suoi dati.
     * 
     * @param id L'identificatore univoco dello snippet.
     * @param category La categoria dello snippet.
     * @param name Il nome dello snippet,
     * @param tags I tag dello snippet.
     * @param code Il codice dello snippet.
     * @param comment Un commento (opzionale) sullo snippet.
     * @param syntax Lo stile di colorazione sintattica associato.
     * @param locked <code>true</code> se lo snippet e' bloccato,
     *        <code>false</code> altrimenti.
     */
    public Snippet(int id, String category, String name, String[] tags,
            String code, String comment, String syntax, boolean locked) {
        if (syntax == null) {
            syntax = new String();
        }

        this.category = category;
        this.name = name;
        this.tags = new ArrayList<String>(Arrays.asList(tags));
        this.code = code;
        this.comment = comment;
        this.syntax = syntax;
        this.id = id;
        this.locked = locked;
    }

    public Snippet(String category, String name, String[] tags, String code,
            String comment, String syntax) {
        this(-1, category, name, tags, code, comment, syntax, false);
    }

    /**
     * Istanzia uno snippet vuoto. I vari attributi dovranno ricevere dei valori
     * validi dai metodi setter.
     */
    public Snippet() {
        this(-1, "", "", new String[] {}, "", "", "", false);
    }

    /**
     * Restituisce la categoria dello snippet.
     * 
     * @return la categoria dello snippet.
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Assegna allo snippet una nuova categoria.
     * 
     * @param category la nuova categoria dello snippet.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Restituisce il nome dello snippet.
     * 
     * @return il nome dello snippet.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Assegna un nuovo nome allo snippet.
     * 
     * @param name il nuovo nome dello snippet.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Restituisce un clone dell'array dei tag dello snippet.
     * 
     * @return un clone dell'array dei tag dello snippet.
     */
    public String[] getTags() {
        // return this.tags.clone();
        return this.tags.toArray(new String[] {});
    }

    /**
     * Aggiorna i tag dello snippet.
     * 
     * @param tags l'array contenente i nuovi tag.
     */
    public void setTags(String[] tags) {
        // this.tags = tags.clone();
        this.tags = new ArrayList<String>(Arrays.asList(tags));
    }

    /**
     * Restituisce il codice dello snippet.
     * 
     * @return il codice dello snippet.
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Aggiorna il codice dello snippet.
     * 
     * @param code il nuovo codice dello snippet.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Restituisce il commento assegnato allo snippet. Se non e' presente un
     * commento viene restituita una stringa vuota.
     * 
     * @return il commento assegnato allo snippet se presente, una stringa vuota
     *         in caso contrario.
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * Aggiorna il commento dello snippet.
     * 
     * @param comment il nuovo commento dello snippet.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Restituisce il nome dello stile di colorazione sintattica assegnato allo
     * snippet.
     * 
     * @return il nome dello stile di colorazione sintattica assegnato allo
     *         snippet.
     */
    public String getSyntax() {
        return this.syntax;
    }

    /**
     * Aggiorna il nome dello stile di colorazione sintattica dello snippet.
     * 
     * @param syntax Il nuovo nome dello stile di colorazione sintattica dello
     *        snippet.
     */
    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    /**
     * Restituisce l'id dello snippet.
     * 
     * @return l'id dello snippet.
     */
    public int getID() {
        return this.id;
    }

    /**
     * Imposta l'id dello snippet.
     * 
     * @param id il nuovo valore dell'id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Indica se lo snippet e' bloccato (read-only) o meno.
     * 
     * @return <code>true</code> se lo snippet e' bloccato, <code>false</code>
     *         altrimenti.
     */
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * Blocca o sblocca lo snippet.
     * 
     * @param locked <code>true</code> per bloccare lo snippet,
     *        <code>false</code> per sbloccarlo.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Restituisce i tag dello snippet sotto forma di un'unica stringa.
     * 
     * @return una stringa contenente i tag dello snippet separati da una
     *         virgola ed uno spazio.
     */
    public String getTagsAsString() {
        if (tags == null || tags.size() == 0 || (tags.size() == 1 && tags.get(0).trim().length() == 0)) {
            return "";
        }

        StringBuilder temp = new StringBuilder();

        for (String s : tags) {
            temp.append(s + ", ");
        }

        return temp.toString();
    }

    public void addTag(String newTag) {
        this.tags.add(newTag);
    }

    @Override
    public String toString() {
        return category + "," + name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Snippet(category, name, tags.toArray(new String[] {}), code,
                comment, syntax);
    }
}
