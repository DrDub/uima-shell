/** 
 * UIMA Shell AE 
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

package fr.univnantes.lina.uima.shell.ae;


import fr.univnantes.lina.uima.shell.types.ShellAnnotation;
import fr.univnantes.lina.uima.util.UIMAUtilities;
//

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas; //
import org.apache.uima.jcas.tcas.Annotation;
// Exception
// ResourceConfigurationException if invalid configuration parameter
// ResourceInitializationException if initialization fails for other
// reason
// AnalysisEngineProcesException if the processing fails
import org.apache.uima.resource.ResourceInitializationException; // import org.apache.uima.resource.ResourceConfigurationException;
//import org.apache.uima.analysis_engine.AnalysisEngineProcessException; 
//import org.apache.uima.resource.ResourceProcessException;
//import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
//
// Logger
import org.apache.uima.util.Level; // import java.util.regex.Matcher;

import com.developpez.adiguba.shell.Shell;
//
// Types des données manipulés
// La partie métier
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter; //
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
//import java.io.InputStream;
//import java.io.InputStreamReader;
// import java.io.OutputStream;
// import java.util.regex.PatternSyntaxException;
// import java.util.regex.Pattern;

import  fr.univnantes.lina.util.JavaUtilities;

/**
 * UIMA Shell wrapper sAnalysis Engine
 * @author Nicolas Hernandez
 */
public class ShellAE extends JCasAnnotator_ImplBase {

	/**
	 * When you use the code source as an AE example, please redefine the 
	 * "Common component constants Default" and the "Common component parameter values"
	 */


	/** Common component constants
	 * To define when you create a new component 
	 * */
	public static String COMPONENT_NAME = "Shell";
	public static String COMPONENT_VERSION = "100930";
	public static String COMPONENT_ID = COMPONENT_NAME + "-"
	+ COMPONENT_VERSION;
	//exception
	public static final String MESSAGE_DIGEST = COMPONENT_ID+"_Messages";
	//tmp file
	//log
	/** Common component parameters in descriptor file*/



	// View name to consider as the view to process
	public static String PARAM_NAME_INPUT_VIEW = "InputView";
	// Type name of the annotations to consider as the context annotations in
	// which
	// the process will be performed
	public static String PARAM_NAME_CONTEXT_ANNOTATION = "ContextAnnotation";
	// Type name of the annotations to consider as the token units to be
	// processed
	public static String PARAM_NAME_INPUT_ANNOTATION = "InputAnnotation";
	// Feature name of the annotations to consider as the token units to be
	// processed
	public static String PARAM_NAME_INPUT_FEATURE = "InputFeature";
	// View name to consider as the view to receive the result
	public static String PARAM_NAME_OUTPUT_VIEW = "OutputView";
	// Type mime to consider for storing the result in the sofaDataString
	public static String PARAM_NAME_OUTPUT_VIEW_TYPE_MIME = "OutputViewTypeMime";	
	// Type name of the annotations to create as the analysis result
	public static String PARAM_NAME_OUTPUT_ANNOTATION = "OutputAnnotation";
	// Type name of the feature to create as the analysis result
	public static String PARAM_NAME_OUTPUT_FEATURE = "OutputFeature";
	// An identifier for the current run.
	// This identifier is added into all the annotations that are created during 
	// the current execution.
	public static String PARAM_NAME_RUNID = "RunId";


	/** Default common component parameter values in descriptor file**/
	// Default view name if none are specified by the view parameter
	public static String DEFAULT_INPUT_VIEW = "_InitialView";
	// Default context annotation name if none is specified by the context
	// annotation parameter
	public static String DEFAULT_CONTEXT_ANNOTATION = "uima.tcas.DocumentAnnotation";
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
	public static String DEFAULT_OUTPUTVIEW_TYPEMIME = "text/plain";
	// 
	public static String INPUTTYPE_ANNOTATION = "annotation";
	public static String INPUTTYPE_VIEW = "view";
	public static String OUTPUTTYPE_ANNOTATION = "annotation";
	public static String OUTPUTTYPE_VIEW = "view";



	/** Specific component parameters in descriptor file */
	public static String PARAM_NAME_ENVVAR = "EnvironmentVariables";
	public static String PARAM_NAME_WORKINGDIR = "WorkingDirectory";
	public static String PARAM_NAME_CHARSET = "CharsetName";
	public static String PARAM_NAME_PRECMDTOKENS = "PreCommandTokens";
	public static String PARAM_NAME_POSTCMDTOKENS = "PostCommandTokens";

