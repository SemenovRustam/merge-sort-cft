package com.semenov.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;

import java.util.List;

@Slf4j
public class DataValidator {

    public void validateCommandLine(CommandLine commandLine) {
        if (commandLine == null) {
            log.error("Parse command line exception");
            System.exit(-1);
        }

        if (!(commandLine.hasOption('i') || commandLine.hasOption('s'))) {
            log.warn("Miss required option  -s or -i");
            System.exit(-1);
        }
        if (commandLine.hasOption('i') && commandLine.hasOption('s')) {
            log.warn("Must be 1 option : -s or -i");
            System.exit(-1);
        }
        if (commandLine.hasOption('a') && commandLine.hasOption('d')) {
            log.warn("Must be 1 option: -a or -d");
            System.exit(-1);
        }
    }

    public void validateSizeListFiles(List<String> listFiles) {
        if (listFiles.size() < 2) {
            log.warn("Not enough input files");
            System.exit(-1);
        }
    }
}
