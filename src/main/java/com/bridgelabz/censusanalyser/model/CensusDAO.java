package com.bridgelabz.censusanalyser.model;

public class CensusDAO {

    //    StateCodeCSV Fields
    public int srNo;
    public int tin;
    public String stateCode;
    public String stateName;

    //    IndianCensusCSV Fileds
    public int densityPerSqKm;
    public int population;
    public int area;
    public String state;

    //    USCensusCSV Fields
    private int housingUnits;
    private float populationDensity;
    private float waterArea;
    private float landArea;
    private float totalArea;
    private float housingDensity;
    private int usPopulation;
    private String usState;
    private String stateID;

    public CensusDAO(IndiaCensusCSV indiaCensusCSV) {
        state = indiaCensusCSV.state;
        area = indiaCensusCSV.areaInSqKm;
        population = indiaCensusCSV.population;
        densityPerSqKm = indiaCensusCSV.densityPerSqKm;
    }

    public CensusDAO(IndiaStateCodeCSV indiaStateCodeCSV) {
        stateName = indiaStateCodeCSV.stateName;
        stateCode = indiaStateCodeCSV.stateCode;
        tin = indiaStateCodeCSV.tin;
        srNo = indiaStateCodeCSV.srNo;
    }

    public CensusDAO(USCensusCSV usCensusCSV) {
        stateID = usCensusCSV.stateID;
        usState = usCensusCSV.state;
        usPopulation = usCensusCSV.population;
        housingDensity = usCensusCSV.housingDensity;
        totalArea = usCensusCSV.totalArea;
        landArea = usCensusCSV.landArea;
        waterArea = usCensusCSV.waterArea;
        populationDensity = usCensusCSV.populationDensity;
        housingUnits = usCensusCSV.housingUnits;

    }
}
