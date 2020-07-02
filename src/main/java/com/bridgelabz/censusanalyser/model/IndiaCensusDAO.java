package com.bridgelabz.censusanalyser.model;

public class IndiaCensusDAO {
    public int srNo;
    public int tin;
    public String stateCode;
    public String stateName;
    public int densityPerSqKm;
    public int population;
    public int area;
    public String state;

    public IndiaCensusDAO(IndiaCensusCSV indiaCensusCSV) {
        state = indiaCensusCSV.state;
        area = indiaCensusCSV.areaInSqKm;
        population = indiaCensusCSV.population;
        densityPerSqKm = indiaCensusCSV.densityPerSqKm;
    }

    public IndiaCensusDAO(IndiaStateCodeCSV indiaStateCodeCSV) {
        stateName = indiaStateCodeCSV.stateName;
        stateCode = indiaStateCodeCSV.stateCode;
        tin = indiaStateCodeCSV.tin;
        srNo = indiaStateCodeCSV.srNo;
    }
}
