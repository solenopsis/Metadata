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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.solenopsis.keraiai.Credentials;
import org.solenopsis.keraiai.LoginContext;
import org.solenopsis.keraiai.credentials.PropertiesCredentials;
import org.solenopsis.keraiai.soap.port.ApiWebServiceEnum;
import org.solenopsis.keraiai.soap.port.WebServiceTypeEnum;
import org.solenopsis.keraiai.wsdl.metadata.AsyncResult;
import org.solenopsis.keraiai.wsdl.metadata.MetadataPortType;
import org.solenopsis.keraiai.wsdl.metadata.PackageTypeMembers;
import org.solenopsis.keraiai.wsdl.metadata.RetrieveRequest;
import org.solenopsis.keraiai.wsdl.metadata.RetrieveResult;

/**
 * Can retrieve the "stock" API WSDLs (apex, enterprise, metadata, partner and tooling).
 *
 * @author Scot P. Floess
 */
public class RetrieveWsdls {
    /**
     * Return our credentials.
     */
    static Credentials getCredentials(final String env) throws Exception {
        final String credentials = System.getProperty("user.home") + "/.solenopsis/credentials/" + env + ".properties";
        System.out.println("Using credentials [" + credentials + "]");
        try (final FileInputStream fis = new FileInputStream(credentials)) {
            final Properties properties = new Properties();
            properties.load(fis);
            return new PropertiesCredentials(properties);
        }
    }

    static List<String> findCustomWsdls(final MetadataPortType metadataPort) throws Exception {
        System.out.println("Retrieving Custom WSDLs...");

        final List<String> retVal = new ArrayList<>();

        final RetrieveRequest retrieveRequest = new RetrieveRequest();
        retrieveRequest.setApiVersion(39.0);

        final PackageTypeMembers types = new PackageTypeMembers();
        types.setName(WildcardEnum.APEX_CLASS.getMetadataType());
        types.getMembers().add("*");

        final org.solenopsis.keraiai.wsdl.metadata.Package pack = new org.solenopsis.keraiai.wsdl.metadata.Package();
        pack.setVersion("39.0");
        pack.getTypes().add(types);

        retrieveRequest.setUnpackaged(pack);

        final AsyncResult asyncResult = metadataPort.retrieve(retrieveRequest);

        RetrieveResult result = null;

        do {
            Thread.sleep(2000);
            result = metadataPort.checkRetrieveStatus(asyncResult.getId(), true);

            System.out.println("    Polling...  ");
        } while (!result.isDone());

        final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(result.getZipFile()));

        ZipEntry zipEntry = null;

        do {
            zipEntry = zis.getNextEntry();

            final byte[] rawData = new byte[1024000];
            zis.read(rawData);

            final String str = new String(rawData);

            if (str.contains("WebService")) {
                retVal.add(new File(zipEntry.getName()).getName().split("\\.")[0]);
            }

        } while (null != zipEntry);

        return retVal;
    }

    static void retrieveWsdl(final LoginContext loginContext, final HttpGet httpGet, final String wsdlFileName) throws Exception {
        final BasicClientCookie cookie = new BasicClientCookie("sid", loginContext.getSessionId());
        cookie.setDomain(".salesforce.com");
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");

        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);

        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        System.out.println("Retreiving [" + wsdlFileName + "]");

        CloseableHttpResponse loginResponse = HttpClients.createDefault().execute(httpGet, localContext);

        final FileWriter writer = new FileWriter(wsdlFileName);

        IOUtils.copy(loginResponse.getEntity().getContent(), writer);

        writer.close();

        loginResponse.close();

    }

    static void retrieveWsdls(final LoginContext loginContext, final List<String> customWsdls) throws Exception {
        retrieveWsdl(loginContext, new HttpGet(loginContext.getBaseServerUrl() + "/services/wsdl/apex"), "apex.wsdl");
        retrieveWsdl(loginContext, new HttpGet(loginContext.getBaseServerUrl() + "/soap/wsdl.jsp?type=*"), "enterprise.wsdl");
        retrieveWsdl(loginContext, new HttpGet(loginContext.getBaseServerUrl() + "/services/wsdl/metadata"), "metadata.wsdl");
        retrieveWsdl(loginContext, new HttpGet(loginContext.getBaseServerUrl() + "/soap/wsdl.jsp"), "partner.wsdl");
        retrieveWsdl(loginContext, new HttpGet(loginContext.getBaseServerUrl() + "/services/wsdl/tooling"), "tooling.wsdl");

        for (final String wsdl : customWsdls) {
            retrieveWsdl(loginContext, new HttpGet(loginContext.getBaseServerUrl() + "/services/wsdl/class/" + wsdl), wsdl + ".wsdl");
        }
    }

    public static void main(final String[] args) throws Exception {
        final MetadataPortType port = (MetadataPortType) WebServiceTypeEnum.METADATA_SERVICE_TYPE.createProxyPort(getCredentials(args[0]), ApiWebServiceEnum.METADATA_SERVICE.getService(), ApiWebServiceEnum.METADATA_SERVICE.getPortType());
        final LoginContext loginContext = (LoginContext) port;

        retrieveWsdls(loginContext, findCustomWsdls(port));
//
//        System.out.println(findCustomWsdls(port));
//
//
//
////        HttpGet request = new HttpGet(loginContext.getBaseServerUrl() + "/soap/wsdl.jsp?type=*");
////        HttpGet request = new HttpGet(loginContext.getBaseServerUrl() + "/services/wsdl/metadata");
////        HttpGet request = new HttpGet(loginContext.getBaseServerUrl() + "/services/wsdl/tooling");
////        HttpGet request = new HttpGet(loginContext.getBaseServerUrl() + "/services/wsdl/apex");
//        HttpGet request = new HttpGet(loginContext.getBaseServerUrl() + "/soap/wsdl.jsp");
//
////        HttpGet request = new HttpGet(loginContext.getBaseServerUrl() + "/services/wsdl/class/CaseAPI");
//
//
//
//
//        System.out.println(stringWriter.toString());
    }
}
