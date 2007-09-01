package fr.lip6.move.coloane.api.session.states.authentication;

import org.antlr.runtime.RecognitionException;

import fr.lip6.move.coloane.api.session.controller.IMessage;

public final class VersionFailure extends RecognitionException implements IMessage {

	private static final long serialVersionUID = 6117512148578100228L;
	private String errorMessage;
	
	public VersionFailure(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
