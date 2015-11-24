package parser;

import datastructs.Product;
import datastructs.Review;

import java.io.*;

public class parser {

	/**
	* Parses the main text file entered (for example, data.txt) 
	* identified via user 'cat' process,
	* and prepares the four text files used for DB processes.
	*/
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
