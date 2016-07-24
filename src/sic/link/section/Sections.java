package sic.link.section;

import java.util.ArrayList;
import java.util.List;

/*
 * A list of sections in a class
 */
public class Sections {

    private List<Section> sections;

    public Sections () {
        this.sections = new ArrayList<>();
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void addSections(List<Section> sectionList) {
        this.sections.addAll(sectionList);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
}
