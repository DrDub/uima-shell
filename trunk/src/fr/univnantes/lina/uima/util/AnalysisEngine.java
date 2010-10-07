/** 
 * Analysis Engine 
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


import fr.univnantes.lina.uima.util.UIMAUtilities;
//

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas; //
import org.apache.uima.jcas.tcas.Annotation;

import org.apache.uima.resource.ResourceInitializationException; 

import java.util.Iterator;


/**
 * UIMA Analysis Engine
 * @author Nicolas Hernandez
 */
public abstract class AnalysisEngine extends JCasAnnotator_ImplBase {

	/**
	 * When you use the code source as an AE example, please redefine the 
	 * "Common component constants Default" and the "Common component parameter values"
	 */

	/** Common component constants
	 * To define when you create a new component 
	 * */
	private String COMPONENT_NAME = "";
	private String COMPONENT_VERSION = "";
	private String COMPONENT_ID = COMPONENT_NAME + "-"
	+ COMPONENT_VERSION;
	//exception
	private final String MESSAGE_DIGEST = COMPONENT_ID+"_Messages";
	//tmp file
	//log
	
	/** Common component parameters in descriptor file*/
	// View name to consider as the view to process
	private static String PARAM_NAME_INPUT_VIEW = "InputView";
	// Type name of the annotations to consider as the context annotations in
	// which
	// the process will be performed
	private static String PARAM_NAME_CONTEXT_ANNOTATION = "ContextAnnotation";
	// Type name of the annotations to consider as the token units to be
	// processed
	private static String PARAM_NAME_INPUT_ANNOTATION = "InputAnnotation";
	// Feature name of the annotations to consider as the token units to be
	// processed
	private static String PARAM_NAME_INPUT_FEATURE = "InputFeature";
	// View name to consider as the view to receive the result
	private static String PARAM_NAME_OUTPUT_VIEW = "OutputView";
	// Type mime to consider for storing the result in the sofaDataString
	private static String PARAM_NAME_OUTPUT_VIEW_TYPE_MIME = "OutputViewTypeMime";	
	// Type name of the annotations to create as the analysis result
	private static String PARAM_NAME_OUTPUT_ANNOTATION = "OutputAnnotation";
	// Type name of the feature to create as the analysis result
	private static String PARAM_NAME_OUTPUT_FEATURE = "OutputFeature";
	// An identifier for the current run.
	// This identifier is added into all the annotations that are created during 
	// the current execution.
	private static String PARAM_NAME_RUNID = "RunId";


	/** Default common component parameter values in descriptor file**/
	// Default view name if none are specified by the view parameter
	private static String DEFAULT_INPUT_VIEW = "_InitialView";
	// Default context annotation name if none is specified by the context
	// annotation parameter
	private static String DEFAULT_CONTEXT_ANNOTATION = "uima.tcas.DocumentAnnotation";
	// Default annotation name if none are specified by the input annotation
	// parameter
	// Default feature name if none are specified by the input feature
	// parameter when the input annotation parameter is set
	//public static String DEFAULT_INPUT_FEATURE = "coveredText";
	//public static String DEFAULT_INPUT_ANNOTATION = "TokenAnnotation";
	// Default annotation name if none are specified by the output annotation
	// parameter
	//public static String DEFAULT_OUTPUT_ANNOTATION = "fr.univnantes.lina.uima.shell.types.ShellAnnotation";
	// Default feature name if none are specified by the output feature
	// parameter
	//public static String DEFAULT_OUTPUT_FEATURE = "value";
	// Default type mime value for the sofaDataString in case of the output type is view
	private static String DEFAULT_OUTPUTVIEW_TYPEMIME = "text/plain";
	// 
	private static String INPUTTYPE_ANNOTATION = "annotation";
	private static String INPUTTYPE_VIEW = "view";
	private static String OUTPUTTYPE_ANNOTATION = "annotation";
	private static String OUTPUTTYPE_VIEW = "view";



	/** Common component variables */
	private String runIdString = null;

	protected String inputViewString = null;
	protected String contextAnnotationString = null;
	protected String inputAnnotationString = null;
	protected String inputFeatureString = null;

	protected String outputViewString = null;
	protected String outputViewTypeMimeString = null;

	protected String outputAnnotationString = null;
	protected String outputFeatureString = null;

	protected String inputType = "";
	protected String outputType = "";



