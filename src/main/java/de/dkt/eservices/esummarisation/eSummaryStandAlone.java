/**
 * Copyright (C) 2015 3pc, Art+Com, Condat, Deutsches Forschungszentrum
 * für Künstliche Intelligenz, Kreuzwerke (http://digitale-kuratierung.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dkt.eservices.esummarisation;

import com.hp.hpl.jena.rdf.model.*;
import eu.freme.common.conversion.etranslate.TranslationConversionService;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.FREMEHttpException;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterFactory;
import eu.freme.common.rest.NIFParameterSet;
import eu.freme.common.rest.RestHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;

import java.util.ArrayList;
import java.util.Map;



/**
 * Created by Ankit Srivastava on 30/09/16.
 */

@RestController
public class eSummaryStandAlone extends BaseRestController {
    Logger logger = Logger.getLogger(eSummaryStandAlone.class);

    @Autowired
    RestHelper restHelper;

    @Autowired
    RDFConversionService rdfConversionService;

    @Autowired
    NIFParameterFactory nifParameterFactory;
    
    

    // endpoint for single document summarisation
    @RequestMapping(value = "/e-summarisation", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> summarise1doc(
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam(value = "input", required = false) String input,
            @RequestParam(value = "i", required = false) String i,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "algorithm", required = false) String algorithm,
            @RequestParam(value = "length", required = false) int sumlength,
            @RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
            @RequestBody (required = false) String postBody,
            @RequestParam Map<String, String> allParams) {

    	 if (input == null) {
 			input = i;
 		}
 		if (informat == null) {
 			informat = f;
 		}
 		if (outformat == null) {
 			outformat = o;
 		}
 		if (prefix == null) {
 			prefix = p;
 		}
         if (prefix == null || prefix.equalsIgnoreCase("")){
 			prefix = DKTNIF.getDefaultPrefix();
 		}
    	
        NIFParameterSet nifParameters = restHelper.normalizeNif(postBody,
                acceptHeader, contentTypeHeader, allParams, false);

        Model model = null;

        try {
            if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
                model = ModelFactory.createDefaultModel();
                rdfConversionService.plaintextToRDF(model, nifParameters.getInput(), null, prefix);
            } else {
                model = rdfConversionService.unserializeRDF(postBody, nifParameters.getInformat());
            }

            Statement firstPlaintext = rdfConversionService.extractFirstPlaintext(model);
            Resource subject = firstPlaintext.getSubject();
            String inputString = null;
            if(contentTypeHeader.equalsIgnoreCase("text/plain")){
            	inputString = new String(postBody);
            }
            else {
				inputString = firstPlaintext.getObject().asLiteral().getString();
			}

            // get shell script (with inputString, language, algorithm, summarylength) and write result to resultString
            // eventually replace with a RESTful service endpoint
            //System.out.println("I am in summarisation 1");
            eSummaryService sumService = new eSummaryService();
            String resultString = sumService.executeCommandSummarise(inputString, language, algorithm, sumlength, true); //boolean indicates is single document
          


            if (!model.getNsPrefixMap().containsValue(RDFConstants.itsrdfPrefix)) {
                model.setNsPrefix("itsrdf", RDFConstants.itsrdfPrefix);
            }

            Literal literal = model.createLiteral(resultString, language);
            //System.out.println("@@Status: " + literal.getString());
            subject.addLiteral(model.getProperty(RDFConstants.itsrdfPrefix + "summary"), literal);
            //System.out.println("I am in summarisation 2");

            return restHelper.createSuccessResponse(model, nifParameters.getOutformat());
        }catch (FREMEHttpException e){
            logger.error("Error", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error", e);
            throw new BadRequestException(e.getMessage());
        }
    }
    
