package net.maatvirtue.commonlib.domain.daemon;

public interface Daemon
{
	void start();
	void stop();
	boolean isRunning();
	void restart();
}
