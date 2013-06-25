package de.akesting.bollinger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Preconditions;

public class BollingerCommandLine {

    private static final String PRGNAME = "bollinger";
    private final Options options;
    private final CommandLineParser parser;
    private CommandLine cmdline = null;

    public BollingerCommandLine() {
        options = new Options();
        initOptions();
        parser = new GnuParser();
    }

    @SuppressWarnings("static-access")
    private void initOptions() {
        options.addOption(OptionBuilder.withDescription("prints this help message").withLongOpt("help").create("h"));
        options.addOption("f", "file", true, "input file");
        options.addOption("s", "sigma", true, "smoothing width parameter");
        options.addOption("x", "xcolumn", true, "first column of input data, default=" + getFirstInputColumn());
        options.addOption("y", "ycolumn", true, "second column of input data, default=" + getSecondInputColumn());
        options.addOption("w", "width", true, "output width, default: same width as input");
        options.addOption("c", "separator", true, "input file separator");
    }

    public void parse(String[] args) {
        try {
            cmdline = parser.parse(options, args, true);
            if (cmdline.hasOption("h")) {
                optionHelp();
            }
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            optionHelp();
        }
    }

    private void optionHelp() {
        System.out.println("option -h. Exit Programm");
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(PRGNAME, options);
        System.exit(0);
    }

    public String getInputFilename() {
        Preconditions.checkNotNull(cmdline, "commandline not yet parsed");
        return Preconditions.checkNotNull(cmdline.getOptionValue("f"));
    }

    public double getSmoothingParameter() {
        Preconditions.checkNotNull(cmdline, "commandline not yet parsed");
        return Double.parseDouble(Preconditions.checkNotNull(cmdline.getOptionValue("s")));
    }

    public double getOutputWidth() {
        if (cmdline != null && cmdline.hasOption("w")) {
            return Double.parseDouble(Preconditions.checkNotNull(cmdline.getOptionValue("w")));
        }
        return -1;
    }

    public boolean hasOutputWidth() {
        return getOutputWidth() != -1;
    }

    public int getFirstInputColumn() {
        if (cmdline != null && cmdline.hasOption("x")) {
            return Integer.parseInt(Preconditions.checkNotNull(cmdline.getOptionValue("x")));
        }
        return 1; // default
    }

    public int getSecondInputColumn() {
        if (cmdline != null && cmdline.hasOption("y")) {
            return Integer.parseInt(Preconditions.checkNotNull(cmdline.getOptionValue("y")));
        }
        return 2; // default
    }

    public char getSeparator() {
        if (cmdline != null && cmdline.hasOption("c")) {
            String value = cmdline.getOptionValue("c");
            Preconditions.checkNotNull(value);
            // Preconditions.checkArgument(value.length() == 1, "only one character separator allowed: " + value);
            return value.charAt(0);
        }
        return ' ';
    }

}
