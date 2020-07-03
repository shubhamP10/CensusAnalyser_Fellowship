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
}
