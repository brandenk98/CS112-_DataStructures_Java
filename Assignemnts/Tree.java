package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
	
				Stack <TagNode> hold = new Stack <TagNode>();
				String a = sc.nextLine();
				a=a.substring(1, a.length()-1);
				TagNode b = new TagNode(a,null,null);
				hold.push(b);
				root=hold.peek();
				
				while(sc.hasNextLine()) {
					int count=0;
					String line = sc.nextLine();
					if(line.charAt(0)=='<') { 
						if(line.charAt(1)=='/') { 
							hold.pop(); 
							continue;
						} else { 
							line=line.substring(1, line.length()-1);
							count =1;
						}
					}
					TagNode tag = new TagNode(line,null,null);
					if(hold.peek().firstChild==null) {
						hold.peek().firstChild=tag;
					} 
					else {
						TagNode child = hold.peek().firstChild;
						while(child.sibling != null) {
							child = child.sibling;
						}
						child.sibling=tag;
					}
					if(count==1) {
						hold.push(tag);
					}
				}
			}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		helperReplaceTag(root, oldTag, newTag);
	}
	private void helperReplaceTag(TagNode r, String oldTag, String newTag) {
		TagNode hold =r;
		if(hold == null) {
			return;
		}
		if(hold.tag.equals(oldTag)) {
			hold.tag=newTag;
		}
		helperReplaceTag(hold.firstChild, oldTag, newTag);
		helperReplaceTag(hold.sibling, oldTag, newTag);
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		TagNode tableCheck;
		tableCheck=checkForTable(root);
		TagNode temp = new TagNode(null, null,null);
		if(tableCheck==null) {
			System.out.println("\n\tThere is no table in this tree!");
			return;
		}
		else if(tableCheck!=null) {
			System.out.println("\n\tThere is a table in this tree!");
		}
		tableCheck = tableCheck.firstChild;
				for(int i =1; i < row; i++) {
			tableCheck=tableCheck.sibling;
		}	
		for(TagNode col = tableCheck.firstChild; col != null; col=col.sibling) {
			TagNode boldRow = new TagNode("b",col.firstChild, null);
			col.firstChild= boldRow;
		}
	}
	private TagNode checkForTable(TagNode hold) {
		int count = 0;
		if(hold == null) {
			return null;
		}
		TagNode temp = null;
		String tag = hold.tag;	
		if(tag.equals("table")) {
			temp=hold;
			return temp;
		}
		if(tag.equals("tr")) {
			count++;
		}	
		TagNode firstCCheck=checkForTable(hold.firstChild);
		TagNode sibCheck=checkForTable(hold.sibling);
		if(firstCCheck != null) {
			return firstCCheck;
		}
		if(sibCheck != null) {
			return sibCheck;
		}
		return null;
	}
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		if(tag.equals("p") || tag.equals("b") || tag.equals("em") || tag.equals("ol") || tag.equals("ul")) {
			removeTagHelper(root, tag);
			}
		System.out.println("\n\tYou did not enter a correct tag to get removed!");
		}
	
	private TagNode removeTagHelper(TagNode hold, String tag) {	
		if(hold==null) {
			return null;
		}
		if(tag.equals("p") || tag.equals("em") || tag.equals("b")) {
			if(hold.tag.equals(tag)&&hold.firstChild!=null) {
				
				hold.tag=hold.firstChild.tag;
				if(hold.firstChild.sibling!=null) {
					TagNode a = null;
					
					for(a=hold.firstChild; a.sibling!=null; a=a.sibling) {
						a.sibling=hold.sibling;
						hold.sibling=hold.firstChild.sibling;
						}
					hold.firstChild=hold.firstChild.firstChild;
					}
				}
			removeTagHelper(hold.firstChild, tag);
			removeTagHelper(hold.sibling, tag);
		}
		else if (tag.equals("ol") || tag.equals("ul")) {
			if(hold.tag.equals(tag)&&hold.firstChild!=null) {
				hold.tag="p";
				TagNode b = null;
				for(b=hold.firstChild; b.sibling!=null; b=b.sibling) {
					b.tag="p";
					}
				b.tag="p";
				b.sibling=hold.sibling;
				hold.sibling=hold.firstChild.sibling;
				hold.firstChild=hold.firstChild.firstChild;
			}
			removeTagHelper(hold.firstChild, tag);
			removeTagHelper(hold.sibling, tag);
		}
		return hold;
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	private boolean isWord(String str, String word) {
	    if(!str.contains(word))
	        return false;

	    int firstCharIndex = str.indexOf(word) + word.length();

        if(str.indexOf(word) == 0 && firstCharIndex > (str.length() - 1))
            return true;

        if(firstCharIndex == (str.length() - 1) && ".,:;!?".contains(Character.toString(str.charAt(firstCharIndex))))
            return true;

        return false;
    }
	private ArrayList<String> parse(String tag, String word) {
	    String cache = "";
	    StringTokenizer tokens = new StringTokenizer(tag, " ", true);
	    ArrayList<String> ret = new ArrayList<String>();

	    while(tokens.hasMoreTokens()) {
	        String str = tokens.nextToken();

	        if(isWord(str, word)) {
                if(!cache.equals(""))
	                ret.add(cache);
                ret.add(str);
                cache = "";
            } else {
	            cache += str;
            }
        }
        if(!cache.equals(""))
            ret.add(cache);
	    return ret;
    }
    private void addTag(TagNode parent, String word, String tag) {
		if(parent == null || parent.firstChild == null)
		    return;
        TagNode ptr = parent.firstChild;
        boolean replaceRoot = (ptr == root);
		while(ptr != null) {
		    addTag(ptr, word, tag);
		    if(ptr.firstChild == null && ptr.tag.contains(word)) {
		        TagNode next = ptr.sibling;
                ArrayList<String> parsing = parse(ptr.tag, word);      
                if(isWord(parsing.get(0), word)) {
                    parent.firstChild = new TagNode(tag, new TagNode(parsing.remove(0), null, null), null);
                    ptr = parent.firstChild;
                } else {
                    parent.firstChild = new TagNode(parsing.remove(0), null, null);
                    ptr = parent.firstChild;
                }
                for(String str : parsing) {
	                if(isWord(str, word)) {
	                    ptr.sibling = new TagNode(tag, new TagNode(str, null, null), null);
                    } else {
	                    ptr.sibling = new TagNode(str, null, null);
                    }
                    ptr = ptr.sibling;
                }
                ptr.sibling = next;
		        ptr = ptr.sibling;
            } else {
                ptr = ptr.sibling;
            }
        }
        if(replaceRoot)
            root = parent.firstChild;
    }
	public void addTag(String word, String tag) {
	    addTag(new TagNode("", root, null), word, tag);
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|---- ");
			} else {
				System.out.print("      ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}