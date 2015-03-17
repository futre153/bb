package org.pabk.emanager;

public interface EventHandler {
	void init(Object[] args);
	void businessLogic();
	int maxNeedLoops();
}