	//public static String PARAM_NAME_COMMAND = "Command";
	//public static String PARAM_NAME_SHELL = "Shell";
	//public static String PARAM_NAME_INLINE = "PipedAndNotAsArgument";

	/** Default specific component parameter values in descriptor file**/
	// public static String DEFAULT_SHELL =  "/bin/bash";

	/** Common component variables */
	private String runIdString = null;

	private String inputViewString = null;
	private String contextAnnotationString = null;
	private String inputAnnotationString = null;
	private String inputFeatureString = null;

	private String outputViewString = null;
	private String outputViewTypeMimeString = null;

	private String outputAnnotationString = null;
	private String outputFeatureString = null;

	private String inputType = "";
	private String outputType = "";


	/** Specific component variable */
	private String[] envVarStringArray = null;
	private String workingDirString = null;
	private File workingDirFile  = null;
	private String charsetNameString = null;
	private String[] preCmdTokensStringArray  = null;
	private String[] postCmdTokensStringArray  = null;
	private String[] cmdArrayStringArray  = null;
	private String cmdString  = null;




	//final private String classDescriptor = "exec";
	//private String command = "";
	//private boolean pipedAndNotAsArgument = true;
	//	private String shell =  DEFAULT_SHELL;
	// private Process m_process;




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


		envVarStringArray = (String[]) aContext.getConfigParameterValue(PARAM_NAME_ENVVAR);
		if (envVarStringArray.length  == 0 ) {envVarStringArray = null;}

		workingDirString = (String) aContext.getConfigParameterValue(PARAM_NAME_WORKINGDIR);
		if (workingDirString != null ) 
		{workingDirFile = new java.io.File (workingDirString);}

		charsetNameString = (String) aContext.getConfigParameterValue(PARAM_NAME_CHARSET);

		preCmdTokensStringArray = (String[]) aContext.getConfigParameterValue(PARAM_NAME_PRECMDTOKENS);
		postCmdTokensStringArray = (String[]) aContext.getConfigParameterValue(PARAM_NAME_POSTCMDTOKENS);

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
	public void browseInput(JCas aJCas, String inputViewString, String contextAnnotationString, String inputAnnotationString, String inputFeatureString, String outputViewString, String outputViewTypeMimeString, String outputAnnotationString, String ouputFeatureString) throws AnalysisEngineProcessException {

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
					//try {
					//	InputAnnotationClass = (Class<Annotation>) Class
					//	.forName(inputAnnotationString);
						InputAnnotationClass = UIMAUtilities.getClass(inputAnnotationString);
					//} catch (ClassNotFoundException e) {

					//} catch (AnalysisEngineProcessException e) {
					//	String errmsg = "Error: Class " + inputAnnotationString
					//	+ " not found !";
					//	throw new AnalysisEngineProcessException(errmsg,
					//			new Object[] { inputAnnotationString },e);	
					//	//e.printStackTrace();
					//}
					inputAnnotation = (Annotation) inputAnnotationIter
					.next();
					InputAnnotationClass.cast(inputAnnotation);

					
					// Invoque la récupération de la valeur dont l'inputFeatureString est spécifiée pour l'annotation courante 
					//inputTextToProcess = inputAnnotation.getCoveredText();
					inputTextToProcess = UIMAUtilities.invokeStringGetMethod(InputAnnotationClass, inputAnnotation, inputFeatureString);
					
					//log ("Debug: inputTextToProcess>"+inputTextToProcess+"<");
					beginFeatureValueFromAnnotationToCreate = inputAnnotation.getBegin(); 
					endFeatureValueFromAnnotationToCreate= inputAnnotation.getEnd(); 
				}
				else {
					inputTextToProcess = inputViewJCas.getSofaDataString();
					beginFeatureValueFromAnnotationToCreate = 0; 
					endFeatureValueFromAnnotationToCreate= inputViewJCas.getSofaDataString().length(); //+1; 
				}


				/** -- Prepare the command **/
				log("Building the command");
				String cmdString = buildTheCommand(preCmdTokensStringArray,inputTextToProcess, postCmdTokensStringArray);

