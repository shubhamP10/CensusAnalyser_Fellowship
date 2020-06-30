package com.bridgelabz.opencsvbuilder.exception;

public class CSVBuilderException extends Exception {
    public ExceptionType type;

    public CSVBuilderException(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }

    public enum ExceptionType {
        UNABLE_TO_PARSE
    }
}
