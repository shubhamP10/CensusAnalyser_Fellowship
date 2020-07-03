package com.bridgelabz.censusanalyser.service;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;
import com.bridgelabz.censusanalyser.model.CensusDAO;
import com.bridgelabz.censusanalyser.model.IndiaCensusCSV;
import com.bridgelabz.censusanalyser.model.IndiaStateCodeCSV;
import com.bridgelabz.censusanalyser.model.USCensusCSV;
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
    private static final String SORTED_STATE_JSON = "./json/indiaSortedStateCensus.json";
    private static final String SORTED_POPULATION_JSON = "./json/indiaSortedPopulationCensus.json";
    private static final String REVERSED_POPULATION_JSON = "./json/indiaSensityWisePopulation.json";
    private static final String AREA_WISE_STATE_JSON = "./json/indiaAreaWiseStateCensus.json";
    private static final String SORTED_US_POPULATION_JSON = "./json/usPopulationWise.json";
    private static final String SORTED_US_POPULATION_DENSITY_JSON = "./json/usPopulationDensityWise.json";
    private static final String SORTED_US_AREA_JSON = "./json/usAreaWise.json";


    Map<String, CensusDAO> censusDAOMap;
    Map<String, CensusDAO> indiaCensusMap;
    Map<String, CensusDAO> usCensusMap;

    public CensusAnalyser() {
        this.censusDAOMap = new HashMap<>();
        this.indiaCensusMap = new HashMap<>();
        this.usCensusMap = new HashMap<>();
    }

    /*Method To Load India Census Data*/
    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        return this.loadCensusData(csvFilePath, IndiaCensusCSV.class);

    }

    /*Method To Load US Census Data*/
    public int loadUSCensusData(String csvFilePath) throws CensusAnalyserException {
        return this.loadCensusData(csvFilePath, USCensusCSV.class);
    }


    private <E> int loadCensusData(String csvFilePath, Class<E> censusCSVClass) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<E> censusCSVIterator = csvBuilder.getOpenCSVFileIterator(reader, censusCSVClass);
            Iterable<E> censusCSV = () -> censusCSVIterator;
            String className = censusCSVClass.getSimpleName();
            if (className.equals("IndiaCensusCSV")) {
                StreamSupport.stream(censusCSV.spliterator(), false)
                        .map(IndiaCensusCSV.class::cast)
                        .forEach(csvCensus -> this.indiaCensusMap.put(csvCensus.state, new CensusDAO(csvCensus)));
            } else if (className.equals("USCensusCSV")) {
                StreamSupport.stream(censusCSV.spliterator(), false)
                        .map(USCensusCSV.class::cast)
                        .forEach(csvCensus -> this.indiaCensusMap.put(csvCensus.state, new CensusDAO(csvCensus)));
            }
            return indiaCensusMap.size();
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
            Iterator stateCSVIterator = csvBuilder.getOpenCSVFileIterator(reader, IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> stateCodeIterable = () -> stateCSVIterator;
            StreamSupport.stream(stateCodeIterable.spliterator(), false)
                    .filter(csvState -> indiaCensusMap.get(csvState.stateName) != null)
                    .forEach(csvState -> indiaCensusMap.get(csvState.stateName).stateCode = csvState.stateCode);
            return indiaCensusMap.size();
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

    /*Method to get State Wise Sorted Census Data*/
    public String getStateWiseSortedCensusData() throws CensusAnalyserException {
        if (indiaCensusMap == null || indiaCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.state);
        List<CensusDAO> censusDAOList = new ArrayList<>(indiaCensusMap.values());
        this.sort(censusDAOList, censusCSVComparator);
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, SORTED_STATE_JSON);
        return sortedData;
    }

    /*Method to get India Population Density Wise Census Data in Reverse Order*/
    public String getDensityWisePopulationSortedCensusData() throws CensusAnalyserException {
        if (indiaCensusMap == null || indiaCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.populationDensity);
        List<CensusDAO> censusDAOList = new ArrayList<>(indiaCensusMap.values());
        this.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, REVERSED_POPULATION_JSON);
        return sortedData;
    }

    /*Method To Get India Area Wise Sorted Census Data*/
    public String getAreaWiseSortedCensusData() throws CensusAnalyserException {
        if (indiaCensusMap == null || indiaCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.totalArea);
        List<CensusDAO> censusDAOList = new ArrayList<>(indiaCensusMap.values());
        this.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, AREA_WISE_STATE_JSON);
        return sortedData;
    }


    /*Method to get India Population Wise Sorted Census Data*/
    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException {
        if (indiaCensusMap == null || indiaCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.population);
        List<CensusDAO> censusDAOList = new ArrayList<>(indiaCensusMap.values());
        this.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, SORTED_POPULATION_JSON);
        return sortedData;
    }

    /*Method to get US Population Wise Sorted Census Data*/
    public String getPopulationWiseSortedCensusDataForUS() throws CensusAnalyserException {
        if (usCensusMap == null || usCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.population);
        List<CensusDAO> censusDAOList = new ArrayList<>(usCensusMap.values());
        this.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, SORTED_US_POPULATION_JSON);
        return sortedData;
    }

    /*Method to get US Population Density Wise Sorted Census Data*/
    public String getPopulationDensityWiseSortedCensusDataForUS() throws CensusAnalyserException {
        if (usCensusMap == null || usCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.populationDensity);
        List<CensusDAO> censusDAOList = new ArrayList<>(usCensusMap.values());
        this.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, SORTED_US_POPULATION_DENSITY_JSON);
        return sortedData;
    }

    /*Method to get US Area Wise Sorted Census Data*/
    public String getAreaWiseSortedCensusDataForUS() throws CensusAnalyserException {
        if (usCensusMap == null || usCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.totalArea);
        List<CensusDAO> censusDAOList = new ArrayList<>(usCensusMap.values());
        this.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        this.jsonWriter(sortedData, SORTED_US_AREA_JSON);
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
    private <E> void sort(List<E> censusDAOList, Comparator<E> censusCSVComparator) {
        for (int i = 0; i < censusDAOList.size(); i++) {
            for (int j = 0; j < censusDAOList.size() - i - 1; j++) {
                E censusCSV = censusDAOList.get(j);
                E censusCSV1 = censusDAOList.get(j + 1);
                if (censusCSVComparator.compare(censusCSV, censusCSV1) > 0) {
                    censusDAOList.set(j, censusCSV1);
                    censusDAOList.set(j + 1, censusCSV);
                }
            }
        }
    }
}
