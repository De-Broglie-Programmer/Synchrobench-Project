package linkedlists.lockbased;

import contention.abstractions.AbstractCompositionalIntSet;

public class HandOverHandListBasedSet extends AbstractCompositionalIntSet {
    // sentinel nodes
    final private Node head;

    public HandOverHandListBasedSet(){
        Node min = new Node(Integer.MIN_VALUE);
        Node max = new Node(Integer.MAX_VALUE);
        min.next = max;
        head = min;
    }

    @Override
    public boolean containsInt(int value){
	try {
	    Node pred = head;
	    Node curr = pred.next;
	    while (curr.value < value) {
		    pred = curr;
		    curr = curr.next;
	    }
	    return (curr.value == value);
        } finally{
            
        }
    }

    @Override
    public boolean addInt(int value){
        head.lock();
        Node pred=head;
        try {
            Node curr=head.next;
            curr.lock();
            try {
                while (curr.value < value){
                    pred.unlock();
                    pred = curr;
                    curr = pred.next;
                    curr.lock();
                }
                if (curr.value==value)
                    return false;
                Node node = new Node(value);
                node.next=curr;
                pred.next=node;
                return true;
            } finally{
                curr.unlock();
            }
        }finally{
            pred.unlock();
        }
    }

    @Override
    public boolean removeInt(int value){
        head.lock();
        Node pred=head;
        try {
            Node curr=pred.next;
            curr.lock();
            try {
                while (curr.value < value){
                    pred.unlock();
                    pred = curr;
                    curr = pred.next;
                    curr.lock();
                }
            if (curr.value==value){
                pred.next=curr.next;
                return true;}
                return false;
            } finally{
                curr.unlock();
            }
        }finally{
        pred.unlock();
        }
    }

     

    @Override
    public void clear() {
       head.next = new Node(Integer.MAX_VALUE);
    }

    /**
     * This method is not thread-safe. It cannot 
     * be made atomic with such locking mechanism
     */
    public int size() {
        int n = 0;
        Node node = head;

        while (node.next.value < Integer.MAX_VALUE) {
            n++;
            node = node.next;  // make sure it eventually steps out while loop
        }
        return n;
    }

    @Override
    public Object getInt(int x) {
	throw new UnsupportedOperationException();	
    }
    
    @Override
    public Object putIfAbsent(int x, int y) {
	throw new UnsupportedOperationException();
    }

}