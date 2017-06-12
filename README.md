# Metadata

## To Run

The current release uses the Solenopsis credentials files and supports the "name" of the environment:

`mvn clean install exec:java -Dexec.mainClass=org.solenopsis.metadata.RetrieveWsdls -Dexec.args="[Solenopsis Env]"`

The credentials for Solenopsis are stored in `${HOME}/.solenopsis/credentials/[env].properties`.

Assuming you have a `foo` environment:
`mvn clean install exec:java -Dexec.mainClass=org.solenopsis.metadata.RetrieveWsdls -Dexec.args="foo"`
