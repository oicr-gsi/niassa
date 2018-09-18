package net.sourceforge.seqware.pipeline.tools;

import io.seqware.pipeline.SqwKeys;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import static net.sourceforge.seqware.common.util.Rethrow.rethrow;

import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

/**
 * <p>
 * Allows for preventing concurrent processes <em>owned by the same user</em>.
 * 
 * <p>
 * By default the lock identifier is a constant. If more flexibility is needed,
 * provide a value to <tt>SW_LOCK_ID</tt> in the seqware settings file.
 * 
 * <p>
 * WARNING: This system will fail to prevent concurrent processes if the value
 * of the SW_LOCK_ID is changed/added/removed in the time between multiple
 * processes acquiring a lock.
 * 
 */
public final class RunLock {

	private static final int MAX_ATTEMPTS = 5;

	/**
	 * Acquires a lock or throws an unchecked exception if it could not be acquired.
	 */
	public RunLock(String host) {
		// SEQWARE-1732 custom lock ID
		String id = ConfigTools.getSettings().get(SqwKeys.SW_LOCK_ID.getSettingKey());
		if (id == null) {
			id = "seqware";
		}

		this.id = id + "-" + host;

		for (int count = 0; count < MAX_ATTEMPTS; count++) {
			try {
				JUnique.acquireLock(this.id);
				return;
			} catch (AlreadyLockedException e) {
				if (count == MAX_ATTEMPTS - 1) {
					throw rethrow(e);
				}
				Log.warn("Failed to acquire lock, sleeping...");
				try {
					Thread.sleep(10_000);
				} catch (InterruptedException eint) {
					// Don't care if interrupted
				}
			}
		}
	}

	private final String id;

	/**
	 * Releases a lock if one was acquired, otherwise no-op.
	 */
	public void release() {
		JUnique.releaseLock(id);
	}
}
