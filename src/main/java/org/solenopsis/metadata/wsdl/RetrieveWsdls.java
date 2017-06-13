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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
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
import org.flossware.jcore.utils.StringUtils;
import org.solenopsis.keraiai.wsdl.metadata.AsyncResult;
import org.solenopsis.keraiai.wsdl.metadata.MetadataPortType;
import org.solenopsis.keraiai.wsdl.metadata.Package;
import org.solenopsis.keraiai.wsdl.metadata.PackageTypeMembers;
import org.solenopsis.keraiai.wsdl.metadata.RetrieveRequest;
import org.solenopsis.keraiai.wsdl.metadata.RetrieveResult;
import org.solenopsis.metadata.WildcardEnum;
import org.solenopsis.metadata.WsdlSubUrlEnum;

/**
 * Can retrieve the "stock" API WSDLs (apex, enterprise, metadata, partner and tooling).
 *
 * @author Scot P. Floess
 */
public class RetrieveWsdls {
    static RetrieveRequest createRetrieveRequest(final String apiVersion) throws Exception {
        final RetrieveRequest retrieveRequest = new RetrieveRequest();
        retrieveRequest.setApiVersion(Double.parseDouble(apiVersion));

        final PackageTypeMembers types = new PackageTypeMembers();
        types.setName(WildcardEnum.APEX_CLASS.getMetadataType());
        types.getMembers().add("*");

        final Package pack = new Package();
        pack.setVersion(apiVersion);
        pack.getTypes().add(types);

        retrieveRequest.setUnpackaged(pack);

        return retrieveRequest;
    }

    static RetrieveResult ensureRetrieveSuccess(final RetrieveResult result) {
        if (result.isSuccess()) {
            return result;
        }

        throw new RuntimeException("Trouble retrieving apex classes:  " + result.getErrorMessage() + " -> " + result.getErrorStatusCode());
    }

    static byte[] retrieveApexClasses(final MetadataPortType metadataPort, final String apiVersion) throws Exception {
        final AsyncResult asyncResult = metadataPort.retrieve(createRetrieveRequest(apiVersion));

        RetrieveResult result = null;

        do {
            Thread.sleep(3000);
            result = metadataPort.checkRetrieveStatus(asyncResult.getId(), true);

            System.out.println("    Polling...  ");
        } while (!result.isDone());

        return ensureRetrieveSuccess(result).getZipFile();
    }

    static List<String> findCustomWsdls(final Context context) throws Exception {
        System.out.println("Retrieving Custom WSDLs...");

        final List<String> retVal = new ArrayList<>();

        final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(retrieveApexClasses(context.port, context.credentials.getApiVersion())));

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

    static void retrieveWsdl(final Context context, final HttpGet httpGet, final String wsdlFileName) throws Exception {
        final BasicClientCookie cookie = new BasicClientCookie("sid", context.loginContext.getSessionId());
        cookie.setDomain(".salesforce.com");
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");

        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);

        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        final String outputFile = StringUtils.concatWithSeparator(false, System.getProperty("file.separator"), context.outputDir, context.prefix + wsdlFileName);

        System.out.println("Retreiving [" + outputFile + "]");

        CloseableHttpResponse loginResponse = HttpClients.createDefault().execute(httpGet, localContext);

        final FileWriter writer = new FileWriter(outputFile);

        IOUtils.copy(loginResponse.getEntity().getContent(), writer);

        writer.close();

        loginResponse.close();

    }

    static void retrieveWsdls(final Context context, final List<String> customWsdls) throws Exception {
        retrieveWsdl(context, new HttpGet(WsdlSubUrlEnum.APEX.computeUrl(context.loginContext.getBaseServerUrl())), "apex.wsdl");
        retrieveWsdl(context, new HttpGet(WsdlSubUrlEnum.ENTERPRISE.computeUrl(context.loginContext.getBaseServerUrl())), "enterprise.wsdl");
        retrieveWsdl(context, new HttpGet(WsdlSubUrlEnum.METADATA.computeUrl(context.loginContext.getBaseServerUrl())), "metadata.wsdl");
        retrieveWsdl(context, new HttpGet(WsdlSubUrlEnum.PARNTER.computeUrl(context.loginContext.getBaseServerUrl())), "partner.wsdl");
        retrieveWsdl(context, new HttpGet(WsdlSubUrlEnum.TOOLING.computeUrl(context.loginContext.getBaseServerUrl())), "tooling.wsdl");

        for (final String wsdl : customWsdls) {
            retrieveWsdl(context, new HttpGet(WsdlSubUrlEnum.APEX.computeUrl(context.loginContext.getBaseServerUrl()) + "/" + wsdl), wsdl + ".wsdl");
        }
    }

    public static void main(final String[] args) throws Exception {
        final Context context = new Context(args);

        retrieveWsdls(context, findCustomWsdls(context));
    }
}
