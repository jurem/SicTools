package sic.link.ui;


import sic.link.LinkerError;
import sic.link.section.ExtDef;
import sic.link.section.ExtRef;
import sic.link.section.Section;
import sic.link.section.Sections;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class EditSectionGui {

    private Sections sections;
    private Section selectedSection = null;
    private List<String> refs;
    private List<String> defs;
    private String selectedSymbol = null;
    private boolean selectedDef = false;

    // left and right panels - for section list and symbol list
    private JPanel leftPanel;
    private JPanel rightPanel;
    // their list modes
    DefaultListModel<String> sectionModel;
    DefaultListModel<String> refsModel;
    DefaultListModel<String> defsModel;
    JList<String> sectionList;
    JList<String> defsList;
    JList<String> refsList;

    // edit panels - one visible at a time
    private JPanel editSectionPanel;
    private JPanel editSymbolPanel;
    // components for editSection
    JLabel sectionTitle;
    JButton upButton;
    JButton downButton;
    JTextField secName;
    JButton secDelete;
    JButton secApply;
    // components for editSection
    JLabel symbolTitle;
    JTextField symName;
    JButton symDelete;
    JButton symApply;

    JButton proceed;
    JButton help;

    public EditSectionGui(Sections sections) {
        this.sections = sections;
    }

    public void sectionEdit(SectionEditListener listener) {
        JFrame frame = new JFrame("Section editor");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // initialize the components

        // for sections and symbols list
        leftPanel = new JPanel();
        rightPanel = new JPanel();
        sectionModel = new DefaultListModel<>();
        refsModel = new DefaultListModel<>();
        defsModel = new DefaultListModel<>();

        // edit panels
        editSectionPanel = new JPanel();
        editSymbolPanel = new JPanel();

        sectionTitle = new JLabel("Selected section : sec name");
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        upButton = new JButton("Up");
        downButton = new JButton("Down");
        secName = new JTextField("Name", 8);
        secDelete = new JButton("Remove");
        secApply = new JButton("Rename");

        symbolTitle = new JLabel("selected Symbol : sym name");
        symbolTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        symName = new JTextField("Name", 8);
        symDelete = new JButton("Remove");
        symApply = new JButton("Rename");

        sectionList = new JList<>(sectionModel);
        refsList = new JList<>(refsModel);
        defsList = new JList<>(defsModel);

        // final button
        proceed = new JButton("Done");
        help = new JButton("Help");



        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(new JLabel("Sections"));
        leftPanel.add(new JScrollPane(sectionList));

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(new JLabel("Definitions"));
        rightPanel.add(new JScrollPane(defsList));
        rightPanel.add(new JLabel("References"));
        rightPanel.add(new JScrollPane(refsList));

        // fill edit section panel
        editSectionPanel.setLayout(new GridLayout(4,1));
        JPanel secTitleContainer = new JPanel();
        secTitleContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        secTitleContainer.add(sectionTitle);
        editSectionPanel.add(secTitleContainer);
        JPanel updown = new JPanel();
        updown.add(upButton);
        updown.add(downButton);
        editSectionPanel.add(updown);
        JPanel secNamePanel = new JPanel();
        secNamePanel.add(new JLabel("Name:"));
        secNamePanel.add(secName);
        editSectionPanel.add(secNamePanel);
        JPanel secRenameDelete = new JPanel();
        secRenameDelete.add(secApply);
        secRenameDelete.add(secDelete);
        editSectionPanel.add(secRenameDelete);
        editSectionPanel.setVisible(false);

        // fill edit symbol panel
        editSymbolPanel.setLayout(new GridLayout(3,1));
        JPanel symTitleContainer = new JPanel();
        symTitleContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        symTitleContainer.add(symbolTitle);
        editSymbolPanel.add(symTitleContainer);
        JPanel symNamePanel = new JPanel();
        symNamePanel.add(new JLabel("Name"));
        symNamePanel.add(symName);
        editSymbolPanel.add(symNamePanel);
        JPanel symRenameDelete = new JPanel();
        symRenameDelete.add(symApply);
        symRenameDelete.add(symDelete);
        editSymbolPanel.add(symRenameDelete);
        editSymbolPanel.setVisible(false);

        // fill sections panel
        fillSections();

        sectionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (sectionList.getSelectedIndex() != -1) {

                    selectedSection = sections.getSections().get(sectionList.getSelectedIndex());

                    editSectionPanel.setVisible(true);
                    editSymbolPanel.setVisible(false);
                    sectionTitle.setText("Selected section: " + selectedSection.getName());
                    secName.setText(selectedSection.getName());

                    // fill symbols panel
                    fillSymbols();
                }
            }
        });
        refsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (refsList.getSelectedIndex() != -1) {

                    selectedSymbol = refs.get(refsList.getSelectedIndex());
                    selectedDef = false;

                    editSymbolPanel.setVisible(true);
                    editSectionPanel.setVisible(false);
                    symbolTitle.setText("Selected reference: " + selectedSymbol);
                    symName.setText(selectedSymbol);
                }
            }
        });

        defsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (defsList.getSelectedIndex() != -1) {

                    selectedSymbol = defs.get(defsList.getSelectedIndex());
                    selectedDef = true;

                    editSymbolPanel.setVisible(true);
                    editSectionPanel.setVisible(false);
                    symbolTitle.setText("Selected definition: " + selectedSymbol);
                    symName.setText(selectedSymbol);
                }
            }
        });

        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = sectionList.getSelectedIndex();
                if (index > 0 && selectedSection != null) {
                    try {
                        sections.move(selectedSection.getName(), index-1);
                        fillSections();
                        sectionList.setSelectedIndex(index-1);
                    } catch (LinkerError linkerError) {
                        LinkerGui.showError(linkerError.getMessage());
                    }
                }
            }
        });

        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = sectionList.getSelectedIndex();
                if (index >= 0 && index < sections.getSections().size()-1 && selectedSection != null) {
                    try {
                        sections.move(selectedSection.getName(), sectionList.getSelectedIndex()+1);
                        fillSections();
                        sectionList.setSelectedIndex(index+1);
                    } catch (LinkerError linkerError) {
                        LinkerGui.showError(linkerError.getMessage());
                    }
                }
            }
        });

        secApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (secName.getText().length() > 6) {
                    LinkerGui.showError("Please enter a name that has 6 or less characters.");
                } else if (!selectedSection.getName().equals(secName.getText())) {
                    try {
                        sections.rename(selectedSection.getName(), secName.getText());
                        fillSections();
                        sectionTitle.setText("Selected section: " + selectedSection.getName());
                    } catch (LinkerError linkerError) {
                        LinkerGui.showError(linkerError.getMessage());
                    }
                }
            }
        });

        symApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (symName.getText().length() > 6) {
                    LinkerGui.showError("Please enter a name that has 6 or less characters.");
                } else if (!selectedSymbol.equals(symName.getText())) {
                    try {
                        if (selectedDef)
                            sections.renameDef(selectedSection.getName(), selectedSymbol, symName.getText());
                        else
                            sections.renameRef(selectedSection.getName(), selectedSymbol, symName.getText());

                        fillSymbols();
                        selectedSymbol = symName.getText();
                        if (selectedDef)
                            symbolTitle.setText("Selected definition: " + selectedSymbol);
                        else
                            symbolTitle.setText("Selected reference: " + selectedSymbol);

                    } catch (LinkerError linkerError) {
                        LinkerGui.showError(linkerError.getMessage());
                    }
                }
            }
        });

        secDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int dialogResult = JOptionPane.showConfirmDialog (null, "Would you like to remove section " + selectedSection.getName() + " from linking?","Confirmation", JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    try {
                        sections.remove(selectedSection.getName());
                        fillSections();
                        editSectionPanel.setVisible(false);
                    } catch (LinkerError linkerError) {
                        LinkerGui.showError(linkerError.getMessage());
                    }
                }
            }
        });

        symDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "Would you like to remove symbol " + selectedSymbol + " from section " + selectedSection.getName() + "?","Confirmation", JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    try {
                        if (selectedDef)
                            sections.removeDef(selectedSection.getName(), selectedSymbol);
                        else
                            sections.removeRef(selectedSection.getName(), selectedSymbol);

                        fillSymbols();
                        editSymbolPanel.setVisible(false);
                    } catch (LinkerError linkerError) {
                        LinkerGui.showError(linkerError.getMessage());
                    }
                }
            }
        });

        proceed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();

                listener.onEdited(sections, "0");
            }
        });

        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });

        JPanel scrollpanels = new JPanel();
        scrollpanels.setLayout(new GridLayout(1, 2));
        scrollpanels.add(leftPanel);
        scrollpanels.add(rightPanel);

        JPanel editPanel = new JPanel();
        editPanel.add(editSectionPanel);
        editPanel.add(editSymbolPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(proceed);
        bottomPanel.add(help);

        frame.add(new JPanel(), BorderLayout.NORTH);
        frame.add(scrollpanels, BorderLayout.CENTER);
        frame.add(new JPanel(), BorderLayout.WEST);
        frame.add(editPanel, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    private void fillSections() {
        sectionModel.clear();
        int i=0;
        for (Section s : sections.getSections()) {
            sectionModel.addElement(i + " - " + s.getName());
            i++;
        }
    }

    private void fillSymbols() {
        refsModel.clear();
        defsModel.clear();
        refs = new ArrayList<>();
        defs = new ArrayList<>();

        for (ExtDef d : selectedSection.getExtDefs()) {
            defs.add(d.getName());
            defsModel.addElement(d.getName());
        }
        for (ExtRef r : selectedSection.getExtRefs()) {
            refs.add(r.getName());
            refsModel.addElement(r.getName());

        }
    }

    public static void showHelp() {
        String nl = System.lineSeparator();
        StringBuilder text = new StringBuilder();
        text.append("Section Editor for SIC/XE Linker")
                .append(nl)
                .append(nl)
                .append("Rename, move or remove the sections and symbols before linking.")
                .append(nl)
                .append(nl)
                .append("List on the left contains all the sections that were parsed from input files.")
                .append(nl)
                .append("Lists on the right contain external definitions and references for selected section.")
                .append(nl)
                .append("Panel on the right has the controls for changing the selected section or external symbol.")
                .append(nl)
                .append(nl)
                .append("Editing a section:")
                .append(nl)
                .append("- Select the section that you want to edit.")
                .append(nl)
                .append("- Rename the section using the Name text field and then click the Rename button.")
                .append(nl)
                .append("- Change the section's position on the list by moving it with the Up/Down buttons on the right.")
                .append(nl)
                .append("- Remove the section from linking by clicking the Remove button.")
                .append(nl)
                .append(nl)
                .append("Editing a symbol:")
                .append(nl)
                .append(" - Select the section that contains the symbol.")
                .append(nl)
                .append(" - Select the symbol from the References or Definitions panels.")
                .append(nl)
                .append(" - Rename the symbol using the Name text field and then click the Rename button.")
                .append(nl)
                .append(" - Remove the symbol from section by clicking the Remove button.")
                .append(nl)
                .append(nl)
                .append("When you want to finish editing and proceed with linking, click the Done button")
                .append(nl);

        JOptionPane optionPane = new JOptionPane(text.toString(), JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog("SIC Linker Help");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
}
