package gr.roropoulos.opensubtool.manager;

import com.github.wtekiela.opensub4j.api.OpenSubtitles;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import gr.roropoulos.opensubtool.helper.FileTypeHelper;
import gr.roropoulos.opensubtool.model.Movie;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.xmlrpc.XmlRpcException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MovieManager {

    private Path parentPath;
    private String parentStringPath;

    public MovieManager(String parentPath) {
        this.parentStringPath = parentPath;
    }

    public Boolean checkIfPathExists() {
        Path moviesPath = Paths.get(parentStringPath);
        if (Files.exists(moviesPath)) {
            this.parentPath = moviesPath;
            return true;
        } else
            return false;
    }

    public List<Movie> getInitialMovieList() {
        List<Movie> moviePathList = new ArrayList<>();
        List<String> fileTypeList = FileTypeHelper.getFileTypeList();

        try (Stream<Path> paths = Files.walk(parentPath)) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    if (fileTypeList.contains(FilenameUtils.getExtension(filePath.getFileName().toString()))) {
                        Movie movie = new Movie();
                        movie.setMovieFile(filePath.toFile());
                        if (checkIfSubtitleExist(movie))
                            movie.setMovieSubDownload(false);
                        else
                            movie.setMovieSubDownload(true);
                        moviePathList.add(movie);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return moviePathList;
    }

    public List<SubtitleInfo> fetchMovieSubtitles(Movie movie, String lang, OpenSubtitles openSubtitles) {
        List<SubtitleInfo> movieSubtitleInfoList = new ArrayList<>();

        try {
            movieSubtitleInfoList = openSubtitles.searchSubtitles(lang, movie.getMovieFile());
        } catch (IOException | XmlRpcException e) {
            e.printStackTrace();
        }
        return movieSubtitleInfoList;
    }

    public Boolean checkIfSubtitleExist(Movie movie) {
        File movieDirectory = new File(movie.getMovieFile().getParent());
        String[] extensions = new String[]{"srt", "sub", "sbv"};
        List<File> files = (List<File>) FileUtils.listFiles(movieDirectory, extensions, true);
        return !files.isEmpty();
    }

}
