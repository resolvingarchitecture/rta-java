package ra.rta.sources.http;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 */
public final class HTTPProducerTester extends TestCase implements ResponseHandler<String> {

	private int status = 0;

	public void testProducer() throws Exception {
		String response = null;
		CloseableHttpClient httpclient = null;
		boolean connectFailed = true;
		int retries = 1;
		httpclient = HttpClients.createDefault();
		do {
			for (int i = 0; i < 100; i++) {
				try {
					HttpPost httpPost = new HttpPost("http://localhost:8080/http-producer/card_transaction.process");
					String body = "{test}";
					httpPost.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
					response = httpclient.execute(httpPost, this);
					System.out.print("Response: " + new String(response.getBytes()));
					connectFailed = false;
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					if (status == 500 && retries > 0) {
						retries--;
					} else {
						throw e;
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} while (connectFailed && retries > 0);
	}

	@Override
	public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
		status = httpResponse.getStatusLine().getStatusCode();
		if (status >= 200 && status < 300) {
			HttpEntity entity = httpResponse.getEntity();
			return entity != null ? EntityUtils.toString(entity) : null;
		}
		throw new ClientProtocolException("Unexpected response status: " + status);
	}

	public int getStatus() {
		return status;
	}
}
