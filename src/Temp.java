import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author android01
 * @date 2018/8/1.
 * @time 10:07.
 */

class Temp {
    public static void main(String[] args) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File("/Users/android01/Desktop/Tools/mlily/strings.txt"));

            NodeList table = document.getElementsByTagName("string");
            //原始中文值
            for (int i = 0; i < table.getLength(); i++) {
                Node string = table.item(i);
                String key = string.getAttributes().getNamedItem("name").getTextContent();
                String value = string.getChildNodes().item(0).getTextContent();
                map.put(key, value);
            }

            //替换翻译
            Document target = documentBuilder.parse(new File("/Users/android01/Desktop/Tools/mlily/中文（繁体）.txt"));
            NodeList targets = target.getElementsByTagName("string");
            for (int i = 0; i < targets.getLength(); i++) {
                Node item = targets.item(i);
                String key = item.getAttributes().getNamedItem("name").getTextContent();
                Node node = item.getChildNodes().item(0);
                if (node != null && map.get(key) !=null) {
                    map.put(key, node.getTextContent());
                }
            }

            File file = new File("/Users/android01/Desktop/Tools/mlily/中文（繁体）2.txt");
            file.createNewFile();
            FileWriter fw = new FileWriter(file, false);
            fw.write("<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">\n");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String temp = "<string name=\"" + entry.getKey() + "\">" + entry.getValue() + "</string>\n";
                fw.write(temp);
            }
            fw.write("\n" + "</resources>");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
