package gr.roropoulos.opensubtool.model;

import com.github.wtekiela.opensub4j.response.SubtitleInfo;

import java.util.ArrayList;
import java.util.List;

public class Subtitle {

    private List<SubtitleInfo> subtitleList = new ArrayList<>();
    private SubtitleInfo subtitleSelected;

    public List<SubtitleInfo> getSubtitleList() {
        return subtitleList;
    }

    public void setSubtitleList(List<SubtitleInfo> subtitleList) {
        this.subtitleList = subtitleList;
    }

    public SubtitleInfo getSubtitleSelected() {
        return subtitleSelected;
    }

    public void setSubtitleSelected(SubtitleInfo subtitleSelected) {
        this.subtitleSelected = subtitleSelected;
    }
}
