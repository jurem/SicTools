package linker;


import org.junit.Assert;
import org.junit.Test;

import sic.link.Linker;
import sic.link.LinkerError;
import sic.link.Options;
import sic.link.section.*;
import sic.link.utils.Parser;
import sic.link.utils.Writer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class LinkerTest {

    @Test
    public void testEmpty() {
        System.out.println("running tests from " + System.getProperty("user.dir"));

        List<String> inputs = new ArrayList<>();
        Linker test = new Linker(inputs, new Options());
        try {
            Assert.assertNull("Linking empty inputs should return null", test.link());
        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

     @Test
     public void testOne1() {
            List<String> inputs = new ArrayList<>();
            inputs.add("tests/linker/one1/file.obj");
            Options options = new Options();
            options.setOutputName("out.obj");
            options.setOutputPath("tests/linker/one1/out.obj");

            Linker test = new Linker(inputs, options);
            try {
                Section out = test.link();

                testSection(out, "out", 0, 0xE + 0xF + 0x6);

                List<TRecord> tRecords = new ArrayList<>();

                String a = "0000E";
                String b = String.format("%05X", 0xE + 0x3);
                tRecords.add(new TRecord(0x0, 0xE, "031" + a + "1900050F1" + b + "3F2FFD"));

                tRecords.add(new TRecord(0xE, 0xF, "000001000001000001000001000001"));

                tRecords.add(new TRecord(0xE + 0xF, 0x6, "000001000001"));

                testTrecords(out, tRecords.size(), tRecords);
                testMrecords(out, 0, new ArrayList<>());
                testExtDefs(out, 0, new ArrayList<>());
                testExtRefs(out, 0, new ArrayList<>());

                if (out.geteRecord() == null || out.geteRecord().getStartAddr() != 0)
                    Assert.fail("wrong E record message");

                testWriterParser(out, options);

            } catch (LinkerError le) {
                Assert.fail("LinkerError: " + le.getMessage());
            }
     }

    @Test
    public void testMulti1() {
            List<String> inputs = new ArrayList<>();
            inputs.add("tests/linker/multi1/func.obj");
            inputs.add("tests/linker/multi1/main.obj");
            Options options = new Options();
            options.setOutputName("out_multiple.obj");
            options.setOutputPath("tests/linker/one1/out.obj");
            options.setMain("main");

            Linker test = new Linker(inputs, options);
            try {
                Section out = test.link();

                testSection(out, "out_mu", 0, 0x7 + 0x12 + 0x1F + 0x9 + 0x6);

                List<TRecord> tRecords = new ArrayList<>();

                String func = "00007";
                tRecords.add(new TRecord(
                        0x0,
                        0x7,
                        "4B1" + func + "3F2FFD"
                ));

                String ref1 = String.format("%05X", 0x7 + 0x12 + 0x1C);
                String ref2 = String.format("%05X", 0x7 + 0x12);
                tRecords.add(new TRecord(
                        0x7,
                        0x12,
                        "031" + ref1 + "1900050F1" + ref1 + "4B1" + ref2 + "4F0000"
                ));

                String data1 = String.format("%05X", 0x7 + 0x12 + 0x1F + 0x9);
                String data2 = String.format("%05X", 0x7 + 0x12 + 0x1F + 0x9 + 0x3);
                tRecords.add(new TRecord(0x7 + 0x12,
                        0x1F,
                        "031" + data1 + "1900050F1" + data1 + "031" + data2 + "1900030F1" + data2 +"0F20034F0000000001"
                ));

                tRecords.add(new TRecord(0x7 + 0x12 + 0x1F,
                        0x9,
                        "1B00001B00004F0000"
                ));

                testTrecords(out, tRecords.size(), tRecords);
                testMrecords(out, 0, new ArrayList<>());
                testExtDefs(out, 0, new ArrayList<>());
                testExtRefs(out, 0, new ArrayList<>());

                if (out.geteRecord() == null || out.geteRecord().getStartAddr() != 0)
                    Assert.fail("wrong E record message");

                testWriterParser(out, options);

            } catch (LinkerError le) {
                Assert.fail("LinkerError: " + le.getMessage());
            }
    }

    @Test
    public void testDemoStack() {
        List<String> inputs = new ArrayList<>();
        inputs.add("tests/linker/stack/main.obj");
        inputs.add("tests/linker/stack/stack.obj");
        Options options = new Options();
        options.setOutputName("demostack.obj");
        options.setOutputPath("tests/linker/stack/demostack.obj");
        options.setMain("main");

        Linker test = new Linker(inputs, options);
        try {
            Section out = test.link();

            testSection(out, "demost", 0, 0x4E + 0x27);

            List<TRecord> tRecords = new ArrayList<>();

            String pop = String.format("%05X", 0x4E + 0x15);
            String push = String.format("%05X", 0x4E + 0x6);
            String stinit = String.format("%05X", 0x4E);

            tRecords.add(new TRecord(
                    0x0, 0x1F,
                    "0101004B1" + stinit + "0100114B1" + push + "0100224B1" + push + "0100334B1" + push + "010044"
            ));

            tRecords.add(new TRecord(
                    0x1F, 0x1D,
                    "4B1" + push + "4B1" + pop + "0F20184B1" + pop + "0F20144B1" + pop + "0F20104B1" + pop
            ));

            tRecords.add(new TRecord(
                    0x3C, 0x12,
                    "0F200C3F2FFD0000AA0000AA0000AA0000AA"
            ));

            tRecords.add(new TRecord(
                    0x4E, 0x1E,
                    "0F20214F00000E201B0320181900030F20124F000003200C1D00030F2006"
            ));

            tRecords.add(new TRecord(
                    0x4E + 0x1E, 0x6,
                    "0220034F00004E"
            ));

            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, 0, new ArrayList<>());
            testExtDefs(out, 0, new ArrayList<>());
            testExtRefs(out, 0, new ArrayList<>());

            if (out.geteRecord() == null || out.geteRecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

    @Test
    public void testPartial1() {
        List<String> inputs = new ArrayList<>();
        inputs.add("tests/linker/partial/main.obj");
        inputs.add("tests/linker/partial/lib.obj");
        Options options = new Options();
        options.setOutputName("prtial.obj");
        options.setOutputPath("tests/linker/partial/prtial.obj");
        options.setMain("main");
        options.setForce(true);

        Linker test = new Linker(inputs, options);
        try {
            Section out = test.link();

            testSection(out, "prtial", 0, 0x6 + 0x1E);

            List<TRecord> tRecords = new ArrayList<>();

            String a = String.format("%05X", 0x1E);
            String b = String.format("%05X", 0x1E + 0x3);
            tRecords.add(new TRecord(
                    0, 0x1B,
                    "031" + a +"0F2014031" + b + "1B200D0F200A031000001B20030F2000"
            ));

            tRecords.add(new TRecord(
                    0x1E, 0x6,
                    "0000AA0000AA"
            ));

            List<MRecord> mRecords = new ArrayList<>();
            mRecords.add(new MRecord(0x12, 0x5, true, "m"));

            List<ExtRef> extRefs = new ArrayList<>();
            extRefs.add(new ExtRef("m"));

            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, mRecords.size(), mRecords);
            testExtDefs(out, 0, new ArrayList<>());
            testExtRefs(out, extRefs.size(), extRefs);

            if (out.geteRecord() == null || out.geteRecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

    @Test
    public void testPartial2() {
        List<String> inputs = new ArrayList<>();
        inputs.add("tests/linker/partial/main.obj");
        inputs.add("tests/linker/partial/lib.obj");
        Options options = new Options();
        options.setOutputName("prtial.obj");
        options.setOutputPath("tests/linker/partial/prtial.obj");
        options.setMain("main");
        options.setForce(true);
        options.setKeep(true);

        Linker test = new Linker(inputs, options);
        try {
            Section out = test.link();

            testSection(out, "prtial", 0, 0x6 + 0x1E);

            List<TRecord> tRecords = new ArrayList<>();

            String a = String.format("%05X", 0x1E);
            String b = String.format("%05X", 0x1E + 0x3);
            tRecords.add(new TRecord(
                    0, 0x1B,
                    "031" + a +"0F2014031" + b + "1B200D0F200A031000001B20030F2000"
            ));

            tRecords.add(new TRecord(
                    0x1E, 0x6,
                    "0000AA0000AA"
            ));

            List<MRecord> mRecords = new ArrayList<>();
            mRecords.add(new MRecord(0x12, 0x5, true, "m"));

            List<ExtRef> extRefs = new ArrayList<>();
            extRefs.add(new ExtRef("m"));

            List<ExtDef> extDefs = new ArrayList<>();
            extDefs.add(new ExtDef("a", 0x1E));
            extDefs.add(new ExtDef("b", 0x1E + 0x3));

            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, mRecords.size(), mRecords);
            testExtDefs(out, extDefs.size(), extDefs);
            testExtRefs(out, extRefs.size(), extRefs);

            if (out.geteRecord() == null || out.geteRecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }


    // private functions for testing

    private void testSection(Section section, String name, long start, long length) {
        if (!section.getName().equals(name))
            Assert.fail("Section not named correctly: '" + section.getName() + "' vs '" + name + "'");

        if (section.getStart() != start)
            Assert.fail("Section start not correct");

        if (section.getLength() != length)
            Assert.fail("Section start not correct");

    }

    private void testTrecords(Section section, int count, List<TRecord> list) {
        if (count <= 0) {
            if (!(section.gettRecords() == null || section.gettRecords().size() == 0)) {
                Assert.fail("Section has T records");
            }
        } else {
            if (section.gettRecords().size() != list.size()) {
                Assert.fail("Section doesn't have the correct number of T records");
                return;
            }

            ListIterator<TRecord> i1 = section.gettRecords().listIterator();
            ListIterator<TRecord> i2 = list.listIterator();
            int i=0;

            while (i1.hasNext() && i2.hasNext()) {
                TRecord t1 = i1.next();
                TRecord t2 = i2.next();

                if (t1.getStartAddr() != t2.getStartAddr())
                    Assert.fail(i + "th T record start is different");
                if (t1.getLength() != t2.getLength())
                    Assert.fail(i + "th T record length is different");
                if (!(t1.getText().equals(t2.getText())))
                    Assert.fail(i + "th T record text is different");

                i++;
            }
        }

    }

    private void testMrecords(Section section, int count, List<MRecord> list) {
        if (count <= 0) {
            if (!(section.getmRecords() == null || section.getmRecords().size() == 0)) {
                Assert.fail("Section still has M records");
            }
        } else {
            if (section.getmRecords().size() != list.size()) {
                Assert.fail("Section doesn't have the correct number of M records");
                return;
            }

            ListIterator<MRecord> i1 = section.getmRecords().listIterator();
            ListIterator<MRecord> i2 = list.listIterator();
            int i=0;

            while (i1.hasNext() && i2.hasNext()) {
                MRecord m1 = i1.next();
                MRecord m2 = i2.next();

                if (m1.getStart() != m2.getStart())
                    Assert.fail(i +"th M record start is different");
                if (m1.getLength() != m2.getLength())
                    Assert.fail(i + "th M record length is different");
                if (!(m1.getSymbol().equals(m2.getSymbol())))
                    Assert.fail(i + "th M record symbol is different");

                i++;
            }
        }

    }

    private void testExtDefs(Section section, int count, List<ExtDef> list) {
        if (count <= 0) {
            if (!(section.getExtDefs() == null || section.getExtDefs().size() == 0)) {
                Assert.fail("Section still has D records");
            }
        } else {
            if (section.getExtDefs().size() != list.size()) {
                Assert.fail("Section doesn't have the correct number of D records");
                return;
            }

            ListIterator<ExtDef> i1 = section.getExtDefs().listIterator();
            ListIterator<ExtDef> i2 = list.listIterator();
            int i=0;

            while (i1.hasNext() && i2.hasNext()) {
                ExtDef d1 = i1.next();
                ExtDef d2 = i2.next();

                if (d1.getAddress() + d1.getCsAddress() != d2.getAddress() + d2.getCsAddress())
                    Assert.fail(i + "th D record address is different");
                if (!(d1.getName().equals(d2.getName())))
                    Assert.fail(i + "th D record name is different");

                i++;
            }
        }

    }

    private void testExtRefs(Section section, int count, List<ExtRef> list) {
        if (count <= 0) {
            if (!(section.getExtRefs() == null || section.getExtRefs().size() == 0)) {
                Assert.fail("Section still has R records");
            }
        } else {
            if (section.getExtRefs().size() != list.size()) {
                Assert.fail("Section doesn't have the correct number of R records");
                return;
            }

            ListIterator<ExtRef> i1 = section.getExtRefs().listIterator();
            ListIterator<ExtRef> i2 = list.listIterator();
            int i=0;

            while (i1.hasNext() && i2.hasNext()) {
                ExtRef r1 = i1.next();
                ExtRef r2 = i2.next();

                if (!(r1.getName().equals(r2.getName())))
                    Assert.fail(i + "th R record name is different");

                i++;
            }
        }
    }

    private void testWriterParser(Section section, Options options) {
        Writer writer = new Writer(section, options);
        try {
            writer.write();
        } catch (LinkerError le) {
            Assert.fail(le.getMessage());
        }

        Parser parser = new Parser(options.getOutputPath(), options);

        List<Section> sects = null;
        try {
            sects = parser.parse();
        } catch (LinkerError le) {
            Assert.fail(le.getMessage());
        }

        if (section != null && sects != null) {
            Section newSection = sects.get(0);

            testSection(newSection, section.getName(), section.getStart(), section.getLength());
            testTrecords(newSection, section.gettRecords() == null ? 0 : section.gettRecords().size(), section.gettRecords());
            testMrecords(newSection, section.getmRecords() == null ? 0 : section.getmRecords().size(), section.getmRecords());
            testExtDefs(newSection, section.getExtDefs() == null ? 0 : section.getExtDefs().size(), section.getExtDefs());
            testExtRefs(newSection, section.getExtRefs() == null ? 0 : section.getExtRefs().size(), section.getExtRefs());

            if (newSection.geteRecord() != null && section.geteRecord() != null) {
                if (newSection.geteRecord().getStartAddr() != section.geteRecord().getStartAddr())
                    Assert.fail("E records not the same");
            } else if (!(newSection.geteRecord() == null && section.geteRecord() == null))
                Assert.fail("E records not the same");

        }

    }
}
