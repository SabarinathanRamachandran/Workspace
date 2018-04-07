package utils;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
/**
 * This class is to extract the row data from Excel input file
 * Date Modified: 07/04/2018 , Modified By: Sabarinathan Ramachandran
 * Comments: 
 */
public class Excelutil {

    private static XSSFWorkbook wBook;
    private static XSSFSheet wSheet;
    
    public void openExcelFile(String filePath) {
    	//Open the Excel File
        try {
            FileInputStream ExcelFile = new FileInputStream(filePath);
            wBook = new XSSFWorkbook(ExcelFile);
            wSheet = wBook.getSheetAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRowCount() {
    	//return number of rows in the Excel sheet
        wSheet = wBook.getSheetAt(0);
        return wSheet.getPhysicalNumberOfRows();
    }

    public String getRowData(int rowNum) {
    	//return the row content
        try {
        	String rowString = wSheet.getRow(rowNum).getCell(0).getStringCellValue()+"," + wSheet.getRow(rowNum).getCell(1).getStringCellValue()+"," + wSheet.getRow(rowNum).getCell(2).getStringCellValue();
            return rowString;
        } catch (Exception e) {
            return "";
        }
    }

}



