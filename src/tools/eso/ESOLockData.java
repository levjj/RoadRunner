package tools.eso;

import rr.state.ShadowLock;
import tools.util.CV;

public class ESOLockData {

	public final ShadowLock peer;
	public final CV cv;
	
	public ESOLockData(ShadowLock ld) {
		this.peer = ld;
		this.cv = new CV(ESOCheckerConstants.CV_INIT_SIZE);
	}

	@Override
	public String toString() {
		return String.format("[CV=%s]", cv);
	}
}
