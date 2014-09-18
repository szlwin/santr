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

package santr.view.parser;

import org.abego.treelayout.TreeForTreeLayout;

import santr.v3.parser.data.RTree;


import java.util.Iterator;
import java.util.NoSuchElementException;


/** Adaptor ANTLR trees to {@link TreeForTreeLayout}. */
public class TreeLayoutAdaptor implements TreeForTreeLayout<RTree> {
	private static class AntlrTreeChildrenIterable implements Iterable<RTree> {
		private final RTree tree;

		public AntlrTreeChildrenIterable(RTree tree) {
			this.tree = tree;
		}

		
		public Iterator<RTree> iterator() {
			return new Iterator<RTree>() {
				private int i = 0;

				
				public boolean hasNext() {
					return tree.getrTreeList().size() > i;
				}

				
				public RTree next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return tree.getrTreeList().get(i++);
				}

				
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private static class AntlrTreeChildrenReverseIterable implements
		Iterable<RTree>
	{
		private final RTree tree;

		public AntlrTreeChildrenReverseIterable(RTree tree) {
			this.tree = tree;
		}

		
		public Iterator<RTree> iterator() {
			return new Iterator<RTree>() {
				private int i = tree.getrTreeList().size();

				
				public boolean hasNext() {
					return i > 0;
				}

				
				public RTree next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return tree.getrTreeList().get(--i);
				}

				
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private RTree root;

	public TreeLayoutAdaptor(RTree root) {
		this.root = root;
	}

	
	public boolean isLeaf(RTree node) {
		return node.getrTreeList().size() == 0;
	}

	
	public boolean isChildOfParent(RTree node, RTree parentNode) {
		return node.getParentTree() == parentNode;
	}

	
	public RTree getRoot() {
		return root;
	}

	
	public RTree getLastChild(RTree parentNode) {
		return parentNode.getrTreeList().get(parentNode.getrTreeList().size() - 1);
	}

	
	public RTree getFirstChild(RTree parentNode) {
		return parentNode.getrTreeList().get(0);
	}

	
	public Iterable<RTree> getChildrenReverse(RTree node) {
		return new AntlrTreeChildrenReverseIterable(node);
	}

	
	public Iterable<RTree> getChildren(RTree node) {
		return new AntlrTreeChildrenIterable(node);
	}
}
