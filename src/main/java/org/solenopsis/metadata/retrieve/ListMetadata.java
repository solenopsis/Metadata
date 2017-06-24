package org.solenopsis.metadata.retrieve;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.solenopsis.keraiai.Credentials;
import org.solenopsis.keraiai.credentials.PropertiesCredentials;
import org.solenopsis.keraiai.soap.port.ApiWebServiceEnum;
import org.solenopsis.keraiai.wsdl.metadata.DescribeMetadataObject;
import org.solenopsis.keraiai.wsdl.metadata.DescribeMetadataResult;
import org.solenopsis.keraiai.wsdl.metadata.FileProperties;
import org.solenopsis.keraiai.wsdl.metadata.ListMetadataQuery;
import org.solenopsis.keraiai.wsdl.metadata.MetadataPortType;

/**
 * @author Scot P. Floess
 */
public class ListMetadata {
    private static final Logger logger = Logger.getLogger(ListMetadata.class.getName());

    /**
     * Return our logger.
     */
    private static Logger getLogger() {
        return logger;
    }
    /**
     * The credentials params
     */
    public static final String CREDENTIALS_PARAM = "--credentials";

    public static void emitMetadata(final String msg, final MetadataPortType metadataPort, final String apiVersion, final StringBuilder sb) throws Exception {
        System.out.println(msg);

        final double version = Double.parseDouble(apiVersion);

        System.out.println("Describing metadata...");

        final DescribeMetadataResult describeMetadata = metadataPort.describeMetadata(version);

        System.out.println("Metadata retrieved...");

        final List<DescribeMetadataObject> metadataObjects = describeMetadata.getMetadataObjects();

        int index = 0;

        for (final DescribeMetadataObject dmo : metadataObjects) {
            index++;

            sb.append("==============================================\n");

            sb.append("Dir:       ").append(dmo.getDirectoryName()).append('\n');
            sb.append("Suffix:    ").append(dmo.getSuffix()).append('\n');
            sb.append("XML:       ").append(dmo.getXmlName()).append('\n');
            sb.append("In folder: ").append(dmo.isInFolder()).append('\n');
            sb.append("Meta file: ").append(dmo.isMetaFile()).append('\n');

            if (dmo.isInFolder()) {
                final ListMetadataQuery query = new ListMetadataQuery();

                // From case #08849132 opened w/ SFDC...
                if ("EmailTemplate".equals(dmo.getXmlName())) {
                    query.setType("EmailFolder");
                } else {
                    query.setType(dmo.getXmlName() + "Folder");
                }

                query.setFolder(dmo.getDirectoryName());

                final List<ListMetadataQuery> metaDataQuertyList = new ArrayList<>();

                metaDataQuertyList.add(query);

                try {
                    final List<FileProperties> filePropertiesList = metadataPort.listMetadata(metaDataQuertyList, version);
                    sb.append("*** Children (").append(filePropertiesList.size()).append("):").append('\n');
                    for (final FileProperties fileProperties : filePropertiesList) {
                        sb.append("    --------------------------\n");
                        sb.append("    Full name: ").append(fileProperties.getFullName()).append('\n');
                        sb.append("    File name: ").append(fileProperties.getFileName()).append('\n');
                        sb.append("    Type:      ").append(fileProperties.getType()).append('\n');

                        final ListMetadataQuery innerQuery = new ListMetadataQuery();
                        innerQuery.setFolder(fileProperties.getFileName().substring(fileProperties.getFileName().lastIndexOf("/") + 1));
                        innerQuery.setType(dmo.getXmlName());

                        final List<ListMetadataQuery> folderQueryList = new ArrayList<ListMetadataQuery>();
                        folderQueryList.add(innerQuery);

                        final List<FileProperties> innerFilePropertiesList = metadataPort.listMetadata(folderQueryList, version);
                        for (final FileProperties innerFileProperties : innerFilePropertiesList) {
                            sb.append("        --------------------------\n");
                            sb.append("        Full name: ").append(innerFileProperties.getFullName()).append('\n');
                            sb.append("        File name: ").append(innerFileProperties.getFileName()).append('\n');
                            sb.append("        Type:      ").append(innerFileProperties.getType()).append('\n');
                        }
                    }
                } catch (final Exception e) {
                    System.out.println(" Problem: " + e.getMessage());
                }
            } else {
                sb.append("Children (").append(dmo.getChildXmlNames().size()).append("):").append('\n');
                for (final String child : dmo.getChildXmlNames()) {
                    sb.append(" ").append(child).append('\n');

                    if (null != child && !"".equals(child)) {
                        final ListMetadataQuery query = new ListMetadataQuery();
                        query.setType(child);

                        final List<ListMetadataQuery> metaDataQuertyList = new ArrayList<ListMetadataQuery>();

                        metaDataQuertyList.add(query);

                        try {
                            final List<FileProperties> filePropertiesList = metadataPort.listMetadata(metaDataQuertyList, version);
                            for (final FileProperties fileProperties : filePropertiesList) {
                                sb.append("    --------------------------\n");
                                sb.append("    Full name: ").append(fileProperties.getFullName()).append('\n');
                                sb.append("    File name: ").append(fileProperties.getFileName()).append('\n');
                                sb.append("    Type:      ").append(fileProperties.getType()).append('\n');

                            }
                        } catch (final Exception e) {
                            System.out.println(" Problem: " + e.getMessage());
                        }
                    }
                }
            }

            sb.append('\n');
        }
    }

