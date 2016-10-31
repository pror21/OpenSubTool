package gr.roropoulos.opensubtool.helper;

import gr.roropoulos.opensubtool.OpenSubTool;
import gr.roropoulos.opensubtool.model.Language;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LanguageHelper {
    public static List<Language> getLanguageList() {
        List<Language> languageList = new ArrayList<>();
        InputStream xml = OpenSubTool.class.getResourceAsStream("/languages.xml");
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xml);
            doc.getDocumentElement().normalize();

            NodeList langNodeList = doc.getElementsByTagName("language");
            for (int temp = 0; temp < langNodeList.getLength(); temp++) {
                Node langNode = langNodeList.item(temp);
                if (langNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element langElement = (Element) langNode;

                    Language language = new Language();
                    language.setName(langElement.getElementsByTagName("name").item(0).getTextContent());
                    language.setCode(langElement.getElementsByTagName("code").item(0).getTextContent());

                    languageList.add(language);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return languageList;
    }
}
