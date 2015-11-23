# CMPUT 291 Project 2

## References:
Berkeley DB development structure, C / Java-based.

## Team members:
Satyen Akolkar and Anson Li

## Project structure:
* /ref contains reference code for Berkeley DB using Python, C, or Java implementation.
* /src contains the code used for the project base.

## How to use:
The program is setup in three stages:

###1. Parsing of text file and generation of four main text files
Before you begin to use Berkeley DB, please enter the following commands into the terminal to instantiate the paths:

	export CLASSPATH=$CLASSPATH:.:/usr/share/java/db.jar
	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/oracle/lib
	rm -f *.idx pterms.txt reviews.txt rterms.txt scores.txt

These ensure that the proper libaries are referenced for Berkeley DB production.
Once you have setup the correct paths, enter the /src folder. Then, simply compile every related function:

	javac Review.java Product.java parser.java IndexGen.java DBQuery.java

Then, run the first program with:

	cat data.txt | java parser 

This results in the generation and the completion of the four main textfiles: reviews.txt, pterms.txt, rterms.txt, and scores.txt.

to compare the file to the sample on eClass run the following where the eclass version is (review_eClass.txt, pterms_eClass.txt ...)

	diff reviews_eClass.txt reviews.txt
	
If there is any output, the two files do not match. If there is no output then the files are identical.

####2. Building indexes
Create the indexes required for part three using the aforementioned program:

	java IndexGen

Once this is run, the .idx values for all files will be generated.

####3. Running queries
I don't know anymore ! please help me i' m s t u c k i n a c o m p u