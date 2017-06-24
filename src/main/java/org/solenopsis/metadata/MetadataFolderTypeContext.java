/*
 * Copyright (C) 2017 Scot P. Floess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.solenopsis.metadata;

import org.flossware.jcore.utils.ObjectUtils;
import org.flossware.jcore.utils.StringUtils;
import org.solenopsis.keraiai.wsdl.metadata.DescribeMetadataObject;
import org.solenopsis.keraiai.wsdl.metadata.MetadataPortType;

/**
 * Defines the metadata folder types.
 *
 * @author Scot P. Floess
 */
public class MetadataFolderTypeContext extends AbstractMetadataTypeContext {
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
    static String computeType(final DescribeMetadataObject describeMetadataObject) {
        // From case #08849132 opened w/ SFDC...
        return StringUtils.concat(EMAIL_XML_NAME.endsWith(describeMetadataObject.getXmlName()) ? EMAIL_TYPE_PREFIX : describeMetadataObject.getXmlName(), FOLDER_TYPE_SUFFIX);
    }

    public MetadataFolderTypeContext(final MetadataPortType port, final double apiVersion, final DescribeMetadataObject describeMetadataObject) {
        super(describeMetadataObject);

        ObjectUtils.ensureObject(port, "Must provide a meta data port!");
    }
}
