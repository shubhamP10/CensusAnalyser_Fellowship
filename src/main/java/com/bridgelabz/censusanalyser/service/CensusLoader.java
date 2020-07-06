package com.bridgelabz.censusanalyser.service;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;
import com.bridgelabz.censusanalyser.model.CensusDAO;
import com.bridgelabz.censusanalyser.model.IndiaCensusCSV;
import com.bridgelabz.censusanalyser.model.IndiaStateCodeCSV;
import com.bridgelabz.censusanalyser.model.USCensusCSV;
import com.bridgelabz.opencsvbuilder.CSVBuilderException;
import com.bridgelabz.opencsvbuilder.CSVBuilderFactory;
import com.bridgelabz.opencsvbuilder.ICSVBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

public class CensusLoader {
    public Map<String, CensusDAO> loadCensusData(CensusAnalyser.Country country, String... csvFilePath) throws CensusAnalyserException {
        if (country.equals(CensusAnalyser.Country.INDIA)) {
            return this.loadCensusData(IndiaCensusCSV.class, csvFilePath);
        } else if (country.equals(CensusAnalyser.Country.US)) {
            return this.loadCensusData(USCensusCSV.class, csvFilePath);
        } else
            throw new CensusAnalyserException("Incorrect Country", CensusAnalyserException.ExceptionType.INVALID_COUNTRY);
    }

    /**
     * Generic Method To Load India Census, US Census And India State Code To The Map
     *
     * @param csvFilePath    csv file path
     * @param censusCSVClass
     * @param <E>            accepts generic inputs
     * @return count of Each File Data
     * @throws CensusAnalyserException
     */
    private <E> Map<String, CensusDAO> loadCensusData(Class<E> censusCSVClass, String... csvFilePath) throws CensusAnalyserException {
        Map<String, CensusDAO> censusMap = new HashMap<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath[0]))) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<E> censusCSVIterator = csvBuilder.getOpenCSVFileIterator(reader, censusCSVClass);
            Iterable<E> censusCSV = () -> censusCSVIterator;
            String className = censusCSVClass.getSimpleName();
            switch (className) {
                case "IndiaCensusCSV":
                    StreamSupport.stream(censusCSV.spliterator(), false)
                            .map(IndiaCensusCSV.class::cast)
                            .forEach(csvCensus -> censusMap.put(csvCensus.state, new CensusDAO(csvCensus)));
                    break;
                case "USCensusCSV":
                    StreamSupport.stream(censusCSV.spliterator(), false)
                            .map(USCensusCSV.class::cast)
                            .forEach(csvCensus -> censusMap.put(csvCensus.state, new CensusDAO(csvCensus)));
                    break;
            }
            if (csvFilePath.length == 1) return censusMap;
            this.loadStateCodeData(csvFilePath[1], censusMap);
            return censusMap;
        } catch (RuntimeException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.RUN_TIME_EXCEPTION);
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    e.type.name());
        }
    }

    private void loadStateCodeData(String csvFilePath, Map<String, CensusDAO> indiaCensusMap) throws CensusAnalyserException, CSVBuilderException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator stateCSVIterator = csvBuilder.getOpenCSVFileIterator(reader, IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> stateCodeIterable = () -> stateCSVIterator;
            StreamSupport.stream(stateCodeIterable.spliterator(), false)
                    .filter(csvState -> indiaCensusMap.get(csvState.stateName) != null)
                    .forEach(csvState -> indiaCensusMap.get(csvState.stateName).stateCode = csvState.stateCode);
            indiaCensusMap.size();
        } catch (RuntimeException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.RUN_TIME_EXCEPTION);
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.STATE_CODE_FILE_PROBLEM);
        }
    }
}
