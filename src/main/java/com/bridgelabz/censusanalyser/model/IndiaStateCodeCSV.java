package com.bridgelabz.censusanalyser.model;

import com.opencsv.bean.CsvBindByName;

public class IndiaStateCodeCSV {

    @CsvBindByName(column = "State Name", required = true)
    public String stateName;

    @CsvBindByName
    public String stateCode;

}
