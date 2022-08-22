package com.semenov.parser;

import com.semenov.exception.ApplicationException;
import com.semenov.validation.DataValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@RequiredArgsConstructor
public class OptionsHandler {
    private final DataValidator dataValidator;
    private final String[] args;
    private boolean isAscending = true;
    private boolean isStrings = true;
    private String outputFileName;
    private List<String> inputFileNames = new ArrayList<>();


    public void parse() {
        Options options = new Options();
        options.addOption("s", false, "Files with String.");
        options.addOption("i", false, "Files with Number.");
        options.addOption("a", false, "Ascending sort");
        options.addOption("d", false, "Descending sort");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.error("Command line parse error: {}", e.getMessage());
            System.exit(-1);
        }

        dataValidator.validateCommandLine(cmd);
        List<String> files = cmd.getArgList();
        dataValidator.validateSizeListFiles(files);

        if (cmd.hasOption('d')) isAscending = false;
        if (cmd.hasOption('i')) isStrings = false;

        outputFileName = getOutputFileName(files);
        inputFileNames = getInputFilesName(files);
    }

    private List<String> getInputFilesName(List<String> files) {
        return files.stream()
                .skip(1L)
                .collect(Collectors.toList());
    }

    private String getOutputFileName(List<String> files) {
        return files.stream()
                .findFirst()
                .orElseThrow(() -> new ApplicationException("Output files not found"));
    }
}

