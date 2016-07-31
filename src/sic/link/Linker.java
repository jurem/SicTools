package sic.link;

import sic.link.section.ExtDef;
import sic.link.section.Section;
import sic.link.section.Sections;
import sic.link.utils.Parser;
import sic.link.visitors.FirstPassVisitor;
import sic.link.visitors.SecondPassVisitor;

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

    // control section table contains all the control sections
    private Map<String, Section> csTable;
    private Map<String, ExtDef> esTable;

    private List<String> inputs;
    private Options options;


    public Linker(List<String> inputs, Options options) {
        this.inputs = inputs;
        this.options = options;

        csTable = new HashMap<>();
        esTable = new HashMap<>();
    }

    public Section link() throws LinkerError {

        // parse all the input files, add  into a Sections class
        Sections sections = new Sections();

        for (String input : inputs) {
            Parser p =  new Parser(input, options);
            sections.addSections(p.parse());
        }

        if (options.isVerbose())
            for (Section s : sections.getSections())
                    System.out.println(s.toString());

        if (options.getMain() != null)
            sections.makeFirst(options.getMain());


        // name the section from options
        if (options.getOutputName() != null) {
            String name = options.getOutputName().replace(".obj", "");
            if (name.length() > 6)
                name = name.substring(0,6);
            sections.setName(name);
        }

        // External Symbol table - used in both visitors
        Map<String, ExtDef> esTable = new HashMap<>();

        // first pass - changes the addresses of sections, text records and ext definitions
        FirstPassVisitor firstPass = new FirstPassVisitor(esTable);
        firstPass.visit(sections);

        // second pass - modifies the text records according to the modification records
        SecondPassVisitor secondPassVisitor = new SecondPassVisitor(esTable, csTable, options);
        secondPassVisitor.visit(sections);

        // clean out used R records
        sections.clean(options.isForce());

        // combine all of the sections into one
        Section combined = sections.combine(options.isKeep());

        if (options.isVerbose()) {
            System.out.println("linked: ");
            System.out.println(combined);
        }

        return combined;
    }
}
