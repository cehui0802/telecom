package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public abstract class PackEntry implements Closeable {
	protected MessageDigest digester;

	protected byte[] result;

	protected byte[] hash;

	public abstract InputStream open() throws IOException;

	public abstract void close() throws IOException;

	public byte[] digestResult() {
		return this.result;
	}

	public void setHash(byte[] hash) {
		if (hash != null) {
			this.hash = hash;
			if (this.digester == null)
				try {
					this.digester = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
		}
	}

	protected void digest() throws IOException {
		if (this.digester != null) {
			if (this.result == null)
				this.result = this.digester.digest();
			if (this.hash != null && !Arrays.equals(this.result, this.hash))
				throw new IOException("Hash is not match");
		}
	}

	public static PackEntry wrap(InputStream in) {
		return wrap(in, (MessageDigest) null);
	}

	public static PackEntry wrap(File in) {
		return wrap(in, (MessageDigest) null);
	}

	public static PackEntry wrap(InputStream in, MessageDigest md) {
		return (in == null) ? null : new Stream(in, md);
	}

	public static PackEntry wrap(File in, MessageDigest md) {
		return (in == null) ? null : new Filer(in, md);
	}

	public static PackEntry wrap(byte[] in) {
		return wrap(new ByteArrayInputStream(in));
	}

	public static class Filer extends PackEntry {
		private InputStream fis;

		private File file;

		private Filer(File file, MessageDigest md) {
			this.file = file;
			this.digester = md;
		}

		public InputStream open() throws IOException {
			if (this.fis == null) {
				this.fis = FileUtils.openInputStream(this.file);
				if (this.digester != null)
					return new DigestInputStream(this.fis, this.digester) {
						public void close() throws IOException {
							super.close();
							PackEntry.Filer.this.digest();
						}
					};
			}
			return this.fis;
		}

		public void close() throws IOException {
			IOUtils.closeQuietly(this.fis);
		}
	}

	public static class Stream extends PackEntry {
		private InputStream in;

		private Stream(InputStream in, MessageDigest md) {
			this.in = in;
			this.digester = md;
		}

		public InputStream open() throws IOException {
			return (this.digester == null) ? this.in : new DigestInputStream(this.in, this.digester) {
				public void close() throws IOException {
					super.close();
					PackEntry.Stream.this.digest();
				}
			};
		}

		public void close() throws IOException {
			IOUtils.closeQuietly(this.in);
		}
	}

	public static class Name extends PackEntry {
		private Packet packet;

		private String name;

		private PackEntry e;

		Name(Packet packet, String name) {
			this.packet = packet;
			this.name = name;
		}

		public String value() {
			return this.name;
		}

		public InputStream open() throws IOException {
			if (this.e == null)
				this.e = this.packet.findEntry(this.name);
			return (this.e == null) ? null : this.e.open();
		}

		public byte[] digestResult() {
			return this.e.digestResult();
		}

		public void close() {
			IOUtils.closeQuietly(this.e);
		}
	}
}
