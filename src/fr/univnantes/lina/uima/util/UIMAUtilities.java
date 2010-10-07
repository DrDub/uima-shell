/** 
 * UIMA Utilities
 * Copyright (C) 2010  Nicolas Hernandez
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fr.univnantes.lina.uima.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.Level;
import org.apache.uima.analysis_component.AnalysisComponent;

import fr.univnantes.lina.util.JavaUtilities;

/**
 * @author hernandez
 *
 */
public class UIMAUtilities {



	/**
	 * Return the view of a jcas corresponding to the given view name
	 * @param aJCas
	 * @param viewNameString string
	 * @return viewJCas
	 * @throws AnalysisEngineProcessException
	 */
	public static JCas getView(JCas aJCas, String viewNameString) throws AnalysisEngineProcessException {
		JCas viewJCas = null;
		try {
			viewJCas = aJCas.getView(viewNameString);
		} catch (CASException exception) {
			String errmsg = "ERROR: The view " + viewNameString
			+ " does not exist in the JCAS!";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] { viewNameString },exception);
			// http://uima.apache.org/downloads/releaseDocs/2.3.0-incubating/docs/api/org/apache/uima/analysis_engine/AnalysisEngineProcessException.html
			// http://uima.apache.org/downloads/releaseDocs/2.3.0-incubating/docs/api/constant-values.html#org.apache.uima.UIMAException.STANDARD_MESSAGE_CATALOG
		}
		return viewJCas;
	}


	/**
	 * Return the sofaDataString of a JCAS corresponding to the given view 
	 * @param aJCas
	 * @return inputSofaDataString
	 * @throws AnalysisEngineProcessException
	 */
	public static String getSofaDataString(JCas aJCas) throws AnalysisEngineProcessException {
		String inputSofaDataString = null ; 
		inputSofaDataString = aJCas.getSofaDataString();

		if (inputSofaDataString == null) {
			String errmsg = "ERROR: The given view " + aJCas.toString()
			+ " does not contain a sofaDataString!";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] { });
		}
		return inputSofaDataString;
	}


	/**
	 * Get the type of a given annotation name and check if it exists
	 * @param aJCas
	 * @param annotationString
	 * @return annotationType
	 * @throws AnalysisEngineProcessException
	 */
	public static Type getType(JCas aJCas, String annotationString) throws AnalysisEngineProcessException {

		// récupère le type context à partir de la String le désignant
		// et vérifie son existence dans le Type System
		Type annotationType = null; 
		annotationType = aJCas.getTypeSystem().getType(
				annotationString);
		// On s'assure que le type existe bien
		if ((annotationType == null)) {
			String errmsg = "Error: Type " + annotationString
			+ " is not defined in the Type System !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] { annotationType });
		}
		return annotationType;
	}

	/**
	 * Get the class of a given annotation name in order to cast annotations latter
	 * Allow to know the type of the annotation to handle only at the runtime level
	 * @param annotationString
	 * @return annotationString
	 * @throws AnalysisEngineProcessException
	 */
	public static Class<Annotation> getClass(String annotationString) throws AnalysisEngineProcessException {
		// Récupère l'annotation courante d'un type connu
		// TokenAnnotation tokenAnnotation = (TokenAnnotation)
		// inputAnnotationIter
		// .next();

		Class<Annotation> annotationClass = null;
		try {
			annotationClass = (Class<Annotation>) Class
			.forName(annotationString);
		} catch (ClassNotFoundException e) {
			String errmsg = "Error: Class " + annotationString
			+ " not found !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] { annotationString },e);	
			//e.printStackTrace();
		}
		// Annotation tokenAnnotation = (Annotation) inputAnnotationIter.next();
		// InputAnnotationClass.cast(tokenAnnotation);

		return annotationClass;

	}

	/**
	 * Get the method of a given annotation Class 
	 * Allow to know the method of the annotation to handle only at the runtime level
	 * @param AnnotationClass
	 * @param featureName
	 * @return getFeatureMethod
	 * @throws AnalysisEngineProcessException
	 */
	public static Method getMethod(Class AnnotationClass, String featureName) throws AnalysisEngineProcessException {

		Method getFeatureMethod = null;

		// coveredText -> getCoveredText
		String getFeatureMethodName = "get" + featureName.substring(0, 1).toUpperCase() + featureName.substring(1);
		try {
			// getPosTag =
			// InputAnnotationClass.getMethod("getPosTag");
			// featureNameGetMethod =
			// InputAnnotationClass.getMethod("getCoveredText");
			getFeatureMethod = AnnotationClass
			.getMethod(getFeatureMethodName);

		} catch (SecurityException e) {
			String errmsg = "Error: SecurityException when getting the " + 
			featureName + " method ("+getFeatureMethodName+") of a given class object !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] { featureName },e);	
			//e.printStackTrace();
		} catch (NoSuchMethodException e) {
			String errmsg = "Error: " + 
			featureName + " method not found for the given class object !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] { featureName },e);	
			//e.printStackTrace();
		}
		return getFeatureMethod;
	}


	/**
	 * This method create an annotation.
	 * 
	 * @param aJCas
	 *            the CAS over which the process is performed
	 * @param annotationNameToCreate
	 * @param beginFeatureValue
	 * @param endFeatureValue
	 * @param featureNameToSet            
	 * @param valueFeatureValue
	 * @throws AnalysisEngineProcessException 
	 */
	public static void createAnnotation(JCas aJCas, String annotationNameToCreate,
			int beginFeatureValue, int endFeatureValue, String featureNameToSet, String valueFeatureValue) throws AnalysisEngineProcessException {

		// Crée une annotation générique
		// SequenceMatch sequenceMatch = new SequenceMatch(aJCas);
		// sequenceMatch.setBegin(patternHashMap.get(patternKeyString).getCurrentStartIndexCursor());
		// sequenceMatch.setEnd(tokenAnnotation.getEnd());
		// sequenceMatch.setValue(patternKeyString);
		// sequenceMatch.addToIndexes();

		// Crée une annotation prédéfinie
		// Object[] args = null;


		try {
			Object[] args = null;

			Class<Annotation> TgtClass = (Class<Annotation>) Class
			.forName(annotationNameToCreate);

			// System.out.println("Debug: ----------------------------------------------------------------------"
			// );
			// System.out.println("Debug: TgtClass.getName()	= " +
			// TgtClass.getName());
			// System.out.println("Debug: patternHashMap.get(patternKeyString).getTargetType()	= "
			// + patternHashMap.get(patternKeyString).getTargetType());

			// Génére le constructeur de la classe de l'annotation à créer
			Constructor<?> tgtConstr = TgtClass
			.getConstructor(new Class[] { JCas.class });

			// Crée une annotation du type target
			Object t = null;
			t = tgtConstr.newInstance(new Object[] { aJCas });
			TgtClass.cast(t);

			// System.out.println("Debug: t.getClass().getName()	= " +
			// t.getClass().getName());
			// System.out.println("Debug: t.getClass().getDeclaredMethods().length	= "
			// + t.getClass().getDeclaredMethods().length);

			// for (int l = 0 ; l < t.getClass().getDeclaredMethods().length ;
			// l++ ) {
			// System.out.println("Debug: t.getClass().getDeclaredMethods()[l]= "
			// + t.getClass().getDeclaredMethods()[l]);
			// }

			// for (int l = 0 ; l < t.getClass().getMethods().length ; l++ ) {
			// System.out.println("Debug: t.getClass().getMethods()[l]= " +
			// t.getClass().getMethods()[l]);
			// }

			// jxpathContext = JXPathContext.newContext(t);
			// Récupère la méthode addToIndexes de la classe target
			Method addToIndexes = TgtClass.getMethod("addToIndexes",
					new Class[] {});
			// Récupère les méthodes pour accéder aux features souhaitées
			Method setBegin = TgtClass.getMethod("setBegin", Integer.TYPE);
			Method setEnd = TgtClass.getMethod("setEnd", Integer.TYPE);

			// value -> setValue
			String getFeatureMethodName = "set" + featureNameToSet.substring(0, 1).toUpperCase() + featureNameToSet.substring(1);

			Method setValue = TgtClass.getMethod(getFeatureMethodName, String.class);

			// Ajouts à l'annotation du type target
			setBegin.invoke(t, beginFeatureValue);
			setEnd.invoke(t, endFeatureValue);
			setValue.invoke(t, valueFeatureValue);


			// Test contre la création d'annotations fantomes
			if (beginFeatureValue < endFeatureValue) 
				addToIndexes.invoke(t, args);

		} catch (IllegalArgumentException e) {
			String errmsg = "Error: IllegalArgumentException  !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);	
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			String errmsg = "Error: IllegalAccessException  !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);	
			//e.printStackTrace();
		} catch (InvocationTargetException e) {
			String errmsg = "Error: InvocationTargetException  !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);	
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			String errmsg = "Error: ClassNotFoundException  !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);	
			//e.printStackTrace();
		} catch (SecurityException e) {
			String errmsg = "Error: SecurityException  !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);	
			//e.printStackTrace();
		} catch (NoSuchMethodException e) {
			String errmsg = "Error: NoSuchMethodException  !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);	
			//e.printStackTrace();
		} catch (InstantiationException e) {
			String errmsg = "Error: InstantiationException  !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);	
			//e.printStackTrace();
		}
	}
	
	/**
	 * This method create a view.
	 * 
	 * @param aJCas
	 *            the CAS over which the process is performed
	 * @param outputViewString
	 * @param sofaDataString
	 * @param sofaDataStringTypeMimeString

	 * @throws AnalysisEngineProcessException 
	 */
	public static void createView(JCas aJCas, String outputViewString, String sofaDataString, String sofaDataStringTypeMimeString) throws AnalysisEngineProcessException {

		try {
			aJCas.createView(outputViewString);
			JCas outputView = UIMAUtilities.getView(aJCas, outputViewString);
			//outputView.setDocumentText(commandResultString);
			outputView.setSofaDataString(sofaDataString,sofaDataStringTypeMimeString);

		} catch (CASException e) {
			String errmsg = "Error: Cannot create the view "+outputViewString +" !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);	
			// e.printStackTrace();
		}
	}

	
	/**
	 * Return the sofaDataString of a JCAS corresponding to the given view 
	 * @param aJCas
	 * @return inputSofaDataString
	 * @throws AnalysisEngineProcessException
	 */
	public static String  createTempTextFile (String prefix, String suffix, String content)throws AnalysisEngineProcessException {
		String tempTextFilePath = null ; 

		try {
			tempTextFilePath = JavaUtilities.createTempTextFile (prefix,suffix,content);

		} catch (IOException ioexception) {
			String errmsg = "ERROR: Cannot create a temporary text file The view !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] { tempTextFilePath },ioexception);
		}
		return tempTextFilePath;
	}

}
