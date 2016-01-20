package org.iitd.eventextraction.data.creation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.iitd.eventextraction.common.CommonFunctions;

import com.nytlabs.corpus.NYTCorpusDocument;
import com.nytlabs.corpus.NYTCorpusDocumentParser;

public class DistantSupervisionDataCreator {

	NYTCorpusDocumentParser nytCorpusDocumentParser = new NYTCorpusDocumentParser();
	
	public ArrayList<String> GetXmlContents(File xmlFile) {
		
		NYTCorpusDocument nytCorpusDocument = nytCorpusDocumentParser.parseNYTCorpusDocumentFromFile(xmlFile, false);
		ArrayList<String> contentsByParagraph = new ArrayList<String>();
		
		for (int i = 0; i < nytCorpusDocument.getBody().size(); i++) {
			if(i==0 && nytCorpusDocument.getBody().get(0).startsWith("LEAD:"))
				continue;
			if(nytCorpusDocument.getBody().get(i).split("[\\s]+").length > 5)
				contentsByParagraph.add(nytCorpusDocument.getBody().get(i));
		}
		
		return contentsByParagraph;
	}
	
	public void AnnotateByDistantSupervision(String nytCorpusFolderPath, String eventsFile) {
		
		HashSet<String> events = GetListOfEvents(eventsFile);
		
		try {
			List<File> files = CommonFunctions.GetFileListing(new File(nytCorpusFolderPath));
			for(File f : files) {
				if(f.getName().endsWith(".xml")) {
					
					ArrayList<String> contents = GetXmlContents(f);
					
					for (int i = 0; i < contents.size(); i++) {
						for (String event : events) {
							List<Integer> matches = findAllMatches(event, contents.get(i));
							if(matches.size() > 0) {
								// To create stand-off annotations
								//System.out.println(contents.get(i) + "\n\t" + event + "\n\t" + f.getAbsolutePath());
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public HashSet<String> GetListOfEvents(String filePath) {
		
		HashSet<String> events = new HashSet<String>();
		BufferedReader br = null;
		try {

			String sCurrentLine;
			br = new BufferedReader(new FileReader(filePath));
			while ((sCurrentLine = br.readLine()) != null)
				events.add(sCurrentLine.trim());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return events;
	}
	
	public static List<Integer> findAllMatches(String pattern, String source) {
		  List<Integer> idx = new ArrayList<Integer>();
		  int id = -1;
		  int shift = pattern.length();
		  int scnIdx = -shift;
		  while (scnIdx != -1 || id == -1) {
		   idx.add(scnIdx);
		   id = scnIdx + shift;
		   scnIdx = source.indexOf(pattern, id);
		  }
		  idx.remove(0);

		  return idx;
	}
	
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		
		String nytCorpusFolderPath = "C:\\iit\\Project\\Data\\LDC2008T19.New-York-Times-Corpus\\data\\1987";
		String eventsFile = "data\\List_of_recurring_events.txt";
		DistantSupervisionDataCreator distantSupervisionDataCreator = new DistantSupervisionDataCreator();
		distantSupervisionDataCreator.AnnotateByDistantSupervision(nytCorpusFolderPath, eventsFile);
		
		long stop = System.currentTimeMillis();
		System.out.println((((double)(stop - start))/1000)  + " s");
	}
}
