package com.bridgelabz.censusanalyser;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;
import com.bridgelabz.censusanalyser.model.IndiaCensusCSV;
import com.bridgelabz.censusanalyser.model.USCensusCSV;
import com.bridgelabz.censusanalyser.service.CensusAnalyser;
import com.google.gson.Gson;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class CensusAnalyserTest {

    private static final String INDIA_CENSUS_CSV_FILE_PATH = "./src/test/resources/IndiaStateCensusData.csv";
    private static final String WRONG_CSV_FILE_PATH = "./src/main/resources/IndiaStateCensusData.csv";
    private static final String WRONG_FILE_TYPE = "./src/test/resources/IndiaStateCensusData.xlsx";
    private static final String STATE_CODE_CSV_FILE_PATH = "./src/test/resources/IndiaStateCode.csv";
    private static final String US_CENSUS_CSV_PATH = "./src/test/resources/USCensusData.csv";
    private static final String WRONG_HEADER_CSV_FILE_PATH = "./src/test/resources/WrongHeaderIndiaStateCensusData.csv";
    private static final String WRONG_DELIMITER_CSV_FILE_PATH = "./src/test/resources/WrongDelimiterIndiaStateCensusData.csv";
    private static final String WRONG_DELIMITER_STATE_CSV_FILE_PATH = "./src/test/resources/WrongDelimiterIndiaStateCode.csv";
    private static final String WRONG_HEADER_STATE_CSV_FILE_PATH = "./src/test/resources/WrongHeaderIndiaStateCode.csv";

    /*Test Cases For Reading indiaStateCensusData.csv*/
    @Test
    public void givenIndianCensusCSVFile_ReturnsCorrectRecords() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            int numOfRecords = censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH);
            assertEquals(29, numOfRecords);
            System.out.println("Pass 1");
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenIndiaCensusData_WithWrongFile_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            ExpectedException exceptionRule = ExpectedException.none();
            exceptionRule.expect(CensusAnalyserException.class);
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, WRONG_CSV_FILE_PATH);
        } catch (CensusAnalyserException e) {
            System.out.println("Pass 2");
            assertEquals(CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM, e.type);
        }
    }

    @Test
    public void givenIndiaCensusData_WithWrongFileType_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            ExpectedException exceptionRule = ExpectedException.none();
            exceptionRule.expect(CensusAnalyserException.class);
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, WRONG_FILE_TYPE);
        } catch (CensusAnalyserException e) {
            System.out.println("Pass 3");
            assertEquals(CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM, e.type);
        }
    }

    @Test
    public void givenIndiaCensusData_WithIncorrectDelimiter_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            ExpectedException exceptionRule = ExpectedException.none();
            exceptionRule.expect(CensusAnalyserException.class);
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, WRONG_DELIMITER_CSV_FILE_PATH);
        } catch (CensusAnalyserException e) {
            System.out.println("Pass 4");
            assertEquals(CensusAnalyserException.ExceptionType.RUN_TIME_EXCEPTION, e.type);
        }
    }

    @Test
    public void givenIndianCensusData_WithWrongHeader_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            ExpectedException exceptionRule = ExpectedException.none();
            exceptionRule.expect(CensusAnalyserException.class);
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, WRONG_HEADER_CSV_FILE_PATH);
        } catch (CensusAnalyserException e) {
            System.out.println("Pass 5");
            assertEquals(CensusAnalyserException.ExceptionType.RUN_TIME_EXCEPTION, e.type);
        }
    }

    /*Test Cases For Reading IndiaStateCode.csv*/

    @Test
    public void givenIndianStateCodeFile_ShouldReturn_CorrectRecords() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            int numOfRecords = censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH, STATE_CODE_CSV_FILE_PATH);
            assertEquals(29, numOfRecords);
            System.out.println("Pass 6");
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenIndiaStateCode_WithWrongFile_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            ExpectedException exceptionRule = ExpectedException.none();
            exceptionRule.expect(CensusAnalyserException.class);
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH, WRONG_CSV_FILE_PATH);
        } catch (CensusAnalyserException e) {
            System.out.println("Pass 7");
            assertEquals(CensusAnalyserException.ExceptionType.STATE_CODE_FILE_PROBLEM, e.type);
        }
    }

    @Test
    public void givenIndiaStateCodeFile_WithWrongFileType_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            ExpectedException exceptionRule = ExpectedException.none();
            exceptionRule.expect(CensusAnalyserException.class);
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH, WRONG_FILE_TYPE);
        } catch (CensusAnalyserException e) {
            System.out.println("Pass 8");
            assertEquals(CensusAnalyserException.ExceptionType.STATE_CODE_FILE_PROBLEM, e.type);
        }
    }

    @Test
    public void givenIndiaStateCodeCSVFile_WithIncorrectDelimiter_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            ExpectedException exceptionRule = ExpectedException.none();
            exceptionRule.expect(CensusAnalyserException.class);
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH, WRONG_DELIMITER_STATE_CSV_FILE_PATH);
        } catch (CensusAnalyserException e) {
            System.out.println("Pass 9");
            assertEquals(CensusAnalyserException.ExceptionType.RUN_TIME_EXCEPTION, e.type);
        }
    }

    @Test
    public void givenIndianStateCodeCSVFile_WithWrongHeader_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            ExpectedException exceptionRule = ExpectedException.none();
            exceptionRule.expect(CensusAnalyserException.class);
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH, WRONG_HEADER_STATE_CSV_FILE_PATH);
        } catch (CensusAnalyserException e) {
            System.out.println("Pass 10");
            assertEquals("Error capturing CSV header!", e.getMessage());
        }
    }

    /*For JSON */

    @Test
    public void givenIndianCensusData_WhenSortedOnState_ShouldReturnSortedResult() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH, STATE_CODE_CSV_FILE_PATH);
            String sortedCensusData = censusAnalyser.getStateWiseSortedCensusData();
            IndiaCensusCSV[] censusCSV = new Gson().fromJson(sortedCensusData, IndiaCensusCSV[].class);
            assertEquals("Andhra Pradesh", censusCSV[0].state);
            System.out.println("Pass 11");
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenIndianCensusData_WhenSortedOnPopulation_ShouldReturnSortedResult() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH, STATE_CODE_CSV_FILE_PATH);
            String sortedCensusData = censusAnalyser.getPopulationWiseSortedCensusData();
            IndiaCensusCSV[] censusCSV = new Gson().fromJson(sortedCensusData, IndiaCensusCSV[].class);
            assertEquals("Uttar Pradesh", censusCSV[0].state);
            System.out.println("Pass 12");
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenIndianCensusSortedData_WhenSortedOnState_ShouldReturnCount() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            int count = censusAnalyser.getJSONCount("./json/indiaSortedStateCensus.json");
            assertEquals(29, count);
            System.out.println("Pass 13");
        } catch (FileNotFoundException e) {
            System.out.println("Exception 13: " + e.getMessage());
        }
    }

    @Test
    public void givenIndianCensusSortedData_WhenWrongJsonFile_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            censusAnalyser.getJSONCount("./json/SortedStateCensus.json");
        } catch (FileNotFoundException e) {
            System.out.println("Exception 13b: " + e.getMessage());
        }
    }

    @Test
    public void givenIndianCensusData_WhenSortedOnDensityWisePopulation_ShouldReturnSortedResult() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH, STATE_CODE_CSV_FILE_PATH);
            String sortedCensusData = censusAnalyser.getDensityWisePopulationSortedCensusData();
            IndiaCensusCSV[] censusCSV = new Gson().fromJson(sortedCensusData, IndiaCensusCSV[].class);
            assertEquals("Bihar", censusCSV[0].state);
            System.out.println("Pass 14");
        } catch (CensusAnalyserException e) {
            System.out.println("Exception 14: " + e.getMessage());
        }
    }

    @Test
    public void givenIndianCensusData_WhenSortedOnArea_ShouldReturnSortedResult() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            censusAnalyser.loadCensusData(CensusAnalyser.Country.INDIA, INDIA_CENSUS_CSV_FILE_PATH, STATE_CODE_CSV_FILE_PATH);
            String sortedCensusData = censusAnalyser.getAreaWiseSortedCensusData();
            IndiaCensusCSV[] censusCSV = new Gson().fromJson(sortedCensusData, IndiaCensusCSV[].class);
            assertEquals("Rajasthan", censusCSV[0].state);
            System.out.println("Pass 15");
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }

    //  For US Census Data
    @Test
    public void givenUSCensusCSVFile_ReturnsCorrectRecords() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            int numOfRecords = censusAnalyser.loadCensusData(CensusAnalyser.Country.US, US_CENSUS_CSV_PATH);
            assertEquals(51, numOfRecords);
            System.out.println("Pass 16");
        } catch (CensusAnalyserException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void givenUSCensusData_SortedByPopulationWise_ShouldReturn_SortedValues() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            censusAnalyser.loadCensusData(CensusAnalyser.Country.US, US_CENSUS_CSV_PATH);
            String sortedCensusData = censusAnalyser.getPopulationWiseSortedCensusDataForUS();
            USCensusCSV[] censusCSV = new Gson().fromJson(sortedCensusData, USCensusCSV[].class);
            Assert.assertThat(censusCSV[0].state, CoreMatchers.is("California"));
            System.out.println("Pass 17");
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenUSCensusData_SortedByPopulationDensity_ShouldReturn_SortedValues() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            censusAnalyser.loadCensusData(CensusAnalyser.Country.US, US_CENSUS_CSV_PATH);
            String sortedCensusData = censusAnalyser.getPopulationDensityWiseSortedCensusDataForUS();
            USCensusCSV[] censusCSV = new Gson().fromJson(sortedCensusData, USCensusCSV[].class);
            Assert.assertThat(censusCSV[0].state, CoreMatchers.is("District of Columbia"));
            System.out.println("Pass 18");
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenUSCensusData_SortedByArea_ShouldReturn_SortedValues() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            censusAnalyser.loadCensusData(CensusAnalyser.Country.US, US_CENSUS_CSV_PATH);
            String sortedCensusData = censusAnalyser.getAreaWiseSortedCensusDataForUS();
            USCensusCSV[] censusCSV = new Gson().fromJson(sortedCensusData, USCensusCSV[].class);
            Assert.assertThat(censusCSV[0].state, CoreMatchers.is("Alaska"));
            System.out.println("Pass 19");
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }

//    uc 11

    @Test
    public void getMostPopulusStateFromIndiaAndUS() throws CensusAnalyserException {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            String[] data = censusAnalyser.getMostPopulusStateByDensityForIndAndUS(INDIA_CENSUS_CSV_FILE_PATH,US_CENSUS_CSV_PATH);
            Assert.assertEquals("Bihar", data[0]);
            Assert.assertEquals("District of Columbia", data[1]);
        } catch (CensusAnalyserException e) {
            System.out.println("ERROR");
        }
    }
}
