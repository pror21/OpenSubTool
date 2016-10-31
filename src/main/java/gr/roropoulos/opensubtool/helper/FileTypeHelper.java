package gr.roropoulos.opensubtool.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileTypeHelper {

    public static List<String> getFileTypeList(File file) {
        ArrayList<String> filetypeList = new ArrayList<>();
        try {
            Scanner s = new Scanner(file);
            while (s.hasNext()) {
                filetypeList.add(s.next());
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filetypeList;
    }
}
