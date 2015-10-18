package tools.eso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import acme.util.Assert;
import acme.util.Util;
import acme.util.decorations.Decoration;
import acme.util.decorations.DecorationFactory;
import acme.util.decorations.DecorationFactory.Type;
import acme.util.decorations.DefaultValue;
import acme.util.option.CommandLine;
import rr.annotations.Abbrev;
import rr.contracts.ThreadUnsafe;
import rr.event.AcquireEvent;
import rr.event.JoinEvent;
import rr.event.MethodEvent;
import rr.event.NewThreadEvent;
import rr.event.ReleaseEvent;
import rr.meta.ClassInfo;
import rr.meta.MetaDataInfoMaps;
import rr.state.ShadowLock;
import rr.state.ShadowThread;
import rr.tool.Tool;
import tools.util.CV;

@Abbrev("ESO")
public class ESOChecker extends Tool {
	
	public static final String val = "kongposh";
	private Collection<ClassInfo> classes;
	
	private Map<Object, CV> esoData = new HashMap<Object, CV>();
	
	static final Decoration<ShadowLock, CV> esoLockData = ShadowLock.makeDecoration("ESO:ShadowLock",
																					DecorationFactory.Type.MULTIPLE, new DefaultValue<ShadowLock, CV>() {
																						public CV get(final ShadowLock ld) {
																							return new CV(ESOCheckerConstants.CV_INIT_SIZE);
																						}
																					});
	
	public static final Decoration<ClassInfo, CV> esoInfoData = MetaDataInfoMaps.getClasses()
	.makeDecoration("ESO:InfoData", Type.MULTIPLE, new DefaultValue<ClassInfo, CV>() {
		public CV get(ClassInfo t) {
			return new CV(ESOCheckerConstants.CV_INIT_SIZE);
		}
	});
	
	static CV ts_get_cv(ShadowThread ts) {
		Assert.panic("Bad");
		return null;
	}
	
	static void ts_set_cv(ShadowThread ts, CV cv) {
		Assert.panic("Bad");
	}
	
	public ESOChecker(String name, Tool next, CommandLine commandLine) {
		super(name, next, commandLine);
		classes = new ArrayList<>();
	}
	
	public boolean isContract(ClassInfo c) {
		if (c.hasAnnotation(ThreadUnsafe.class)) {
			classes.add(c);
			return true;
		}
		return false;
	}
	
	@Override
	public void create(NewThreadEvent e) {
		
		ShadowThread ct = e.getThread();
		int cid = ct.getTid();
		boolean isException = false;
		CV pcv = null;
		int pid = -1;
		try {
			ShadowThread pt = ct.getParent(); // Get current vector clock
			// value of the parent
			// thread
			pid = pt.getTid();
			pcv = ts_get_cv(pt);
			
		} catch (Exception exception) {
			isException = true;
			// This catching of exception is intentional for the exception
			// thrown for accessing the parent of thread - 0
		}
		
		CV cv = ts_get_cv(ct);
		if (cv == null) {
			cv = createCV(ESOCheckerConstants.CV_INIT_SIZE);
			cv.set(cid, 1);
			if (!isException) {
				cv.max(pcv);
				pcv.inc(pid);
			}
			ts_set_cv(ct, cv);
		}
		
		super.create(e);
	}
	
	@Override
	public void postJoin(JoinEvent je) {
		CV ctcv = getCv(je.getThread());
		CV jtcv = getCv(je.getJoiningThread());
		ctcv.max(jtcv);
	}
	
	@Override
	public void acquire(AcquireEvent ae) {
		final ShadowThread ct = ae.getThread();
		final ShadowLock l = ae.getLock();
		getCv(ct).max(esoLockData.get(l));
		super.acquire(ae);
	}
	
	@Override
	public void release(ReleaseEvent re) {
		final ShadowThread ct = re.getThread();
		final ShadowLock l = re.getLock();
		CV lcv = esoLockData.get(l);
		CV ctcv = getCv(ct);
		lcv.set(ct.getTid(), ctcv.get(ct.getTid())); // updated the lock vc
		// value for current
		// thread to current
		// thread vc value
		ctcv.inc(ct.getTid()); // incremented the cv value for current thread
		super.release(re);
	}
	
	@Override
	public void enter(MethodEvent me) {
		
		synchronized (this) {
			Object eso = me.getTarget();
			CV esocv = esoData.get(eso);
			if (esocv == null) {
				esocv = createCV(ESOCheckerConstants.CV_INIT_SIZE);
				esoData.put(eso, esocv);
			}
			ClassInfo esoInfo = me.getInfo().getOwner();
			if ((classes.contains(esoInfo) || isContract(esoInfo)) && isInstance(me)) {
				ShadowThread ct = me.getThread();
				// CV esocv = esoInfoData.get(esoInfo);
				int cid = ct.getTid();
				CV ctcv = getCv(ct);
				if (esocv.anyGt(ctcv)) {
					reportContractViolation(me);
					// System.exit(0);
				} else {
					esocv.set(cid, ctcv.get(cid));
					ctcv.inc(cid);
				}
			}
			super.enter(me);
		}
	}
	
	private boolean isInstance(MethodEvent me) {
		return !me.getInfo().isStatic() && me.getTarget() != null;
	}
	
	private void reportContractViolation(MethodEvent me) {
		Util.printf("\nContract Violation: %s\n", me + "  by thread " + me.getThread().getTid());
	}
	
	// @Override
	// public void exit(MethodEvent me) {
	// if (classes.contains(me.getInfo().getOwner()) && isInstance(me)) {
	// for (ShadowLock sl : me.getThread().getLocksHeld()) {
	// if (sl.getLock() == me.getTarget()) {
	// me.getThread().release(me.getTarget());
	// break;
	// }
	// }
	// }
	// super.exit(me);
	// }
	
	private CV getCv(ShadowThread td) {
		CV cv = ts_get_cv(td);
		return cv;
	}
	
	private CV createCV(int size) {
		CV cv = new CV(size);
		return cv;
		
	}
}