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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jcodecollector.data.DBMS;
import jcodecollector.common.bean.Snippet;
import jcodecollector.util.GeneralInfo;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class PackageManager {

    /**
     * Esporta in XML un gruppo di snippet.
     * 
     * @param file Il file in cui salvare gli snippet.
     * @param category La categoria degli snippet da esportare. Se
     *        <code>null</code> vengono esportati tutti gli snippet.
     * @return <code>true</code> se l'esportazione avviene con successo,
     *         <code>false</code> altrimenti
     */
    public static boolean exportSnippets(File file, String category) {
        ArrayList<Snippet> array = null;

        if (category == null) {
            array = DBMS.getInstance().getAllSnippets();
        } else {
            array = DBMS.getInstance().getSnippets(category);
        }

        Element root = new Element("jcc-snippets-package");
        root.setAttribute("version", GeneralInfo.APPLICATION_VERSION);

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

            root.addContent(element);
        }

        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(new Document(root), new FileOutputStream(file));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Snippet> readPackage(File file) {
        ArrayList<Snippet> array = new ArrayList<Snippet>();
        Element root = null;

        try {
            SAXBuilder builder = new SAXBuilder();
            root = builder.build(file).getRootElement();
        } catch (Exception ex) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Iterator<Element> iterator = root.getChildren("snippet").iterator();
        while (iterator.hasNext()) {
            // l'elemento e' uno snippet
            Element e = iterator.next();

            String category = e.getChildTextTrim("category");
            String name = e.getChildTextTrim("name");
            String syntax = e.getChildTextTrim("syntax");
            String code = e.getChildTextTrim("code");
            String comment = e.getChildTextTrim("comment");

            @SuppressWarnings("unchecked")
            List<Element> tagElements = e.getChildren("tag");
            String[] tags = new String[tagElements.size()];
            for (int i = 0; i < tags.length; i++) {
                tags[i] = tagElements.get(i).getTextTrim();
            }

            // creo lo snippet
            Snippet snippet = new Snippet(-1, category, name, tags, code, comment, syntax, false);
            array.add(snippet);
        }

        return array;
    }

    private PackageManager() {
        // do nothing
    }

}
