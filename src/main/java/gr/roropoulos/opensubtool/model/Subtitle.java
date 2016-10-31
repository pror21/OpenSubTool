package gr.roropoulos.opensubtool.model;

import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Subtitle {
    private final ObservableList<SubtitleInfo> infoList = FXCollections.observableArrayList();

    private SubtitleInfo selectedSubtitleInfo;

    public ObservableList<SubtitleInfo> getSubtitleInfoList() {
        return infoList;
    }

    public SubtitleInfo getSelectedSubtitleInfo() {
        return selectedSubtitleInfo;
    }

    public void setSelectedInfo(SubtitleInfo subtitleInfo) {
        selectedSubtitleInfo = subtitleInfo;
    }

}
