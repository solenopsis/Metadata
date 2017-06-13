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
package org.solenopsis.metadata.wsdl;

import org.flossware.jcore.utils.StringUtils;

/**
 * The sub URL to use when gathering WSDLs.
 *
 * @author Scot P. Floess
 */
public enum WsdlSubUrlEnum {
    APEX("/services/wsdl/apex"),
    CUSTOM("/services/wsdl/class/"),
    ENTERPRISE("/soap/wsdl.jsp?type=*"),
    METADATA("/services/wsdl/metadata"),
    PARNTER("/soap/wsdl.jsp"),
    TOOLING("/services/wsdl/tooling");

    private final String subUrl;

    private WsdlSubUrlEnum(final String subUrl) {
        this.subUrl = subUrl;
    }

    public String getSubUrl() {
        return subUrl;
    }

    public String computeUrl(final String baseUrl) {
        return StringUtils.concatWithSeparator(false, "/", baseUrl, getSubUrl());
    }
}
