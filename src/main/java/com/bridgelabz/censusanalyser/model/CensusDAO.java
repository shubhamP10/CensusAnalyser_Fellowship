package com.bridgelabz.censusanalyser.model;

public class CensusDAO {

    public String state;
    public String stateCode;
    public int population;
    public double populationDensity;
    public double totalArea;

    public CensusDAO(IndiaCensusCSV indiaCensusCSV) {
        state = indiaCensusCSV.state;
        totalArea = indiaCensusCSV.areaInSqKm;
        population = indiaCensusCSV.population;
        populationDensity = indiaCensusCSV.densityPerSqKm;
    }

    public CensusDAO(USCensusCSV usCensusCSV) {
        stateCode = usCensusCSV.stateID;
        state = usCensusCSV.state;
        population = usCensusCSV.population;
        totalArea = usCensusCSV.totalArea;
        populationDensity = usCensusCSV.populationDensity;
    }

    public CensusDAO(IndiaStateCodeCSV csvCensus) {
        stateCode = csvCensus.stateCode;
        state = csvCensus.stateName;
    }

//    public IndiaCensusCSV getIndiaCensusCSV() {
//        return new IndiaCensusCSV(state, population, (int) populationDensity, (int) totalArea);
//    }
}