	/**
	 * @see AnalysisComponent#initialize(UimaContext)
	 */
	public void initialize(UimaContext aContext)
	throws ResourceInitializationException {
		super.initialize(aContext);
		
		/** Get parameter values **/
		runIdString = (String) aContext
		.getConfigParameterValue(PARAM_NAME_RUNID); 

		inputViewString = (String) aContext.getConfigParameterValue(PARAM_NAME_INPUT_VIEW);
		if (inputViewString == null) {
			// If no input view is specified, we use the default (i.e. _InitialView)
			inputViewString = DEFAULT_INPUT_VIEW;
		}

		contextAnnotationString = (String) aContext
		.getConfigParameterValue(PARAM_NAME_CONTEXT_ANNOTATION);
		if (contextAnnotationString == null) {
			// If no context specified, we do over the whole CAS
			// en d'autres termes, on traite le uima.tcas.DocumentAnnotation
			contextAnnotationString = DEFAULT_CONTEXT_ANNOTATION;
		}
		// ... otherwise over segments covered by the contextAnnotation
		// parameter

		inputAnnotationString = (String) aContext
		.getConfigParameterValue(PARAM_NAME_INPUT_ANNOTATION); 

		inputFeatureString = (String) aContext
		.getConfigParameterValue(PARAM_NAME_INPUT_FEATURE);
		if (((inputFeatureString != null) && (inputAnnotationString == null)) || ((inputFeatureString == null) && (inputAnnotationString != null)) ){
			String errmsg = "Error: If one of the parameter " + PARAM_NAME_INPUT_ANNOTATION
			+ " or " + PARAM_NAME_INPUT_FEATURE
			+ " is defined, both must be !";
			throw new  ResourceInitializationException(errmsg,
					new Object[] {  });	
			//e.printStackTrace();
		}

		outputViewString = (String) aContext.getConfigParameterValue(PARAM_NAME_OUTPUT_VIEW);
		if (outputViewString == null) {
			// If no output view is specified, we set it to inputViewString
			outputViewString = inputViewString;
		}
		outputViewTypeMimeString = (String) aContext.getConfigParameterValue(PARAM_NAME_OUTPUT_VIEW_TYPE_MIME);
		if (outputViewTypeMimeString == null) {
			// If no output view type mime is specified, we set it a default one
			outputViewTypeMimeString = DEFAULT_OUTPUTVIEW_TYPEMIME;
		}
		outputAnnotationString = (String) aContext
		.getConfigParameterValue(PARAM_NAME_OUTPUT_ANNOTATION); 
		outputFeatureString = (String) aContext
		.getConfigParameterValue(PARAM_NAME_OUTPUT_FEATURE); 
		// outputAnnotationString ET outputFeatureString doivent être initialisés les deux à la fois ou aucun d'eux
		if (((outputAnnotationString != null) && (outputFeatureString == null)) || ((outputAnnotationString == null) && (outputFeatureString != null)) ){
			String errmsg = "Error: If one of the parameter " + PARAM_NAME_OUTPUT_ANNOTATION
			+ " or " + PARAM_NAME_OUTPUT_FEATURE
			+ " is defined, both must be !";
			throw new  ResourceInitializationException(errmsg,
					new Object[] {  });	
			//e.printStackTrace();
		}

		// Si l'input_type est annotation, alors on va traiter chacune d'elle
		if (inputAnnotationString != null) inputType = INPUTTYPE_ANNOTATION;
		// Sinon on va traiter le datastring de la vue
		else  inputType = INPUTTYPE_VIEW;
		if ((outputAnnotationString != null) && (outputFeatureString != null)) {	
			outputType = OUTPUTTYPE_ANNOTATION;
		}	else outputType = OUTPUTTYPE_VIEW;



		// String[] patternStrings = (String[])
		// aContext.getConfigParameterValue("Patterns");
		// mLocations = (String[])
		// aContext.getConfigParameterValue("Locations");

		// compile regular expressions
		// mPatterns = new Pattern[patternStrings.length];
		// for (int i = 0; i < patternStrings.length; i++) {
		// mPatterns[i] = Pattern.compile(patternStrings[i]);
		// }

		// dans le process
		// Vérifier que context, input et output AnnotationString  si !=null alors ont un type défini dans le TS
		// Vérifier aussi que l'input view existe

	}


	/**
	 * @see JCasAnnotator_ImplBase#process(JCas)
	 */
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		/** -- process the analysis **/

