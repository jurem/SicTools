package sic.link.ui;


import sic.link.LinkerError;
import sic.link.section.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LinkerCli {


    public static Sections interactiveReorder(Sections sections) {
        System.out.println("SIC Linker Interactive Section Editor");
        System.out.println("Type 'help' for list of commands or 'done' to finish editing");

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
                        System.out.println("    Name    Drecords Rrecords");
                        int i = 0;
                        for (Section s : sections.getSections()) {
                            System.out.println(String.format("%2d) %-6s | %6d | %6d ", i, s.getName(), s.getExtDefs().size(), s.getExtRefs().size()));
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
                            System.out.println(secInfo.getName());
                            System.out.println("External definitions:");
                            for (ExtDef d : secInfo.getExtDefs())
                                System.out.println(String.format(" - %-6s at 0x%06X", d.getName(), d.getAddress()));
                            if (secInfo.getExtDefs().size() == 0)
                                System.out.println(" - ");

                            System.out.println("External references:");
                            for (ExtRef r : secInfo.getExtRefs())
                                System.out.println(String.format(" - %-6s", r.getName()));
                            if (secInfo.getExtRefs().size() == 0)
                                System.out.println(" - ");

                            System.out.println(secInfo.gettRecords().size() + " T records");
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
                        else {
                            try {
                                sections.rename(oldName, newName);
                            } catch (LinkerError linkerError) {
                                System.out.println(linkerError.getMessage());
                            }
                        }

                    } break;

                    // renames a symbol in a specified section
                    // rename-sym <section name> <old name> <new name>
                    case "rename-sym": {
                        String secName = null;
                        String symName = null;
                        String newName = null;


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
                        else {
                            try {
                                sections.renameSymbol(secName, symName, newName);
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

                    case "remove-sym": {
                        String secName = null;
                        String symName = null;

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
                                sections.removeSymbol(secName, symName);
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
                        System.out.println("  rename-sym <section> <symbol> <new name>: renames a symbol in the section");
                        System.out.println("  remove-sym <section> <symbol> : removes a symbol in the section");
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
