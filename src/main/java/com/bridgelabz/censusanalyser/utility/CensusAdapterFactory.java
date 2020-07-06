package com.bridgelabz.censusanalyser.utility;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;
import com.bridgelabz.censusanalyser.model.CensusDAO;
import com.bridgelabz.censusanalyser.service.CensusAnalyser;

import java.util.Map;

public class CensusAdapterFactory {
    /**
     * METHOD TO INVOKE THE CENSUS ADAPTER AS PER COUNTRY
     *
     * @param country provides country to load data
     * @return object of a required adapter class
     * @throws CensusAnalyserException while handling the occurred exception
     */
    public static Map<String, CensusDAO> getCensusDataObject(CensusAnalyser.Country country,
                                                             String... csvFilePath) throws CensusAnalyserException {
        if (country.equals(CensusAnalyser.Country.INDIA))
            return new IndiaCensusAdapter().loadCensusData(csvFilePath);
        return new USCensusAdapter().loadCensusData(csvFilePath);
    }
}