		log("-----------------------------------------------------------------------------------------------------------------");
		log("Process the input view or annotation of a given type (potentially covered by a context annotation of a given type)");
		browseInput(aJCas, inputViewString, contextAnnotationString, inputAnnotationString,  inputFeatureString, outputViewString, outputViewTypeMimeString, outputAnnotationString, outputFeatureString);

	}

	


	/**
	 * This method is invoked when the analysis has to be processed for some
	 * input annotations which  belongs to specific contextAnnotation.
	 * 
	 * @param aJCas
	 *            the CAS over which the process is performed
	 * @param inputViewString
	 * @param contextAnnotationType
	 *            Type name of the annotations to consider as the context
	 *            annotations in which the process will be performed
	 * @param inputAnnotationType
	 *            Type name of the annotations to consider as the token units to
	 *            be processed
	 * @param inputFeatureString
	 * @param outputViewString
	 * @param outputViewTypeMimeString
	 * @param outputAnnotationString
	 * @param ouputFeatureString
	 * @throws AnalysisEngineProcessException
	 */
	private void browseInput(JCas aJCas, String inputViewString, String contextAnnotationString, String inputAnnotationString, String inputFeatureString, String outputViewString, String outputViewTypeMimeString, String outputAnnotationString, String ouputFeatureString) throws AnalysisEngineProcessException {

		// var to concat the results in case of a view as the output type 
		String commandResultString = "";

		/** -- Prepare the view to be processed**/
		log("Getting the inputViewJCas");
		JCas inputViewJCas = UIMAUtilities.getView(aJCas,inputViewString);

		/** -- In case of the output type is annotation, get the view to store the result **/
		// si les param annotations sont renseignés alors cela signifie que l'on
		// suppose qu'une vue existe pour accueillir les annotations
		// on effectue ici le getView pour d'éviter de le faire à chaque tour de boucle 
		// si l'inputType est annotation
		JCas outputViewJCas = null;
		if (outputType.equalsIgnoreCase(OUTPUTTYPE_ANNOTATION)) {
			log("Getting the outputViewJCas");
			outputViewJCas = UIMAUtilities.getView(aJCas,outputViewString);		
		}	


		//
		Boolean contextLoopHasNext = false; 

		// Structure de données nécessaires en cas d'inputType == annotation
		Type contextAnnotationType = null;
		Type inputAnnotationType = null;

		AnnotationIndex<Annotation> contextAnnIdx = null; 
		Iterator<Annotation> contextAnnIdxIter = null;

		if (inputType.equalsIgnoreCase(INPUTTYPE_ANNOTATION)) {
			log("Getting the Context Annotation index");
			// Récupère les types d'annotations
			contextAnnotationType = UIMAUtilities.getType(inputViewJCas, contextAnnotationString);
			inputAnnotationType = UIMAUtilities.getType(inputViewJCas, inputAnnotationString);

			// Récupère une liste de contexts
			// Récupération d'index d'annotations à partir de type d'annotation!
			// soit comme cela
			//   AnnotationIndex idxMonType = (AnnotationIndex)
			//   cas.getAnnotationIndex(inputAnnotationType);
			//   FSIterator monTypeIt = idxMonType.iterator();
			//   while (monTypeIt.hasNext()) {
			//      On peut le manipuler comme on veut ...
			//   }
			// soit comme cela
			// sans reflect
			//    FSIndex tokenAnnotationFSIdx =
			//    aJCas.getAnnotationIndex(TokenAnnotation.type);
			// avec reflect
			//    FSIndex<Annotation> inputAnnotationFSIdx = aJCas
			//    .getAnnotationIndex(inputAnnotationType);
			contextAnnIdx = (AnnotationIndex<Annotation>) inputViewJCas
			.getAnnotationIndex(contextAnnotationType);
			contextAnnIdxIter = contextAnnIdx.iterator();

			if (contextAnnIdxIter.hasNext())  contextLoopHasNext = true;
		}
		// else if (inputType.equalsIgnoreCase(INPUTTYPE_VIEW))
		else  contextLoopHasNext = true;

		// Pour chaque context ou input view
		while (contextLoopHasNext) {

			//
			Boolean annotationLoopHasNext = false; 

			// Structure de données nécessaires en cas d'inputType == annotation
			Annotation contextAnnotation = null ;
			Iterator<Annotation> inputAnnotationIter = null;

			if (inputType.equalsIgnoreCase(INPUTTYPE_ANNOTATION)) {

				log("Getting the Input Annotation index");
				contextAnnotation = (Annotation) contextAnnIdxIter.next();

				// Récupération de la liste des inputAnnotation
				// en théorie : création d'un index à partir DES inputAnnotationType et récupération d'un iterator dessus
				// mais pour l'instant on suppose qu'UN seul inputAnnotationType n'est possible à la fois
				inputAnnotationIter = inputViewJCas
				.getAnnotationIndex(inputAnnotationType).subiterator(
						contextAnnotation);
				// Iterator<Annotation> inputAnnotationIter = inputAnnotationFSIdx
				// .iterator();
				if (inputAnnotationIter.hasNext())  annotationLoopHasNext = true;
			}
			else annotationLoopHasNext = true;

			// COMMENT FAIRE POUR FACTORISER SON CODE ie utiliser le même code pour traiter l input type vue ou annotation de la même manière ?
			// Dans le cas où INPUT_A est null cad que l'on veut traiter l'INPUT_V
			// Alors en supposant qu'on arrive à entrer dans cette boucle grâce au default de CONTEXT_A (i.e. DocumentAnnotation)
			// et que le contenu marqué par CONTEXT_A correspond au SofaDataString...
			// alors l'idée pour pouvoir bénéficier du code ci-dessous est de dire que l'inputAnnotation est DocumentAnnotation
			// Attention ca voudrait dire que si CONTEXT_A est défini mais pas INPUT_A alors CONTEXT_A aura le même rôle que INPUT_A
			// A poursuivre...
			//if (outputAnnotationString == null) {
			//}

			// Pour chaque inputAnnotation présent dans le context ou input view
			while (annotationLoopHasNext) {

				String inputTextToProcess = "" ;
				int beginFeatureValueFromAnnotationToCreate ; 
				int endFeatureValueFromAnnotationToCreate; 

				// Structure de données nécessaires en cas d'inputType == annotation
				Class<Annotation> InputAnnotationClass;
				Annotation inputAnnotation = null;

				// Récupère le texte à traiter et ses offsets qui pourront éventuellement servir
				// si l'outputType est Annotation
				log("Getting the text to proceed");
				if (inputType.equalsIgnoreCase(INPUTTYPE_ANNOTATION)) {

					// Récupère et cast l'inputAnnotation courante à manipuler
					InputAnnotationClass = UIMAUtilities.getClass(inputAnnotationString);

					inputAnnotation = (Annotation) inputAnnotationIter
					.next();
					InputAnnotationClass.cast(inputAnnotation);


					// Invoque la récupération de la valeur dont l'inputFeatureString est spécifiée pour l'annotation courante 
					// inputTextToProcess = inputAnnotation.getCoveredText();
					inputTextToProcess = UIMAUtilities.invokeStringGetMethod(InputAnnotationClass, inputAnnotation, inputFeatureString);

					//log ("Debug: inputTextToProcess>"+inputTextToProcess+"<");
					beginFeatureValueFromAnnotationToCreate = inputAnnotation.getBegin(); 
					endFeatureValueFromAnnotationToCreate= inputAnnotation.getEnd(); 
				}
				else {
					inputTextToProcess = inputViewJCas.getSofaDataString();
					beginFeatureValueFromAnnotationToCreate = 0; 
					endFeatureValueFromAnnotationToCreate = inputViewJCas.getSofaDataString().length(); //+1; 
				}


				/** -- Execute and get result **/
				log("Executing ");
				String commandLocalResultString = "";
				commandLocalResultString =  execute (inputViewJCas, inputTextToProcess, beginFeatureValueFromAnnotationToCreate, endFeatureValueFromAnnotationToCreate);
				
				
				// Soit pour chaque annotation en entrée à traiter soit pour la vue en entrée
				if (outputType.equalsIgnoreCase(OUTPUTTYPE_ANNOTATION)) {
					/** -- Create annotation**/
					log("Creating output annotation");
					//createANewAnnotation(aJCas, inputAnnotation.getBegin(),inputAnnotation.getEnd(),commandLocalResultString);
					UIMAUtilities.createAnnotation(outputViewJCas,outputAnnotationString, beginFeatureValueFromAnnotationToCreate,endFeatureValueFromAnnotationToCreate,outputFeatureString,commandLocalResultString);
				}
				else { 
					// L'output_type est view
					// On stocke les résultats obtenus pour chaque annotation
					// On copiera le tout dans le sofaDataString en une seule fois
					//if (commandResultString == null ) {commandResultString = commandLocalResultString;}
					//else {
					log("Concating the result");
					commandResultString += //"\n"+
						commandLocalResultString;
					//}
				}	

				if (inputType.equalsIgnoreCase(INPUTTYPE_ANNOTATION)) {
					if (inputAnnotationIter.hasNext())  annotationLoopHasNext = true;
					else annotationLoopHasNext = false;}
				else annotationLoopHasNext = false;
			}

			if (inputType.equalsIgnoreCase(INPUTTYPE_ANNOTATION)) {
				if (contextAnnIdxIter.hasNext())  contextLoopHasNext = true;
				else contextLoopHasNext = false;}
			else contextLoopHasNext = false;
		}

		/** -- Create view **/
		// output_v_string est défini ; potentiellement il est égal à input_v ; normalement la vue n'existe pas et est à créer
		if (outputType.equalsIgnoreCase(OUTPUTTYPE_VIEW)) {
			log("Creating output view");
			// ici on suppose que outputViewString ne correspond à aucune vue existante (a fortiori est différent de inputViewString) 
			// et que createView génèrera une erreur si la vue existe déjà
			UIMAUtilities.createView(aJCas, outputViewString, commandResultString, outputViewTypeMimeString);
		}
	}

	/**
	 * This abstract method corresponds to the main process method 
	 * of Analysis Engines that inherits from this class. 
	 * Such Analysis Engines must override this method.  
	 * 
	 * @param cas the CAS view that will be processed. 
	 * @throws AnalysisEngineProcessException if something wrong happened
	 * while processing this CAS view. 
	 */
	protected abstract String execute(JCas inputViewJCas, String inputTextToProcess, int beginFeatureValue, int endFeatureValue) throws AnalysisEngineProcessException;
	
	/**
	 * Log messages
	 * @param message to log 
	 */
	protected void log(String message) {
		//getContext()
		//.getLogger()
		//.log(Level.FINEST,	COMPONENT_ID + "- "+ message);
		System.out.println(COMPONENT_ID + "- "+ message);
	}


	/**
	 * @return the cOMPONENT_NAME
	 */
	protected String getCOMPONENT_NAME() {
		return COMPONENT_NAME;
	}


	/**
	 * @param cOMPONENTNAME the cOMPONENT_NAME to set
	 */
	protected void setCOMPONENT_NAME(String cOMPONENTNAME) {
		COMPONENT_NAME = cOMPONENTNAME;
	}


	/**
	 * @return the cOMPONENT_VERSION
	 */
	protected String getCOMPONENT_VERSION() {
		return COMPONENT_VERSION;
	}


	/**
	 * @param cOMPONENTVERSION the cOMPONENT_VERSION to set
	 */
	protected void setCOMPONENT_VERSION(String cOMPONENTVERSION) {
		COMPONENT_VERSION = cOMPONENTVERSION;
	}


	/**
	 * @return the cOMPONENT_ID
	 */
	protected String getCOMPONENT_ID() {
		return COMPONENT_ID;
	}


	/**
	 * @param cOMPONENTID the cOMPONENT_ID to set
	 */
	protected void setCOMPONENT_ID(String cOMPONENTID) {
		COMPONENT_ID = cOMPONENTID;
	}


	/**
	 * @return the iNPUTTYPE_ANNOTATION
	 */
	protected static String getINPUTTYPE_ANNOTATION() {
		return INPUTTYPE_ANNOTATION;
	}


	/**
	 * @param iNPUTTYPEANNOTATION the iNPUTTYPE_ANNOTATION to set
	 */
	protected static void setINPUTTYPE_ANNOTATION(String iNPUTTYPEANNOTATION) {
		INPUTTYPE_ANNOTATION = iNPUTTYPEANNOTATION;
	}


	/**
	 * @return the iNPUTTYPE_VIEW
	 */
	protected static String getINPUTTYPE_VIEW() {
		return INPUTTYPE_VIEW;
	}


	/**
	 * @param iNPUTTYPEVIEW the iNPUTTYPE_VIEW to set
	 */
	protected static void setINPUTTYPE_VIEW(String iNPUTTYPEVIEW) {
		INPUTTYPE_VIEW = iNPUTTYPEVIEW;
	}


	/**
	 * @return the oUTPUTTYPE_ANNOTATION
	 */
	protected static String getOUTPUTTYPE_ANNOTATION() {
		return OUTPUTTYPE_ANNOTATION;
	}


	/**
	 * @param oUTPUTTYPEANNOTATION the oUTPUTTYPE_ANNOTATION to set
	 */
	protected static void setOUTPUTTYPE_ANNOTATION(String oUTPUTTYPEANNOTATION) {
		OUTPUTTYPE_ANNOTATION = oUTPUTTYPEANNOTATION;
	}


	/**
	 * @return the oUTPUTTYPE_VIEW
	 */
	protected static String getOUTPUTTYPE_VIEW() {
		return OUTPUTTYPE_VIEW;
	}


	/**
	 * @param oUTPUTTYPEVIEW the oUTPUTTYPE_VIEW to set
	 */
	protected static void setOUTPUTTYPE_VIEW(String oUTPUTTYPEVIEW) {
		OUTPUTTYPE_VIEW = oUTPUTTYPEVIEW;
	}


	/**
	 * @return the runIdString
	 */
	protected String getRunIdString() {
		return runIdString;
	}


	/**
	 * @param runIdString the runIdString to set
	 */
	protected void setRunIdString(String runIdString) {
		this.runIdString = runIdString;
	}


	/**
	 * @return the inputViewString
	 */
	protected String getInputViewString() {
		return inputViewString;
	}


	/**
	 * @param inputViewString the inputViewString to set
	 */
	protected void setInputViewString(String inputViewString) {
		this.inputViewString = inputViewString;
	}


	/**
	 * @return the contextAnnotationString
	 */
	protected String getContextAnnotationString() {
		return contextAnnotationString;
	}


	/**
	 * @param contextAnnotationString the contextAnnotationString to set
	 */
	protected void setContextAnnotationString(String contextAnnotationString) {
		this.contextAnnotationString = contextAnnotationString;
	}


	/**
	 * @return the inputAnnotationString
	 */
	protected String getInputAnnotationString() {
		return inputAnnotationString;
	}


	/**
	 * @param inputAnnotationString the inputAnnotationString to set
	 */
	protected void setInputAnnotationString(String inputAnnotationString) {
		this.inputAnnotationString = inputAnnotationString;
	}


	/**
	 * @return the inputFeatureString
	 */
	protected String getInputFeatureString() {
		return inputFeatureString;
	}


	/**
	 * @param inputFeatureString the inputFeatureString to set
	 */
	protected void setInputFeatureString(String inputFeatureString) {
		this.inputFeatureString = inputFeatureString;
	}


	/**
	 * @return the outputViewString
	 */
	protected String getOutputViewString() {
		return outputViewString;
	}


	/**
	 * @param outputViewString the outputViewString to set
	 */
	protected void setOutputViewString(String outputViewString) {
		this.outputViewString = outputViewString;
	}


	/**
	 * @return the outputViewTypeMimeString
	 */
	protected String getOutputViewTypeMimeString() {
		return outputViewTypeMimeString;
	}


	/**
	 * @param outputViewTypeMimeString the outputViewTypeMimeString to set
	 */
	protected void setOutputViewTypeMimeString(String outputViewTypeMimeString) {
		this.outputViewTypeMimeString = outputViewTypeMimeString;
	}


	/**
	 * @return the outputAnnotationString
	 */
	protected String getOutputAnnotationString() {
		return outputAnnotationString;
	}


	/**
	 * @param outputAnnotationString the outputAnnotationString to set
	 */
	protected void setOutputAnnotationString(String outputAnnotationString) {
		this.outputAnnotationString = outputAnnotationString;
	}


	/**
	 * @return the outputFeatureString
	 */
	protected String getOutputFeatureString() {
		return outputFeatureString;
	}


	/**
	 * @param outputFeatureString the outputFeatureString to set
	 */
	protected void setOutputFeatureString(String outputFeatureString) {
		this.outputFeatureString = outputFeatureString;
	}


	/**
	 * @return the inputType
	 */
	protected String getInputType() {
		return inputType;
	}


	/**
	 * @param inputType the inputType to set
	 */
	protected void setInputType(String inputType) {
		this.inputType = inputType;
	}


	/**
	 * @return the outputType
	 */
	protected String getOutputType() {
		return outputType;
	}


	/**
	 * @param outputType the outputType to set
	 */
	protected void setOutputType(String outputType) {
		this.outputType = outputType;
	}



}