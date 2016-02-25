/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package santr.view.grammer;

import org.abego.treelayout.TreeForTreeLayout;

import santr.gtree.model.GTree;


import java.util.Iterator;
import java.util.NoSuchElementException;


/** Adaptor ANTLR trees to {@link TreeForTreeLayout}. */
public class GTreeLayoutAdaptor implements TreeForTreeLayout<GTree> {
	private static class AntlrTreeChildrenIterable implements Iterable<GTree> {
		private final GTree tree;

		public AntlrTreeChildrenIterable(GTree tree) {
			this.tree = tree;
		}

		
		public Iterator<GTree> iterator() {
			return new Iterator<GTree>() {
				private int i = 0;

				
				public boolean hasNext() {
					return tree.getgTreeArray().length > i;
				}

				
				public GTree next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return tree.getgTreeArray()[i++];
				}

				
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private static class AntlrTreeChildrenReverseIterable implements
		Iterable<GTree>
	{
		private final GTree tree;

		public AntlrTreeChildrenReverseIterable(GTree tree) {
			this.tree = tree;
		}

		
		public Iterator<GTree> iterator() {
			return new Iterator<GTree>() {
				private int i = tree.getgTreeArray().length;

				
				public boolean hasNext() {
					return i > 0;
				}

				
				public GTree next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return tree.getgTreeArray()[--i];
				}

				
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private GTree root;

	public GTreeLayoutAdaptor(GTree root) {
		this.root = root;
	}

	
	public boolean isLeaf(GTree node) {
		return node.getgTreeArray() == null || node.getgTreeArray().length == 0;
	}

	
	public boolean isChildOfParent(GTree node, GTree parentNode) {
		return node.getParent() == parentNode;
	}

	
	public GTree getRoot() {
		return root;
	}

	
	public GTree getLastChild(GTree parentNode) {
		return parentNode.getgTreeArray()[parentNode.getgTreeArray().length - 1];
	}

	
	public GTree getFirstChild(GTree parentNode) {
		return parentNode.getgTreeArray()[0];
	}

	
	public Iterable<GTree> getChildrenReverse(GTree node) {
		return new AntlrTreeChildrenReverseIterable(node);
	}

	
	public Iterable<GTree> getChildren(GTree node) {
		return new AntlrTreeChildrenIterable(node);
	}
}
