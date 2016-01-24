package org.iitd.eventextraction.featureExtractor;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.MentionsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class FeatureExtractor {

	static String[] features = {};
	
	StanfordCoreNLP pipeline = null;
	
	public FeatureExtractor() {
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention");
		pipeline = new StanfordCoreNLP(props);
	}
	
	private Annotation GetStanfordCoreNlpDocument(String text) {
		
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		
		return document;
	}
	
	public void Extract(String text) {
		
		Annotation document = GetStanfordCoreNlpDocument(text);
		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
		      System.out.println("---");
		      System.out.println("mentions");
		      for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
		        System.out.println("\t"+m);
		       }
		    }
	}
	
	public static void main(String[] args) {
		
		String text = "The 2007 ICC Cricket World Cup was the 9th edition of the Cricket World Cup tournament that took place in the West Indies from 13 March to 28 April 2007, using the sport's One Day International format. "
				+ "There were a total of 51 matches played, three fewer than at the 2003 World Cup (despite a field larger by two teams). The 16 competing teams were initially divided into four groups, with the two best-performing teams from each group moving on to a \"Super 8\" format. "
				+ "From this, Australia, New Zealand, Sri Lanka and South Africa won through to the semi-finals, with Australia defeating Sri Lanka in the final to win their third consecutive World Cup and their fourth overall. "
				+ "Australia's unbeaten record in the tournament increased their total to 29 consecutive World Cup matches without loss, a streak dating back to 23 May 1999, during the group stage of the 1999 World Cup. "
				+ "The tournament also saw upsets in the first round with tournament favourites India and Pakistan failing to making it past the group stage while Bangladesh, the lowest-ranked Test playing nation, and Ireland, an associate (non-test playing) nation, made it to the Super 8s.";
		FeatureExtractor featureExtractor = new FeatureExtractor();
		featureExtractor.Extract(text);
	}
}
