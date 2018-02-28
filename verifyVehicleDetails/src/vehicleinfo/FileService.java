package vehicleinfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileService {
	private static String EXCEL_AND_CSV_MIME_TYPE = "application/vnd.ms-excel";
	private static String EXCEL_AND_EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	
	/*============================================================================================================================================
	 This Function: Is to get the file path for the given MIME type
	 Date Modified: 27/02/2018
	 Modified By  : Saba
	 Comments     : 
	 ============================================================================================================================================*/
	public List <FileBean> getFileInfo(String fPath) throws IOException{
	        File afile = new File(fPath);
	        
	        if (afile.exists() && afile.isDirectory()) { //if path exist
	        	List <FileBean> fileList = new ArrayList <FileBean>();
		        for( File f : afile.listFiles()){
		        	FileBean rFile = new FileBean();
		        	rFile.setFilename(f.getName());
		        	rFile.setFileMimeType(Files.probeContentType(Paths.get(f.getPath())));
		        	rFile.setFileSize(f.length());
		        	rFile.setFileExtention(getExtension(f.getName()));
		        	rFile.setFilePath(f.getPath());
		        	fileList.add(rFile);
		        	
		        	
		        }
		        return fileList;
		    }else{
		    	//if path not exist
		    	System.out.println("Err: Directory not found - " +fPath);
		    	return null; //if path or file does not exist
		    }
	}

	/*============================================================================================================================================
	 This Function: Is to get the files that are matching the given MIME type
	 Date Modified: 27/02/2018
	 Modified By  : Saba
	 Comments     : 
	 ============================================================================================================================================*/
	public List <FileBean> getSupportedMIMEtypeFiles(String fPath) throws IOException{
		List <FileBean> fileList = getFileInfo(fPath);
		List<FileBean> filteredFileList = new ArrayList<FileBean>();
        for( FileBean f:fileList){
    	    Path source = Paths.get(f.getFilePath());
    	    //System.out.println(Files.probeContentType(source));
    	    //extract only the supported mime type files
			if (EXCEL_AND_CSV_MIME_TYPE.equals(Files.probeContentType(source)) || EXCEL_AND_EXCEL_MIME_TYPE.equals(Files.probeContentType(source))) {
    	    	filteredFileList.add(f);
    	    }
        }
        return filteredFileList;
	}

	/*============================================================================================================================================
	 This Function: Is to get the extension of the file
	 Date Modified: 27/02/2018
	 Modified By  : Saba
	 Comments     : 
	 ============================================================================================================================================*/
	  private static String getExtension(String filename) {
	        if (filename == null) {
	            return null;
	        }
	        int extensionPos = filename.lastIndexOf('.');
	        if (extensionPos == -1) {
	            return "";
	        } else {
	            return filename.substring(extensionPos + 1);
	        }
	    }
	  
}
