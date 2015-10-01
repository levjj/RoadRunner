package tools.eso;

import java.util.ArrayList;
import java.util.Collection;

import acme.util.Util;
import acme.util.option.CommandLine;
import rr.annotations.Abbrev;
import rr.contracts.ThreadUnsafe;
import rr.event.MethodEvent;
import rr.meta.ClassInfo;
import rr.state.ShadowLock;
import rr.state.ShadowThread;
import rr.tool.Tool;

@Abbrev("ESO")
public class ESOChecker extends Tool {

	private Collection<ClassInfo> classes;
	
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
	public void enter(MethodEvent me) {
		ClassInfo owner = me.getInfo().getOwner();
		if ((classes.contains(owner) || isContract(owner)) && isInstance(me)) {
			ShadowThread st = ShadowLock.get(me.getTarget()).getHoldingThread();
			if (st != null && st != me.getThread()) {
				this.reportContractViolation(me);
			} else {
				me.getThread().acquire(me.getTarget());
			}
		}
		super.enter(me);
	}

	private boolean isInstance(MethodEvent me) {
		return !me.getInfo().isStatic() && me.getTarget() != null;
	}
	
	private void reportContractViolation(MethodEvent me) {
		Util.printf("\nContract Violation: %s\n", me);
	}
	
	@Override
	public void exit(MethodEvent me) {
		if (classes.contains(me.getInfo().getOwner()) && isInstance(me)) {
			for (ShadowLock sl : me.getThread().getLocksHeld()) {
				if (sl.getLock() == me.getTarget()) {
					me.getThread().release(me.getTarget());
					break;
				}
			}
		}
		super.exit(me);
	}

}