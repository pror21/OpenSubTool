package gr.roropoulos.opensubtool.task;

import com.github.wtekiela.opensub4j.api.OpenSubtitles;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import gr.roropoulos.opensubtool.model.Movie;
import javafx.concurrent.Task;

import java.util.List;

public class FetchTask extends Task<List<SubtitleInfo>> {
    private Movie movie;
    private OpenSubtitles openSubtitles;
    private String lang;

    public FetchTask(Movie movie, String lang, OpenSubtitles openSubtitles) {
        this.movie = movie;
        this.lang = lang;
        this.openSubtitles = openSubtitles;
    }

    @Override
    public List<SubtitleInfo> call() throws Exception {
        return openSubtitles.searchSubtitles(lang, movie.getMovieFile());
    }
}
