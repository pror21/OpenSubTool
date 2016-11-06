package gr.roropoulos.opensubtool.task;

import gr.roropoulos.opensubtool.manager.SubtitleFileManager;
import gr.roropoulos.opensubtool.model.Movie;
import javafx.concurrent.Task;

import java.util.List;

public class DownloadTask extends Task<Integer> {
    private List<Movie> movieList;

    public DownloadTask(List<Movie> movieList) { this.movieList = movieList; }

    @Override
    public Integer call() throws Exception {
        Integer counter = 0;
        for(Movie movie : movieList) {
            SubtitleFileManager subtitleFileManager = new SubtitleFileManager(movie);
            subtitleFileManager.downloadSubtitle();
            counter++;
        }
        return counter;
    }
}
