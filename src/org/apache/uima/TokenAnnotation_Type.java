
/* First created by JCasGen Wed Sep 29 23:45:19 CEST 2010 */
package org.apache.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Single token annotation
 * Updated by JCasGen Thu Sep 30 22:49:27 CEST 2010
 * @generated */
public class TokenAnnotation_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TokenAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TokenAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TokenAnnotation(addr, TokenAnnotation_Type.this);
  			   TokenAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TokenAnnotation(addr, TokenAnnotation_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = TokenAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.uima.TokenAnnotation");
 
  /** @generated */
  final Feature casFeat_tokenType;
  /** @generated */
  final int     casFeatCode_tokenType;
  /** @generated */ 
  public String getTokenType(int addr) {
        if (featOkTst && casFeat_tokenType == null)
      jcas.throwFeatMissing("tokenType", "org.apache.uima.TokenAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_tokenType);
  }
  /** @generated */    
  public void setTokenType(int addr, String v) {
        if (featOkTst && casFeat_tokenType == null)
      jcas.throwFeatMissing("tokenType", "org.apache.uima.TokenAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_tokenType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public TokenAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_tokenType = jcas.getRequiredFeatureDE(casType, "tokenType", "uima.cas.String", featOkTst);
    casFeatCode_tokenType  = (null == casFeat_tokenType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tokenType).getCode();

  }
}



    