package com.mihmetcrido;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    static final String LAST = "└── ";
    static final String NOTLAST = "├── ";
    static final String SUBFILE = "│   ";
    static final String LEVEL = "    ";

    public static void main(String[] args) {
        // Define command-line options
        Option pathOption = new Option("p", "path", true, "Path to the directory (default: current directory)");
        pathOption.setRequired(false);

        Option showHiddenOption = new Option("h", "show-hidden", false, "Show hidden files and directories");
        showHiddenOption.setRequired(false);

        Option excludeNameOption = new Option("x", "exclude-name", true, "Exclude files or directories by name (comma-separated, e.g., 'target,.git')");

        Option depthOption = new Option("d", "depth", true, "Limit the depth of the directory tree traversal");
        depthOption.setRequired(false);

        Option sortOption = new Option("s", "sort", true, "Sort files by: name (default), size, modified, or reverse");
        sortOption.setRequired(false);

        Option typeOption = new Option("t", "type", true, "Filter files by type (e.g., .txt, .java)");
        typeOption.setRequired(false);

        Option showSizeOption = new Option("z", "show-size", false, "Display file sizes");
        showSizeOption.setRequired(false);

        Option showModifiedOption = new Option("m", "show-modified", false, "Display the last modified date");
        showModifiedOption.setRequired(false);

        Option followLinksOption = new Option("l", "follow-links", false, "Follow symbolic links");
        followLinksOption.setRequired(false);

        Options options = new Options();
        options.addOption(pathOption);
        options.addOption(showHiddenOption);
        options.addOption(excludeNameOption);
        options.addOption(depthOption);
        options.addOption(sortOption);
        options.addOption(typeOption);
        options.addOption(showSizeOption);
        options.addOption(showModifiedOption);
        options.addOption(followLinksOption);

        HelpFormatter formatter = new HelpFormatter();

        try {
            processPath(args, options);
        } catch (ParseException e) {
            System.out.println("Error parsing command-line arguments: " + e.getMessage());
            formatter.printHelp("java PathHandler", options);
            System.exit(1);
        }
    }

    private static void processPath(final String[] args, final Options options) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String pathValue = cmd.getOptionValue("path", System.getProperty("user.dir"));
        boolean showHidden = cmd.hasOption("show-hidden");
        boolean showSize = cmd.hasOption("show-size");
        boolean showModified = cmd.hasOption("show-modified");
        boolean followLinks = cmd.hasOption("follow-links");

        // Depth option to limit the tree traversal depth
        int maxDepth = Integer.MAX_VALUE; // default: no depth limit
        if (cmd.hasOption("depth")) {
            maxDepth = Integer.parseInt(cmd.getOptionValue("depth"));
        }

        // Sorting option to determine sort order
        String sortOrder = cmd.getOptionValue("sort", "name").toLowerCase();

        // File type filter
        String fileTypeFilter = cmd.getOptionValue("type", "");

        // Excluded names list
        Set<String> excludedNames = new HashSet<>();
        if (cmd.hasOption("exclude-name")) {
            String[] excludeNamesArray = cmd.getOptionValue("exclude-name").split(",");
            excludedNames.addAll(Arrays.asList(excludeNamesArray));
        }

        Path path = Paths.get(pathValue);
        File currentPath = path.toFile();

        System.out.println("<" + currentPath.getName() + ">/");
        System.out.println(recursivelyBuildTree(currentPath, "", true, showHidden, excludedNames, maxDepth, 0, sortOrder, fileTypeFilter, showSize, showModified, followLinks));
    }

    private static String recursivelyBuildTree(File currentPath, String indent, boolean isLast, boolean showHidden,
        Set<String> excludedNames, int maxDepth, int currentDepth, String sortOrder,
        String fileTypeFilter, boolean showSize, boolean showModified, boolean followLinks) {
        if (currentDepth > maxDepth) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        File[] files = currentPath.listFiles();
        if (files != null) {
            // Sort the files based on the provided sortOrder
            Comparator<File> comparator = getComparator(sortOrder);
            Arrays.sort(files, comparator);

            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                // Check if the file should be excluded based on hidden status or name
                if ((!showHidden && (file.isHidden() || file.getName().startsWith("."))) || excludedNames.contains(file.getName())) {
                    continue; // Skip this file or directory
                }

                // Always show directories, but filter files based on file type
                boolean isMatchingFile = file.isFile() && (fileTypeFilter.isEmpty() || file.getName().endsWith(fileTypeFilter));
                boolean isDirectory = file.isDirectory();

                // Skip files that do not match the type filter
                if (!isMatchingFile && !isDirectory) {
                    continue; // Skip this file
                }

                boolean lastFileInDir = (i == files.length - 1);

                // Add the appropriate symbol depending on whether it's the last file in the directory
                builder.append(indent);
                if (lastFileInDir) {
                    builder.append(LAST);
                } else {
                    builder.append(NOTLAST);
                }

                builder.append(file.getName()).append(getFileIndicator(file));

                // Append file size if the option is enabled
                if (showSize && file.isFile()) {
                    builder.append(" [").append(file.length()).append(" bytes]");
                }

                // Append last modified date if the option is enabled
                if (showModified) {
                    builder.append(" (Modified: ").append(new Date(file.lastModified())).append(")");
                }

                builder.append(System.lineSeparator());

                // If the file is a directory or a symlink (with followLinks enabled), recursively build its tree structure
                if (isDirectory || (followLinks && file.isFile() && file.getAbsolutePath().contains("->"))) {
                    String newIndent = indent + (lastFileInDir ? LEVEL : SUBFILE);
                    builder.append(recursivelyBuildTree(file, newIndent, lastFileInDir, showHidden, excludedNames, maxDepth,
                        currentDepth + 1, sortOrder, fileTypeFilter, showSize, showModified, followLinks));
                }
            }
        }
        return builder.toString();
    }

    private static Comparator<File> getComparator(String sortOrder) {
        switch (sortOrder) {
            case "size":
                return Comparator.comparingLong(File::length);
            case "modified":
                return Comparator.comparingLong(File::lastModified);
            case "reverse":
                return Comparator.comparing(File::getName, Comparator.reverseOrder());
            default: // Default is by name
                return Comparator.comparing(file -> file.getName().toLowerCase());
        }
    }

    private static String getFileIndicator(final File file) {
        return file.isDirectory() ? "/" : "";
    }
}
