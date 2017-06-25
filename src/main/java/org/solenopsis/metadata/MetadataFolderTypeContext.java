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

import java.util.List;
import org.flossware.jcore.AbstractStringifiable;
import org.flossware.jcore.utils.ObjectUtils;
import org.solenopsis.keraiai.wsdl.metadata.FileProperties;

/**
 * Defines the metadata folder types.
 *
 * @author Scot P. Floess
 */
public class MetadataFolderTypeContext extends AbstractStringifiable {
    private final FileProperties fileProperties;

    private final List<FileProperties> filePropertiesList;

    public MetadataFolderTypeContext(final FileProperties fileProperties, final List<FileProperties> filePropertiesList) {
        this.fileProperties = ObjectUtils.ensureObject(fileProperties, "Must provide FileProperties!");
        this.filePropertiesList = ObjectUtils.ensureObject(filePropertiesList, "Must provide a list of FileProperties!");
    }

    public FileProperties getFileProperties() {
        return fileProperties;
    }

    public List<FileProperties> getFilePropertiesList() {
        return filePropertiesList;
    }

    @Override
    public StringBuilder toStringBuilder(StringBuilder stringBuilder, String prefix) {
        final String newPrefix = prefix + "    ";

        appendLine(stringBuilder, newPrefix, "--------------------------");
        appendLine(stringBuilder, newPrefix, "Full name: ", getFileProperties().getFullName());
        appendLine(stringBuilder, newPrefix, "File name: ", getFileProperties().getFileName());
        appendLine(stringBuilder, newPrefix, "Type:      ", getFileProperties().getType());

        for (final FileProperties fileProperties : getFilePropertiesList()) {
            appendLine(stringBuilder, newPrefix, "    --------------------------");
            appendLine(stringBuilder, newPrefix, "    Full name: ", fileProperties.getFullName());
            appendLine(stringBuilder, newPrefix, "    File name: ", fileProperties.getFileName());
            appendLine(stringBuilder, newPrefix, "    Type:      ", fileProperties.getType());
        }

        return stringBuilder;
    }
}
