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

import santr.v4.parser.ParserTree;
import santr.v4.parser.TreeUtil;


import java.util.Iterator;
import java.util.NoSuchElementException;


/** Adaptor ANTLR trees to {@link TreeForTreeLayout}. */
public class TreeLayoutAdaptor implements TreeForTreeLayout<ParserTree> {
	private static class AntlrTreeChildrenIterable implements Iterable<ParserTree> {
		private final ParserTree tree;

		public AntlrTreeChildrenIterable(ParserTree tree) {
			this.tree = tree;
		}

		
		public Iterator<ParserTree> iterator() {
			return new Iterator<ParserTree>() {
				private int i = 0;

				
				public boolean hasNext() {
					return TreeUtil.getTreeList(tree)!=null && TreeUtil.getTreeList(tree).size() > i;
				}

				
				public ParserTree next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return TreeUtil.getTreeList(tree).get(i++);
				}

				
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private static class AntlrTreeChildrenReverseIterable implements
		Iterable<ParserTree>
	{
		private final ParserTree tree;

		public AntlrTreeChildrenReverseIterable(ParserTree tree) {
			this.tree = tree;
		}

		
		public Iterator<ParserTree> iterator() {
			return new Iterator<ParserTree>() {
				private int i = TreeUtil.getTreeList(tree)!=null ? TreeUtil.getTreeList(tree).size():0;

				
				public boolean hasNext() {
					return i > 0;
				}

				
				public ParserTree next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return TreeUtil.getLast(tree)==null ?null:TreeUtil.getTreeList(tree).get(--i);
				}

				
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private ParserTree root;

	public TreeLayoutAdaptor(ParserTree root) {
		this.root = root;
	}

	
	public boolean isLeaf(ParserTree node) {
		return TreeUtil.getLast(node) == null;
	}

	
	public boolean isChildOfParent(ParserTree node, ParserTree parentNode) {
		return TreeUtil.getParent(node) == parentNode;
	}

	
	public ParserTree getRoot() {
		return root;
	}

	
	public ParserTree getLastChild(ParserTree parentNode) {
		return TreeUtil.getLast(parentNode);
	}

	
	public ParserTree getFirstChild(ParserTree parentNode) {
		return TreeUtil.getTreeList(parentNode).get(0);
	}

	
	public Iterable<ParserTree> getChildrenReverse(ParserTree node) {
		return new AntlrTreeChildrenReverseIterable(node);
	}

	
	public Iterable<ParserTree> getChildren(ParserTree node) {
		return new AntlrTreeChildrenIterable(node);
	}
}
