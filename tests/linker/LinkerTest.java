package linker;


import org.junit.Assert;
import org.junit.Test;

import sic.link.Linker;
import sic.link.LinkerError;
import sic.link.Options;
import sic.link.section.*;
import sic.link.utils.Parser;
import sic.link.utils.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class LinkerTest {

    @Test
    public void testEmpty() {
        System.out.println("running testEmpty, expecting a LinkerError");

        List<String> inputs = new ArrayList<>();
        Linker test = new Linker(inputs, new Options());
        try {
            Assert.assertNull("Linking empty inputs should return null", test.link());
        } catch (LinkerError le) {
            // Linking empty inputs should throw a LinkerError
            System.out.println("LinkerError: " + le.getMessage());
        }
    }

     @Test
     public void testOne1() {
        System.out.println("running testOne1");

        List<String> inputs = new ArrayList<>();
        inputs.add("tests/linker/one/file.obj");
        Options options = new Options();
        options.setOutputName("out.obj");
        options.setOutputPath("tests/linker/one/out.obj");

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

        List<MRecord> mRecords = new ArrayList<>();
        mRecords.add(new MRecord(0x1, 5, true, null));
        mRecords.add(new MRecord(0x8, 5, true, null));


        testTrecords(out, tRecords.size(), tRecords);
        testMrecords(out, mRecords.size(), mRecords, "out");
        testExtDefs(out, 0, new ArrayList<>());
        testExtRefs(out, 0, new ArrayList<>());

        if (out.getERecord() == null || out.getERecord().getStartAddr() != 0)
        Assert.fail("wrong E record message");

        testWriterParser(out, options);

        } catch (LinkerError le) {
        Assert.fail("LinkerError: " + le.getMessage());
        }
     }

    @Test
    public void testOne2() {
        System.out.println("running testOne2");

        List<String> inputs = new ArrayList<>();
        inputs.add("tests/linker/one2/prog.obj");
        Options options = new Options();
        options.setOutputName("linked.obj");
        options.setOutputPath("tests/linker/one2/linked.obj");

        Linker test = new Linker(inputs, options);
        try {
            Section out = test.link();

            testSection(out, "linked", 0, 0xF + 0x6 + 0xF);

            List<TRecord> tRecords = new ArrayList<>();

            String ref1 = String.format("%05X", 0xF);
            String ref2 = String.format("%05X", 0xF + 0x3);
            String sect3 = String.format("%05X", 0xF + 0x6);
            tRecords.add(new TRecord(0x0, 0xF, "031" + ref1 + "0F1" + ref2 + "4B1" + sect3 + "3F2FFD"));

            tRecords.add(new TRecord(0xF, 0x3, "00000F"));

            tRecords.add(new TRecord(0xF + 0x6, 0xC, "01000F19000F0F20034F0000"));

            List<MRecord> mRecords = new ArrayList<>();
            mRecords.add(new MRecord(0x1, 5, true, null));
            mRecords.add(new MRecord(0x5, 5, true, null));
            mRecords.add(new MRecord(0x9, 5, true, null));


            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, mRecords.size(), mRecords, "linked");
            testExtDefs(out, 0, new ArrayList<>());
            testExtRefs(out, 0, new ArrayList<>());

            if (out.getERecord() == null || out.getERecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

    @Test
    public void testMulti1() {
        System.out.println("running testMulti1");

        List<String> inputs = new ArrayList<>();
        inputs.add("tests/linker/multi/func.obj");
        inputs.add("tests/linker/multi/main.obj");
        Options options = new Options();
        options.setOutputName("out_multiple.obj");
        options.setOutputPath("tests/linker/multi/out.obj");
        options.setMain("main");

        Linker test = new Linker(inputs, options);
        try {
            Section out = test.link();

            testSection(out, "out_mu", 0, 0x7 + 0x12 + 0x1F + 0x9 + 0x6);

            List<TRecord> tRecords = new ArrayList<>();
            List<MRecord> mRecords = new ArrayList<>();

            String func = "00007";
            tRecords.add(new TRecord(
                    0x0,
                    0x7,
                    "4B1" + func + "3F2FFD"
            ));
            mRecords.add(new MRecord(0x1, 5, true, null));

            String ref1 = String.format("%05X", 0x7 + 0x12 + 0x1C);
            String ref2 = String.format("%05X", 0x7 + 0x12);
            tRecords.add(new TRecord(
                    0x7,
                    0x12,
                    "031" + ref1 + "1900050F1" + ref1 + "4B1" + ref2 + "4F0000"
            ));
            mRecords.add(new MRecord(0x7 + 0x1, 5, true, null));
            mRecords.add(new MRecord(0x7 + 0x8, 5, true, null));
            mRecords.add(new MRecord(0x7 + 0xC, 5, true, null));

            String data1 = String.format("%05X", 0x7 + 0x12 + 0x1F + 0x9);
            String data2 = String.format("%05X", 0x7 + 0x12 + 0x1F + 0x9 + 0x3);
            tRecords.add(new TRecord(0x7 + 0x12,
                    0x1F,
                    "031" + data1 + "1900050F1" + data1 + "031" + data2 + "1900030F1" + data2 +"0F20034F0000000001"
            ));
            mRecords.add(new MRecord(0x7 + 0x12 + 0x1, 5, true, null));
            mRecords.add(new MRecord(0x7 + 0x12 + 0x8, 5, true, null));
            mRecords.add(new MRecord(0x7 + 0x12 + 0xC, 5, true, null));
            mRecords.add(new MRecord(0x7 + 0x12 + 0x13, 5, true, null));

            tRecords.add(new TRecord(0x7 + 0x12 + 0x1F,
                    0x9,
                    "1B00001B00004F0000"
            ));

            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, mRecords.size(), mRecords, "out_mu");
            testExtDefs(out, 0, new ArrayList<>());
            testExtRefs(out, 0, new ArrayList<>());

            if (out.getERecord() == null || out.getERecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

    @Test
    public void testDemoStack() {
        System.out.println("running testDemoStack");

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
            List<MRecord> mRecords = new ArrayList<>();

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
            mRecords.add(new MRecord(0x4, 5, true, null));
            mRecords.add(new MRecord(0xB, 5, true, null));
            mRecords.add(new MRecord(0x12, 5, true, null));
            mRecords.add(new MRecord(0x19, 5, true, null));
            mRecords.add(new MRecord(0x20, 5, true, null));
            mRecords.add(new MRecord(0x24, 5, true, null));
            mRecords.add(new MRecord(0x2B, 5, true, null));
            mRecords.add(new MRecord(0x32, 5, true, null));
            mRecords.add(new MRecord(0x39, 5, true, null));

            tRecords.add(new TRecord(
                    0x4E + 0x1E, 0x6,
                    "0220034F00004E"
            ));

            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, mRecords.size(), mRecords, "demost");
            testExtDefs(out, 0, new ArrayList<>());
            testExtRefs(out, 0, new ArrayList<>());

            if (out.getERecord() == null || out.getERecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

    @Test
    public void testPartial1() {
        System.out.println("running testPartial1");

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
            List<MRecord> mRecords = new ArrayList<>();

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

            mRecords.add(new MRecord(0x1, 5, true, null));
            mRecords.add(new MRecord(0x8, 5, true, null));
            mRecords.add(new MRecord(0x12, 5, true, "m"));

            List<ExtRef> extRefs = new ArrayList<>();
            extRefs.add(new ExtRef("m"));

            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, mRecords.size(), mRecords, "prtial");
            testExtDefs(out, 0, new ArrayList<>());
            testExtRefs(out, extRefs.size(), extRefs);

            if (out.getERecord() == null || out.getERecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

    @Test
    public void testPartial2() {
        System.out.println("running testPartial2");

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
            List<MRecord> mRecords = new ArrayList<>();

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

            mRecords.add(new MRecord(0x1, 5, true, null));
            mRecords.add(new MRecord(0x8, 5, true, null));
            mRecords.add(new MRecord(0x12, 0x5, true, "m"));

            List<ExtRef> extRefs = new ArrayList<>();
            extRefs.add(new ExtRef("m"));

            List<ExtDef> extDefs = new ArrayList<>();
            extDefs.add(new ExtDef("a", 0x1E));
            extDefs.add(new ExtDef("b", 0x1E + 0x3));

            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, mRecords.size(), mRecords, "prtial");
            testExtDefs(out, extDefs.size(), extDefs);
            testExtRefs(out, extRefs.size(), extRefs);

            if (out.getERecord() == null || out.getERecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

    @Test
    public void testFactorial() {
        System.out.println("running testFactorial");

        // Link -o outfac.obj main.obj fact.obj print.obj stack.obj ending.obj
        List<String> inputs = new ArrayList<>();
        inputs.add("tests/linker/factorial/main.obj");
        inputs.add("tests/linker/factorial/fact.obj");
        inputs.add("tests/linker/factorial/print.obj");
        inputs.add("tests/linker/factorial/stack.obj");
        inputs.add("tests/linker/factorial/ending.obj");
        Options options = new Options();
        options.setOutputName("outfac.obj");
        options.setOutputPath("tests/linker/factorial/outfac.obj");

        Linker test = new Linker(inputs, options);
        try {
            Section out = test.link();

            testSection(out, "outfac", 0, 0xF2 + 0x103 + 0x123 + 0x27 + 0x3);

            List<TRecord> tRecords = new ArrayList<>();
            List<MRecord> mRecords = new ArrayList<>();

            // main.obj
            String fact = String.format("%05X", 0xF2);
            String print = String.format("%05X", 0xF2 + 0x103);
            String result = String.format("%05X", 0xF2 + 0x3A);
            String stinit = String.format("%05X", 0xF2 + 0x103 + 0x123);
            tRecords.add(new TRecord(
                    0, 0x1D,
                    "01202F" + "4B1" + stinit + "0100010F1" + result + "03201E1900010F201829000A33200F"
            ));

            tRecords.add(new TRecord(
                    0x1D, 0x15,
                    "4B1" + fact + "031" + result + "4B1" + print + "3F2FDB3F2FFD000000"
            ));
            mRecords.add(new MRecord(0x4, 5, true, null));
            mRecords.add(new MRecord(0xB, 5, true, null));
            mRecords.add(new MRecord(0x1E, 5, true, null));
            mRecords.add(new MRecord(0x22, 5, true, null));
            mRecords.add(new MRecord(0x26, 5, true, null));

            // fact.obj
            String pop = String.format("%05X", 0xF2 + 0x103 + 0x123 + 0x15);
            String push = String.format("%05X", 0xF2 + 0x103 + 0x123 + 0x6);
            tRecords.add(new TRecord(
                    0xF2, 0x1D,
                    "2900013320310F20341720344B1" + push + "03202D4B1" + push + "0320231D0001"
            ));
            tRecords.add(new TRecord(
                    0xF2 + 0x1D, 0x1D,
                    "4B2FE04B1" + pop + "0F20194B1" + pop + "23200C0F20090B200C4F00004F0000"
            ));
            tRecords.add(new TRecord(
                    0xF2 + 0x3A, 0x3,
                    "000001"
            ));
            mRecords.add(new MRecord(0xF2 + 0xD, 5, true, null));
            mRecords.add(new MRecord(0xF2 + 0x14, 5, true, null));
            mRecords.add(new MRecord(0xF2 + 0x21, 5, true, null));
            mRecords.add(new MRecord(0xF2 + 0x28, 5, true, null));

            // print.obj
            tRecords.add(new TRecord(
                    0xF2 + 0x103, 0x1E,
                    "0F205D03205A1F20512900003B200C03204821000A0F20423F2FE803203C"
            ));
            tRecords.add(new TRecord(
                    0xF2 + 0x103 + 0x1E, 0x1E,
                    "25000A0F203629000033202103203327202A190030DD00011D003023201E"
            ));
            tRecords.add(new TRecord(
                    0xF2 + 0x103 + 0x3C, 0x1E,
                    "0F201E03201E1F20180F20183F2FD00100010F200901000ADD00014F0000"
            ));
            tRecords.add(new TRecord(
                    0xF2 + 0x103 + 0x5A, 0x3,
                    "000001"
            ));

            //stack.obj
            tRecords.add(new TRecord(
                    0xF2 + 0x103 + 0x123, 0x1E,
                    "0F20214F00000E201B0320181900030F20124F000003200C1D00030F2006"
            ));
            tRecords.add(new TRecord(
                    0xF2 + 0x103 + 0x123 + 0x1E, 0x6,
                    "0220034F0000"
            ));

            // ending.obj
            tRecords.add(new TRecord(
                    0xF2 + 0x103 + 0x123 + 0x27, 0x3,
                    "000011"
            ));

            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, mRecords.size(), mRecords, null);
            testExtDefs(out, 0, new ArrayList<>());
            testExtRefs(out, 0, new ArrayList<>());

            if (out.getERecord() == null || out.getERecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

    @Test
    public void testNametest() {
        System.out.println("running testFactorial");

        // Link -o outfac.obj main.obj fact.obj print.obj stack.obj ending.obj
        List<String> inputs = new ArrayList<>();
        inputs.add("tests/linker/nametest/first.obj");
        inputs.add("tests/linker/nametest/test.obj");
        inputs.add("tests/linker/nametest/extd.obj");
        Options options = new Options();
        options.setOutputName("nmtest.obj");
        options.setOutputPath("tests/linker/nametest/nmtest.obj");

        Linker test = new Linker(inputs, options);
        try {
            Section out = test.link();

            testSection(out, "nmtest", 0, 0x139 + 0x2045 + 0x6);

            List<TRecord> tRecords = new ArrayList<>();
            List<MRecord> mRecords = new ArrayList<>();

            // first.obj
            String testRef = String.format("%05X", 0x139);

            tRecords.add(new TRecord(
                    0, 0xA,
                    "4B1" + testRef + "03000F0F2000"
            ));
            mRecords.add(new MRecord(0x1, 5, true, null));

            // test.obj
            String mrecord1 = String.format("%05X", 0x01033 + 0x139);
            String mrecord2 = String.format("%05X", 0x01036 + 0x139);
            String mrecord3 = String.format("%05X", 0x01039 + 0x139);
            String extd1 = String.format("%05X", 0x139 + 0x2045);
            String extd2 = String.format("%05X", 0x139 + 0x2045 + 0x3);
            String mrecord4 = String.format("%05X", 0x0203C + 0x139);
            tRecords.add(new TRecord(
                    0x139, 0x20,
                    "01000F0F1" + mrecord1 + "031" + mrecord2 + "0F2019031" + mrecord3 + "0F2015031" + extd1
                     + "0F2011031" + extd2
            ));
            tRecords.add(new TRecord(
                    0x139 + 0x20, 0x13,
                    "0F1" + mrecord4 + "4F000000000B00001600002100002C"
            ));
            tRecords.add(new TRecord(
                    0x139 + 0x1033, 0x9,
                    "0F10203C4F000000000B00001600002100002C"
            ));
            tRecords.add(new TRecord(
                    0x139 + 0x203C, 0x9,
                    "00005800006300006E"
            ));
            mRecords.add(new MRecord(0x139 + 0x4, 5, true, null));
            mRecords.add(new MRecord(0x139 + 0x8, 5, true, null));
            mRecords.add(new MRecord(0x139 + 0xF, 5, true, null));
            mRecords.add(new MRecord(0x139 + 0x16, 5, true, null)); // from refs
            mRecords.add(new MRecord(0x139 + 0x1D, 5, true, null)); // from refs
            mRecords.add(new MRecord(0x139 + 0x21, 5, true, null));

            // extd.obj
            tRecords.add(new TRecord(
                    0x139 + 0x2045, 0x6,
                    "00000F000010"
            ));


            testTrecords(out, tRecords.size(), tRecords);
            testMrecords(out, mRecords.size(), mRecords, "nmtest");
            testExtDefs(out, 0, new ArrayList<>());
            testExtRefs(out, 0, new ArrayList<>());

            if (out.getERecord() == null || out.getERecord().getStartAddr() != 0)
                Assert.fail("wrong E record message");

            testWriterParser(out, options);

        } catch (LinkerError le) {
            Assert.fail("LinkerError: " + le.getMessage());
        }
    }

    @Test
    public void testAbsolute() {
        System.out.println("running testAbsolute, expecting a LinkerError");

        List<String> inputs = new ArrayList<>();
        inputs.add("tests/linker/absolute/main.obj");
        inputs.add("tests/linker/absolute/abs.obj");
        Options options = new Options();
        options.setOutputName("absfail.obj");
        options.setOutputPath("tests/linker/absolute/absfail.obj");

        Linker test = new Linker(inputs, options);
        try {
            Section out = test.link();

        } catch (LinkerError linkerError) {
            // Linking absolute sections should throw a LinkerError
            System.out.println("LinkerError: " +  linkerError.getMessage());
        }
    }


    // private functions for testing
    // -----------------------------------------------------------------
    private void testSection(Section section, String name, long start, long length) {
        Assert.assertEquals("Section start test", start, section.getStart());
        Assert.assertEquals("Section length test", length, section.getLength());
        Assert.assertEquals("Section name test", name, section.getName());
    }

    private void testTrecords(Section section, int count, List<TRecord> list) {
        if (count <= 0) {
            if (!(section.getTRecords() == null || section.getTRecords().size() == 0)) {
                Assert.fail("Section has T records");
            }
        } else {
            Assert.assertEquals("T record count test", list.size(), section.getTRecords().size());


            ListIterator<TRecord> i1 = list.listIterator();
            ListIterator<TRecord> i2 = section.getTRecords().listIterator();
            int i=0;

            while (i1.hasNext() && i2.hasNext()) {
                TRecord t1 = i1.next();
                TRecord t2 = i2.next();

                Assert.assertEquals(i + "th T record start test",t1.getStartAddr(), t2.getStartAddr());
                Assert.assertEquals(i + "th T record length test",t1.getLength(), t2.getLength());
                Assert.assertEquals(i + "th T record text test",t1.getText(), t2.getText());
                i++;
            }
        }

    }

    private void testMrecords(Section section, int count, List<MRecord> list, String progname) {
        if (count <= 0) {
            if (!(section.getMRecords() == null || section.getMRecords().size() == 0)) {
                Assert.fail("Section still has M records");
            }
        } else {
            Assert.assertEquals("M record count test", list.size(), section.getMRecords().size());

            ListIterator<MRecord> i1 = list.listIterator();
            ListIterator<MRecord> i2  = section.getMRecords().listIterator();

            int i=0;

            while (i1.hasNext() && i2.hasNext()) {
                MRecord m1 = i1.next();
                MRecord m2 = i2.next();

                Assert.assertEquals(i + "th M record start test",m1.getStart(), m2.getStart());
                Assert.assertEquals(i + "th M record length test",m1.getLength(), m2.getLength());
                Assert.assertEquals(i + "th M record flag test",m1.isPositive(), m2.isPositive());
                if (m1.getSymbol() == null || m1.getSymbol().equals(progname)) {
                    if (m2.getSymbol() != null && !m2.getSymbol().equals(progname))
                        Assert.fail(i + "th M record should have +" + progname + " or no symbol");
                } else {
                    Assert.assertEquals(i + "th M record symbol test", m1.getSymbol(), m2.getSymbol());
                }
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
            Assert.assertEquals("D record count test", list.size(), section.getExtDefs().size());


            ListIterator<ExtDef> i1 = list.listIterator();
            ListIterator<ExtDef> i2 = section.getExtDefs().listIterator();
            int i=0;

            while (i1.hasNext() && i2.hasNext()) {
                ExtDef d1 = i1.next();
                ExtDef d2 = i2.next();

                Assert.assertEquals(i + "th D record address test",
                        d1.getAddress() + d1.getCsAddress(), d2.getAddress() + d2.getCsAddress());
                Assert.assertEquals(i + "th D record name test",d1.getName(), d2.getName());
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
            Assert.assertEquals("R record count test", list.size(), section.getExtRefs().size());

            ListIterator<ExtRef> i1 = list.listIterator();
            ListIterator<ExtRef> i2 = section.getExtRefs().listIterator();
            int i=0;

            while (i1.hasNext() && i2.hasNext()) {
                ExtRef r1 = i1.next();
                ExtRef r2 = i2.next();

                Assert.assertEquals(i + "th R record name test",r1.getName(), r2.getName());
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
            testTrecords(newSection, section.getTRecords() == null ? 0 : section.getTRecords().size(), section.getTRecords());
            testMrecords(newSection, section.getMRecords() == null ? 0 : section.getMRecords().size(), section.getMRecords(), newSection.getName());
            testExtDefs(newSection, section.getExtDefs() == null ? 0 : section.getExtDefs().size(), section.getExtDefs());
            testExtRefs(newSection, section.getExtRefs() == null ? 0 : section.getExtRefs().size(), section.getExtRefs());

            if (newSection.getERecord() != null && section.getERecord() != null) {
                Assert.assertEquals("E records should be the same", section.getERecord().getStartAddr(), newSection.getERecord().getStartAddr());
            } else if (!(newSection.getERecord() == null && section.getERecord() == null))
                Assert.fail("E records not the same");

        }

    }
}
