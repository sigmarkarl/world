package org.simmi.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String uid, String power) throws IllegalArgumentException;
	String highScore(String name, String uid, int hscore, int w, int h, String superpowers, String bonuspower) throws IllegalArgumentException;
}
