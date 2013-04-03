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

import java.util.Collection;

/**
 * Questa classe incapsula il concetto di "stile". Ogni stile ha un nome ed una
 * serie di keywords, chiavi che saranno colorate all'interno dell'editor.
 * 
 * @author Alessandro Cocco me@alessandrococco.com
 */
public class Syntax implements Comparable<Syntax> {
    /** Il nome del linguaggio a cui appartiene questa sintassi. */
    private String name;

    /** Le parole chiave da colorare. */
    private String[] keywords;

    public Syntax() {
        this("", new String[] {});
    }

    public Syntax(String name) {
        this(name, new String[] {});
    }

    public Syntax(String name, String[] keywords) {
        this.name = name;
        this.keywords = keywords.clone();
    }

    public Syntax(String name, Collection<String> keywords) {
        this(name, keywords.toArray(new String[] {}));
    }

    public String[] getKeywords() {
        return keywords.clone();
    }

    public String getKeywordsAsString() {
        String k = new String();

        for (String s : keywords) {
            k += s.trim() + ", ";
        }

        return k;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords.clone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Syntax other = (Syntax) obj;
        if (this.name != other.name
                && (this.name == null || !this.name.equals(other.name))) {
            return false;
        }

        return true;
    }

    public int compareTo(Syntax o) {
        return name.compareToIgnoreCase(o.name);
    }
}