    /**
     * Return our credentials.
     */
    public static Credentials getCredentials() throws Exception {
        final Properties properties = new Properties();

        properties.load(new FileInputStream("/home/sfloess/.solenopsis/credentials/dev.properties"));

        return new PropertiesCredentials(properties);
    }

    public static void emitMetadataChildren(final List<String> childXmlNames) {
        System.out.println("    Total Children [" + childXmlNames.size() + "]");

        for (final String xmlName : childXmlNames) {
            System.out.println("        " + xmlName);
        }
    }

    public static void emitMetadata(final DescribeMetadataObject metadata) {
        System.out.println("\nDirectory:  " + metadata.getDirectoryName());
        System.out.println("    XML Name  [" + metadata.getXmlName() + "]");
        System.out.println("    Suffix    [" + metadata.getSuffix() + "]");
        System.out.println("    Folder    [" + metadata.isInFolder() + "]");
        System.out.println("    Meta file [" + metadata.isMetaFile() + "]");

        emitMetadataChildren(metadata.getChildXmlNames());
    }

    public static void emitMetadata(final List<DescribeMetadataObject> metadataList) {
        System.out.println("\n\nMetadata...\n");
        for (final DescribeMetadataObject metadata : metadataList) {
            emitMetadata(metadata);
        }
    }

    public static void emitMetadata(DescribeMetadataResult metadataResult) {
        emitMetadata(metadataResult.getMetadataObjects());
    }

    /**
     * Emit all our metadata.
     *
     * @param port
     *
     * @throws Exception
     */
    public static void emitMetadata(final MetadataPortType port, final double version) throws Exception {
        System.out.println("Double version [" + version + "] -> [" + port + "]");
        int runNum = 1;

        while (true) {
            emitMetadata(port.describeMetadata(version));

            System.out.println("----------------------------------------------------");
            System.out.println(new Date() + "   -> " + (runNum++));
            System.out.println("----------------------------------------------------");

            synchronized (ListMetadata.class) {
                ListMetadata.class.wait(60000);
            }
        }
    }

    /**
     * Emit all our metadata.
     *
     * @param port
     *
     * @throws Exception
     */
    public static void emitMetadata(final MetadataPortType port, final String version) throws Exception {
        System.out.println("String version [" + version + "]");
        emitMetadata(port, Double.parseDouble(version));
    }

    /**
     * Emit all our metadata.
     *
     * @param port
     *
     * @throws Exception
     */
    public static void emitMetadata(final MetadataPortType port, final Credentials creds) throws Exception {
        emitMetadata(port, creds.getApiVersion());
    }

    /**
     * Return the user.home
     */
    public static void main(final String[] args) {
        try {
            final Credentials creds = getCredentials();
            final MetadataPortType port = ApiWebServiceEnum.METADATA_SERVICE.createProxyPort(creds);

            final StringBuilder sb = new StringBuilder();
            emitMetadata("Grabbing metadata", port, "39.0", sb);

            System.out.println(sb.toString());

//            process(port);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
