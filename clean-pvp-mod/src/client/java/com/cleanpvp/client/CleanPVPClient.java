package com.cleanpvp.client;

import net.fabricmc.api.ClientModInitializer;

public class CleanPVPClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HudManager.initialize();
	}
}
