package rr.contracts;

import java.util.ArrayList;
import java.util.Collection;

import rr.annotations.Abbrev;
import rr.event.ClassInitializedEvent;
import rr.event.MethodEvent;
import rr.meta.ClassInfo;
import rr.state.ShadowLock;
import rr.state.ShadowThread;
import rr.tool.Tool;
import acme.util.Util;
import acme.util.option.CommandLine;

@Abbrev("UT")
public class UnsynchronizedTool extends Tool {

	private Collection<ClassInfo> classes;
	
	public UnsynchronizedTool(String name, Tool next, CommandLine commandLine) {
		super(name, next, commandLine);
		classes = new ArrayList<>();
	}
	
	public boolean isContract(ClassInfo c) {
		//Util.println("###Check class: " + c.getName());
		for (ClassInfo in : c.getInterfaces()) {
			if (in.getName().equals("rr/contracts/ThreadUnsafe")) {
				classes.add(c);
				//Util.println("###Thread-unsafe class: " + c.getName());
				return true;
			}
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
				//Util.println("###Acquire lock");
				me.getThread().acquire(me.getTarget());
			}
		}
		super.enter(me);
	}

	private boolean isInstance(MethodEvent me) {
		// int lastBlock = me.getThread().getBlockDepth() - 1;
		// Object lastTarget = me.getThread().getBlock(lastBlock).getTarget();
		// return lastTarget != me.getTarget();
		return !me.getInfo().isStatic() && me.getTarget() != null;
	}
	
	private void reportContractViolation(MethodEvent me) {
		Util.printf("\nContract Violation: %s\n", me);
	}

	@Override
	public void exit(MethodEvent me) {
		if (classes.contains(me.getInfo().getOwner()) && isInstance(me)) {
			//Util.println("###Release lock");
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