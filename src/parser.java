/**
* Phase 1: Preparing Data Files
*
* Write a program that reads reviews from standard input and construct the following four files.
* Before constructing these files, escape every double quote " in the input by replacing it with &quot; 
* and escape every backslash (\) by replacing it with double backslash (\\). 
* Backslash is escaped since Berkeley DB does not like it in the input; double quote is also escaped so 
* the symbol can be used to encode the character strings.
*
* reviews.txt: This file has one row for each review record. The fields are ordered as given in the input, 
* and the consecutive fields are separated by a comma. The fields product title, profile name (of the reviewer), 
* review summary and review text are placed inside quotations to avoid a possible mixup in the separator character, 
* for example when a comma appears inside a text field. The first field is the record number (or record id) which is assigned 
* sequentially to input records with the first record assigned id 1. Here is this file for our sample file with 10 records.
*
* pterms.txt: This file includes terms of length 3 or more characters extracted from product titles; a term is a consecutive 
* sequence of alphanumeric and underscore '_' characters, i.e [0-9a-zA-Z_] or the character class \w in Perl or Python. 
* The format of the file is as follows: for every term T in a product title of a review with id I, there is a row in 
* this file of the form T',I where T' is the lowercase form of T. That means, terms must be converted to all lowercase before 
* writing them in this file.
*
* rterms.txt: This file includes terms of length 3 or more characters extracted from the fields review summary and 
* review text. The file format and the way a term is defined is the as given above for the filepterms.txt. 
*
* scores.txt: This file includes one line for each review record in the form of sc:I where sc is the review score 
* and I is the review id.
**/

/*
Intended table design:

create table PRODUCT {
	productId CHAR[15],
	title CHAR[150],
	price INT,
	PRIMARY KEY (productId)
}

create table REVIEW {
	userId CHAR[15],
	productId CHAR[15],
	profileName CHAR[50],
	helpfulness CHAR[10],
	score DOUBLE,
	time DATETIME,
	summary CHAR[50],
	text CHAR[500],
	PRIMARY KEY (userId, productId),
	FOREIGN KEY(productId) ON DELETE CASCADE
}

*/
//package c291proj2;

import com.sleepycat.db.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

public class parser {

	public static void main(String[] args){

		// reference @ http://www.mkyong.com/java/how-to-read-file-from-java-bufferedreader-example/
		BufferedReader br = null;

		try {

			File reviewsFile = new File("./reviews.txt");
			File ptermsFile = new File("./pterms.txt");
			File rtermsFile = new File("./rterms.txt");
			File scoresFile = new File("./scores.txt");

			PrintWriter reviewsWriter = new PrintWriter(reviewsFile, "UTF-8");
			PrintWriter ptermsWriter = new PrintWriter(ptermsFile, "UTF-8");
			PrintWriter rtermsWriter = new PrintWriter(rtermsFile, "UTF-8");
			PrintWriter scoresWriter = new PrintWriter(scoresFile, "UTF-8");

			String sCurrentLine;
			br = new BufferedReader(new InputStreamReader(System.in));
			Integer counter = 1;
			Product product = new Product();
			Review review = new Review();
			while ((sCurrentLine = br.readLine()) != null) {
				// currentline holds the full value, but can be a simple space
				if (!sCurrentLine.equals("")) {
					String replaced = sCurrentLine.split(": ", 2)[0].replaceAll("\"", "&quot;").replaceAll("\\\\","\\\\\\\\");
					String value = sCurrentLine.split(": ", 2)[1].replaceAll("\"", "&quot;").replaceAll("\\\\","\\\\\\\\"); // doesn't account for any further splits...
					switch (replaced) {
						case "product/productId":
							product.setID(value);
							review.setProductID(value);
							break;
						case "product/title":
							product.setTitle(value);
							break;
						case "product/price":
							product.setPrice(value);
							break;
						case "review/userId":
							review.setUserID(value);
							break;
						case "review/profileName":
							review.setProfileName(value);
							break;
						case "review/helpfulness":
							review.setHelpfulness(value);
							break;
						case "review/score":
							review.setScore(Double.valueOf(value));
							break;
						case "review/time":
							review.setTime(value);
							break;
						case "review/summary":
							review.setSummary(value);
							break;
						case "review/text":
							review.setText(value);
							break;
						default:
							throw new IllegalArgumentException("Invalid value entered: " + replaced);
					}
					//System.out.println(sCurrentLine);
				} else {

					reviewsWriter.println(counter+","+product.getID()+",\""+product.getTitle()+"\","+product.getPrice()+","+review.getUserID()+
						",\""+review.getProfileName()+"\","+review.getHelpfulness()+","+review.getScore()+","+review.getTime()+",\""+review.getSummary()+"\",\""+
						review.getText()+"\"");
					scoresWriter.println(review.getScore()+","+counter);

					// dev for pterms: http://stackoverflow.com/questions/10038377/for-loop-to-separate-a-string-with-spaces-java
					String[] titleParts = product.getTitle().replace("!"," ").split("[\\W]");
					for (String part: titleParts) {
						/**
						* FIXME: Change split regex to include underscore character, _ . 
						*/
						if (part.length() >= 3) {
							ptermsWriter.println(part.toLowerCase() + "," + counter); 
						}
					}

					// dev for rterms: http://stackoverflow.com/questions/10038377/for-loop-to-separate-a-string-with-spaces-java
					String[] sumParts = review.getSummary().replace("!"," ").split("[\\W]");
					for (String part: sumParts) {
						/**
						* FIXME: Change split regex to include underscore character, _ . 
						*/
						if (part.length() >= 3) {
							rtermsWriter.println(part.toLowerCase() + "," + counter); 
						}
					}

					// dev for rterms: http://stackoverflow.com/questions/10038377/for-loop-to-separate-a-string-with-spaces-java
					String[] txtParts = review.getText().replace("!"," ").split("[\\W]");
					for (String part: txtParts) {
						/**
						* FIXME: Change split regex to include underscore character, _ . 
						*/
						if (part.length() >= 3) {
							rtermsWriter.println(part.toLowerCase() + "," + counter); 
						}
					}
					product = new Product(); // clear items
					review = new Review();
					counter++;
				}
			}
			reviewsWriter.close();
			ptermsWriter.close();
			rtermsWriter.close();
			scoresWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
}
