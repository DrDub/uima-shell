/** 
 * Java Utilities
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
package fr.univnantes.lina.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author hernandez-n
 *
 */
public class JavaUtilities {


	/*
	 * Fusionne deux tableaux de Type T en un seul
	 * */
	public static <T> T[] concat (T[] a, T[] b) {
	    final int alen = a.length;
	    final int blen = b.length;
	    final T[] result = (T[]) java.lang.reflect.Array.
	            newInstance(a.getClass().getComponentType(), alen + blen);
	    System.arraycopy(a, 0, result, 0, alen);
	    System.arraycopy(b, 0, result, alen, blen);
	    return result;
	}
	
	/**
	 * Create a temporary text file with a text given in parameter and return its absolute path
	 * @param prefixTmpFile
	 * @param suffixTmpFile
	 * @param text
	 * @return fileAbsPathStrg
	 * @throws IOException 
	 */
	public static String createTempTextFile (String prefixTmpFile, String suffixTmpFile, String text) throws IOException {
		String fileAbsPathStrg = null;
		File file = File.createTempFile(prefixTmpFile, suffixTmpFile);
		fileAbsPathStrg = file.getAbsolutePath();

		FileWriter fstream = new FileWriter(fileAbsPathStrg);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(text);
		out.close();
		return fileAbsPathStrg;
	}
}
