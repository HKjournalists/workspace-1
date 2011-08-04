package edu.bupt.rmi;

import java.io.Serializable;

public class ComputingUnit implements Serializable {

	private static final long serialVersionUID = 4800167515123394318L;

	private String ip;
	private int port;
	private int ftpPort;
	private String ftpUsername;
	private String ftpPassword;

	public ComputingUnit(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	@Override
	public String toString() {
		return "ComputingUnit [ftpPassword=" + ftpPassword + ", ftpPort=" + ftpPort + ", ftpUsername=" + ftpUsername + ", ip=" + ip + ", port=" + port + "]";
	}
}
