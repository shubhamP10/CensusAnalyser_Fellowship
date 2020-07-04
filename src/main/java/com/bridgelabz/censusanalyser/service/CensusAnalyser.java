package com.bridgelabz.censusanalyser.service;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;
import com.bridgelabz.censusanalyser.model.CensusDAO;
import com.bridgelabz.censusanalyser.model.IndiaCensusCSV;
import com.bridgelabz.censusanalyser.model.IndiaStateCodeCSV;
import com.bridgelabz.censusanalyser.model.USCensusCSV;
import com.bridgelabz.censusanalyser.utility.CensusUtility;
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
        indiaCensusMap = new CensusLoader().loadCensusData(csvFilePath, IndiaCensusCSV.class);
        return indiaCensusMap.size();
    }

    /*Method To Load US Census Data*/
    public int loadUSCensusData(String csvFilePath) throws CensusAnalyserException {
        usCensusMap = new CensusLoader().loadCensusData(csvFilePath, USCensusCSV.class);
        return usCensusMap.size();
    }

    /*Method to Load State Code*/
    public int loadStateCode(String csvFilePath) throws CensusAnalyserException {
        return new CensusLoader().loadStateCodeData(csvFilePath,indiaCensusMap);
    }

    /*Method to get US and Indian state for Most population by density */
    public String[] getMostPopulusStateByDensityForIndAndUS() throws CensusAnalyserException {
        if (indiaCensusMap == null || indiaCensusMap.size() == 0 && usCensusMap == null || usCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        String usData = getPopulationDensityWiseSortedCensusDataForUS();
        String indiaData = getDensityWisePopulationSortedCensusData();
        IndiaCensusCSV[] sortedIndData = new Gson().fromJson(indiaData, IndiaCensusCSV[].class);
        USCensusCSV[] sortedUSData = new Gson().fromJson(usData, USCensusCSV[].class);
        String mostPopulusByDensityIND = sortedIndData[0].state;
        String mostPopulusByDensityUS = sortedUSData[0].state;
        return new String[]{mostPopulusByDensityIND, mostPopulusByDensityUS};
    }

    /*Method to get State Wise Sorted Census Data*/
    public String getStateWiseSortedCensusData() throws CensusAnalyserException {
        if (indiaCensusMap == null || indiaCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.state);
        List<CensusDAO> censusDAOList = new ArrayList<>(indiaCensusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator);
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, SORTED_STATE_JSON);
        return sortedData;
    }

    /*Method to get India Population Density Wise Census Data in Reverse Order*/
    public String getDensityWisePopulationSortedCensusData() throws CensusAnalyserException {
        if (indiaCensusMap == null || indiaCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.populationDensity);
        List<CensusDAO> censusDAOList = new ArrayList<>(indiaCensusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, REVERSED_POPULATION_JSON);
        return sortedData;
    }

    /*Method To Get India Area Wise Sorted Census Data*/
    public String getAreaWiseSortedCensusData() throws CensusAnalyserException {
        if (indiaCensusMap == null || indiaCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.totalArea);
        List<CensusDAO> censusDAOList = new ArrayList<>(indiaCensusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, AREA_WISE_STATE_JSON);
        return sortedData;
    }


    /*Method to get India Population Wise Sorted Census Data*/
    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException {
        if (indiaCensusMap == null || indiaCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.population);
        List<CensusDAO> censusDAOList = new ArrayList<>(indiaCensusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, SORTED_POPULATION_JSON);
        return sortedData;
    }

    /*Method to get US Population Wise Sorted Census Data*/
    public String getPopulationWiseSortedCensusDataForUS() throws CensusAnalyserException {
        if (usCensusMap == null || usCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.population);
        List<CensusDAO> censusDAOList = new ArrayList<>(usCensusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, SORTED_US_POPULATION_JSON);
        return sortedData;
    }

    /*Method to get US Population Density Wise Sorted Census Data*/
    public String getPopulationDensityWiseSortedCensusDataForUS() throws CensusAnalyserException {
        if (usCensusMap == null || usCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.populationDensity);
        List<CensusDAO> censusDAOList = new ArrayList<>(usCensusMap.values());
        CensusUtility.sort(censusDAOList, censusCSVComparator.reversed());
        String sortedData = new Gson().toJson(censusDAOList);
        CensusUtility.jsonWriter(sortedData, SORTED_US_POPULATION_DENSITY_JSON);
        return sortedData;
    }

    /*Method to get US Area Wise Sorted Census Data*/
    public String getAreaWiseSortedCensusDataForUS() throws CensusAnalyserException {
        if (usCensusMap == null || usCensusMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDAO> censusCSVComparator = Comparator.comparing(census -> census.totalArea);
        List<CensusDAO> censusDAOList = new ArrayList<>(usCensusMap.values());
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
}
