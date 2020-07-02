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
}
