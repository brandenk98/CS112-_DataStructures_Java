package apps;

import structures.*;
import java.util.ArrayList;


public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		//Follows the algorithm given to us in the directions
				//1
				PartialTreeList partials = new PartialTreeList();
				//2
				for(Vertex v : graph.vertices) {
					//Create a partial tree with only v
					PartialTree pt = new PartialTree(v);
					//Mark v as belonging to a 
					Vertex.Neighbor n = v.neighbors;
					while(n != null){
						pt.getArcs().insert(new PartialTree.Arc(v,  n.vertex, n.weight));
						n = n.next;
					}
					partials.append(pt);
				}
				return partials;
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		ArrayList<PartialTree.Arc> edge = new ArrayList<PartialTree.Arc>();
		while(ptlist.size()>1){
			//remove the first pt from ptx from L
			PartialTree ptx = ptlist.remove();
			System.out.println(ptx.toString());
			MinHeap<PartialTree.Arc> pqx = ptx.getArcs();
			//remove the highest priority arc from pqx
			PartialTree.Arc g = pqx.getMin();
			Vertex v1 = ptx.getRoot();
			Vertex v2 = g.v2;
			//if v2 belongs to ptx go to step 4 and pick the new highest
			while(!pqx.isEmpty()&& v2.getRoot().equals(v1)) {
				g=pqx.deleteMin();
				v2=g.v2;
			}
			//find the partial tree pty where v2 belong too
			//remove pty
			//pqy pty p queue
			PartialTree pty = ptlist.removeTreeContaining(v2);
			MinHeap<PartialTree.Arc> pqy = pty.getArcs();
			//merge ptx and pty by putting them in the same p queues
			//append the resulting tree to L
			pty.getRoot().parent=ptx.getRoot();
			pqy.merge(pqx);
			try {
				pqx.getMin();
			} catch(Exception e) {
				break;
			}
			ptx.merge(pty);
			ptlist.append(ptx);
			edge.add(g);
		}
		return edge;
	}
}