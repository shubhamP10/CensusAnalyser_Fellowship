package com.bridgelabz.censusanalyser.utility;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;
import com.bridgelabz.censusanalyser.model.CensusDAO;
import com.bridgelabz.censusanalyser.model.IndiaCensusCSV;
import com.bridgelabz.censusanalyser.model.IndiaStateCodeCSV;
import com.bridgelabz.censusanalyser.model.USCensusCSV;
import com.bridgelabz.opencsvbuilder.CSVBuilderException;
import com.bridgelabz.opencsvbuilder.CSVBuilderFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

public abstract class CensusAdapter {
    public abstract Map<String, CensusDAO> loadCensusData(String... csvFilePath)
            throws CensusAnalyserException;

    /**
     * Generic Method To Load India Census, US Census And India State Code To The Map
     *
     * @param csvFilePath    csv file path
     * @param censusCSVClass
     * @param <E>            accepts generic inputs
     * @return count of Each File Data
     * @throws CensusAnalyserException
     */
    public <E> Map<String, CensusDAO> loadCensusData(Class<E> censusCSVClass, String... csvFilePath) throws CensusAnalyserException {
        Map<String, CensusDAO> censusMap = new HashMap<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath[0]))) {

            Iterator<E> censusIterator = CSVBuilderFactory.createCSVBuilder()
                    .getOpenCSVFileIterator(reader, censusCSVClass);
            Iterable<E> censusCSV = () -> censusIterator;
            String className = censusCSVClass.getSimpleName();
            switch (className) {
                case "IndiaCensusCSV":
                    StreamSupport.stream(censusCSV.spliterator(), false)
                            .map(IndiaCensusCSV.class::cast)
                            .forEach(csvCensus -> censusMap.put(csvCensus.state, new CensusDAO(csvCensus)));
                    if (csvFilePath.length == 2) {
                        try (Reader codeReader = Files.newBufferedReader(Paths.get(csvFilePath[1]))) {
                            Iterator<IndiaStateCodeCSV> stateCodeIterator = CSVBuilderFactory.createCSVBuilder()
                                    .getOpenCSVFileIterator(codeReader, IndiaStateCodeCSV.class);
                            Iterable<IndiaStateCodeCSV> codeCSVIterable = () -> stateCodeIterator;
                            StreamSupport.stream(codeCSVIterable.spliterator(), false)
                                    .filter(csvState -> censusMap.get(csvState.stateName) != null)
                                    .forEach(csvState -> censusMap.get(csvState.stateName).stateCode = csvState.stateCode);
                        }
                    }
                    break;
                case "USCensusCSV":
                    StreamSupport.stream(censusCSV.spliterator(), false)
                            .map(USCensusCSV.class::cast)
                            .forEach(csvCensus -> censusMap.put(csvCensus.state, new CensusDAO(csvCensus)));
                    break;
            }
            return censusMap;
        } catch (NoSuchFileException e) {
            throw new CensusAnalyserException("Entered wrong file name/path or wrong file extension",
                    CensusAnalyserException.ExceptionType.CSV_FILE_PROBLEM);
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (RuntimeException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.RUN_TIME_EXCEPTION);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    e.type.name());
        }
    }
}
