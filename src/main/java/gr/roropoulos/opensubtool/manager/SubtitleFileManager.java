package gr.roropoulos.opensubtool.manager;

import com.sun.webkit.network.URLs;
import gr.roropoulos.opensubtool.model.Movie;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class SubtitleFileManager {
    private Movie movie;

    public SubtitleFileManager(Movie movie) {
        this.movie = movie;
    }

    public void downloadSubtitle() {
        String movieParentPath = movie.getMovieFile().getParent();
        String movieFileName = movie.getMovieFile().getName();
        String subtitleGzPath = movieParentPath + File.separator + FilenameUtils.getBaseName(movieFileName) + ".gz";
        String subtitleSrtPath = movieParentPath + File.separator + FilenameUtils.getBaseName(movieFileName) + ".srt";
        File subtitleFile = new File(subtitleGzPath);

        try {
            FileUtils.copyURLToFile(URLs.newURL(movie.getSubtitle().getSelectedSubtitleInfo().getDownloadLink()), subtitleFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        decompressGzFile(subtitleGzPath, subtitleSrtPath);
        deleteGzFile(subtitleGzPath);
    }

    private void decompressGzFile(String compressedFile, String decompressedFile) {
        try {
            FileInputStream fis = new FileInputStream(compressedFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(decompressedFile);
            copyFile(gis, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream is, OutputStream os) throws Exception {
        int oneByte;
        while ((oneByte = is.read()) != -1) {
            os.write(oneByte);
        }
        os.close();
        is.close();
    }

    private void deleteGzFile(String subtitleGzPath) {
        File fileToDelete = FileUtils.getFile(subtitleGzPath);
        FileUtils.deleteQuietly(fileToDelete);
    }
}
