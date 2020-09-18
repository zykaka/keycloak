package com.eigpay.keycloak.connectid;

import org.keycloak.provider.wildfly.WildflyLifecycleListener;

public class ConnectionID {
	
	public static void init() {
		WildflyLifecycleListener listener = new WildflyLifecycleListener();
	}

}
