package sic.link.ui;


import sic.link.Linker;
import sic.link.LinkerError;
import sic.link.Options;
import sic.link.section.ExtDef;
import sic.link.section.ExtRef;
import sic.link.section.Section;
import sic.link.section.Sections;
import sic.link.utils.Writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LinkerCli {

    public static File link(Options options, List<String> inputs) throws LinkerError {
        Linker linker = new Linker(inputs, options);

        if (options.isEditing()) {
            Sections sections = linker.parse();

            sections = LinkerCli.sectionEdit(sections);

            Section linkedSection = linker.passAndCombine(sections);

            Writer writer = new Writer(linkedSection, options);
            return writer.write();

        } else {
            Section linkedSection = linker.link();

            Writer writer = new Writer(linkedSection, options);
            return writer.write();
        }

    }

    public static Sections sectionEdit(Sections sections) {
        System.out.println("SIC Linker Interactive Section Editor");
        System.out.println("Enter 'help' for list of commands or 'done' to finish editing");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String prompt = "> ";

        boolean done = false;
        while (!done) {
            System.out.print(prompt);
            try {

                String input = in.readLine();

                if (input == null) {
                    done = true;
                    break;
                }

                String[] args = input.split(" ");
                if (args.length == 0) {
                    continue;
                }
/*
                System.out.print("args = [" );
                for (String s : args)
                    System.out.print(s + ", ");
                System.out.println("]");
*/
                List<String> cleanedArgs = new ArrayList<>();
                for (String arg : args)
                    if (arg.length() > 0)
                        cleanedArgs.add(arg);

                args = new String[cleanedArgs.size()];
                cleanedArgs.toArray(args);
/*
                System.out.print("cleaned args = [" );
                for (String s : args)
                    System.out.print(s + ", ");
                System.out.println("]");
*/

                if (args.length == 0) {
                    continue;
                }

                switch (args[0]) {

                    // prints a list of all sections
                    case "list":
                        System.out.println("     Name       Defs      Refs");
                        int i = 0;
                        for (Section s : sections.getSections()) {
                            System.out.println(String.format("%2d)  %-6s |  %6d |  %6d |", i, s.getName(), s.getExtDefs().size(), s.getExtRefs().size()));
                            i++;
                        }
                        break;

                    // displays info about a section
                    // info <section name>
                    case "info":
                        Section secInfo = null;
                        if (args.length > 1) {
                            secInfo = sections.getSection(args[1]);
                        }

                        if (secInfo == null) {
                            System.out.println("Please specify a valid section name");
                        } else {
                            // display section info
                            System.out.println("External definitions:");
                            for (ExtDef d : secInfo.getExtDefs())
                                System.out.println(String.format(" - %-6s at 0x%06X", d.getName(), d.getAddress()));
                            if (secInfo.getExtDefs().size() == 0)
                                System.out.println(" no definitions ");

                            System.out.println("External references:");
                            for (ExtRef r : secInfo.getExtRefs())
                                System.out.println(String.format(" - %-6s", r.getName()));
                            if (secInfo.getExtRefs().size() == 0)
                                System.out.println(" no references ");

                        }
                        break;

                    // renames a section
                    // reaname <old name> <new name>
                    case "rename": {
                        String oldName = null;
                        String newName = null;

                        if (args.length > 1) {
                            oldName = args[1];
                            if (oldName.length() == 0)
                                oldName = null;
                        }

                        if (args.length > 2) {
                            newName = args[2];

                            if (newName.length() == 0)
                                newName = null;
                        }

                        if (oldName == null)
                            System.out.println("Please specify a valid section name");
                        else if (newName == null)
                            System.out.println("Please specify a new name for section " + oldName);
                        else if (newName.length() > 6) {
                            System.out.println("New section name should have 6 characters or less.");
                        } else {
                            try {
                                sections.rename(oldName, newName);
                            } catch (LinkerError linkerError) {
                                System.out.println(linkerError.getMessage());
                            }
                        }

                    } break;

                    // renames a symbol in a specified section
                    // rename-ref <section name> <old name> <new name>
                    // rename-def <section name> <old name> <new name>
                    case "rename-ref":
                    case "rename-def": {
                        String secName = null;
                        String symName = null;
                        String newName = null;

                        boolean def = args[0].equals("rename-def");


                        if (args.length > 1) {
                            secName = args[1];
                            if (secName.length() == 0)
                                secName = null;
                        }

                        if (args.length > 2) {
                            symName = args[2];
                            if (symName.length() == 0)
                                symName = null;
                        }

                        if (args.length > 3) {
                            newName = args[3];
                            if (newName.length() == 0)
                                newName = null;
                        }

                        if (secName == null)
                            System.out.println("Please specify a valid section name");
                        else if (symName == null)
                            System.out.println("Please specify a valid symbol name");
                        else if (newName == null)
                            System.out.println("Please specify a new name for symbol " + symName);
                        else if (newName.length() > 6) {
                            System.out.println("New symbol name should have 6 characters or less.");
                        } else {
                            try {
                                if (def)
                                    sections.renameDef(secName, symName, newName);
                                else
                                    sections.renameRef(secName, symName, newName);

                            } catch (LinkerError le) {
                                System.out.println(le.getMessage());
                            }
                        }
                    } break;


                    case "remove": {

                        String secName = null;

                        if (args.length > 1) {
                            secName = args[1];
                            if (secName.length() == 0)
                                secName = null;
                        }

                        if (secName == null) {
                            System.out.println("Please specify a valid section name");
                        } else {
                            try {
                                sections.remove(secName);
                            } catch (LinkerError le) {
                                System.out.println(le.getMessage());
                            }
                        }

                    } break;

                    case "remove-def":
                    case "remove-ref": {
                        String secName = null;
                        String symName = null;
                        boolean def = args[0].equals("remove-def");

                        if (args.length > 1) {
                            secName = args[1];
                            if (secName.length() == 0)
                                secName = null;
                        }

                        if (args.length > 2) {
                            symName = args[2];
                            if (symName.length() == 0)
                                symName = null;
                        }

                        if (secName == null) {
                            System.out.println("Please specify a valid section name");
                        } else  if (symName == null){
                            System.out.println("Please specify a valid symbol name");
                        } else {
                            try {
                                if (def)
                                    sections.removeDef(secName, symName);
                                else
                                    sections.removeRef(secName, symName);

                            } catch (LinkerError le) {
                                System.out.println(le.getMessage());
                            }
                        }

                    } break;

                    case "move": {

                        String secName = null;
                        int position = -1;

                        if (args.length > 1) {
                            secName = args[1];
                            if (secName.length() == 0)
                                secName = null;
                        }

                        if (args.length > 2) {
                            try {
                                position = Integer.parseInt(args[2]);
                            } catch (Throwable t) {
                                position = -1;
                            }

                        }

                        if (secName == null) {
                            System.out.println("Please specify a valid section name");
                        } else if (position < 0) {
                            System.out.println("Please specify a valid position");
                        } else {
                            try {
                                sections.move(secName, position);
                            } catch (LinkerError le) {
                                System.out.println(le.getMessage());
                            }
                        }

                    } break;

                    case "done":
                        done = true;
                        break;


                    case "help":
                    default:
                        System.out.println("Available commands:");
                        System.out.println();

                        System.out.println("  list : prints a list of sections");
                        System.out.println("  info <section> : prints detailed info about a section");
                        System.out.println("  rename <section> <new name>: renames a section");
                        System.out.println("  remove <section> : removes a section");
                        System.out.println("  rename-def <section> <symbol> <new name>: renames a definition in the section");
                        System.out.println("  rename-ref <section> <symbol> <new name>: renames a reference in the section");
                        System.out.println("  remove-def <section> <symbol> : removes a definition in the section");
                        System.out.println("  remove-ref <section> <symbol> : removes a reference in the section");
                        System.out.println("  move <section> <position>: moves section to specified position");

                        System.out.println();
                        System.out.println("  done : closes the editor");
                        System.out.println("  help : prints this help");

                }

            } catch (IOException ioe) {
                System.out.println("IO Exception, please try again");
            }
        }

        System.out.println("exiting interactive reorder");
        return sections;

    }
}
