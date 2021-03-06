import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

/**
 * @author panchenhuan
 * @date 2018/7/9.
 * @time 13:50.
 *
 * xls 内容格式:
 * ---------------------------------------
 * |             | 中文 | 英文  | 日语 |...|
 * ---------------------------------------
 * |text_hello   | 你好 | hello | *** |...|
 * ---------------------------------------
 * |text_bye     | 再见 | bye   | *** |...|
 * ---------------------------------------
 */

class Main {

    public static void main(String[] args) {
        //翻译源文件路径
        String excelSourcePath = "/Users/android01/Desktop/Tools/mlily.xls";
        //需要的翻译的语言,要与excel文件的列头对应,也是输出文件的文件名
        String[] translations = {/*"中文", "英文", */"中文（繁体）", "德語","法語","西語","日語","韓語"};
        //输出路径
        String outputDirPath = "/Users/android01/Desktop/Tools/mlily";

        List<File> translationsXml = null;
        try {
            translationsXml = createTranslationsXml(translations, outputDirPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        File file = new File(excelSourcePath);
        Map<String, List<String>> parse = null;
        try {
            parse = parseXLS(file, translations, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (parse != null && !parse.isEmpty()) {
            System.out.println(parse.toString());
            Iterator<Map.Entry<String, List<String>>> iterator = parse.entrySet().iterator();
            try {
                while (iterator.hasNext()) {
                    Map.Entry<String, List<String>> entry = iterator.next();
                    String key = entry.getKey();
                    List<String> value = entry.getValue();
                    for (int i = 0; i < translations.length; i++) {
                        File xml = translationsXml.get(i);
                        String message = value.get(i);
                        writeMessage(xml,key,message);
                    }
                }
                for (File file1 : translationsXml) {
                    writeMessage(file1,"</resources>");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeMessage(File file, String keyword,String message) throws IOException {
        FileWriter fw = new FileWriter(file, true);
        String temp = "<string name=\""+keyword+"\">"+message+"</string>\n";
        fw.write(temp);
        fw.close();
    }
    private static void writeMessage(File file,String message) throws IOException {
        FileWriter fw = new FileWriter(file, true);
        fw.write(message);
        fw.close();
    }

    private static List<File> createTranslationsXml(@NotNull String[] translations, @NotNull String outputDirPath) throws IOException {
        List<File> files = new ArrayList<>();
        File outDir = new File(outputDirPath);
        if (!outDir.isDirectory()) outDir.mkdirs();
        for (String translation : translations) {
            File file = new File(outDir, translation + ".txt");
            if (file.exists()) file.delete();
            file.createNewFile();
            files.add(file);
            writeMessage(file,"<resources>\n");
        }
        return files;
    }

    private static Map<String, List<String>> parseXLS(@NotNull File file, @NotNull String[] translations, int sheetIndex) throws Exception {
        Map<String, List<String>> parseMap = new LinkedHashMap<>();

        Workbook workbook = Workbook.getWorkbook(file);
        Sheet sheet = workbook.getSheet(sheetIndex);
        int sheetRows = sheet.getRows();//总共的行数
        int sheetColumns = sheet.getColumns();//总共的列数

        int[] translationsIndexs = new int[translations.length];
        for (int i = 0; i < translations.length; i++) {
            translationsIndexs[i] = sheet.findCell(translations[i]).getColumn();
        }
        //sheet.getCell(列,行)
        for (int i = 1; i < sheetRows; i++) {
            String keyword = sheet.getCell(0, i).getContents();
            if (keyword == null||keyword.equals("")) continue;
            List<String> contents = new ArrayList<>();
            for (int translationsIndex : translationsIndexs) {
                String content = sheet.getCell(translationsIndex, i).getContents();
                if (content == null) content = "";
                contents.add(content);
            }
            parseMap.put(keyword, contents);
        }
        workbook.close();
        return parseMap;
    }
}
