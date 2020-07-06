package com.bridgelabz.censusanalyser.service;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;
import com.bridgelabz.censusanalyser.model.CensusDAO;
import com.bridgelabz.censusanalyser.model.IndiaCensusCSV;
import com.bridgelabz.censusanalyser.model.USCensusCSV;
import com.bridgelabz.censusanalyser.utility.CensusUtility;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class CensusAnalyser {
    private static final String SORTED_STATE_JSON = "./json/indiaSortedStateCensus.json";
    private static final String SORTED_POPULATION_JSON = "./json/indiaSortedPopulationCensus.json";
    private static final String REVERSED_POPULATION_JSON = "./json/indiaSensityWisePopulation.json";
    private static final String AREA_WISE_STATE_JSON = "./json/indiaAreaWiseStateCensus.json";
    private static final String SORTED_US_POPULATION_JSON = "./json/usPopulationWise.json";
    private static final String SORTED_US_POPULATION_DENSITY_JSON = "./json/usPopulationDensityWise.json";
    private static final String SORTED_US_AREA_JSON = "./json/usAreaWise.json";

    Map<String, CensusDAO> censusDAOMap;
    Map<String, CensusDAO> censusMap;

    public CensusAnalyser() {
        this.censusDAOMap = new HashMap<>();
        this.censusMap = new HashMap<>();
    }

    /*Method To Load India Census Data*/
    public int loadCensusData(Country country, String... csvFilePath) throws CensusAnalyserException {
        censusMap = new CensusLoader().loadCensusData(country, csvFilePath);
        return censusMap.size();
    }

    /*Method to get US and Indian state for Most population by density */
    public String[] getMostPopulusStateByDensityForIndAndUS(String... csvFilePath) throws CensusAnalyserException {
        this.loadCensusData(Country.INDIA,csvFilePath[0]);
        String indiaData = getDensityWisePopulationSortedCensusData();
        this.loadCensusData(Country.US,csvFilePath[1]);
        String usData = getPopulationDensityWiseSortedCensusDataForUS();

        IndiaCensusCSV[] sortedIndData = new Gson().fromJson(indiaData, IndiaCensusCSV[].class);
        USCensusCSV[] sortedUSData = new Gson().fromJson(usData, USCensusCSV[].class);
        String mostPopulusByDensityIND = sortedIndData[0].state;
        String mostPopulusByDensityUS = sortedUSData[0].state;
        return new String[]{mostPopulusByDensityIND, mostPopulusByDensityUS};
    }

    /*Method to get State Wise Sorted Census Data*/
    public String getStateWiseSortedCensusData() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.state);
        List<CensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator);
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, SORTED_STATE_JSON);
        return sortedData;
    }

    /*Method to get India Population Density Wise Census Data in Reverse Order*/
    public String getDensityWisePopulationSortedCensusData() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.populationDensity);
        List<CensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, REVERSED_POPULATION_JSON);
        return sortedData;
    }

    /*Method To Get India Area Wise Sorted Census Data*/
    public String getAreaWiseSortedCensusData() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.totalArea);
        List<CensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, AREA_WISE_STATE_JSON);
        return sortedData;
    }

    /*Method to get India Population Wise Sorted Census Data*/
    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.population);
        List<CensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, SORTED_POPULATION_JSON);
        return sortedData;
    }

    /*Method to get US Population Wise Sorted Census Data*/
    public String getPopulationWiseSortedCensusDataForUS() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.population);
        List<CensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, SORTED_US_POPULATION_JSON);
        return sortedData;
    }

    /*Method to get US Population Density Wise Sorted Census Data*/
    public String getPopulationDensityWiseSortedCensusDataForUS() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.populationDensity);
        List<CensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, SORTED_US_POPULATION_DENSITY_JSON);
        return sortedData;
    }

    /*Method to get US Area Wise Sorted Census Data*/
    public String getAreaWiseSortedCensusDataForUS() throws CensusAnalyserException {
        if (censusMap == null || censusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.totalArea);
        List<CensusDAO> censusDAOList = new ArrayList<>(censusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, SORTED_US_AREA_JSON);
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

    public enum Country {INDIA, US}
}
