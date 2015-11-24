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

	javac datastructs/Review.java datastructs/Product.java datastructs/GenericStack.java datastructs/Query.java datastructs/StringEntry.java exceptions/DBMSException.java indexer/IndexGen.java parser/DataFileGenerator.java parser/parser.java querier/DBMS.java querier/QueryRunner.java


Then, run the first program with:

	cat data.txt | java parser.parser

This results in the generation and the completion of the four main textfiles: reviews.txt, pterms.txt, rterms.txt, and scores.txt. Please replace 'data.txt' with the text file you are interested in using! 
To compare the file to the sample on eClass, run the following where the eclass version is (review_eClass.txt, pterms_eClass.txt ...)

	diff reviews_eClass.txt reviews.txt
	
If there is any output, the two files do not match. If there is no output then the files are identical.

####2. Building indexes
Create the indexes required for part three using the aforementioned program:

	java indexer.IndexGen

Once this is run, the .idx values for all files will be generated.

####3. Running queries
Once you have built the indexes, all that's left is running the query system. That can be done by:

	java querier.QueryRunner

The following queries are available:
	
	r:____ 

	Query the review summary and text for the selected word (must be 3 characters or more).

	p:____

	Query the product name for the selected word (must be 3 characters or more).

	______

	Query the product name, review summary and text for the selected word (must be 3 characters or more).

	_____%

	Query the product name, review summary and text for an entry that begins with the selected text

	rscore </=/> INT

	Find all reviews with a score less than / equal to / greater than the integer value.

	rdate </=/> DATE

	Find all reviews that begin before / at / after the date selected.

	pprice </=/> PRICE

	Find all products that have a price less than / equal to / greater than the input price (input as XX.XX).

All the queries can be completed together and interchangably.