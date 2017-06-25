package org.solenopsis.metadata.retrieve;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.flossware.jcore.utils.StringUtils;
import org.solenopsis.keraiai.wsdl.metadata.DescribeMetadataObject;
import org.solenopsis.keraiai.wsdl.metadata.DescribeMetadataResult;
import org.solenopsis.keraiai.wsdl.metadata.FileProperties;
import org.solenopsis.keraiai.wsdl.metadata.ListMetadataQuery;
import org.solenopsis.keraiai.wsdl.metadata.MetadataPortType;
import org.solenopsis.metadata.MetadataContext;
import org.solenopsis.metadata.MetadataFolderTypeContext;
import org.solenopsis.metadata.MetadataTypeContext;

/**
 * Can retrieve metadata in it's entirety or condensed (meaning
 *
 * @author Scot P. Floess
 */
public final class RetrieveMetadata {
    /**
     * Our logger.
     */
    private static final Logger logger = Logger.getLogger(RetrieveMetadata.class.getName());

    /**
     * Default constructor not allowed.
     */
    private RetrieveMetadata() {
    }

    /**
     * Return our logger.
     */
    private static Logger getLogger() {
        return logger;
    }
    public static final String EMAIL_XML_NAME = "EmailTemplate";

    public static final String EMAIL_TYPE_PREFIX = "Email";

    public static final String FOLDER_TYPE_SUFFIX = "Folder";

    /**
     * When dealing with a metadata type for folders, if we have the XML name being EmailTemplate, the type will be EmailFolder.
     * Otherwise the type is the metadata XML name plus Folder. This can be found in case #08849132 opened w/ SFDC...
     *
     * @param describeMetadataObject contains the metadata for which we will examine the xml name to compute the type.
     *
     * @return the type - either EmailFolder or the xml name plus Folder.
     */
    static String computeFolderType(final DescribeMetadataObject describeMetadataObject) {
        // From case #08849132 opened w/ SFDC...
        return StringUtils.concat(EMAIL_XML_NAME.endsWith(describeMetadataObject.getXmlName()) ? EMAIL_TYPE_PREFIX : describeMetadataObject.getXmlName(), FOLDER_TYPE_SUFFIX);
    }

    static String computeFolder(final FileProperties fileProperties) {
        return fileProperties.getFileName().substring(fileProperties.getFileName().lastIndexOf("/") + 1);
    }

    static void retrieveMetadataFolderType(final MetadataPortType port, final Double apiVersion, final DescribeMetadataObject describeMetadataObject, final FileProperties fileProperties, final MetadataContext metadataContext) throws Exception {
        final ListMetadataQuery query = new ListMetadataQuery();
        query.setFolder(computeFolder(fileProperties));
        query.setType(describeMetadataObject.getXmlName());

        final List<ListMetadataQuery> metaDataQuertyList = new ArrayList<>();
        metaDataQuertyList.add(query);

        metadataContext.getMetadataFolderTypeContextList().add(new MetadataFolderTypeContext(fileProperties, port.listMetadata(metaDataQuertyList, apiVersion)));
    }

    static void retrieveMetadataFolderType(final MetadataPortType port, final Double apiVersion, final DescribeMetadataObject describeMetadataObject, final List<FileProperties> filePropertiesList, final MetadataContext metadataContext) throws Exception {
        for (final FileProperties fileProperties : filePropertiesList) {
            retrieveMetadataFolderType(port, apiVersion, describeMetadataObject, fileProperties, metadataContext);
        }
    }

    static void retrieveMetadataFolderType(final MetadataPortType port, final Double apiVersion, final DescribeMetadataObject describeMetadataObject, final MetadataContext metadataContext) throws Exception {
        final ListMetadataQuery query = new ListMetadataQuery();

        query.setType(computeFolderType(describeMetadataObject));
        query.setFolder(describeMetadataObject.getDirectoryName());

        final List<ListMetadataQuery> metaDataQuertyList = new ArrayList<>();

        metaDataQuertyList.add(query);

        retrieveMetadataFolderType(port, apiVersion, describeMetadataObject, port.listMetadata(metaDataQuertyList, apiVersion), metadataContext);
    }

    static void retrieveMetadataType(final MetadataPortType port, final Double apiVersion, final String xmlName, final MetadataContext metadataContext) throws Exception {
        final ListMetadataQuery query = new ListMetadataQuery();

        query.setType(xmlName);

        final List<ListMetadataQuery> metaDataQuertyList = new ArrayList<>();

        metaDataQuertyList.add(query);

        metadataContext.getMetadataTypeContextList().add(new MetadataTypeContext(xmlName, port.listMetadata(metaDataQuertyList, apiVersion)));
    }

    static void retrieveMetadataType(final MetadataPortType port, final Double apiVersion, final DescribeMetadataObject describeMetadataObject, final MetadataContext metadataContext) throws Exception {
        for (final String childXmlName : describeMetadataObject.getChildXmlNames()) {
            retrieveMetadataType(port, apiVersion, childXmlName, metadataContext);
        }
    }

    static MetadataContext retrieveMetadata(final MetadataPortType port, final Double apiVersion, final MetadataContext metadataContext) throws Exception {
        for (final DescribeMetadataObject describeMetadataObject : metadataContext.getDescribeMetadataResult().getMetadataObjects()) {
            if (describeMetadataObject.isInFolder()) {
                retrieveMetadataFolderType(port, apiVersion, describeMetadataObject, metadataContext);
            } else {
                retrieveMetadataType(port, apiVersion, describeMetadataObject, metadataContext);
            }
        }

        return metadataContext;
    }

    static MetadataContext retrieveMetadata(final MetadataPortType port, final Double apiVersion, final DescribeMetadataResult describeMetadataResult) throws Exception {
        return retrieveMetadata(port, apiVersion, new MetadataContext(describeMetadataResult));
    }

    static MetadataContext retrieveMetadata(final MetadataPortType port, final Double apiVersion, final boolean isCondensed) throws Exception {
        return retrieveMetadata(port, apiVersion, port.describeMetadata(apiVersion));
    }

    static MetadataContext retrieveMetadata(final MetadataPortType port, final String apiVersion, final boolean isCondensed) throws Exception {
        return retrieveMetadata(port, Double.parseDouble(apiVersion), isCondensed);
    }

    public static MetadataContext getMetadataComplete(final MetadataPortType port, final String apiVersion) throws Exception {
        return retrieveMetadata(port, apiVersion, false);
    }

    public static MetadataContext getMetadataCondensed(final MetadataPortType port, final String apiVersion) throws Exception {
        return retrieveMetadata(port, apiVersion, false);
    }
}
