package Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;

import edu.stanford.nlp.hcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.hcoref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class NLPAnalyzer {
	
	static StanfordCoreNLP pipeline; 
	private static HashSet<String> goodPosTags = new HashSet<String>(Arrays.asList("NN","NNP","VBG","NNS"));
	private static HashSet<String> goodParseTags = new HashSet<String>(Arrays.asList("dobj","obj","nsubj","subj","root"));
	private static TreebankLanguagePack tlp;
	private static GrammaticalStructureFactory gsf;
	  
    public static void init() { 
    	Properties props = new Properties();
    	props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
        pipeline = new StanfordCoreNLP(props); 
        
        tlp = new PennTreebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();
    } 

	public static int findSentiment(String text) {

       int mainSentiment = 0;
       if (text != null && text.length() > 0) {
           int longest = 0;
           Annotation annotation = pipeline.process(text);
           for (CoreMap sentence : annotation
                   .get(CoreAnnotations.SentencesAnnotation.class)) {
               Tree tree = sentence
                       .get(SentimentAnnotatedTree.class);
               int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
               String partText = sentence.toString();
               if (partText.length() > longest) {
                   mainSentiment = sentiment;
                   longest = partText.length();
               }

           }
       }
       return mainSentiment;
	}
	
	public static double coreference(String text, String newMsg){
    	String newConvo = text + " " + newMsg;
    	Annotation document = new Annotation(text);
    	Annotation newDocument = new Annotation(newConvo);
    	pipeline.annotate(document);
    	Map<Integer, CorefChain> corefAnnotations = document.get(CorefChainAnnotation.class);
    	pipeline.annotate(newDocument);
    	Map<Integer, CorefChain> newCorefAnnotations = newDocument.get(CorefChainAnnotation.class);
    	int newMentions = 0;
    	int newChainMentions = 0;
    	Iterator<CorefChain> oldchains = corefAnnotations.values().iterator();
    	Iterator<CorefChain> newchains = newCorefAnnotations.values().iterator();
    	while(oldchains.hasNext()){
    		CorefChain occ = oldchains.next();
    		CorefChain ncc = newchains.next(); //newchains always has at least as many chains as oldchains
    		
    		List<CorefChain.CorefMention> oms = occ.getMentionsInTextualOrder();
    		for(CorefChain.CorefMention isI : oms){
    			if (isI.mentionSpan == "I")
    				continue;
    		}
    		List<CorefChain.CorefMention> nms = ncc.getMentionsInTextualOrder();
    		newMentions += nms.size() - oms.size();
    	}
    	while(newchains.hasNext()){
    		CorefChain ncc = newchains.next();
    		List<CorefChain.CorefMention> nms = ncc.getMentionsInTextualOrder();
    		newChainMentions += nms.size(); 
    	}
    	
    	int chains = newCorefAnnotations.values().size() - corefAnnotations.values().size();
    	// Return (# new mentions in old chains) / (# new mentions) or 0 if no new mentions at all
    	int denom = (newMentions + newChainMentions) == 0 ? 1 : newMentions + newChainMentions;
    	return (double)(newMentions) / denom;
    }

	public List<NounToken> getTopics(String text){
		Annotation annotation = new Annotation(text);
	    pipeline.annotate(annotation);
	    PriorityQueue<NounToken> topics = new PriorityQueue<NounToken>(5, new Comparator<NounToken>(){
	    	public int compare(NounToken left, NounToken right){
	    		return right.score - left.score;
	    	}
	    });
	    
	    List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
	    for(CoreMap sentence: sentences){ 
	    	Tree tree = sentence.get(TreeAnnotation.class);
	    	GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
	    	Collection<TypedDependency> td = gs.typedDependenciesCollapsed();

	    	ArrayList<NounToken> words = new ArrayList<NounToken>();
	    	for(TypedDependency t: td){
	    		NounToken tok = new NounToken(t);
//	    		System.err.println(tok);
	    		if(goodPosTags.contains(tok.pos) || goodParseTags.contains(tok.tag) || !tok.ner.equals("O")){
		    		while(words.size()!=0 && (tok.pos.startsWith("NN") || tok.tag.equals("compound"))){ // merge with previous
		    			NounToken prev = words.get(words.size()-1);
		    			if(tok.index - prev.index < 2 && prev.pos.startsWith("NN")){
		    				words.remove(words.size()-1);
		    				tok.text = prev.text + " " + tok.text;	    	
		    				tok.lemma = prev.lemma + " " + tok.lemma;
		    				tok.score = Math.max(tok.score, prev.score);
		    			} else {break;}
		    		}
	    			words.add(tok);
	    		}
	    	}
	    	for(NounToken tok:words){
//	    		System.err.println(tok);
	    		if(tok.pos.startsWith("NNP") ||
	    				(tok.index !=1 &&!tok.text.equals("I") && tok.text.matches(".*\\p{javaUpperCase}.*")) ||
	    				!(tok.ner.equals("O") || tok.ner.equals("NUMBER")))
	    			tok.setTopic(EntityIdentity.identifyEntity(tok.getTopic()));
	    		topics.add(tok);
	    	}
	    }   
	    
	    ArrayList<NounToken> ans = new ArrayList<NounToken>();
	    int totalScore = 0;
	    Iterator<NounToken> itr = topics.iterator();
	    while(itr.hasNext())
	    	totalScore += itr.next().score;
	    totalScore =  totalScore > 0 ? totalScore : 1;
	    while(topics.size()>0 && ans.size() < 4){
	    	NounToken nt = topics.poll();
	    	nt.denom = totalScore;
	    	ans.add(nt);
	    }
	    return ans;
	}

	public class NounToken{
		public String text,lemma,tag,pos,ner;
		public int index, score, denom;
		private HashMap<String, Integer> nerScore = new HashMap<String, Integer>();
		private HashMap<String, Integer> posScore = new HashMap<String, Integer>();
		private HashMap<String, Integer> tagScore = new HashMap<String, Integer>();

		public NounToken(TypedDependency t){
			nerScore.put("PERSON", 7);nerScore.put("LOCATION", 6);nerScore.put("TIME", 6);
			posScore.put("NN", 5);posScore.put("NNS", 5);posScore.put("NNP", 5);posScore.put("NNPS", 5);posScore.put("PRP", 1);
			tagScore.put("nsubj", 2);tagScore.put("dobj", 4);tagScore.put("root", 3);
			
    		text = t.dep().originalText();
    		lemma = t.dep().lemma();
    		pos = t.dep().tag();
    		tag = t.reln().getShortName();
    		ner = t.dep().ner();
    		index = t.dep().index();
			score = 0;
			denom = 1;
			if(nerScore.containsKey(ner))
				score += nerScore.get(ner);
			if(posScore.containsKey(pos))
				score += posScore.get(pos);
			if(tagScore.containsKey(tag))
				score += tagScore.get(tag);
		}
		
		public String toString(){
			return String.format("%s/%s  @%d POS:%s DEP:%s NER:%s SCORE:%d/%d=%f", text, lemma, index, pos, tag, ner, score, denom, getScore());
		}
		
		public String getTopic(){
			return ner.equals("O") ? text : lemma;
		}
		
		public void setTopic(String topic){
			if(ner.equals("O")) text = topic;
			else lemma = topic;
		}
		
		public double getScore(){
			return (double)score/denom;
		}
		
		@Override
		public boolean equals(Object o){
			return o instanceof NounToken ? getTopic().equals(((NounToken)o).getTopic()) : getTopic().equals(o);
		}
		
		@Override
		public int hashCode(){
			return getTopic().hashCode();
		}
		
	}
	
}
