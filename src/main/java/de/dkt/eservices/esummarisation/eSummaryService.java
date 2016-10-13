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
    public String pwd = "/usr/local/summarisation/WS_dkt/"; // for dkt server
    
	/**
	 * Method to send a shell script execution command summarise to the server
	 * @param inputDoc document to summarise
	 * @param dLang id of the language, ie language of the inputDoc
	 * 
	 * @return the output of the shell command, usually the summarised document in dLang language
	 */
	public String executeCommandSummarise(String inputDoc, String dLang){

    	
        
        File f = new File(pwd, "tempFile");  // temporary file created in pwd
        String command;

        //System.out.println("I have landed\n");

        
        // storing the inputDoc in a file
        try {
            PrintWriter outf = new PrintWriter(new FileWriter(f));
            outf.println(inputDoc);
            outf.close();
        }
        catch (IOException e) {}

        // The actual command to call the shell script with the 1 arguments: file
        // TODO: add language parameter
        command = pwd + "summarize.py " + f.getAbsoluteFile();
     
        

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
	
	/**
	 * Method to send a shell script execution command tokenization to the server
	 * @param inputStr string to translate
	 * @param ilang id of the language, ie language of the inputStr
	 * @param tokenize boolean value to indicate tokenize (true) or detokenize (false)
	 * 
	 * @return the output of the shell command, usually the tokenized or detokenized sentence in language
	 */
	public String executeCommandTokenize(String inputStr, String ilang, boolean tokenize){

    	
        
        File f = new File(pwd, "tempFile");  // temporary file created in pwd
        String command;

        
        // storing the inputStr in a file
        try {
            PrintWriter outf = new PrintWriter(new FileWriter(f));
            outf.println(inputStr);
            outf.close();
        }
        catch (IOException e) {}

        // The actual command to call the shell script with the 2 arguments: file lang
        if(tokenize) { // the output will be the tokenized
        	command = "sh " + pwd + "run_tokenize.sh -i " + f.getAbsoluteFile() + " -l " + ilang;
        }
        else {  // the output will be detokenized
        	command = "sh " + pwd + "run_detokenize.sh -i " + f.getAbsoluteFile() + " -l " + ilang;
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
	
	
	/**
	 * Method to send a shell script execution command casing to the server
	 * @param inputStr string to translate
	 * @param ilang id of the language, ie language of the inputStr
	 * @param case boolean value to indicate lowercasing (true) or recasing (false)
	 * 
	 * @return the output of the shell command, usually the lowercased or recased sentence in language
	 */
	public String executeCommandCase(String inputStr, String ilang, boolean lcase){

    	
        
        File f = new File(pwd, "tempFile");  // temporary file created in pwd
        String command;

        
        // storing the inputStr in a file
        try {
            PrintWriter outf = new PrintWriter(new FileWriter(f));
            outf.println(inputStr);
            outf.close();
        }
        catch (IOException e) {}

        // The actual command to call the shell script with the 2 arguments: file lang
        if(lcase) { // the output will be the lowercased
        	command = "sh " + pwd + "run_lowercase.sh -i " + f.getAbsoluteFile() + " -l " + ilang;
        }
        else {  // the output will be recased
        	command = "sh " + pwd + "run_recase.sh -i " + f.getAbsoluteFile() + " -l " + ilang;
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
	
	
	/**
	 * Method to send a shell script execution command splitting document to the server
	 * @param inputStr string to translate
	 * @param ilang id of the language, ie language of the inputStr
	 * 
	 * @return the output of the shell command, usually the docment slit into one sentence per line
	 */
	public String executeCommandSplit(String inputStr, String ilang){

    	
        
        File f = new File(pwd, "tempFile");  // temporary file created in pwd
        String command;

        
        // storing the inputStr in a file
        try {
            PrintWriter outf = new PrintWriter(new FileWriter(f));
            outf.println(inputStr);
            outf.close();
        }
        catch (IOException e) {}

        // The actual command to call the shell script with the 2 arguments: file lang
       
        command = "sh " + pwd + "run_sentsplit.sh -i " + f.getAbsoluteFile() + " -l " + ilang;
        
        

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
