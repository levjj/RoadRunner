/******************************************************************************

  Copyright (c) 2010, Cormac Flanagan (University of California, Santa Cruz)
  and Stephen Freund (Williams College) 

  All rights reserved.  

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above
   copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided
   with the distribution.

 * Neither the names of the University of California, Santa Cruz
   and Williams College nor the names of its contributors may be
   used to endorse or promote products derived from this software
   without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 ******************************************************************************/

package rr.profiling;

import rr.annotations.Abbrev;
import rr.event.MethodEvent;
import rr.state.ShadowVar;
import rr.tool.Tool;
import rr.meta.MethodInfo;
import acme.util.count.Timer;
import acme.util.option.CommandLine;
import acme.util.Util;

import java.util.TreeSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Comparator;

/**
 * Count the number of events for most common cases.
 */

@Abbrev("PR")
final public class MethodProfiler extends Tool {

	Map<MethodInfo, Long> totalTime = new HashMap<MethodInfo, Long>();
	Deque<Long> currentTimers = new ArrayDeque<Long>();

	public MethodProfiler(String name, Tool next, CommandLine commandLine) {
		super(name, next, commandLine);
	}

	@Override
	public void enter(MethodEvent me) {
		currentTimers.addFirst(System.currentTimeMillis());
		super.enter(me);
	}

	@Override
	public void exit(MethodEvent me) {
		super.exit(me);
		long time = System.currentTimeMillis() - currentTimers.removeFirst();
		if (totalTime.containsKey(me.getInfo())) {
			time += totalTime.get(me.getInfo());
		}
		totalTime.put(me.getInfo(), time);
	}

	final static Comparator<Map.Entry<MethodInfo,Long>> timeComparator = new Comparator<Map.Entry<MethodInfo,Long>>() {
		@Override
		public int compare(Map.Entry<MethodInfo,Long> t1, Map.Entry<MethodInfo,Long> t2) {
			return t1.getValue() > t2.getValue() ? 1 : -1;
		}
	};

	@Override
	public void fini() {
		Set<Map.Entry<MethodInfo,Long>> entries, sortedEntries;
		entries = totalTime.entrySet();
		sortedEntries = new TreeSet<MethodInfo,Long>(timeComparator);
		sortedEntries.addAll(entries);

		for (Map.Entry<MethodInfo,Long> ml : sortedEntries) {
			Util.logf("%s: %d ms", ml.getKey().toSimpleName(), ml.getValue());
		}
	}
}
// vim: noet:ts=4:sw=4
