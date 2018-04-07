package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is to extract the row data from CSV input file
 * Date Modified: 07/04/2018 , Modified By: Sabarinathan Ramachandran
 * Comments: 
 */
public class CSVutil {

    private static BufferedReader bufr;
    private List<String> mylist = new ArrayList<String>();
    private String[] csvArray;

    public void openCSVFile(String filePath) {
		//Open the CSV file
        try {
        	bufr = new BufferedReader(new FileReader(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRowCount() throws IOException {
    	//return number of rows in the CSV file
    	int rowCount;
    	String rline;
    	rowCount = 0;
        try {
			while ((rline = bufr.readLine()) != null) {
				mylist.add(rline);
				rowCount++;
			}
			csvArray = mylist.toArray(new String[mylist.size()]);
	        return rowCount;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getRowData(int rowNum) {
    	//return the row content
        try {
			return csvArray[rowNum];
        } catch (Exception e) {
            return "";
        }
    }	
	
}
