package com.bridgelabz.censusanalyser.service;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;
import com.bridgelabz.censusanalyser.model.IndiaCensusCSV;
import com.bridgelabz.censusanalyser.model.IndiaCensusDAO;
import com.bridgelabz.censusanalyser.model.IndiaStateCodeCSV;
import com.bridgelabz.opencsvbuilder.CSVBuilderException;
import com.bridgelabz.opencsvbuilder.CSVBuilderFactory;
import com.bridgelabz.opencsvbuilder.ICSVBuilder;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.StreamSupport;

public class CensusAnalyser {
    private static final String SORTED_STATE_JSON = "./sortedStateCensus.json";
    private static final String SORTED_POPULATION_JSON = "./sortedPopulationCensus.json";
    private static final String REVERSED_POPULATION_JSON = "./densityWisePopulation.json";
    private static final String AREA_WISE_STATE_JSON = "./areaWiseStateCensus.json";
    List<IndiaCensusDAO> censusList;
    List<IndiaCensusDAO> stateCodeList;
    Map<String, IndiaCensusDAO> stateCodeMap;
    Map<String, IndiaCensusDAO> censusMap;

    public CensusAnalyser() {
        this.censusList = new ArrayList<>();
        this.stateCodeList = new ArrayList<>();
        this.censusMap = new HashMap<>();
        this.stateCodeMap = new HashMap<>();
    }

    /*Method To Load India Census Data*/
    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaCensusCSV> censusCSVIterator = csvBuilder.getOpenCSVFileIterator(reader, IndiaCensusCSV.class);
            Iterable<IndiaCensusCSV> indiaCensusCSVS = () -> censusCSVIterator;
            StreamSupport.stream(indiaCensusCSVS.spliterator(), false)
                    .forEach(csvCensus -> this.censusMap.put(csvCensus.state, new IndiaCensusDAO(csvCensus)));
            return censusMap.size();
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

    /*Method to Load State Code*/
    public int loadStateCode(String csvFilePath) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaStateCodeCSV> stateCSVIterator = csvBuilder.getOpenCSVFileIterator(reader, IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> stateCodeIterable = () -> stateCSVIterator;
            StreamSupport.stream(stateCodeIterable.spliterator(), false)
                    .forEach(csvCensus -> this.stateCodeMap.put(csvCensus.stateName, new IndiaCensusDAO(csvCensus)));
            return stateCodeMap.size();
        } catch (IndexOutOfBoundsException e) {
            throw new CensusAnalyserException("Index Out Of Bound", CensusAnalyserException.ExceptionType.INDEX_OUT_OF_BOUND);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.RUN_TIME_EXCEPTION);
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.STATE_CODE_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    e.type.name());
        }
    }

    /*Method to get Count from Iterator*/
    private <E> int getCount(Iterator<E> iterator) {
        Iterable<E> csvIterable = () -> iterator;
        return (int) StreamSupport.stream(csvIterable.spliterator(), false).count();
    }

    /*Method to get State Wise Sorted Census Data*/
    public String getStateWiseSortedCensusData() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusDAO> censusCSVComparator = Comparator.comparing(census -> census.state);
        List<IndiaCensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        this.sort(censusDAOList, censusCSVComparator);
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, SORTED_STATE_JSON);
        return sortedData;
    }

    /*Method to get Population Wise Sorted Census Data*/
    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusDAO> censusCSVComparator = Comparator.comparing(census -> census.population);
        List<IndiaCensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        this.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, SORTED_POPULATION_JSON);
        return sortedData;
    }

    /*Method to get Population Wise Census Data in Reverse Order*/
    public String getDensityWisePopulationSortedCensusData() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusDAO> censusCSVComparator = Comparator.comparing(census -> census.densityPerSqKm);
        List<IndiaCensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        this.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, REVERSED_POPULATION_JSON);
        return sortedData;
    }

    /*Method To Get Area Wise Sorted Census Data*/
    public String getAreaWiseSortedCensusData() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusDAO> censusCSVComparator = Comparator.comparing(census -> census.area);
        List<IndiaCensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        this.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, AREA_WISE_STATE_JSON);
        return sortedData;
    }

    /*Method to get Number of Records in JSON
     * @Param File Path
     * */
    public int getJSONCount(String filePath) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        IndiaCensusCSV[] json = new Gson().fromJson(br, IndiaCensusCSV[].class);
        List<IndiaCensusCSV> indiaCensusCSVList = Arrays.asList(json);
        return indiaCensusCSVList.size();
    }

    /*Method for JSON Writer
     * @Param JSON String
     * @Param File Path
     * */
    private void jsonWriter(String jsonString, String filePath) throws CensusAnalyserException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonString);
        } catch (IOException e) {
            throw new CensusAnalyserException("JSON Write Error", CensusAnalyserException.ExceptionType
                    .JSON_WRITE_ERROR);
        }
    }

    /*Method to sort Census Data
     * @param Comparator Object
     * */
    private void sort(List<IndiaCensusDAO> censusDAOList, Comparator<IndiaCensusDAO> censusCSVComparator) {
        for (int i = 0; i < censusDAOList.size(); i++) {
            for (int j = 0; j < censusDAOList.size() - i - 1; j++) {
                IndiaCensusDAO censusCSV = censusDAOList.get(j);
                IndiaCensusDAO censusCSV1 = censusDAOList.get(j + 1);
                if (censusCSVComparator.compare(censusCSV, censusCSV1) > 0) {
                    censusDAOList.set(j, censusCSV1);
                    censusDAOList.set(j + 1, censusCSV);
                }
            }
        }
    }
}
