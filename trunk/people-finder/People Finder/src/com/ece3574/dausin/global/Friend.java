package com.ece3574.dausin.global;

import android.graphics.Bitmap;

public class Friend implements Comparable<Friend>{
	public String id;
	public String name;
	public String pictureURL;
	public Bitmap pictureBitmap;
	@Override
	public int compareTo(Friend other) {
		// TODO Auto-generated method stub
		Friend otherFriend = other;
		if(otherFriend.name.equals(this.name))
			return 0;
		else
			return this.name.compareTo(otherFriend.name);
	}
}