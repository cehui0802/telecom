package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;

import java.io.InputStream;
import java.net.URI;

import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.PackEntry;

public final class Common {
	private String title;

	private String format;

	private int dpi;

	private float width;

	private float height;

	private URI uri;

	private PackEntry data;

	private Argument argument;

	private Common(String title, String format) {
		this.title = title;
		this.format = (format == null) ? null : format.toLowerCase();
	}

	public Common(String title, String format, URI uri) {
		this(title, format);
		this.uri = uri;
	}

	public Common(String title, String format, InputStream data) {
		this(title, format);
		this.data = PackEntry.wrap(data);
	}

	public Common(String title, String format, int dpi, URI uri) {
		this(title, format);
		this.dpi = dpi;
		this.uri = uri;
	}

	public Common(String title, String format, float width, URI uri) {
		this(title, format);
		this.width = width;
		this.uri = uri;
	}

	public Common(String title, String format, int dpi, InputStream data) {
		this(title, format);
		this.dpi = dpi;
		this.data = PackEntry.wrap(data);
	}

	public Common(String title, String format, int dpi, PackEntry data) {
		this(title, format);
		this.dpi = dpi;
		this.data = data;
	}

	public Common(String title, String format, float width, InputStream data) {
		this(title, format);
		this.width = width;
		this.data = PackEntry.wrap(data);
	}

	public Common(String title, String format, float width, float height, InputStream data) {
		this(title, format);
		this.width = width;
		this.height = height;
		this.data = PackEntry.wrap(data);
	}

	public Common(String title, String format, float width, PackEntry data) {
		this(title, format);
		this.width = width;
		this.data = data;
	}

	public String getTitle() {
		return this.title;
	}

	public String getFormat() {
		return this.format;
	}

	public int getDPI() {
		return this.dpi;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public URI getURI() {
		return this.uri;
	}

	public PackEntry getData() {
		return this.data;
	}

	public Argument getArgument() {
		return this.argument;
	}

	public void setArgument(Argument argument) {
		this.argument = argument;
	}
}
