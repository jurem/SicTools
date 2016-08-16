package sic.link.section;

import sic.link.LinkerError;

import java.util.*;

/*
 * A list of sections in a class
 */
public class Sections {

    private List<Section> sections;
    private Map<String, Section> map;
    private String name;

    public Sections () {
        this.sections = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public void addSection(Section section) throws LinkerError {

        if (map.get(section.getName()) != null)
            throw new LinkerError("sections", "Duplicated section name: " + section.getName(), section.getLocation());
        map.put(section.getName(), section);

        sections.add(section);
    }

    public void addSections(List<Section> sectionList) throws LinkerError {
        for (Section s : sectionList)
            addSection(s);
    }

    public void rename(String oldName, String newname) throws LinkerError {
        Section renamed = map.get(oldName);
        Section used = map.get(newname);
        if (renamed == null) {
            // old section is not in the map
            throw new LinkerError("rename section", oldName + " not found");
        } else if (used != null) {
            // new name is already used
            throw new LinkerError("rename section", newname + " is already in use");
        } else {
            renamed.setName(newname);
            map.remove(oldName);
            map.put(newname, renamed);
        }
    }

    public void renameDef(String sectionName, String oldName, String newName) throws LinkerError{
        Section s = map.get(sectionName);

        if (s == null)
            throw new LinkerError("rename definition","section " + sectionName + " not found");
        else {
            ExtDef symD = null;
            for (ExtDef d : s.getExtDefs())
                if (d.getName().equals(oldName))
                    symD = d;

            if (symD == null)
                throw new LinkerError("rename definition", oldName + " not found");
            else {
                if (symD != null)
                    symD.setName(newName);

            }
        }
    }

    public void renameRef(String sectionName, String oldName, String newName) throws LinkerError{
        Section s = map.get(sectionName);

        if (s == null)
            throw new LinkerError("rename reference", "section " + sectionName + " not found");
        else {
            ExtRef symR = null;
            boolean exists = false;

            for (ExtRef r : s.getExtRefs()) {
                if (r.getName().equals(oldName))
                    symR = r;

                if (r.getName().equals(newName)) {
                    exists = true;
                }
            }

            if (symR == null)
                throw new LinkerError("rename reference", oldName + " not found");
            else if (exists) {
                throw new LinkerError("rename reference", newName + " already exists");
            } else {
                if (symR != null)
                    symR.setName(newName);

                for (MRecord m : s.getmRecords()) {
                    if (m.getSymbol().equals(oldName)) {
                        m.setSymbol(newName);
                    }
                }
            }
        }
    }

    public void remove(String sectionName) throws LinkerError {
        Section s = map.get(sectionName);

        if (s == null)
            throw new LinkerError("remove section", sectionName + " not found");
        else {
            map.remove(sectionName);
            sections.remove(s);
        }
    }

    public void removeDef(String sectionName, String symbolName) throws LinkerError {
        Section s = map.get(sectionName);

        if (s == null)
            throw new LinkerError("remove definition", sectionName + " not found");
        else {

            boolean removed = false;

            ListIterator<ExtDef> iterDef = s.getExtDefs().listIterator();
            while(iterDef.hasNext()) {
                ExtDef d = iterDef.next();
                if (d.getName().equals(symbolName)) {
                    iterDef.remove();
                    removed = true;
                }
            }

            if (!removed)
                throw new LinkerError("remove definition", symbolName + " not found");
        }
    }
    public void removeRef(String sectionName, String symbolName) throws LinkerError {
        Section s = map.get(sectionName);

        if (s == null)
            throw new LinkerError("remove reference", sectionName + " not found");
        else {

            boolean removed = false;

            ListIterator<ExtRef> iterRef = s.getExtRefs().listIterator();
            while(iterRef.hasNext()) {
                ExtRef r = iterRef.next();
                if (r.getName().equals(symbolName)) {
                    iterRef.remove();
                    removed = true;
                }
            }

            ListIterator<MRecord> iterM = s.getmRecords().listIterator();
            while(iterM.hasNext()) {
                MRecord m = iterM.next();
                if (m.getSymbol().equals(symbolName)) {
                    iterM.remove();
                    removed = true;
                }
            }

            if (!removed)
                throw new LinkerError("remove reference", symbolName + " not found");
        }
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

    public Section getSection(String name) {
        return map.get(name);
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
        try {
            move(name, 0);
        } catch (LinkerError le) {
            throw new LinkerError("options", "specified main section " + name + " does not exist");
        }
    }

    public void move(String name, int position) throws LinkerError {
        Section section = null;
        for (Section s : sections)
            if (s.getName().equals(name)) {
                section = s;
                break;
            }

        if (section == null)
            throw new LinkerError("move section", name + " not found");
        else if (position >= sections.size() || position < 0)
            throw new LinkerError("move section", "position " + position + " is out of bounds");
        else {
            sections.remove(section);
            sections.add(position, section);
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
