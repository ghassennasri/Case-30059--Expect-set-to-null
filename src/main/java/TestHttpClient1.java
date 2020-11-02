import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.*;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class TestHttpClient1 {
    private static final Logger logger = LoggerFactory.getLogger(TestHttpClient1.class);
    private static final String CONTENT_TYPE_MULTIPART = "multipart/mixed; boundary=19dj0d239d23d";
    private static final String BOUNDARY = "19dj0d239d23d";
    public static void main(String[] args) throws IOException {
            HttpHost target
                    = new HttpHost("localhost", 8000, "http");
            CredentialsProvider credsProvider = new BasicCredentialsProvider();

            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials("admin", "admin");
            credsProvider.setCredentials(
                    new AuthScope(target.getHostName(), target.getPort()),
                    credentials);
            CloseableHttpClient httpclient
                    = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .build();

            try {

                CloseableHttpResponse response;
                HttpPost httpPost = new HttpPost("/v1/documents"); // real request
                File file = new File("orders.xml");
                StringBuilder buffer = new StringBuilder();
                buffer.append("attachment; name=\"");
                buffer.append("gnaTest.xml");
                buffer.append("\"");
                buffer.append("; filename=\"gnaTest1.xml\"");
                String contentDisposition = buffer.toString();
                final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
                FormBodyPart filePart = FormBodyPartBuilder.create("file",fileBody)
                        .setField(MIME.CONTENT_DISPOSITION, contentDisposition).build();
                builder
                        .setMode(HttpMultipartMode.STRICT)
                        .seContentType(ContentType.APPLICATION_OCTET_STREAM)
                        .setBoundary(BOUNDARY)
                ;

                builder.addPart(filePart);
                HttpEntity entity = builder.build();

                httpPost.setEntity(entity);
                httpPost.addHeader("Content-Type", CONTENT_TYPE_MULTIPART);
                httpPost.setHeader("Accept", "application/xml");
                //set Expect header to null
                httpPost.setHeader("Expect", null);

                response = httpclient.execute(target, httpPost);

                System.out.println(response);

            } catch ( Exception e) {
                logger.error(e.getMessage(),e);
            } finally {
                httpclient.close();
            }
        }
    }





