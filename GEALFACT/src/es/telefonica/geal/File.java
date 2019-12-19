package es.telefonica.geal;

import java.io.InputStream;

public class File {
	private String fileName;
	private InputStream fileContent;
	private String link;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public InputStream getFileContent() {
		return fileContent;
	}
	public void setFileContent(InputStream fileContent) {
		this.fileContent = fileContent;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
}
