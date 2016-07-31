package sic.link.section;

import sic.link.LinkerError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * A list of sections in a class
 */
public class Sections {

    private List<Section> sections;
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void sort() {
        Collections.sort(sections, (s1, s2) -> {
            if (s1.getStart() < s2.getStart())
                return -1;
            else if (s1.getStart() > s2.getStart())
                return 1;
            else
                return 0;
        });
    }

    public void makeFirst(String name) throws LinkerError {
        Section first = null;
        for (Section s : sections)
            if (s.getName().equals(name)) {
                first = s;
                break;
            }

        if (first == null)
            throw new LinkerError("options", "specified main section " + name + " does not exist");
        else {
            sections.remove(first);
            sections.add(0, first);
        }
    }

   /*
    * combines all sections into one
    * if section include external defs or refs they will be removed
    */
    public Section combine() {
        if (sections == null || sections.size() == 0)
            return null;

        // if sections are not named yet, name them after first section
        if (name == null)
            name = sections.get(0).getName();

        long start = sections.get(0).getStart();
        List<TRecord> tRecords = new ArrayList<>();
        List<MRecord> mRecords = new ArrayList<>();

        for (Section s : sections) {
            tRecords.addAll(s.gettRecords());
            mRecords.addAll(s.getmRecords());
        }
        Section last = sections.get(sections.size()-1);
        long length = last.getStart() + last.getLength() - start;

        Section combined = new Section(name, start, length, tRecords, mRecords, null, null, new ERecord(start));

        return combined;
    }
}
