### OVERVIEW ###
This UIMA _Analysis Engine_ (AE) allows you to **run Shell command over a _Common Analysis Structure_ (CAS) element (_view_ or _annotation_) and to store the result either as a new view or a new annotation**.

It mainly aims at running within a UIMA aggregate processing chain some external tools available via command line. These tools should perform their processing by taking the input as a file name parameter or a standard input ( _stdin_ ) and produce the result via the standard output ( _stdout_ ).

With the [uima-connectors project](http://code.google.com/p/uima-connectors/), this current component aims at solving interoperability issues when dealing with non native UIMA tools.


The input must be either the _SofaDataString_ of a specified view, or the value of an annotation _String feature_ which should also be specified (e.g. _org.apache.uima.TokenAnnotation_ and _coveredText_ ).
It is possible to only process some annotations belonging themselves to some parts of the sofa specified also as an annotation.
The processing result can be saved as either the SofaDataString of a newly created view or the string feature value of a newly created annotation.

### FEATURES ###

  * implémentation d'un AE réalisant une _commande SHELL sur un élément input du CAS  (vue|annotation) et produisant un nouvel élément output (vue|annotation) au sein du CAS_
  * traitement utilisant l'[API SHELL de F. Martini](http://blog.developpez.com/adiguba/p3035/java/5-0-tiger/runtime-exec-n-est-pas-des-plus-simple/) (alias adiGuba) distribuée sous licence CeCILL-C et qui permet de simplifier l'exécution de programme et de ligne de commande depuis Java.
  * gestion de _multiples formats_ des commandes à exécuter où la donnée à traiter peut aussi bien provenir de l'entrée standard ou constituer un fichier texte passé en argument, être récupéré du système de fichier, pipé, redirigé...
  * traitement de la commande SHELL sous forme d'un _thread_
  * _paramétrage libre et générique des éléments du CAS (view/annotation/feature) d'entrée (à traiter) et de sortie (à créer)_ notamment à l'aide de l'API `java.lang.reflect` (constitue des exemples de code générique)

### CITE ###

If you use this project to support academic research, then please cite the following paper as appropriate. Thanks also to let me know by email (_nicolas.hernandez   @gmail.com)_.

```
%% hal-00481459, version 1
%% http://hal.archives-ouvertes.fr/hal-00481459/en/
@inproceedings{HERNANDEZ:2010:HAL-00481459:1,
    HAL_ID = {hal-00481459},
    URL = {http://hal.archives-ouvertes.fr/hal-00481459/en/},
    title = { {B}uilding a {F}rench-speaking community around {UIMA}, gathering research, education and industrial partners, mainly in {N}atural {L}anguage {P}rocessing and {S}peech {R}ecognizing domains},
    author = {{H}ernandez, {N}icolas and {P}oulard, {F}abien and {V}ernier, {M}atthieu and {R}ocheteau, {J}{\'e}r{\^o}me},
   
    affiliation = {{L}aboratoire d'{I}nformatique de {N}antes {A}tlantique - {LINA} - {CNRS} : {UMR}6241 - {U}niversit{\'e} de {N}antes - {E}cole des {M}ines de {N}antes },
    booktitle = {{W}orkshop {A}bstracts {LREC} 2010 {W}orkshop '{N}ew {C}hallenges for {NLP} {F}rameworks' },
    pages = {p64 },
    address = {{L}a {V}alleta {M}alte },
    day = {22},
    month = {05},
    year = {2010},
    URL = {http://hal.archives-ouvertes.fr/hal-00481459/PDF/nlpf_lrec10.pdf},
}
```

If you want to receive notifications on major updates, please send an email to the `nicolas.hernandez`'s gmail account with the following subject:  `uima-shell request for notifcation`.