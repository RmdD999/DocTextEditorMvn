import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class FileContentReplacer {
    public static String msg = "";

    public static String ReplaceTextFolders(String ori, String replace, File selectedFile) {
        try {
            File[] files = selectedFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    //recursion here for looping all the doc and docx files in one folder.
                    ReplaceTextFolders(ori, replace, file);
                }
                if (file.getName().endsWith("docx")) {
                    if(file.length() != 0) {
                        ReplaceSingleFile(ori, replace, file);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "替换完毕！";
    }

    public static String ReplaceSingleFile(String ori, String replace, File selectedFile) {
        Path filePath = Paths.get(selectedFile.getAbsolutePath());

        if (Files.exists(filePath)) {
            if (selectedFile.getName().endsWith("docx")) {
                //replace text for docx file
                msg = FileContentReplacer.replaceContentDocx(filePath, ori, replace, selectedFile);
            }
        }
        else{
            msg = "文件不存在!";
        }
        return msg;
    }

    //content replace for .docx file
    public static String replaceContentDocx(Path path, String ori, String replace, File selectedFile) {
        if(selectedFile.length() == 0) {
            return "此文件无内容";
        }
        // some files start with ~$XXX.docx which means this is a temporary file, or owner file.
        if(selectedFile.getName().startsWith("~$")){
            return selectedFile.getName()+" not supported, it is a temporary file!";
        }
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(path))) {
            XWPFWordExtractor xwe = new XWPFWordExtractor(doc);
            //for loop to replace text in paragraphs
            for (XWPFParagraph p : doc.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains(ori)) {
                            text = text.replace(ori, replace);
                            r.setText(text, 0);
                        }
                    }
                }
            }

            //for loop to replace text in table(s)
            for (XWPFTable tbl : doc.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            List<XWPFRun> runs = p.getRuns();
                            for (XWPFRun run : runs) {
                                String text = run.getText(0);
                                if (text != null && text.contains(ori)) {
                                    text = text.replace(ori, replace);
                                    run.setText(text, 0);
                                }
                            }
                        }
                    }
                }
            }
            doc.write(new FileOutputStream(selectedFile.getAbsoluteFile()));
            //finish writing and close all the stream
            xwe.close();
            doc.close();
            msg = "替换完成!";

        } catch (IOException e) {
            e.printStackTrace();
        }

        return msg;
    }

}
