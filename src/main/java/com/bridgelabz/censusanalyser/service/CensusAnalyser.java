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
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class CensusAnalyser {
    private static final String SORTED_STATE_JSON = "./sortedStateCensus.json";
    private static final String SORTED_POPULATION_JSON = "./sortedPopulationCensus.json";
    private static final String REVERSED_POPULATION_JSON = "./reversedPopulationCensus.json";
    private static final String AREA_WISE_STATE_JSON = "./areaWiseStateCensus.json";
    List<IndiaCensusDAO> censusList;
    List<IndiaCensusDAO> stateCodeList;

    public CensusAnalyser() {
        this.censusList = new ArrayList<>();
    }

    /*Method To Load India Census Data*/
    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaCensusCSV> censusCSVIterator = csvBuilder.getOpenCSVFileIterator(reader, IndiaCensusCSV.class);
            while (censusCSVIterator.hasNext()) {
                this.censusList.add(new IndiaCensusDAO(censusCSVIterator.next()));
            }
            return censusList.size();
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
            StreamSupport.stream(stateCodeIterable.spliterator(),false)
                                    .filter(csvState -> censusList.get(0) != null)
                                    .forEach(csvState -> censusList.get(0).stateCode = csvState.stateCode);
            return censusList.size();
        } catch (IndexOutOfBoundsException e){
            throw new CensusAnalyserException("Index Out Of Bound",CensusAnalyserException.ExceptionType.INDEX_OUT_OF_BOUND);
        } catch (RuntimeException e) {
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
        if (censusList == null || censusList.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusDAO> censusCSVComparator = Comparator.comparing(census -> census.state);
        this.sort(censusCSVComparator);
        String sortedStateCensus = new Gson().toJson(this.censusList);
        this.jsonWriter(sortedStateCensus, SORTED_STATE_JSON);
        return sortedStateCensus;
    }

    /*Method to get Population Wise Sorted Census Data*/
    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException {
        if (censusList == null || censusList.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusDAO> censusCSVComparator = Comparator.comparing(census -> census.population);
        this.sort(censusCSVComparator.reversed());
        String sortedStateCensus = new Gson().toJson(this.censusList);
        this.jsonWriter(sortedStateCensus, SORTED_POPULATION_JSON);
        return sortedStateCensus;
    }

    /*Method to get Population Wise Census Data in Reverse Order*/
    public String getReversedPopulationWiseSortedCensusData() throws CensusAnalyserException {
        if (censusList == null || censusList.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusDAO> censusCSVComparator = Comparator.comparing(census -> census.population);
        this.sort(censusCSVComparator);
        String sortedStateCensus = new Gson().toJson(this.censusList);
        this.jsonWriter(sortedStateCensus, REVERSED_POPULATION_JSON);
        return sortedStateCensus;
    }

    /*Method To Get Area Wise Sorted Census Data*/
    public String getAreaWiseSortedCensusData() throws CensusAnalyserException {
        if (censusList == null || censusList.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusDAO> censusCSVComparator = Comparator.comparing(census -> census.area);
        this.sort(censusCSVComparator.reversed());
        String sortedStateCensus = new Gson().toJson(this.censusList);
        this.jsonWriter(sortedStateCensus, AREA_WISE_STATE_JSON);
        return sortedStateCensus;
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
    private void sort(Comparator<IndiaCensusDAO> censusCSVComparator) {
        IntStream.range(0, censusList.size() - 1).flatMap(i -> IntStream.range(0, censusList.size() - i - 1)).forEach(j -> {
            IndiaCensusDAO census1 = censusList.get(j);
            IndiaCensusDAO census2 = censusList.get(j + 1);
            if (censusCSVComparator.compare(census1, census2) > 0) {
                censusList.set(j, census2);
                censusList.set(j + 1, census1);
            }
        });
    }
}