				/** -- Execute and get result **/
				log("Executing the command >"+cmdString+"<");
				String commandLocalResultString = "";
				commandLocalResultString = command(cmdString, charsetNameString, workingDirString, envVarStringArray);

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
	 * Crée un processus représentant la commande du shell et l'associe à une instance de ProcessConsumer
	 * @param cmdString
	 * @param charsetNameString
	 * @param workingDirString
	 * @param envVarStringArray
	 * @return commandResultString
	 * @throws AnalysisEngineProcessException
	 */
	private String command(String cmdString, String charsetNameString, String workingDirString, String [] envVarStringArray)
	throws AnalysisEngineProcessException {
		String commandResultString = null;
		Shell sh = new Shell(); 

		/** Etat de l'environnement d'exécution du shell **/
		// Retourne le nom du shell système.
		log("Debug: nom du shell système >"+sh.toString()+"<");
		// Retourne le charset associé avec cette instance de shell. 
		log("Debug: charset du shell>"+sh.getCharset().toString()+"<");
		// Retourne le répertoire à partir duquel les commandes du shell seront lancés. 
		log("Debug: repertoire d'exécution du shell>"+sh.getDirectory().getAbsolutePath()+"<");
		// Retourne une map contenant les variables d'environnements utilisateurs. Cette Map est librement modifiables afin d'ajouter/supprimer des éléments.

		Map<String,String> varUserEnvMap = sh.getUserEnv();
		if (envVarStringArray != null) log("Debug: envVar.length"+envVarStringArray.length);	

		// Indique si les variables d'environnements de l'application Java courante doivent être passé aux commandes lancées par ce shell.
		//if (sh.isSystemEnvInherited()) {
		//	log("Debug: les variables d'environnements de l'application Java courante sont passées aux commandes lancées par ce shell");	
		//}

		// Déclaration au sein du shell des var d'env définies en paramètres
		if (envVarStringArray != null)
			for(int i = 0; i < envVarStringArray.length; i++){
				int firstEqualPos = envVarStringArray[i].indexOf("=");
				String key = envVarStringArray[i].substring(0, firstEqualPos);
				String value = envVarStringArray[i].substring(firstEqualPos+1, envVarStringArray[i].length());
				//log("Debug: var param"+key+"="+value);	
				varUserEnvMap.put(key, value);
			}

		if (varUserEnvMap.isEmpty()) {
			log("Debug: pas de variables d'environnement définies par paramètre");
		}
		else {
			Iterator varUserEnvIter = varUserEnvMap.keySet().iterator();
			while (varUserEnvIter.hasNext()) {
				String key = (String) varUserEnvIter.next();
				String value = (String) varUserEnvMap.get(key);
				log("Debug: user env var "+key+"="+value);	
			}
		}	

		// Modifie le charset associé avec cette instance de shell.
		if (charsetNameString != null)  sh.setCharset(charsetNameString);			
		// Modifie le répertoire à partir duquel les commandes du shell seront lancés.
		if (workingDirString != null ) sh.setDirectory(workingDirFile);


		// Crée un processus représentant la commande du shell et l'associe à une instance de ProcessConsumer
		try {
			commandResultString = sh.command(cmdString).consumeAsString();
		}
		catch (IllegalStateException e) {
			String errmsg = "ERROR: IllegalStateException with sh.command(cmdString).consumeAsString() !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);
			//e.printStackTrace();
		} catch (IOException e) {
			String errmsg = "ERROR: IOException with sh.command(cmdString).consumeAsString() !";
			throw new AnalysisEngineProcessException(errmsg,
					new Object[] {  },e);
			//e.printStackTrace();
		}
		return commandResultString;
	}

	/**
	 * Build the command 
	 * @param preCmdTokensStringArray
	 * @param dataString
	 * @param postCmdTokensStringArray
	 * @return cmdString
	 * @throws AnalysisEngineProcessException 
	 */
	private String buildTheCommand(String[] preCmdTokensStringArray, String dataString, String[] postCmdTokensStringArray) throws AnalysisEngineProcessException {
		log("Creating a temporary file containing the dataString to proceed");
		String[] dataStringArray = new String[1]; 
		dataStringArray[0] = UIMAUtilities.createTempTextFile (COMPONENT_ID + "_tmp_", ".bak", dataString);

		String[] tmp = JavaUtilities.concat(preCmdTokensStringArray, dataStringArray);
		cmdArrayStringArray = JavaUtilities.concat (tmp, postCmdTokensStringArray);
		String cmdString = "" ;
		for(int i = 0; i < cmdArrayStringArray.length; i++){
			cmdString = cmdString + " " + cmdArrayStringArray[i];
		}
		return cmdString;
	}



	/**
	 * Log messages
	 * @param message to log 
	 */
	private void log(String message) {
		//getContext()
		//.getLogger()
		//.log(Level.FINEST,	COMPONENT_ID + "- "+ message);
		System.out.println(COMPONENT_ID + "- "+ message);
	}



}