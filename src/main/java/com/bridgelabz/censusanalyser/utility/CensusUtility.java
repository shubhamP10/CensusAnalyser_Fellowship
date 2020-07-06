package com.bridgelabz.censusanalyser.utility;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;

import java.io.FileWriter;
import java.io.IOException;

public class CensusUtility {
    /*Method for JSON Writer
     * @Param JSON String
     * @Param File Path
     * */
    public static void jsonWriter(String jsonString, String filePath) throws CensusAnalyserException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonString);
        } catch (IOException e) {
            throw new CensusAnalyserException("JSON Write Error", CensusAnalyserException.ExceptionType
                    .JSON_WRITE_ERROR);
        }
    }
}
