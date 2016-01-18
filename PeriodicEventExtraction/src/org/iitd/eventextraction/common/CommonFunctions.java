package org.iitd.eventextraction.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class CommonFunctions {
	
public static Object ReadObject(String FileName) throws Exception {
		
		FileInputStream fis = null;
		ObjectInputStream in = null;

		Object o = new Object();
		fis = new FileInputStream(FileName);
			in = new ObjectInputStream(fis);
			o = in.readObject();
			in.close();

		return o;
	}
	
	public static void WriteObject(String FileName, Object obj) 
	{
		FileOutputStream fos;
		ObjectOutputStream out;
		try {
			fos = new FileOutputStream(FileName);
			out = new ObjectOutputStream(fos);

			out.writeObject(obj);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void WriteToFile(String filePath, String content) {
		try {

			File file = new File(filePath);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map SortByComparator(Map unsortMap) {
		 
		List list = new LinkedList(unsortMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
                                       .compareTo(((Map.Entry) (o1)).getValue());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public static List<File> GetFileListing(
		    File aStartingDir
		  ) throws FileNotFoundException {
		    validateDirectory(aStartingDir);
		    List<File> result = getFileListingNoSort(aStartingDir);
		    Collections.sort(result);
		    return result;
		  }

	private static List<File> getFileListingNoSort(File aStartingDir) throws FileNotFoundException 
	{
		  List<File> result = new ArrayList<File>();
		    File[] filesAndDirs = aStartingDir.listFiles();
		    List<File> filesDirs = Arrays.asList(filesAndDirs);
		    for(File file : filesDirs) {
		      if ( ! file.isFile() ) {
		        //must be a directory
		        //recursive call!
		        List<File> deeperList = getFileListingNoSort(file);
		        result.addAll(deeperList);
		      }
		      else
		    	  result.add(file);
		    }
		    return result;
	}
	
	private static void validateDirectory (File aDirectory) throws FileNotFoundException 
	{
		  if (aDirectory == null) {
		      throw new IllegalArgumentException("Directory should not be null.");
		    }
		    if (!aDirectory.exists()) {
		      throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		    }
		    if (!aDirectory.isDirectory()) {
		      throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		    }
		    if (!aDirectory.canRead()) {
		      throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		    }
	}
}
