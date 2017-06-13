# Metadata

This project contains various metadata related applications.

## Retrieving WSDLs

You can automagically download all API and custom WSDLs for your org.

### To Run

We support the following application args:
* --solenopsis [name of env]
* --cred [fully qualified path to a credentials property file]
* --prefix [prefix for each wsdl file]
* --dir [fully qualified path to store downloads WSDLs]

Notes:
* If you do not provide a `--dir` option, your home directory will be the output directory.
* You need to provide either a `--solenopsis` or `--cred` params.

`mvn clean install exec:java -Dexec.mainClass=org.solenopsis.metadata.wsdl.RetrieveWsdls -Dexec.args="[aforementioned parameters]"`