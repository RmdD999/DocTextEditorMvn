import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import static javax.swing.JOptionPane.showMessageDialog;

public class Main {
    static JPanel panel = new JPanel();
    static JLabel currentSelectLabel = new JLabel();
    static JTextField oriTextField = new JFormattedTextField();
    static JTextField replaceTextField = new JFormattedTextField();
    static File selectedFile = null;

    public static void main(String[] args) {
        setUI();
    }

    public static void setUI() {
        JFrame f = new JFrame("文本替换工具"); // creating instance of JFrame

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JLabel l = setLabel();
        JLabel l2 = new JLabel("原文字:");
        JLabel l3 = new JLabel("替换文字:");

        JButton selectBtn = setSelectBtn();
        JButton convertBtn = setConvertBtn();

        panel.add(selectBtn);
        panel.add(currentSelectLabel);
        panel.add(l);
        panel.add(l2);
        panel.add(oriTextField);
        panel.add(l3);
        panel.add(replaceTextField);
        panel.add(convertBtn); //adding JButton to JFrame
        f.add(panel);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 200);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static JLabel setLabel() {
        return new JLabel("请输入需要替换的文本:");
    }

    public static JButton setSelectBtn() {
        JButton selectBtn = new JButton("选择文件");
        selectBtn.addActionListener(e -> {
            try {
                openFileChooser();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return selectBtn;
    }

    public static JButton setConvertBtn() {
        JButton convertBtn = new JButton("替换");
        convertBtn.addActionListener(e -> {
            try {
                convert();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        return convertBtn;
    }

    public static void openFileChooser() throws IOException {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
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
            selectedFile = jfc.getSelectedFile();
            showMessageDialog(null, "你选择了: " + selectedFile.getName());
        }

    }

    public static void convert() throws IOException {
        String msg = "";
        String originalText = oriTextField.getText().trim();
        String replaceText = replaceTextField.getText().trim();

        if (originalText.isEmpty() || originalText.isBlank() || replaceText.isEmpty() || replaceText.isBlank()) {
            msg = "转换失败，请填写要转换的文本";
        }
         else if (selectedFile == null) {
            msg = "请选择要转换的文件.";
        }
         else if(selectedFile.length()==0){
             msg = "此文件没有任何内容";
        }else {
            //single file
            if (selectedFile.isFile()) {
                msg = FileContentReplacer.ReplaceSingleFile(originalText,replaceText,selectedFile);
                // folder(s)
            } else {
                msg = FileContentReplacer.ReplaceTextFolders(originalText,replaceText,selectedFile);
            }
        }
        showMessageDialog(null, msg);
    }

}