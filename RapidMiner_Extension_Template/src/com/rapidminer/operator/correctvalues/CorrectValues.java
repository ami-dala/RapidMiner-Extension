package com.rapidminer.operator.correctvalues;

import java.util.ArrayList;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.ExampleSetPrecondition;
import com.rapidminer.operator.ports.metadata.SimplePrecondition;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeEnumeration;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.Ontology;

public class CorrectValues extends Operator{
	
	int distanceMetric = 2;
	
	public static final String PARAMETER_DICTIONARY_ENUM = "dictionary";
	public static final String PARAMETER_STRING = "string";
	
	private InputPort exampleSetInput = getInputPorts().createPort("example set");
	private OutputPort correctedExampleSetOutput = getOutputPorts().createPort("corrected example set");
	private OutputPort exampleSetOutput = getOutputPorts().createPort("example set");
	
	public CorrectValues(OperatorDescription description){
		super(description);
		exampleSetInput.addPrecondition(new ExampleSetPrecondition(exampleSetInput, Ontology.NOMINAL));
	}
	
	@Override
	public void doWork() throws OperatorException {
		
		ExampleSet exampleSet = exampleSetInput.getData();
		Attributes attributes = exampleSet.getAttributes();
		
			Attribute singleAttribute = (Attribute)attributes;
		
			String[] dictionary = ParameterTypeEnumeration.transformString2Enumeration(getParameter(PARAMETER_DICTIONARY_ENUM));
		
			exampleSetOutput.deliver(exampleSet);
		
			for (Example example : exampleSet) {
				String inputWord = example.getNominalValue(singleAttribute);
			
				for(String dictionaryWord:dictionary){
					if(minDistance(inputWord, dictionaryWord)<=distanceMetric){
						example.setValue(singleAttribute, dictionaryWord);
					}
				}
			}	
		
			correctedExampleSetOutput.deliver(exampleSet);
		
		 
	}
	
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add(new ParameterTypeEnumeration(PARAMETER_DICTIONARY_ENUM, "A list which contains the allowed string values.", 
				new ParameterTypeString(PARAMETER_STRING, "Allowed string values.")));
		return types;
	}
	
	
	public static int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}
}
