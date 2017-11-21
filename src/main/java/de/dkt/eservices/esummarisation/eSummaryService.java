package de.dkt.eservices.esummarisation;

/**
 * Created by Ankit Srivastava on 30/09/16.
 * Class encapsulating command for summarising a document, i.e. invoking shell script for python from Java
 */

import java.io.BufferedReader;																											
import java.io.InputStreamReader;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.io.FileWriter;
import java.io.IOException;
//import de.dkt.eservices.erattlesnakenlp.modules.Sparqler;

public class eSummaryService {

	// hard-coded public variables: the location of the summarize.py tool
    //public String pwd = "/Users/ansr01/Software/summarise/summarize.py/"; // for local machine
    public String pwd = "/usr/local/summarisation/"; // for dkt server
    
	/**
	 * Method to send a shell script execution command summarise to the server
	 * @param inputDoc document to summarise
	 * @param dLang id of the language, ie language of the inputDoc
	 * @param algorithm one amongst [lead, centroid, lexpagerank, textrank, submodular]
	 * @param sumlength is integer indicating length of summary
	 * @param isSingle indicates true for Single Document and false for Multi Document
	 * @return the output of the shell command, usually the summarised document in dLang language
	 */
	public String executeCommandSummarise(String inputDoc, String dLang, String algorithm, int sumlength, boolean isSingle){

    	
        
        File f = new File(pwd, "tempFile");  // temporary file created in pwd
        String command;
        String langName = new String();
        if (dLang.equalsIgnoreCase("en")){
        	langName = new String ("2");
        }
        if (dLang.equalsIgnoreCase("de")){
        	langName = new String ("3");
        }
        if (dLang.equalsIgnoreCase("es")){
        	langName = new String ("3");
        }
        
        //System.out.println("I have landed\n");
        int method; //default value for algorithm
        switch (algorithm.toLowerCase()){
		case "lead":{
			method=1;
			break;
		}
		case "centroid":{
			method=2;
			break;
		}
		case "lexpagerank":{
			method=4;
			break;
		}
		case "textrank":{
			method=5;
			break;
		}
		case "submodular":{
			method=6;
			break;
		}
		default:
			method = 2;
			break;
	}
	

        
        // storing the inputDoc in a file
        try {
            PrintWriter outf = new PrintWriter(new FileWriter(f));
            outf.println(inputDoc);
            outf.close();
        }
        catch (IOException e) {}

        // The actual command to call the shell script 
       if(isSingle){ // Single Document Summarisation
    	   command = "sh " + pwd + "getSummary.sh -i " + f.getAbsoluteFile() + " -l " + langName + " -m " + new Integer(method).toString() + " -n " + new Integer(sumlength).toString();
       }
       else { // multi document summarisation
    	   command = "sh " + pwd + "getMultiSummary.sh -i " + f.getAbsoluteFile() + " -l " + langName + " -m " + new Integer(method).toString() + " -n " + new Integer(sumlength).toString();
       }
        
     
        

        // Executing the command using a Process object
        StringBuffer output = new StringBuffer();
        Process p;

        try{
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            //BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("UTF-8")));
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            //System.out.println("I have received output from translation\n");

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line);
                //System.out.println("arre:" + line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
	
	// Abstract Summarisation
	public String executeCommandAbstract(String inputDoc, String dLang){

    	
        
        File f = new File(pwd, "tempFile");  // temporary file created in pwd
        String command;
        String langName = new String();
        if (dLang.equalsIgnoreCase("en")){
        	langName = new String ("english");
        }
        if (dLang.equalsIgnoreCase("de")){
        	langName = new String ("german");
        }
        if (dLang.equalsIgnoreCase("es")){
        	langName = new String ("spanish");
        }
        
       
	

        
        // storing the inputDoc in a file
        try {
            PrintWriter outf = new PrintWriter(new FileWriter(f));
            outf.println(inputDoc);
            outf.close();
        }
        catch (IOException e) {}

        
    	command = "sh " + pwd + "getAbsSummary.sh -i " + f.getAbsoluteFile() + " -l " + langName;
       
        
     
        

        // Executing the command using a Process object
        StringBuffer output = new StringBuffer();
        Process p;

        try{
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            //BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("UTF-8")));
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            //System.out.println("I have received output from translation\n");

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line);
                //System.out.println("arre:" + line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
        
    
    public static void main(String[] args) {

    }
}
