package gr.roropoulos.opensubtool.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;

public class Movie {

    private File movieFile;
    private Subtitle movieSubtitle;
    private BooleanProperty movieSubDownload = new SimpleBooleanProperty(false);

    public File getMovieFile() {
        return movieFile;
    }

    public void setMovieFile(File movieFile) {
        this.movieFile = movieFile;
    }

    public Subtitle getMovieSubtitle() {
        return movieSubtitle;
    }

    public void setMovieSubtitle(Subtitle movieSubtitle) {
        this.movieSubtitle = movieSubtitle;
    }

    public boolean getMovieSubDownload() {
        return movieSubDownload.get();
    }

    public BooleanProperty movieSubDownloadProperty() {
        return movieSubDownload;
    }

    public void setMovieSubDownload(boolean movieSubDownload) {
        this.movieSubDownload.set(movieSubDownload);
    }
}
