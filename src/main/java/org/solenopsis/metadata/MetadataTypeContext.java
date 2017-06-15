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
import org.solenopsis.keraiai.wsdl.metadata.DescribeMetadataObject;

/**
 * Defines the metadata types.
 *
 * @author Scot P. Floess
 */
public class MetadataTypeContext {
    private final DescribeMetadataObject describeMetadataObject;

    public MetadataTypeContext(final DescribeMetadataObject describeMetadataObject) {
        this.describeMetadataObject = ObjectUtils.ensureObject(describeMetadataObject, "Must provide a DescribeMetadataObject!");
    }

    public DescribeMetadataObject getDescribeMetadataObject() {
        return describeMetadataObject;
    }
}
