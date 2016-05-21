package io.macgyver.cli;

public class CLIRemoteException extends CLIException {

	int code=0;
	public CLIRemoteException(String message, int code) {
		super(message+" ("+code+")");
		this.code = code;
	}
	
}
