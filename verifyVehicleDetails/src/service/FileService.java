package service;

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

	/**
	 * This Function is to get the file path for the given MIME type
	 * Date Modified: 05/04/2018	 Modified By: Saba
	 * Comments     : 
	 */
	public List <FileInfoBean> getFileInfo(String fPath) throws IOException{
	        File afile = new File(fPath);
	        
	        if (afile.exists() && afile.isDirectory()) { //if path exist
	        	List <FileInfoBean> fileList = new ArrayList <FileInfoBean>();
		        for( File f : afile.listFiles()){
		        	FileInfoBean rFile = new FileInfoBean();
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

	/**
	 * This Function is to get the files that are matching the given MIME type
	 * Date Modified: 05/04/2018	 Modified By: Saba
	 * Comments     : 
	 */
	public List <FileInfoBean> getSupportedMIMEtypeFiles(String fPath) throws IOException{
		List <FileInfoBean> fileList = getFileInfo(fPath);
		List<FileInfoBean> filteredFileList = new ArrayList<FileInfoBean>();
        for( FileInfoBean f:fileList){
    	    Path source = Paths.get(f.getFilePath());
    	    //System.out.println(Files.probeContentType(source));
    	    //extract only the supported mime type files
			if (EXCEL_AND_CSV_MIME_TYPE.equals(Files.probeContentType(source)) || EXCEL_AND_EXCEL_MIME_TYPE.equals(Files.probeContentType(source))) {
    	    	filteredFileList.add(f);
    	    }
        }
        return filteredFileList;
	}

	/**
	 * This Function is to get the extension of the file
	 * Date Modified: 05/04/2018	 Modified By: Saba
	 * Comments     : 
	 */
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
