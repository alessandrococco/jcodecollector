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
package jcodecollector.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jcodecollector.common.bean.Snippet;
import jcodecollector.data.DBMS;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Classe che si occupa di leggere/scrivere file XML.
 * 
 * @author Alessandro Cocco me@alessandrococco.com
 */
public class XMLManagerOldVersion {

    public static boolean createPackage(File file, String name) {
        ArrayList<Snippet> array = DBMS.getInstance().getSnippets(name);
        Element root_xml = new Element("jcc-snippets-package");
        boolean success;

        Iterator<Snippet> iterator = array.iterator();
        while (iterator.hasNext()) {
            Snippet snippet = iterator.next();
            Element element = new Element("snippet");

            Element category_xml = new Element("category");
            category_xml.setText(snippet.getCategory());
            element.addContent(category_xml);

            Element name_xml = new Element("name");
            name_xml.setText(snippet.getName());
            element.addContent(name_xml);

            String[] tags = snippet.getTags();
            for (String tag : tags) {
                Element tag_xml = new Element("tag");
                tag_xml.setText(tag);
                element.addContent(tag_xml);
            }

            Element syntax_xml = new Element("syntax");
            syntax_xml.setText(snippet.getSyntax());
            element.addContent(syntax_xml);

            Element code_xml = new Element("code");
            code_xml.setText(snippet.getCode());
            element.addContent(code_xml);

            Element comment_xml = new Element("comment");
            comment_xml.setText(snippet.getComment());
            element.addContent(comment_xml);

            Element locked_xml = new Element("locked");
            locked_xml.setText(String.valueOf(snippet.isLocked()));
            element.addContent(locked_xml);

            root_xml.addContent(element);
        }

        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(new Document(root_xml), new FileOutputStream(file));

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            success = false;
        }

        return success;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Snippet> readPackage(File file) {
        Element root_xml = null;

        try {
            SAXBuilder builder = new SAXBuilder();
            root_xml = builder.build(file).getRootElement();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        ArrayList<Snippet> array = new ArrayList<Snippet>();

        Iterator<Element> iterator = root_xml.getChildren("snippet").iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();

            String category = element.getChildTextTrim("category");
            String name = element.getChildTextTrim("name");
            String syntax = element.getChildTextTrim("syntax");
            String code = element.getChildTextTrim("code");
            String comment = element.getChildTextTrim("comment");

            List<Element> tags_xml = element.getChildren("tag");
            String[] tags = new String[tags_xml.size()];
            for (int i = 0; i < tags.length; i++) {
                tags[i] = tags_xml.get(i).getTextTrim();
            }

            boolean locked = Boolean.parseBoolean(element.getChildTextTrim("locked"));

            Snippet snippet = new Snippet(-1, category, name, tags, code, comment, syntax, locked);
            array.add(snippet);
        }

        return array;
    }

    public static void importFromXML(File file) throws IOException,
            JDOMException {
        Element root = new SAXBuilder().build(file).getRootElement();

        // ArrayList<Syntax> syntaxes = getSyntaxesFromFile(root);
        ArrayList<Snippet> snippets = getSnippetsFromFile(root);

        // for (Syntax s : syntaxes) {
        // DBMS.getInstance().insertNewSyntax(s);
        // }

        // inserisco gli snippet
        for (Snippet s : snippets) {
            DBMS.getInstance().insertNewSnippet(s);
        }
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Snippet> getSnippetsFromFile(Element root) {
        ArrayList<Snippet> snippets = new ArrayList<Snippet>();

        List<Element> categoriesList = root.getChildren("snippets");
        Iterator<Element> iterator = categoriesList.iterator();
        while (iterator.hasNext()) {
            Element categoryElement = iterator.next();

            String category = categoryElement.getAttributeValue("category");

            List<Element> snippetsList = categoryElement.getChildren("snippet");
            Iterator<Element> innerIterator = snippetsList.iterator();
            while (innerIterator.hasNext()) {
                Element snippet = innerIterator.next();

                int id = Integer.parseInt(snippet.getAttributeValue("id"));
                String name = snippet.getChildTextTrim("name");
                String syntax = snippet.getChildTextTrim("style");
                boolean locked = Boolean.parseBoolean(snippet.getChildTextTrim("is_locked"));
                String code = snippet.getChildTextTrim("code");
                String comment = snippet.getChildTextTrim("comment");

                List<Element> tagsList = snippet.getChild("tags").getChildren("tag");
                ArrayList<String> tags = new ArrayList<String>(tagsList.size());
                Iterator<Element> tagsIterator = tagsList.iterator();
                while (tagsIterator.hasNext()) {
                    tags.add(tagsIterator.next().getTextTrim());
                }

                snippets.add(new Snippet(id, category, name, tags.toArray(new String[] {}), code, comment, syntax, locked));
            }
        }

        return snippets;
    }

    // /**
    // * Restituisce l'array di stili ottenuto a partire dalla radice XML
    // indicata
    // * come parametro.
    // *
    // * @param root La radice del file XML da cui leggere gli stili.
    // * @return un array di <code>CodeSyntax</code> contenente tutti gli stili
    // * trovati a partire dalla radice XML indicata
    // */
    // @SuppressWarnings("unchecked")
    // public static ArrayList<Syntax> getSyntaxesFromFile(Element root) {
    // ArrayList<Syntax> syntaxes = new ArrayList<Syntax>();
    //
    // Iterator<Element> syntaxesIterator =
    // root.getChildren("style").iterator();
    // while (syntaxesIterator.hasNext()) {
    // Element currentElement = syntaxesIterator.next();
    //
    // String name = currentElement.getAttributeValue("name");
    //
    // ArrayList<String> keywords = new ArrayList<String>();
    // Iterator<Element> keywordsIterator = currentElement.getChildren(
    // "keyword").iterator();
    // while (keywordsIterator.hasNext()) {
    // keywords.add(keywordsIterator.next().getTextTrim());
    // }
    //
    // syntaxes.add(new Syntax(name, keywords));
    // }
    //
    // return syntaxes;
    // }
}
