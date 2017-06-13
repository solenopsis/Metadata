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

import java.io.File;
import org.flossware.jcore.utils.StringUtils;
import org.solenopsis.keraiai.Credentials;
import org.solenopsis.keraiai.LoginContext;
import org.solenopsis.keraiai.credentials.FilePropertiesCredentials;
import org.solenopsis.keraiai.soap.port.ApiWebServiceEnum;
import org.solenopsis.keraiai.wsdl.metadata.MetadataPortType;

/**
 * Context for command line options.
 *
 * @author Scot P. Floess
 */
final class Context {
    Credentials credentials;

    String outputDir = System.getProperty("user.home");

    String prefix = "";

    MetadataPortType port;

    LoginContext loginContext;

    void setCredentials(final String fileName) {
        credentials = new FilePropertiesCredentials(fileName);
        port = ApiWebServiceEnum.METADATA_SERVICE.createProxyPort(credentials);
        loginContext = (LoginContext) port;
    }

    void setSolenopsisCredentials(final String env) {
        setCredentials(StringUtils.concatWithSeparator(true, System.getProperty("file.separator"), System.getProperty("user.home"), ".solenopsis", "credentials", env + ".properties"));
    }

    void ensureCredentials() {
        if (null == credentials) {
            System.err.println("\nYou must provide either --solenopsis or --creds parameters!\n");
            System.exit(1);
        }
    }

    Context(final String[] args) {
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            switch (args[argIndex]) {
                case "--solenopsis":
                    setSolenopsisCredentials(args[++argIndex]);
                    break;

                case "--creds":
                    setCredentials(args[++argIndex]);
                    break;

                case "--prefix":
                    prefix = args[++argIndex];
                    break;

                case "--dir":
                    outputDir = args[++argIndex];
                    new File(outputDir).mkdirs();
                    break;
            }
        }

        ensureCredentials();
    }
}
