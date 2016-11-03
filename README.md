# e-Summarisation

The e-Summarisation module performs single document summarisation in multiple languages using DKT-Project-specific wrapper scripts built on top of a Python-based summarisation tool Given a text and language name, a file can be summarised and displayed in NIF format.

Note, the current implementation summarises in the following languages: 
* English
* German
* Spanish 

The system is constantly being updated and newer functionalities will be added. 

## Endpoint

http://api.digitale-kuratierung.de/api/e-summarisation

### Input
The API conforms to the general NIF API specifications. For more details, see:
http://persistence.uni-leipzig.org/nlp2rdf/specification/api.html
In addition to the input and Content-type (header) parameters, the following parameters have to be set to perform Summarisation on the input:  

`language`: The language of the input text. 
  
`input`: The text to be summarised. 

For now, only the following language pairs are supported. English (`en`), German (`de`), and Spanish (`es`) 


### Output
A document in NIF format.

Example cURL post for e-smt:
`curl -X POST --header "Content-type:text/plain" -d 'Text to summarize.' "http://api.digitale-kuratierung.de/api/e-summarisation?language=en"`


## Notes on DKT Summarisation Software
This end-point (hosted on the dkt-api server) relies on external software and assumes that summarize.py engine is installed at: /usr/local/summarisation, and the handler script (run_summarize.sh) is placed at: /usr/local/summarisation


Please contact Ankit Srivastava (firstName.lastName@dfki.de) for more information.
