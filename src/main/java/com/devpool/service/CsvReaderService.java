package com.devpool.service;

import com.devpool.model.Patron;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvReaderService {
    public List<Patron> readCsvFile(String csvFile) throws IOException {
        try (var reader = new FileReader(csvFile);
             var csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {
            var patrons = new ArrayList<Patron>();

            for (CSVRecord record : csvParser) {
                log.debug("Record: {}", record.stream().toList());
                patrons.add(new Patron(record.get(0), Long.parseLong(record.get(1).trim())));
            }

            return patrons;
        } catch (IOException e) {
            log.error("Can't read CSV file due to {}", e.getMessage());
            throw e;
        }
    }
}