    // endpoint for multidocument summarisation
    @RequestMapping(value = "/e-summarisation/multidoc", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> summarisemultidoc(
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam(value = "input", required = false) String input,
            @RequestParam(value = "i", required = false) String i,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "algorithm", required = false) String algorithm,
            @RequestParam(value = "length", required = false) int sumlength,
            @RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
            @RequestBody (required = false) String postBody,
            @RequestParam Map<String, String> allParams) {

    	 if (input == null) {
 			input = i;
 		}
 		if (informat == null) {
 			informat = f;
 		}
 		if (outformat == null) {
 			outformat = o;
 		}
 		if (prefix == null) {
 			prefix = p;
 		}
         if (prefix == null || prefix.equalsIgnoreCase("")){
 			prefix = DKTNIF.getDefaultPrefix();
 		}
    	
        NIFParameterSet nifParameters = restHelper.normalizeNif(postBody,
                acceptHeader, contentTypeHeader, allParams, false);

        Model model = null;

        try {
            if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
                model = ModelFactory.createDefaultModel();
                rdfConversionService.plaintextToRDF(model, nifParameters.getInput(), null, prefix);
            } else {
                model = rdfConversionService.unserializeRDF(postBody, nifParameters.getInformat());
            }

            Statement firstPlaintext = rdfConversionService.extractFirstPlaintext(model);
            Resource subject = firstPlaintext.getSubject();
            String inputString = null;
            if(contentTypeHeader.equalsIgnoreCase("text/plain")){
            	inputString = new String(postBody);
            }
            else {
				inputString = firstPlaintext.getObject().asLiteral().getString();
			}

            // get shell script (with inputString, language, algorithm, summarylength) and write result to resultString
            // eventually replace with a RESTful service endpoint
            //System.out.println("I am in summarisation 1");
            eSummaryService sumService = new eSummaryService();
            String resultString = sumService.executeCommandSummarise(inputString, language, algorithm, sumlength, false); //boolean indicates not single document
          


            if (!model.getNsPrefixMap().containsValue(RDFConstants.itsrdfPrefix)) {
                model.setNsPrefix("itsrdf", RDFConstants.itsrdfPrefix);
            }

            Literal literal = model.createLiteral(resultString, language);
            //System.out.println("@@Status: " + literal.getString());
            subject.addLiteral(model.getProperty(RDFConstants.itsrdfPrefix + "summary"), literal);
            //System.out.println("I am in summarisation 2");

            return restHelper.createSuccessResponse(model, nifParameters.getOutformat());
        }catch (FREMEHttpException e){
            logger.error("Error", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error", e);
            throw new BadRequestException(e.getMessage());
        }
    }
    
    // endpoint for abstractive summarisation: prototype
    @RequestMapping(value = "/e-summarisation/abstract", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> summariseabs(
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam(value = "input", required = false) String input,
            @RequestParam(value = "i", required = false) String i,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
            @RequestBody (required = false) String postBody,
            @RequestParam Map<String, String> allParams) {

    	 if (input == null) {
 			input = i;
 		}
 		if (informat == null) {
 			informat = f;
 		}
 		if (outformat == null) {
 			outformat = o;
 		}
 		if (prefix == null) {
 			prefix = p;
 		}
         if (prefix == null || prefix.equalsIgnoreCase("")){
 			prefix = DKTNIF.getDefaultPrefix();
 		}
    	
        NIFParameterSet nifParameters = restHelper.normalizeNif(postBody,
                acceptHeader, contentTypeHeader, allParams, false);

        Model model = null;

        try {
            if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
                model = ModelFactory.createDefaultModel();
                rdfConversionService.plaintextToRDF(model, nifParameters.getInput(), null, prefix);
            } else {
                model = rdfConversionService.unserializeRDF(postBody, nifParameters.getInformat());
            }

            Statement firstPlaintext = rdfConversionService.extractFirstPlaintext(model);
            Resource subject = firstPlaintext.getSubject();
            String inputString = null;
            if(contentTypeHeader.equalsIgnoreCase("text/plain")){
            	inputString = new String(postBody);
            }
            else {
				inputString = firstPlaintext.getObject().asLiteral().getString();
			}

            // get shell script (with inputString, language, algorithm, summarylength) and write result to resultString
            // eventually replace with a RESTful service endpoint
            //System.out.println("I am in summarisation 1");
            eSummaryService sumService = new eSummaryService();
            String resultString = sumService.executeCommandAbstract(inputString, language); //abstract summarisation
          


            if (!model.getNsPrefixMap().containsValue(RDFConstants.itsrdfPrefix)) {
                model.setNsPrefix("itsrdf", RDFConstants.itsrdfPrefix);
            }

            Literal literal = model.createLiteral(resultString, language);
            //System.out.println("@@Status: " + literal.getString());
            subject.addLiteral(model.getProperty(RDFConstants.itsrdfPrefix + "summary"), literal);
            //System.out.println("I am in summarisation 2");

            return restHelper.createSuccessResponse(model, nifParameters.getOutformat());
        }catch (FREMEHttpException e){
            logger.error("Error", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error", e);
            throw new BadRequestException(e.getMessage());
        }
    }
}
