package gr.roropoulos.opensubtool.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;

public class Movie {

    private File movieFile;
    private Subtitle subtitle;
    private BooleanProperty downloadProperty = new SimpleBooleanProperty(false);

    public File getMovieFile() {
        return movieFile;
    }

    public void setMovieFile(File movieFile) {
        this.movieFile = movieFile;
    }

    public Subtitle getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Subtitle subtitle) {
        this.subtitle = subtitle;
    }

    public boolean getDownloadProperty() {
        return downloadProperty.get();
    }

    public BooleanProperty downloadProperty() {
        return downloadProperty;
    }

    public void setDownloadProperty(boolean downloadProperty) {
        this.downloadProperty.set(downloadProperty);
    }
}
