package sic.link.section;

import sic.link.LinkerError;

import java.util.*;

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
    */
    public Section combine(boolean keep) {
        if (sections == null || sections.size() == 0)
            return null;

        // if sections are not named yet, name them after first section
        if (name == null)
            name = sections.get(0).getName();

        long start = sections.get(0).getStart();
        List<TRecord> tRecords = new ArrayList<>();
        List<MRecord> mRecords = new ArrayList<>();
        List<ExtDef> extDefs = new ArrayList<>();
        List<ExtRef> extRefs = new ArrayList<>();

        for (Section s : sections) {
            tRecords.addAll(s.gettRecords());
            mRecords.addAll(s.getmRecords());
            if (keep)
                extDefs.addAll(s.getExtDefs());
            extRefs.addAll(s.getExtRefs());
        }
        Section last = sections.get(sections.size()-1);
        long length = last.getStart() + last.getLength() - start;

        Section combined = new Section(name, start, length, tRecords, mRecords, extRefs, extDefs, new ERecord(start));

        return combined;
    }

    /*
     * cleans out unneccessary ExtRefs
     */

    public void clean(boolean force) {
        if (force) {
            // remove some of them
            for (Section s : sections) {
                if (s.getmRecords().size() == 0) {
                    // remove all
                    s.setExtRefs(new ArrayList<>());
                } else {
                    // add remaining mRecord symbols to a Set
                    Set<String> remaining = new HashSet<>();
                    for (MRecord m : s.getmRecords()) {
                        if (m.getSymbol() != null)
                            remaining.add(m.getSymbol());
                    }

                    // remove a ref if it's not in the Set
                    ListIterator<ExtRef> iter = s.getExtRefs().listIterator();
                    while (iter.hasNext()) {
                        ExtRef r = iter.next();
                        if (!remaining.contains(r.getName()))
                            iter.remove();
                    }
                }

            }

        }
        else {
            // remove all of them
            for (Section s : sections)
                s.setExtRefs(new ArrayList<>());
        }
    }
}
