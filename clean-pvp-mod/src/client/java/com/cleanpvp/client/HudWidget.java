package com.cleanpvp.client;

public enum HudWidget {
	KEYSTROKES("Keystrokes"),
	FPS("FPS"),
	CPS("CPS"),
	ARMOR("Armor"),
	POTIONS("Potions");

	private final String title;

	HudWidget(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
