package com.ece3574.dausin.qsort;

import java.util.*;

import com.ece3574.dausin.global.Friend;

public class MyQsort {

	/**
	 * @param args
	 */

	
	
	
	static <E extends Comparable<? super E>>
	void qsort(E[] A, int i, int j, int minimum) {      // Quicksort
		
		int pivotindex = findpivot(A, i, j); // Pick a pivot
		DSutil.swap(A, pivotindex, j);       // Stick pivot at end
		// k will be the first position in the right subarray
		int k = partition(A, i-1, j, A[j]);
		
		DSutil.swap(A, k, j);
	
		if ((k-i) > 1) qsort(A, i, k-1,minimum);
		if ((j-k) > 1) qsort(A, k+1, j,minimum);

	}

	

	static <E extends Comparable<? super E>>
	int partition(E[] A, int l, int r, E pivot) {
		do {                 // Move bounds inward until they meet
			while (A[++l].compareTo(pivot)<0);
			while ((r!=0) && (A[--r].compareTo(pivot)>0));

			DSutil.swap(A, l, r);
		} while (l < r);
		DSutil.swap(A, l, r);
		// Swap out-of-place values
		// Stop when they cross
		// Reverse last, wasted swap
		return l;
	}


	static <E extends Comparable<? super E>>
	int findpivot(E[] A, int i, int j)
	{ return (i+j)/2; }




	
	public static ArrayList<Friend> sortArrayList(ArrayList<Friend> list)
	{
		Friend[] test = new Friend[list.size()];
		list.toArray(test);
		MyQsort.qsort(test, 0, list.size() - 1, 0);
		list = new ArrayList<Friend>(Arrays.asList(test));
		return list;
	}

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		ArrayList<Integer> list = new ArrayList<Integer>();
//		list.add(0);
//		list.add(7);
//		list.add(2);
//		list.add(4);
//		list.add(67);
//		list.add(19);
//		list = sortArrayList(list);
//		for(int i = 0; i < list.size(); i++)
//		{
//			System.out.print(list.get(i) + " ");
//		}
//		System.out.println("\nLE FIN");
//	}


}


