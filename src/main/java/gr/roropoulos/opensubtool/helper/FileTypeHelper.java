package gr.roropoulos.opensubtool.helper;

import gr.roropoulos.opensubtool.OpenSubTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileTypeHelper {

    public static List<String> getFileTypeList() {
        ArrayList<String> filetypeList = new ArrayList<>();
        InputStream filetypes = OpenSubTool.class.getResourceAsStream("/filetypes.txt");
        Scanner s = new Scanner(filetypes);
        while (s.hasNext()) {
            filetypeList.add(s.next());
        }
        s.close();
        return filetypeList;
    }
}
