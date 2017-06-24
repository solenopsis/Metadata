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
import org.flossware.jcore.AbstractCommonBase;
import org.flossware.jcore.utils.ObjectUtils;
import org.solenopsis.keraiai.wsdl.metadata.DescribeMetadataResult;
import org.solenopsis.keraiai.wsdl.metadata.MetadataPortType;

/**
 * Holds metadata data.
 *
 * @author Scot P. Floess
 */
public class MetadataContext extends AbstractCommonBase {
    private final DescribeMetadataResult describeMetadataResult;

    private final List<MetadataTypeContext> metadataTypeContextList;

    private final List<MetadataFolderTypeContext> metadataFolderTypeContextList;

    public MetadataContext(final MetadataPortType port, final double apiVersion) {
        ObjectUtils.ensureObject(port, "Must provide a metadata port!");

        this.describeMetadataResult = port.describeMetadata(apiVersion);

        this.metadataTypeContextList = new ArrayList<>(describeMetadataResult.getMetadataObjects().size() - 4);
        this.metadataFolderTypeContextList = new ArrayList<>(4);

        describeMetadataResult.getMetadataObjects().forEach((describeMetadataObject) -> {
            if (describeMetadataObject.isInFolder()) {
                metadataTypeContextList.add(new MetadataTypeContext(port, apiVersion, describeMetadataObject));
            } else {
                metadataFolderTypeContextList.add(new MetadataFolderTypeContext(port, apiVersion, describeMetadataObject));
            }
        });
    }

    public MetadataContext(final MetadataPortType port, final String apiVersion) {
        this(port, Double.parseDouble(apiVersion));
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
}
