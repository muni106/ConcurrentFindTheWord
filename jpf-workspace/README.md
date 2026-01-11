PCD a.y. 2024-2025 - ISI LM UNIBO - Cesena Campus

## JPF Lightweight Environment 

This is a customised lightweight environment to work with JPF (Java Path Finder). 

- Be sure that you are using a JDK 8 by running `javac -version`and `java -version` (**It works only with JDK 8**)
- To compile sources in `src` (from a terminal/shell):  

	`jpf-workspace> javac -classpath JPF/build/jpf-classes.jar -d bin <java src files>`  

	Example:  

	`jpf-workspace> javac -classpath JPF/build/jpf-classes.jar -d bin src/pcd/lab02/jpf/*.java`

- To run JPF - two options:
	- using a *.jpf file:  
  
		`jpf-workspace> java -jar JPF/build/RunJPF.jar <JPF file>`

		Example:

		`jpf-workspace> java -jar JPF/build/RunJPF.jar TestScenarios.jpf`
	- specifying the main class:  

		`jpf-workspace> java -jar JPF/build/RunJPF.jar +classpath=bin <Java main class>`

		Example

		`jpf-workspace> java -jar JPF/build/RunJPF.jar +classpath=bin pcd.lab02.jpf.TestLostUpdate `
	- Options: memory extension & listeners:  

		`jpf-workspace> java -Xmx1024m -jar ./JPF/build/RunJPF.jar +classpath=bin/main +listener=gov.nasa.jpf.listener.PreciseRaceDetector pcd.lab02.jpf.TestLostUpdate`


  


