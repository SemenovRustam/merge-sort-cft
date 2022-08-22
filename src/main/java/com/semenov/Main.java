package com.semenov;


import com.semenov.parser.OptionsHandler;
import com.semenov.service.SorterService;
import com.semenov.validation.DataValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("start sorted");
        DataValidator validator = new DataValidator();
        OptionsHandler optionsHandler = new OptionsHandler(validator, args);

        optionsHandler.parse();
        String outputFileName = optionsHandler.getOutputFileName();
        List<String> inputFileNames = optionsHandler.getInputFileNames();
        boolean isAscending = optionsHandler.isAscending();
        boolean isStrings = optionsHandler.isStrings();

        SorterService sorter = SorterService.builder()
                .inputFileNames(inputFileNames)
                .outputFileName(outputFileName)
                .isAscending(isAscending)
                .isStrings(isStrings)
                .build();

        sorter.sort();
        log.info("sorted finish successfully");
    }
}