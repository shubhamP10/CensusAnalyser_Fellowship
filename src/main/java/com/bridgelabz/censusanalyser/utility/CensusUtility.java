package com.bridgelabz.censusanalyser.utility;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

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

    /*Method to sort Census Data
     * @param Comparator Object
     * */
    public static   <E> void sort(List<E> censusDAOList, Comparator<E> censusCSVComparator) {
        for (int i = 0; i < censusDAOList.size(); i++) {
            for (int j = 0; j < censusDAOList.size() - i - 1; j++) {
                E censusCSV = censusDAOList.get(j);
                E censusCSV1 = censusDAOList.get(j + 1);
                if (censusCSVComparator.compare(censusCSV, censusCSV1) > 0) {
                    censusDAOList.set(j, censusCSV1);
                    censusDAOList.set(j + 1, censusCSV);
                }
            }
        }
    }
}
