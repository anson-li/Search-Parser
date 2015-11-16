# CMPUT 291 Project 2

## References:
Berkeley DB development structure, C / Java-based.

## Team members:
Satyen Akolkar and Anson Li

## Project structure:
/ref contains reference code for Berkeley DB using Python, C, or Java implementation.
/src contains the code used for the project base.

## How to use:
The program is setup in three stages:

###1. Parsing of text file and generation of four main text files
First, simply compile every related function:

	javac Review.java Product.java parser.java 

Then, run the first program with:

	java parser 

This results in the generation and the completion of the four main textfiles: reviews.txt, pterms.txt, rterms.txt, and scores.txt.

####2. Building indexes
First, sort the generated text files using the following instructions:

	sort -u -o rterms.txt rterms.txt
	sort -u -o pterms.txt pterms.txt
	sort -u -o scores.txt scores.txt

These text files will be updated on their own files, so there's no need to resort the files after.