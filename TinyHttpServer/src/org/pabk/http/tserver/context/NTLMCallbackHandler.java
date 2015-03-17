package org.pabk.http.tserver.context;

import javax.security.auth.callback.CallbackHandler;

public abstract class NTLMCallbackHandler implements CallbackHandler {
	private NTLMMessage negotiateMessage;
	private NTLMMessage challengeMessage;
	private NTLMMessage responseMessage;
	protected final NTLMMessage getNegotiateMessage() {
		return negotiateMessage;
	}
	protected final void setNegotiateMessage(NTLMMessage negotiateMessage) {
		this.negotiateMessage = negotiateMessage;
	}
	protected final NTLMMessage getChallengeMessage() {
		return challengeMessage;
	}
	protected final void setChallengeMessage(NTLMMessage challengeMessage) {
		this.challengeMessage = challengeMessage;
	}
	protected final NTLMMessage getResponseMessage() {
		return responseMessage;
	}
	protected final void setResponseMessage(NTLMMessage responseMessage) {
		this.responseMessage = responseMessage;
	}
}
