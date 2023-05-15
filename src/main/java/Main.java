import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;

public class Main {
    public static void main(String[] args) {
        setUI();
    }

    public static void setUI(){
        JFrame f = new JFrame("Doc/Docs Text Editor"); // creating instance of JFrame
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
        JLabel l = setLabel();
        JLabel l2 = new JLabel("Original:");
//        l2.setBounds(70,200,100,40);
        JLabel l3 = new JLabel("Replace:");
//        l3.setBounds(70,250,100,40);

        //--------------------------------------------------
        //textfields
        JTextField oriTextField = new JFormattedTextField();
//        oriTextField.setBounds(125,200,150,40);
        JTextField replaceTextField = new JFormattedTextField();
//        replaceTextField.setBounds(125,250,150,40);

        JButton selectBtn = setSelectBtn();
        JButton convertBtn = setConvertBtn();

        p.add(selectBtn);
        p.add(l);
        p.add(l2);
        p.add(oriTextField);
        p.add(l3);
        p.add(replaceTextField);
        p.add(convertBtn); //adding JButton to JFrame
        f.add(p);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400,200);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static JLabel setLabel(){
        return new JLabel("Please enter the texts that your want to convert:");
    }

    public static JButton setSelectBtn(){
        JButton selectBtn = new JButton("Choose file(s)");
        selectBtn.addActionListener(e -> {
            try {
                openFileChooser();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return selectBtn;
    }

    public static JButton setConvertBtn(){
        return new JButton("Convert");
    }

    public static void openFileChooser() throws IOException {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //only could select doc or docx files
        jfc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()){
                    return true;
                }
                else{
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".doc") || filename.endsWith(".docx");
                }
            }

            @Override
            public String getDescription() {
                return "doc Files(*.doc)";
            }
        });

        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            if(selectedFile.isDirectory()){
                File[] files = selectedFile.listFiles();
                //todo process folders
            }
            else{
                //todo when it is only a file
                convert(selectedFile);
            }
        }

    }

    public static String convert(File file){
        String msg = "";
        Path filePath = Paths.get(file.getAbsolutePath());
        if (Files.exists(filePath)) {
            //find file via its path
            try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(filePath))){
                XWPFWordExtractor xwe = new XWPFWordExtractor(doc);
                String docText = xwe.getText();

                //for loop to replace text in paragraphs
                for(XWPFParagraph p : doc.getParagraphs()){
                    List<XWPFRun> runs = p.getRuns();
                    if(runs!=null) {
                        for(XWPFRun r: runs){
                            String text = r.getText(0);
                            if(text !=null && text.contains("Servlet")){
                                text = text.replace("Servlet","Raymond");
                                r.setText(text,0);
                            }
                        }
                    }
                }

                //for loop to replace text in table(s)
                for(XWPFTable tbl: doc.getTables()){
                    for(XWPFTableRow row: tbl.getRows()){
                        for(XWPFTableCell cell: row.getTableCells()){
                            for(XWPFParagraph p: cell.getParagraphs()){
                                for(XWPFRun run: p.getRuns()){
                                    String text = run.getText(0);
                                    if(text != null && text.contains("Servlet")){
                                        text = text.replace("Servlet","Raymond");
                                        run.setText(text,0);
                                    }
                                }
                            }
                        }
                    }
                }
                doc.write(new FileOutputStream("output.docx"));
                msg = "Converted completed!";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File not found at the specified path.");
            msg = "File not found at the specified path.";
        }
        return msg;
    }
}


//todo modify data in doc file