package com.bridgelabz.censusanalyser.model;

public class USCensusDAO {
    //    USCensusCSV Fields
    public int housingUnits;
    public float populationDensity;
    public float waterArea;
    public float landArea;
    public float totalArea;
    public float housingDensity;
    public int usPopulation;
    public String state;
    public String stateID;

    public USCensusDAO(USCensusCSV usCensusCSV) {
        stateID = usCensusCSV.stateID;
        state = usCensusCSV.state;
        usPopulation = usCensusCSV.population;
        housingDensity = usCensusCSV.housingDensity;
        totalArea = usCensusCSV.totalArea;
        landArea = usCensusCSV.landArea;
        waterArea = usCensusCSV.waterArea;
        populationDensity = usCensusCSV.populationDensity;
        housingUnits = usCensusCSV.housingUnits;

    }
}
