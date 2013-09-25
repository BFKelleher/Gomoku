/* Copyright 2011 Brian Kelleher
This file is part of ConnectFive.

ConnectFive is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

ConnectFive is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ConnectFive.  If not, see <http://www.gnu.org/licenses/>.*/
package net.briankelleher.connectfive;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

public class Space extends ImageView{
	private int owner;
	private int type;
	private int score;
	public final static int CENTER = 0;
	public final static int DOWN = 1;
	public final static int DOWNLEFT = 2;
	public final static int DOWNRIGHT = 3;
	public final static int LEFT = 4;
	public final static int RIGHT = 5;
	public final static int STANDARD = 6;
	public final static int UP = 7;
	public final static int UPLEFT = 8;
	public final static int UPRIGHT = 9;
	public Space(Context context) {
		this(context, STANDARD, 0);
	}
	public Space(Context context, int t) {
		this(context, t, 0);
	}
	public Space(Context context, int t, int o) {
		super(context);
		type = t;
		owner = o;
		score = 0;
	}
	public void play(int player) {
		owner = player; // 1 is black, 2 is white, 0 is empty
		if (type == STANDARD || type == CENTER) {
			if (owner == 1) {
				setImageResource(R.drawable.blackstandardcenter);
			} else if (owner == 2) {
				setImageResource(R.drawable.whitestandardcenter);
			} else if (owner == 0) {
				if (type == STANDARD) {
					setImageResource(R.drawable.emptystandard);
				} else {
					setImageResource(R.drawable.emptycenter);
				}
			} else {
				Log.e("ConnectFiveSpace", "Owner was not set properly.");
			}
		} else if (type == DOWN) {
			if (owner == 1) {
				setImageResource(R.drawable.blackdown);
			} else if (owner == 2) {
				setImageResource(R.drawable.whitedown);
			} else if (owner == 0) {
				setImageResource(R.drawable.emptydown);
			} else {
				Log.e("ConnectFiveSpace", "Owner was not set properly.");
			}
		} else if (type == DOWNLEFT) {
			if (owner == 1) {
				setImageResource(R.drawable.blackdownleft);
			} else if (owner == 2) {
				setImageResource(R.drawable.whitedownleft);
			} else if (owner == 0) {
				setImageResource(R.drawable.emptydownleft);
			} else {
				Log.e("ConnectFiveSpace", "Owner was not set properly.");
			}
		} else if (type == DOWNRIGHT) {
			if (owner == 1) {
				setImageResource(R.drawable.blackdownright);
			} else if (owner == 2) {
				setImageResource(R.drawable.whitedownright);
			} else if (owner == 0) {
				setImageResource(R.drawable.emptydownright);
			} else {
				Log.e("ConnectFiveSpace", "Owner was not set properly.");
			}
		} else if (type == LEFT) {
			if (owner == 1) {
				setImageResource(R.drawable.blackleft);
			} else if (owner == 2) {
				setImageResource(R.drawable.whiteleft);
			} else if (owner == 0) {
				setImageResource(R.drawable.emptyleft);
			} else {
				Log.e("ConnectFiveSpace", "Owner was not set properly.");
			}
		} else if (type == RIGHT) {
			if (owner == 1) {
				setImageResource(R.drawable.blackright);
			} else if (owner == 2) {
				setImageResource(R.drawable.whiteright);
			} else if (owner == 0) {
				setImageResource(R.drawable.emptyright);
			} else {
				Log.e("ConnectFiveSpace", "Owner was not set properly.");
			}
		} else if (type == UP) {
			if (owner == 1) {
				setImageResource(R.drawable.blackup);
			} else if (owner == 2) {
				setImageResource(R.drawable.whiteup);
			} else if (owner == 0) {
				setImageResource(R.drawable.emptyup);
			} else {
				Log.e("ConnectFiveSpace", "Owner was not set properly.");
			}
		} else if (type == UPLEFT) {
			if (owner == 1) {
				setImageResource(R.drawable.blackupleft);
			} else if (owner == 2) {
				setImageResource(R.drawable.whiteupleft);
			} else if (owner == 0) {
				setImageResource(R.drawable.emptyupleft);
			} else {
				Log.e("ConnectFiveSpace", "Owner was not set properly.");
			}
		} else if (type == UPRIGHT) {
			if (owner == 1) {
				setImageResource(R.drawable.blackupright);
			} else if (owner == 2) {
				setImageResource(R.drawable.whiteupright);
			} else if (owner == 0) {
				setImageResource(R.drawable.emptyupright);
			} else {
				Log.e("ConnectFiveSpace", "Owner was not set properly.");
			}
		} else {
			Log.e("ConnectFiveSpace", "Type not set properly");
		}
	}
	public void setType(int t) {
		type = t;
	}
	public void setScore(int s) {
		score = s;
	}
	public void addToScore(int s) {
		score += s;
	}
	public int getType() {
		return type;
	}
	public int getOwner() {
		return owner;
	}
	public int getScore() {
		return score;
	}
}
