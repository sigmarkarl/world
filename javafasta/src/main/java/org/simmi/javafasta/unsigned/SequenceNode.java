package org.simmi.javafasta.unsigned;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.simmi.javafasta.shared.Sequence;

public class SequenceNode extends Sequence {
	int refstart;
	int refstop;
	
	public SequenceNode( String name, Map<String,Sequence> map, int start, int stop ) {
		super( name, map );
		refstart = start;
		refstop = stop;
	}
	
	Set<SequenceNode>	connections = new HashSet<SequenceNode>();
}
