package sic.link;

import sic.link.section.ExtDef;
import sic.link.section.Section;
import sic.link.section.Sections;
import sic.link.visitors.FirstPassVisitor;
import sic.link.visitors.SecondPassVisitor;

import java.io.*;
import java.util.ArrayList;
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

    // control section table contains all the control sections
    private Map<String, Section> csTable;
    private Map<String, ExtDef> esTable;

    private List<String> inputs;
    private List<String> options;


    public Linker(List<String> inputs, List<String> options) {
        this.inputs = inputs;
        this.options = options;

        csTable = new HashMap<>();
        esTable = new HashMap<>();
    }

    public Section link() throws LinkerError{
        //TODO add options


        // parse all the input files, add  into a Sections class
        Sections sections = new Sections();

        for (String input : inputs) {
            Parser p =  new Parser(input);
            sections.addSections(p.parse());
        }

        // note: any sorting of the sections should be done here


        // External Symbol table - used in both visitors
        Map<String, ExtDef> esTable = new HashMap<>();

        // first pass - changes the addresses of sections, text records and ext definitions
        FirstPassVisitor firstPass = new FirstPassVisitor(esTable);
        firstPass.visit(sections);

        // second pass - modifies the text records according to the modification records
        SecondPassVisitor secondPassVisitor = new SecondPassVisitor(esTable);
        secondPassVisitor.visit(sections);




        return null;
    }
}
