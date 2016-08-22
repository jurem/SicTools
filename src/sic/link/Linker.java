package sic.link;

import sic.link.section.ExtDef;
import sic.link.section.Section;
import sic.link.section.Sections;
import sic.link.utils.Parser;
import sic.link.visitors.FirstPassVisitor;
import sic.link.visitors.SecondPassVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * main linker class
 * 'inputs' is a list of paths to .obj files to be linked
 * 'options' is a list of linker options
 * link() performs the linking and returns the resulting Section
 */
public class Linker {
    private static final String PHASE = "linker";

    private List<String> inputs;
    private Options options;


    public Linker(List<String> inputs, Options options) {
        this.inputs = inputs;
        this.options = options;
    }

    public Section link() throws LinkerError {
        Sections sections = parse();

        Section combined = passAndCombine(sections);

        return combined;
    }

    public Sections parse() throws LinkerError {
        // parse all the input files, add into a Sections class
        Sections sections = new Sections();

        for (String input : inputs) {
            Parser p = new Parser(input, options);
            sections.addSections(p.parse());
        }

        if (sections.getSections().size() == 0)
            throw new LinkerError(PHASE, "No sections found in given input files.");

        log("read " + sections.getSections().size() + " sections", sections.getSections());


        if (options.getMain() != null) {
            log("moving " + options.getMain() + " (main) to start of sections list");
            try {
                sections.move(options.getMain(), 0);
            } catch (LinkerError le) {
                throw new LinkerError("options", "specified main section " + options.getMain() + " does not exist");
            }
        }

        // name the section from options
        if (options.getOutputName() != null) {
            String name = options.getOutputName().replace(".obj", "");
            if (name.length() > 6)
                name = name.substring(0, 6);
            sections.setName(name);
            log("setting output section name to " + sections.getName());
        }

        return sections;
    }

    public Section passAndCombine(Sections sections) throws LinkerError {

        if (sections.getSections().size() == 0)
            throw new LinkerError(PHASE, "No sections to link.");

        if (sections.getName() == null)
            sections.setName(sections.getSections().get(0).getName());

        // External Symbol table - used in both visitors
        Map<String, ExtDef> esTable = new HashMap<>();

        log("starting first pass");
        // first pass - changes the addresses of sections, text records and ext definitions
        FirstPassVisitor firstPass = new FirstPassVisitor(esTable);
        firstPass.visit(sections);
        log(sections.getSections(), esTable.values());

        log("starting second pass");
        // second pass - modifies the text records according to the modification records
        SecondPassVisitor secondPassVisitor = new SecondPassVisitor(sections.getName(), esTable, options);
        secondPassVisitor.visit(sections);

        log("cleaning the output section (R and M records)");
        // clean out used R records
        sections.clean();

        log("combining section into one");
        // combine all of the sections into one
        Section combined = sections.combine(options.isKeep());

        if (options.isVerbose()) {
            System.out.println();
            System.out.println("finished linking");
            System.out.println("output linked section: ");
            System.out.println(combined);
        }

        return combined;
    }


    private void log(String str) {
        if (options.isVerbose()) {
            System.out.println();
            System.out.println(str);
        }
    }

    private void log(String str, List<Section> list) {
        if (options.isVerbose()) {
            System.out.println();
            System.out.println(str);
            for (Section s : list)
                System.out.println(s.toString());
        }

    }

    private void log(List<Section> sections, Collection<ExtDef> extDefs) {
        if (options.isVerbose()) {
            System.out.println();
            System.out.println("Control sections:");
            System.out.println(" Name     CS addr    Length ");
            for (Section s : sections)
                System.out.println(String.format("%6s | 0x%06X | 0x%06X", s.getName(), s.getStart(), s.getLength()));
            System.out.println("");
            System.out.println("External symbols:");
            System.out.println(" Name     CS addr    ES addr");
            for (ExtDef d : extDefs)
                System.out.println(String.format("%6s | 0x%06X | 0x%06X", d.getName(), d.getCsAddress(), d.getAddress()));
        }
    }
}
