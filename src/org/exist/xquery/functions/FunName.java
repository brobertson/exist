/* eXist Open Source Native XML Database
 * Copyright (C) 2000-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * $Id$
 */

package org.exist.xquery.functions;

import org.exist.dom.QName;
import org.exist.xquery.Cardinality;
import org.exist.xquery.Dependency;
import org.exist.xquery.Function;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.Profiler;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.Item;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.Type;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/**
 * xpath-library function: string(object)
 *
 */
public class FunName extends Function {

	public final static FunctionSignature signatures[] = {
		new FunctionSignature(
			new QName("name", Function.BUILTIN_FUNCTION_NS),
			"Returns the name of a node, as an xs:string that is " +
			"either the zero-length string, or has the lexical form of an xs:QName",
			new SequenceType[0],
			new SequenceType(Type.STRING, Cardinality.ZERO_OR_ONE)
		),
		new FunctionSignature(
			new QName("name", Function.BUILTIN_FUNCTION_NS),
			"Returns the name of a node, as an xs:string that is " +
			"either the zero-length string, or has the lexical form of an xs:QName",
			new SequenceType[] { new SequenceType(Type.NODE, Cardinality.ZERO_OR_ONE) },
			new SequenceType(Type.STRING, Cardinality.ZERO_OR_ONE)
		)
	};

	public FunName(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}
	
	public Sequence eval(Sequence contextSequence, Item contextItem) throws XPathException {
        if (context.getProfiler().isEnabled()) {
            context.getProfiler().start(this);       
            context.getProfiler().message(this, Profiler.DEPENDENCIES, "DEPENDENCIES", Dependency.getDependenciesName(this.getDependencies()));
            if (contextSequence != null)
                context.getProfiler().message(this, Profiler.START_SEQUENCES, "CONTEXT SEQUENCE", contextSequence);
            if (contextItem != null)
                context.getProfiler().message(this, Profiler.START_SEQUENCES, "CONTEXT ITEM", contextItem.toSequence());
        }
        
		if(contextItem != null)
			contextSequence = contextItem.toSequence();
        
        Sequence result;
        Item item = null;
        // check if the node is passed as an argument or should be taken from
        // the context sequence
		if(getArgumentCount() > 0) {
			Sequence seq = getArgument(0).eval(contextSequence);
			if(seq.getLength() > 0)
				item = seq.itemAt(0);
		} else {
			if(contextSequence.getLength() > 0)
				item = contextSequence.itemAt(0);
		}
        if(item == null)
            result = Sequence.EMPTY_SEQUENCE;
        else {
            if(!Type.subTypeOf(item.getType(), Type.NODE))
                throw new XPathException(getASTNode(), getName() + " expects an argument of type node(); got: " +
                        Type.getTypeName(item.getType()));
            
            Node n = ((NodeValue)item).getNode();
    		switch(n.getNodeType()) {
    			case Node.ELEMENT_NODE:
    			case Node.ATTRIBUTE_NODE:
    				result = new StringValue(n.getNodeName());
    			case Node.PROCESSING_INSTRUCTION_NODE:
    				result = new StringValue(((ProcessingInstruction)n).getTarget());
                    break;
                //TODO : what kind of default do we expect here ? -pb
    			default:
    				return new StringValue("");
    		}
        }
        
        if (context.getProfiler().isEnabled()) 
            context.getProfiler().end(this, "", result); 
        
        return result;          
        
	}
}
