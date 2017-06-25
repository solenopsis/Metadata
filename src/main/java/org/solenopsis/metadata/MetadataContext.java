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

import java.util.ArrayList;
import java.util.List;
import org.flossware.jcore.AbstractStringifiable;
import org.flossware.jcore.utils.ObjectUtils;
import org.solenopsis.keraiai.wsdl.metadata.DescribeMetadataResult;

/**
 * Holds metadata data.
 *
 * @author Scot P. Floess
 */
public class MetadataContext extends AbstractStringifiable {
    private final DescribeMetadataResult describeMetadataResult;

    private final List<MetadataTypeContext> metadataTypeContextList;

    private final List<MetadataFolderTypeContext> metadataFolderTypeContextList;

    public MetadataContext(final DescribeMetadataResult describeMetadataResult) {
        this.describeMetadataResult = ObjectUtils.ensureObject(describeMetadataResult, "Must provide a DescribeMetadataResult!");
        this.metadataTypeContextList = new ArrayList<>();
        this.metadataFolderTypeContextList = new ArrayList<>();
    }

    public DescribeMetadataResult getDescribeMetadataResult() {
        return describeMetadataResult;
    }

    public List<MetadataTypeContext> getMetadataTypeContextList() {
        return metadataTypeContextList;
    }

    public List<MetadataFolderTypeContext> getMetadataFolderTypeContextList() {
        return metadataFolderTypeContextList;
    }

    @Override
    public StringBuilder toStringBuilder(StringBuilder stringBuilder, String prefix) {
        final String newPrefix = prefix + "    ";

        appendLine(stringBuilder, newPrefix, "Metadata Types:");

        for (final MetadataTypeContext metadataTypeContext : getMetadataTypeContextList()) {
            metadataTypeContext.toStringBuilder(stringBuilder, newPrefix);
        }

        appendLine(stringBuilder, newPrefix, "\nMetadata Folder Types:");

        for (final MetadataFolderTypeContext metadataFolderTypeContext : getMetadataFolderTypeContextList()) {
            metadataFolderTypeContext.toStringBuilder(stringBuilder, newPrefix);
        }

        return stringBuilder;
    }
}
