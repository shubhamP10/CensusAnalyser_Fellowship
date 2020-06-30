package com.bridgelabz.opencsvbuilder;

public class CSVBuilderException extends Exception {
    public ExceptionType type;

    public CSVBuilderException(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }

    public enum ExceptionType {
        CENSUS_FILE_PROBLEM, WRONG_FILE_TYPE, RUN_TIME_EXCEPTION, UNABLE_TO_PARSE
    }
}
