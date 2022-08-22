package com.semenov.service;

import com.semenov.exception.ApplicationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@Builder
public class SorterService {
    private final String outputFileName;
    private final List<String> inputFileNames;
    private final boolean isAscending;
    private final boolean isStrings;

    public void sort() {
        Scanner[] scanners = getScanners();
        List<Object> list = readFirstValues(scanners);

        while (!list.stream().allMatch(Objects::isNull)) {
            List<Object> listWithoutNull = list.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Object currentValue = getValueFromList(listWithoutNull);
            int currentValueIndex = list.indexOf(currentValue);

            Object nextValue = readNextValue(scanners[currentValueIndex], currentValue);

            list.set(currentValueIndex, nextValue);

            writeValueToFile(currentValue);
        }
    }

    private Scanner[] getScanners() {
        FileInputStream[] inputStreamArray = getInputStreams(inputFileNames);
        Scanner[] scanners = new Scanner[inputFileNames.size()];

        for (int i = 0; i < inputFileNames.size(); i++) {
            try {
                inputStreamArray[i] = new FileInputStream(inputFileNames.get(i));
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
            scanners[i] = new Scanner(inputStreamArray[i], StandardCharsets.UTF_8);
        }
        return scanners;
    }

    private FileInputStream[] getInputStreams(List<String> inputFileNames) {
        return new FileInputStream[inputFileNames.size()];
    }

    private List<Object> readFirstValues(Scanner[] scanners) {
        List<Object> list = new ArrayList<>();
        Arrays.stream(scanners)
                .forEach(scanner -> {
                    Object value = readNextValue(scanner, null);
                    list.add(value);
                });
        return list;
    }

    private Object getValueFromList(List<Object> listWithoutNull) {
        Stream<Object> objectStream = listWithoutNull.stream();
        if (isStrings) {
            Comparator<Object> comparator = Comparator.comparing(String::valueOf);
            return isAscending ? objectStream.min(comparator).orElseThrow(() -> new ApplicationException("Error while find min value")) :
                    objectStream.max(comparator).orElseThrow(() -> new ApplicationException("Error while find max value"));
        }

        IntStream intStream = objectStream.mapToInt(Integer.class::cast);
        return isAscending ? intStream.min().orElseThrow(() -> new ApplicationException("Error while find min value"))
                : intStream.max().orElseThrow(() -> new ApplicationException("Error while find max value"));
    }

    private Object readNextValue(Scanner scanner, Object currentValue) {
        Object nextValue = null;

        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (isCorrectString(line)) {
                if (!isStrings) {
                    try {
                        nextValue = Integer.parseInt(line);
                    } catch (NumberFormatException e) {
                        log.error("Error while parse String to Integer {}", line);
                        nextValue = readNextValue(scanner, currentValue);
                    }
                } else {
                    nextValue = line;
                }

                if (!isCorrectSorting(nextValue, currentValue)) {
                    log.warn("The specified sort type is out of sequence.");
                    nextValue = readNextValue(scanner, currentValue);
                }
            } else {
                log.warn("String {} contains a space ", line);
                nextValue = readNextValue(scanner, currentValue);
            }
        }
        return nextValue;
    }

    private boolean isCorrectString(String line) {
        return line.chars()
                .noneMatch(Character::isSpaceChar);
    }

    private boolean isCorrectSorting(Object nextValue, Object currentValue) {
        if (currentValue != null && !nextValue.equals(currentValue)) {
            if (isStrings) {
                int result = nextValue.toString().compareTo(currentValue.toString());
                return isAscending && (result > 0) || !isAscending && (result < 0);
            } else {
                int value = (int) currentValue;
                return isAscending && ((int) nextValue > value) ||
                        !isAscending && ((int) nextValue < value);
            }
        }
        return true;
    }

    private void writeValueToFile(Object currentValue) {
        try {
            Path outputPath = Path.of(outputFileName);
            Files.write(outputPath, (currentValue + "\n").getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
