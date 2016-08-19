package sic.link.ui;


import sic.link.Linker;
import sic.link.LinkerError;
import sic.link.Options;
import sic.link.section.*;
import sic.link.utils.Writer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LinkerGui {

    private Options options;
    private List<String> inputs;
    private LinkListener listener;

    private JFrame frame;

    // top panel components
    private JTextField name;
    private JLabel label;
    private JButton browse;

    // right panel components
    private JButton up;
    private JButton down;
    private JButton remove;


    // options
    private JCheckBox force;
    private JCheckBox keep;
    private JCheckBox verbose;
    private JCheckBox edit;
    private JCheckBox main;
    private JTextField mainName;

    // bottom components
    private JButton add;
    private JButton help;
    private JButton done;

    // list
    private JList<String> list;
    private DefaultListModel<String> model;
    private JScrollPane scrollPane;



    public LinkerGui(Options options, List<String> inputs, LinkListener listener) {
        this.options = options;
        this.inputs = inputs;
        this.listener = listener;
    }

    public void gui() {

        frame = new JFrame("Select .obj files to link");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // central scrollpane
        model = new DefaultListModel<>();
        list = new JList<>(model);
        scrollPane = new JScrollPane(list);

        //top name input
        name = new JTextField(40);
        label = new JLabel("Output file:");
        browse = new JButton("Browse...");

        // bottom buttons
        add = new JButton("Add .obj");

        force = new JCheckBox("Force");
        force.setToolTipText("Force linking even if not all external symbols are resolved");

        keep = new JCheckBox("Keep");
        keep.setToolTipText("Keep external symbol definitions in the output obj");

        verbose = new JCheckBox("Verbose");
        verbose.setToolTipText("Show debbuging information on standard output");

        edit = new JCheckBox("Edit");
        verbose.setToolTipText("Edit the sections and symbols before linking");

        main = new JCheckBox("Main");
        main.setToolTipText("If not specified, first section will be used");
        mainName = new JTextField(8);
        mainName.setEnabled(false);
        mainName.setMaximumSize(new Dimension(mainName.getMaximumSize().width, mainName.getPreferredSize().height));
        mainName.setVisible(false);

        help = new JButton("Help");
        done = new JButton("Link");

        up = new JButton("Up");
        up.setEnabled(false);
        up.setPreferredSize(new Dimension(up.getMaximumSize().width, up.getPreferredSize().height));
        down = new JButton("Down");
        down.setEnabled(false);
        down.setPreferredSize(new Dimension(down.getMaximumSize().width, down.getPreferredSize().height));

        remove = new JButton("Remove");
        remove.setEnabled(false);
        remove.setHorizontalAlignment(SwingConstants.CENTER);

        // action listeners

        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fc.setFileFilter(new FileNameExtensionFilter("Sic object files", "obj"));

                if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    if (f.exists())
                        showWarning("Output file already exists and will be overwritten!");

                    name.setText(f.getAbsolutePath());
                }
            }
        });

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fc.setFileFilter(new FileNameExtensionFilter("Sic object files", "obj"));
                fc.setMultiSelectionEnabled(true);

                if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File[] files = fc.getSelectedFiles();
                    for (File f : files)
                        model.addElement(f.getPath());
                }
            }
        });

        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });

        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Options opt = new Options();
                if (options != null)
                    opt.copyFrom(options);

                String strName = name.getText();

                if (strName.length() <= 0) {
                    showError("Output file not specified");
                    return;
                } else if (!strName.endsWith(".obj")) {
                    strName += ".obj";
                }

                File out = new File(strName);
                opt.setOutputName(out.getName());
                opt.setOutputPath(out.getAbsolutePath());

                opt.setForce(force.isSelected());
                opt.setVerbose(verbose.isSelected());
                opt.setEditing(edit.isSelected());
                opt.setKeep(keep.isSelected());
                if (main.isSelected()) {
                    if (mainName.getText().length() <= 0) {
                        showError("Main section not specified");
                        return;
                    } else {
                        opt.setMain(mainName.getText());
                    }
                }
                final List<String> in = new ArrayList<String>();
                for (int i=0; i<model.size(); i++)
                    in.add(model.get(i));

                try {
                    link(in, opt);
                } catch (LinkerError linkerError) {
                    System.err.println(linkerError.getMessage());

                    //notify the listener
                    if (listener != null)
                        listener.onLinked(null, linkerError.getMessage());
                }

            }
        });

        up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection = list.getSelectedIndex();
                if (selection > 0 && selection < model.size()) {
                    String selectedStr = model.remove(selection);
                    model.add(selection - 1, selectedStr);
                    list.setSelectedIndex(selection - 1);
                }
            }
        });

        down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection = list.getSelectedIndex();
                if (selection >= 0 && selection < model.size()-1) {
                    String selectedStr = model.remove(selection);
                    model.add(selection + 1, selectedStr);
                    list.setSelectedIndex(selection + 1);
                }
            }
        });

        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection = list.getSelectedIndex();
                if (selection >= 0 && selection < model.size()) {
                    model.remove(selection);
                }
            }
        });

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedIndex() == -1) {
                    up.setEnabled(false);
                    down.setEnabled(false);
                    remove.setEnabled(false);
                } else {
                    up.setEnabled(true);
                    down.setEnabled(true);
                    remove.setEnabled(true);
                }
            }
        });

        // fill with any info already given
        if (options != null) {
            if (options.getOutputPath() != null)
                name.setText(options.getOutputPath());
            if (options.isForce())
                force.setSelected(true);
            if (options.isKeep())
                keep.setSelected(true);
            if (options.isVerbose())
                verbose.setSelected(true);
            if (options.isEditing())
                edit.setSelected(true);
            if (options.getMain() != null) {
                main.setSelected(true);
                mainName.setText(options.getMain());
                mainName.setVisible(true);
                mainName.setEnabled(true);
            }

        }

        if (inputs != null) {
            for (String str : inputs)
                model.addElement(str);
        }

        JPanel botPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel optionsPanel = new JPanel();
        JPanel topPanel = new JPanel();

        topPanel.add(label);
        topPanel.add(name);
        topPanel.add(browse);

        rightPanel.add(up);
        rightPanel.add(down);
        rightPanel.add(remove);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        optionsPanel.add(new JLabel("Options:"));
        optionsPanel.add(force);
        optionsPanel.add(keep);
        optionsPanel.add(verbose);
        optionsPanel.add(edit);
        optionsPanel.add(main);
        optionsPanel.add(mainName);
        main.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (main.isSelected()) {
                    mainName.setEnabled(true);
                    mainName.setVisible(true);
                    mainName.getParent().revalidate();
                } else {
                    mainName.setEnabled(false);
                    mainName.setVisible(false);
                    mainName.getParent().revalidate();

                }
            }
        });
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        rightPanel.add(new JPanel());
        rightPanel.add(optionsPanel);

        botPanel.add(add);
        botPanel.add(done);
        botPanel.add(help);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(botPanel, BorderLayout.SOUTH);
        frame.add(rightPanel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JPanel(), BorderLayout.WEST);


        frame.setSize(960, 480);
        frame.setVisible(true);

    }

    private void link(List<String> in, Options opt) throws LinkerError {
        final Linker linker = new Linker(in, opt);

        if (opt.isEditing()) {
            Sections sections = linker.parse();

            EditSectionGui editSectionGui = new EditSectionGui(sections);
            editSectionGui.sectionEdit(new SectionEditListener() {
                @Override
                public void onEdited(Sections sections, String message) {

                    Section linkedSection = null;
                    try {
                        linkedSection = linker.passAndCombine(sections);

                        Writer writer = new Writer(linkedSection, options);
                        File file = writer.write();

                        //notify the listener
                        if (listener != null)
                            listener.onLinked(file, "0");
                        frame.dispose();

                    } catch (LinkerError linkerError) {
                        System.err.println(linkerError.getMessage());

                        //notify the listener
                        if (listener != null)
                            listener.onLinked(null, linkerError.getMessage());
                    }
                }
            });
        } else {
            Section linkedSection = linker.link();

            Writer writer = new Writer(linkedSection, opt);
            File file = writer.write();

            //notify the listener
            if (listener != null)
                listener.onLinked(file, "0");
            frame.dispose();
        }
    }

    // static methods for dialogs

    public static void showSuccess(String path) {
        JOptionPane optionPane = new JOptionPane("Linking successful, output: " + path, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog("Success");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    public static void showError(String error) {
        JOptionPane optionPane = new JOptionPane(error, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog("Error");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    public static void showWarning(String warning) {
        JOptionPane optionPane = new JOptionPane(warning, JOptionPane.WARNING_MESSAGE);
        JDialog dialog = optionPane.createDialog("Warning");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    public static void showHelp() {
        String nl = System.lineSeparator();
        StringBuilder text = new StringBuilder();
        text.append("Linker for SIC/XE Hypothetical computer")
                .append(nl)
                .append(nl)
                .append("Links the given relative obj files into one, and resolves references between them them using the R and D records.")
                .append(nl)
                .append("The resulting file with resolved references can be loaded and executed using the SicTools simulator.")
                .append(nl)
                .append(nl)
                .append("Usage:")
                .append(nl)
                .append("1. Specify the output file using the input field at the top.")
                .append(nl)
                .append("2. Add the assembled obj files to the list using the 'Add .obj' button at the bottom.")
                .append(nl)
                .append("3. Move them in the desired order using the Up/Down/Remove buttons on the right.")
                .append(nl)
                .append("4. Specify any other options by ticking the checkboxes on the bottom right.")
                .append(nl)
                .append("5. Start the linking process by pressing the 'Link' button at the bottom.")
                .append(nl)
                .append(nl)
                .append("Options: ")
                .append(nl)
                .append(" - Force : forces linking even if some references were not resolved. The file needs to be linked further.")
                .append(nl)
                .append(" - Keep : keeps the external symbol definitions in the obj file.")
                .append(nl)
                .append(" - Main : specifies the main/starting section. When this option is selected an input field for the section name will be shown.")
                .append(nl)
                .append(" - Interactive : allows the sections and symbols to be edited before linking.")
                .append(nl);


        JOptionPane optionPane = new JOptionPane(text.toString(), JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog("SIC Linker Help");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
}
