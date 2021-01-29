package com.telecom.ecloudframework.sys.rest.ofd.custom.agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import com.telecom.ecloudframework.sys.rest.ofd.custom.agent.callback.CallbackProxy;
import com.telecom.ecloudframework.sys.rest.ofd.custom.agent.callback.EmptyTransferCallback;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Packet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPAgent extends ConvertAgent {

	private static Logger log = LoggerFactory.getLogger(HTTPAgent.class);

	private static Set<String> prepare, doing, success;
	private static long period;

	static {
		// Properties p = new Properties();
		// try {
		// p.load(HTTPAgent.class.getResourceAsStream("/http.properties"));
		// prepare = fill(p.getProperty("prepare", ""));
		// doing = fill(p.getProperty("doing", ""));
		// success = fill(p.getProperty("success", ""));
		// period = Long.valueOf(p.getProperty("period", "1000"));
		//
		// } catch (IOException e) {
		// log.error(e.getMessage(), e);
		// }

		prepare = fill("-5,1000,1001,1002");
		doing = fill("0,1003");
		success = fill("1010");
		period = Long.valueOf("1000");

	}

	private static Set<String> fill(String v) {
		Set<String> set = new HashSet<String>();
		if (v != null) {
			String[] array = v.split(",");
			if (array.length > 0) {
				for (String s : array) {
					set.add(s.trim());
				}
			}
		}
		return set;
	}

	private String baseURL;
	private long delay = period;
	private DefaultHttpClient client;

	private Map<String, String> headers;

	public HTTPAgent(String baseURL, long delay) {
		this(baseURL);
		this.delay = delay;
		log.debug("Query delay {}", this.delay);
	}

	public HTTPAgent(String baseURL, Map<String, String> headers) {
		this(baseURL);
		this.headers = headers;
	}

	public HTTPAgent(String baseURL, long delay, int PoolSize) {
		this(baseURL, delay);
		if (this.executor != null) {
			this.executor.shutdown();
		}
		this.executor = Executors.newFixedThreadPool(PoolSize);
	}

	public HTTPAgent(String baseURL) {
		this.baseURL = baseURL;
		log.debug("Base url {}", baseURL);
		if (!this.baseURL.endsWith("/")) {
			this.baseURL += "/";
		}
		if (this.executor != null) {
			this.executor.shutdown();
		}
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

		this.createHttpClient();

	}

	private void addHeader(HttpRequestBase hr) {
		if (headers != null) {
			for (Map.Entry<String, String> en : headers.entrySet()) {
				hr.addHeader(en.getKey(), en.getValue());
			}
		}
	}

	private String upload(InputStream packet, String token, boolean raise) throws IOException {
		log.debug("Upload...");

		if (packet == null) {
			throw new IOException("Packet is null");
		}

		DefaultHttpClient client = this.createHttpClient();
		try {
			HttpPost post = new HttpPost(baseURL + "upload");
			this.addHeader(post);
			// MultipartEntityBuilder meb = MultipartEntityBuilder.create().addPart("file",
			// new InputStreamBody(new AutoCloseInputStream(packet),
			// ContentType.DEFAULT_BINARY, "packet"));

			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("file",
					new InputStreamBody(new AutoCloseInputStream(packet), "application/octet-stream", "packet"));

			if (token != null) {
				reqEntity.addPart("token", new StringBody(token));
				reqEntity.addPart("raise", new StringBody(String.valueOf(raise)));
			}

			post.setEntity(reqEntity);

			HttpResponse response = client.execute(post);
			try {
				log.debug("Response {}", response.getStatusLine());

				HttpEntity entity = response.getEntity();
				String ret = EntityUtils.toString(entity);

				log.debug("Ticket {}", ret);

				EntityUtils.consume(entity);
				if (ret != null && ret.startsWith("Failed")) {
					throw new IOException("upload failed!".concat(ret));
				}
				return ret;
			} finally {
//				client.getConnectionManager().shutdown();
			}
		} finally {
			// IOUtils.closeQuietly(client);
		}
	}

	private String query(String ticket) throws IOException {
		log.debug("Query {} ...", ticket);

		DefaultHttpClient client = this.createHttpClient();
		try {
			HttpGet get = new HttpGet(baseURL + "query?ticket=" + ticket);
			this.addHeader(get);
			HttpResponse response = client.execute(get);
			try {
				log.debug("Response {}", response.getStatusLine());

				HttpEntity entity = response.getEntity();
				String ret = EntityUtils.toString(entity);

				log.debug("Return {}", ret);

				EntityUtils.consume(entity);

				return ret;
			} finally {
//				client.getConnectionManager().shutdown();
			}
		} finally {
			// IOUtils.closeQuietly(client);
		}
	}

	private void download(String ticket, final OutputStream out) throws IOException {
		if (out == null) {
			log.debug("Out is null, skip download...");
			return;
		}
		log.debug("Download {} ...", ticket);

		DefaultHttpClient client = this.createHttpClient();
		try {
			HttpGet get = new HttpGet(baseURL + "download?ticket=" + ticket);
			this.addHeader(get);
			try {

				HttpResponse httpResponse = client.execute(get);

				InputStream is = httpResponse.getEntity().getContent();
				try {
					int l = IOUtils.copy(is, out);

					log.debug("Receive {}", l);
				} finally {
					IOUtils.closeQuietly(is);
					EntityUtils.consume(httpResponse.getEntity());
				}

			} finally {
				client.getConnectionManager().shutdown();
			}
		} finally {
			// IOUtils.closeQuietly(client);
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public void convert(InputStream packet, final OutputStream result, String token, boolean raise)
			throws ConvertException, IOException {
		final String ticket = this.upload(packet, token, raise);

		String ret = null;
		for (;;) {
			try {
				Thread.sleep(this.delay);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
			ret = this.query(ticket);
			if (ret == null) {
				log.warn("转换因为未知原因失败");
				break;
			}
			ret = ret.trim();
			if (prepare.contains(ret) || doing.contains(ret)) {
				log.debug("Continue...");
				continue;
			}
			break;
		}
		if (ret != null && (success.contains(ret) || ret.startsWith("http"))) {
			this.download(ticket, result);
		} else {
			throw new ConvertException("Failed " + ret);
		}
	}

	@Override
	public void convertNoWait(InputStream packet, ConvertCallback callback, String token, boolean raise)
			throws IOException {
		TransferCallback tc;
		if (callback instanceof TransferCallback) {
			tc = (TransferCallback) callback;
		} else {
			tc = new EmptyTransferCallback();
		}
		tc.onUploadStart();

		final TransferCallback itc = tc;
		final String ticket = this.upload(new CCIS(packet) {
			@Override
			public void onClose() {
				itc.onUploadEnd();
			}
		}, token, raise);
		tc.onTicket(ticket);

		if (callback == null) {
			log.debug("Callback is null, only upload");
			return;
		}
		final ConvertCallback cc = new CallbackProxy(callback);
		this.executor.execute(new Runnable() {

			@Override
			public void run() {
				String ret;
				boolean start = false;
				for (;;) {
					try {
						Thread.sleep(HTTPAgent.this.delay);
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
					}
					try {
						ret = query(ticket);
					} catch (IOException e) {
						cc.onException(e);
						return;
					}
					if (ret == null) {
						log.warn("转换因为未知原因失败");
						break;
					}
					ret = ret.trim();
					if (prepare.contains(ret)) {
						log.debug("Prepare...");
						continue;
					}
					if (doing.contains(ret)) {
						if (!start) {
							start = true;
							cc.onStart();
						}

						log.debug("Doing...");
						continue;
					}
					break;
				}
				if (ret != null && (success.contains(ret) || ret.startsWith("http"))) {
					try {
						itc.onDownloadStart();
						download(ticket, cc.openOutput());
						itc.onDownloadEnd();
						cc.onSuccess();
					} catch (IOException e) {
						cc.onException(e);
					}
				} else {
					cc.onFailed(ret, null);
				}
			}
		});
	}

	@Override
	public String submit(InputStream packet, String token, boolean raise) throws IOException {
		return this.upload(packet, token, raise);
	}

	public String getWebReaderPath(Packet packet) throws ConvertException, IOException {
		return this.getWebReaderPath(packet, null, false);
	}

	public String getWebReaderPath(Packet packet, String token, boolean raise) throws ConvertException, IOException {
		return this.getWebReaderPath(ConvertAgent.store(packet), token, raise);
	}

	public String getWebReaderPath(InputStream packet) throws ConvertException, IOException {
		return this.getWebReaderPath(packet, null, false);
	}

	public String getWebReaderPath(InputStream packet, String token, boolean raise)
			throws ConvertException, IOException {
		String url = baseURL + "reader?file=";
		final String ticket = this.upload(packet, token, raise);
		String ret = null;
		for (;;) {
			try {
				Thread.sleep(this.delay);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
			ret = this.query(ticket);
			if (ret == null) {
				log.warn("转换因为未知原因失败");
				break;
			}
			ret = ret.trim();
			if (prepare.contains(ret) || doing.contains(ret)) {
				log.debug("Continue...");
				continue;
			}
			break;
		}

		if (ret != null && (success.contains(ret) || ret.startsWith("http"))) {
			return url + ticket;
		} else {
			throw new ConvertException("Failed " + ret);
		}

	}

	private DefaultHttpClient createHttpClient() {
		if (client == null) {

			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client = new DefaultHttpClient(httpParams);

		}
		return client;
	}

}
