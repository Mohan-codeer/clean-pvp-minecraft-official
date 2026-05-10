package com.cleanpvp.client;

import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;
import java.util.Deque;

public class ClickTracker {
	private static final long WINDOW_MS = 1000L;
	private final Deque<Long> leftClicks = new ArrayDeque<>();
	private final Deque<Long> rightClicks = new ArrayDeque<>();
	private boolean leftWasDown;
	private boolean rightWasDown;

	public void tick(Minecraft client) {
		long now = System.currentTimeMillis();
		boolean leftDown = client.options.keyAttack.isDown();
		boolean rightDown = client.options.keyUse.isDown();

		if (leftDown && !leftWasDown) {
			leftClicks.addLast(now);
		}
		if (rightDown && !rightWasDown) {
			rightClicks.addLast(now);
		}

		leftWasDown = leftDown;
		rightWasDown = rightDown;
		prune(now);
	}

	public int getLeftCps() {
		return leftClicks.size();
	}

	public int getRightCps() {
		return rightClicks.size();
	}

	private void prune(long now) {
		while (!leftClicks.isEmpty() && now - leftClicks.peekFirst() > WINDOW_MS) {
			leftClicks.pollFirst();
		}
		while (!rightClicks.isEmpty() && now - rightClicks.peekFirst() > WINDOW_MS) {
			rightClicks.pollFirst();
		}
	}
}
