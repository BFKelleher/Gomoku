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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectFiveActivity extends Activity implements OnClickListener {
	public TableLayout table;
	public LinearLayout slot;
	public TableRow tr;
	public Space[][] board;
	public Space sp;
	public Display display;
	public boolean turn;
	public boolean going;
	public TextView tv;
	public Button no;
	public Button yes;
	public boolean won;
	public boolean blackAllowThreeThree;
	public boolean whiteAllowThreeThree;
	public boolean blackAllowFourFour;
    public boolean whiteAllowFourFour;
	public SharedPreferences sharedPrefs;
	public boolean changedPrefs;
	public boolean played;
	public Boolean useAI;
	public static final int CLOSE_BY_FACTOR = 20;
	public static final int MAKES_THREE_FACTOR = 50;
	public static final int MAKES_CLOSED_FOUR_FACTOR = 85;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	if (useAI == null) {
    		useAI = false;
    	}
    	turn = true; //true is black, false is white
    	going = false;
    	won = false;
    	changedPrefs = false;
    	played = false;
        int rows = 19;
        int columns = 19;
        display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight();
        Log.i("ConnectFiveActivity", "Width is " + width);
        Log.i("ConnectFiveActivity", "Height is " + height);
        int sideLength = Math.min(height, width) / 19;
        Log.i("ConnectFiveActivity", "Side Length is " + sideLength);
        setContentView(R.layout.main);
        table = (TableLayout)findViewById(R.id.table);
        board = new Space[rows][columns];
        for (int row = 0; row < rows; row++) {
        	tr = new TableRow(this);
        	tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        	for (int column = 0; column < columns; column++) {
        		String col;
        		String r;
        		if (column < 10) {
        			col = "0" + column;
        		} else {
        			col = "" + column;
        		}
        		if (row < 10) {
        			r = "0" + row;
        		} else {
        			r = "" + row;
        		}
        		board[row][column] = new Space(this);
        		sp = board[row][column];
        		sp.setAdjustViewBounds(true);
        		sp.setTag(col + r);
        		String tag = sp.getTag().toString();
        		if (tag.equals("0000")) {
        			sp.setImageResource(R.drawable.emptyupleft);
        			sp.setType(Space.UPLEFT);
        		} else if (tag.equals("1800")) {
        			sp.setImageResource(R.drawable.emptyupright);
        			sp.setType(Space.UPRIGHT);
        		} else if (tag.equals("0018")) {
        			sp.setImageResource(R.drawable.emptydownleft);
        			sp.setType(Space.DOWNLEFT);
        		} else if (tag.equals("1818")) {
        			sp.setImageResource(R.drawable.emptydownright);
        			sp.setType(Space.DOWNRIGHT);
        		} else if (column == 0) {
        			sp.setImageResource(R.drawable.emptyleft);
        			sp.setType(Space.LEFT);
        		} else if (column == 18) {
        			sp.setImageResource(R.drawable.emptyright);
        			sp.setType(Space.RIGHT);
        		} else if (row == 0) {
        			sp.setImageResource(R.drawable.emptyup);
        			sp.setType(Space.UP);
        		} else if (row == 18) {
        			sp.setImageResource(R.drawable.emptydown);
        			sp.setType(Space.DOWN);
        		} else if (tag.equals("0303") || tag.equals("0903") || tag.equals("1503") || tag.equals("0309") || tag.equals("0909") || tag.equals("1509") || tag.equals("0315") || tag.equals("0915") || tag.equals("1515")) {
        			sp.setImageResource(R.drawable.emptycenter);
        			sp.setType(Space.CENTER);
        		} else {
        			sp.setImageResource(R.drawable.emptystandard);
        			sp.setType(Space.STANDARD);
        		}
        		sp.setMaxHeight(sideLength);
        		sp.setMaxWidth(sideLength);
        		sp.setOnClickListener(this);
        		tr.addView(sp);
        	}
        	table.addView(tr);
        	yes = new Button(this);
			yes.setText(R.string.yes);
			yes.setTag("yes");
			no = new Button(this);
			no.setText(R.string.no);
			no.setTag("no");
			yes.setOnClickListener(this);
			if (useAI) {
        		Toast.makeText(this, R.string.on, Toast.LENGTH_SHORT).show();
			}
			yes.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			no.setOnClickListener(this);
			no.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			yes.setTextSize(25);
			no.setTextSize(25);
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			blackAllowThreeThree = sharedPrefs.getBoolean("black_allow_three_three", false);
			whiteAllowThreeThree = sharedPrefs.getBoolean("white_allow_three_three", true);
			blackAllowFourFour = sharedPrefs.getBoolean("black_allow_four_four", false);
			whiteAllowFourFour = sharedPrefs.getBoolean("white_allow_four_four", true);
        }
    }
    public boolean onCreateOptionsMenu(Menu m) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, m);
    	return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset) {
        	onCreate(null);
        } else if (id == R.id.pref) {
        	Intent myIntent = new Intent(this, ConnectFivePreferences.class);
            startActivity(myIntent);
        } else if (id == R.id.about) {
        	Toast.makeText(this, R.string.aboutText, Toast.LENGTH_LONG).show();
        } else if (id == R.id.useai) {
        	useAI = !useAI;
        	if (useAI) {
        		Toast.makeText(this, R.string.on, Toast.LENGTH_SHORT).show();
        		if (!turn) {
            		aiPlay();
            	}
        	}
        	if (!useAI) {
        		Toast.makeText(this, R.string.off, Toast.LENGTH_SHORT).show();
        	}
        }
        return super.onOptionsItemSelected(item);
    }
	public void onClick(View v) {
		tv = (TextView)findViewById(R.id.tv);
		if (changedPrefs && !played) {
			blackAllowThreeThree = sharedPrefs.getBoolean("black_allow_three_three", false);
			whiteAllowThreeThree = sharedPrefs.getBoolean("white_allow_three_three", true);
			blackAllowFourFour = sharedPrefs.getBoolean("black_allow_four_four", false);
			whiteAllowFourFour = sharedPrefs.getBoolean("white_allow_four_four", true);
			changedPrefs = false;
		}
		if (blackAllowThreeThree != sharedPrefs.getBoolean("black_allow_three_three", false) || whiteAllowThreeThree != sharedPrefs.getBoolean("white_allow_three_three", true) || whiteAllowFourFour != sharedPrefs.getBoolean("white_allow_four_four", true) || blackAllowFourFour != sharedPrefs.getBoolean("black_allow_four_four", false)) {
			changedPrefs = true;
		}
		if (played && changedPrefs) {
			Toast.makeText(this, R.string.changedpref, Toast.LENGTH_LONG).show();
			return;
		}
		if (won) {
			Toast.makeText(this, R.string.won, Toast.LENGTH_SHORT).show();
			return;
		}
		int player;
		if (turn) {
			player = 1;
		} else {
			player = 2;
		}
		if (!going) {
			sp = (Space)v;
			if (sp.getOwner() != 0) {
				return;
			}
			sp.play(player);
			if ((!blackAllowThreeThree && turn) || (!whiteAllowThreeThree && !turn)) {
				if (checkForThreeThree(player)) {
					if (turn) {
						Toast.makeText(this, R.string.blackattoff, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(this, R.string.whiteattoff, Toast.LENGTH_SHORT).show();
					}
					sp.play(0);
					return;
				}
			}
			if ((!blackAllowFourFour && turn) || (!whiteAllowFourFour && !turn)) {
				if (checkForFourFour(player)) {
					if (turn) {
						Toast.makeText(this, R.string.blackaffoff, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(this, R.string.whiteaffoff, Toast.LENGTH_SHORT).show();
					}
					sp.play(0);
					return;
				}
			}
			tv.setText(R.string.sure);
			slot = (LinearLayout)findViewById(R.id.slot);
			slot.addView(yes);
			slot.addView(no);
			going = true;
		}
		if (v == yes) {
			slot.removeAllViews();
			going = false;
			played = true;
			if (checkForWin(player)) {
				won = true;
				if (turn) {
					tv.setText(R.string.blackwin);
					return;
				} else {
					tv.setText(R.string.whitewin);
					return;
				}
			}
			turn = !turn;
			if (turn) {
				tv.setText(R.string.blackturn);
			} else {
				tv.setText(R.string.whiteturn);
				if (useAI) {
					aiPlay();
				}
			}
		} else if (v == no) {
			slot.removeAllViews();
			sp.play(0);
			going = false;
			if (turn) {
				tv.setText(R.string.blackturn);
			} else {
				tv.setText(R.string.whiteturn);
			}
		}
	}
	public boolean checkForWin(int player) {
		int xPos = Integer.parseInt(sp.getTag().toString().substring(0, 2));
		int yPos = Integer.parseInt(sp.getTag().toString().substring(2));
		int count = 0;
		for (int row = 0; row < 19; row++) {
			if (board[row][xPos].getOwner() == player) {
				count++;
			}
			if (count == 5) {
				return true;
			}
			if (board[row][xPos].getOwner() != player) {
				count = 0;
			}
		}
		count = 0;
		for (int col = 0; col < 19; col++) {
			if (board[yPos][col].getOwner() == player) {
				count++;
			}
			if (count == 5) {
				return true;
			}
			if (board[yPos][col].getOwner() != player) {
				count = 0;
			}
		}
		count = 1;
		for (int diag = 1; diag <= 4; diag++) {
			if (xPos  - diag < 0 || yPos  - diag < 0) {
				break;
			}
			if (board[yPos - diag][xPos -diag].getOwner() == player) {
				count++;
			}
			if (count == 5) {
				return true;
			}
			if (board[yPos - diag][xPos - diag].getOwner() != player) {
				break;
			}
		}
		for (int diag = 1; diag <= 4; diag++) {
			if (xPos + diag > 18 || yPos + diag > 18) {
				break;
			}
			if (board[yPos + diag][xPos + diag].getOwner() == player) {
				count++;
			}
			if (count == 5) {
				return true;
			}
			if (board[yPos + diag][xPos + diag].getOwner() != player) {
				break;
			}
		}
		count = 1;
		for (int diag = 1; diag <= 4; diag++) {
			if (yPos + diag > 18 || xPos  - diag < 0) {
				break;
			}
			if (board[yPos + diag][xPos -diag].getOwner() == player) {
				count++;
			}
			if (count == 5) {
				return true;
			}
			if (board[yPos + diag][xPos - diag].getOwner() != player) {
				break;
			}
		}
		for (int diag = 1; diag <= 4; diag++) {
			if (xPos + diag > 18 || yPos - diag < 0) {
				break;
			}
			if (board[yPos - diag][xPos + diag].getOwner() == player) {
				count++;
			}
			if (count == 5) {
				return true;
			}
			if (board[yPos - diag][xPos + diag].getOwner() != player) {
				break;
			}
		}
		return false;
	}
	public boolean checkForThreeThree(int player) {
		boolean horizontal = false;
		boolean vertical = false;
		boolean negDiag = false;
		boolean posDiag = false;
		int threeCount = 0;
		int xPos = Integer.parseInt(sp.getTag().toString().substring(0, 2));
		int yPos = Integer.parseInt(sp.getTag().toString().substring(2));
		if (xPos > 1 && xPos < 17) {
			if (board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos - 2].getOwner() == 0 && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == 0) {
				threeCount++;
				horizontal = true;
			}
		}
		if (!horizontal && xPos > 0 && xPos < 16) {
			if (board[yPos][xPos - 1].getOwner() == 0 && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == player && board[yPos][xPos + 3].getOwner() == 0) {
				threeCount++;
				horizontal = true;
			}
		}
		if (!horizontal && xPos > 2 && xPos < 18) {
			if (board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos - 2].getOwner() == player && board[yPos][xPos + 1].getOwner() == 0 && board[yPos][xPos - 3].getOwner() == 0) {
				threeCount++;
				horizontal = true;
			}
		}
		if (yPos > 1 && yPos < 17) {
			if (board[yPos - 1][xPos].getOwner() == player && board[yPos - 2][xPos].getOwner() == 0 && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == 0) {
				threeCount++;
				vertical = true;
			}
		}
		if (!vertical && yPos > 0 && yPos < 16) {
			if (board[yPos - 1][xPos].getOwner() == 0 && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == player && board[yPos + 3][xPos].getOwner() == 0) {
				threeCount++;
				vertical = true;
			}
		}
		if (!vertical && yPos > 2 && yPos < 18) {
			if (board[yPos - 1][xPos].getOwner() == player && board[yPos - 2][xPos].getOwner() == player && board[yPos + 1][xPos].getOwner() == 0 && board[yPos - 3][xPos].getOwner() == 0) {
				threeCount++;
				vertical = true;
			}
		}
		if (yPos > 1 && yPos < 17 && xPos > 1 && xPos < 17) {
			if (board[yPos - 1][xPos - 1].getOwner() == player && board[yPos - 2][xPos - 2].getOwner() == 0 && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == 0) {
				threeCount++;
				negDiag = true;
			}
		}
		if (!negDiag && yPos > 0 && yPos < 16 && xPos > 0 && xPos < 16) {
			if (board[yPos - 1][xPos - 1].getOwner() == 0 && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == player && board[yPos + 3][xPos + 3].getOwner() == 0) {
				threeCount++;
				negDiag = true;
			}
		}
		if (!negDiag && yPos > 2 && yPos < 18  && xPos > 2 && xPos < 18) {
			if (board[yPos - 1][xPos - 1].getOwner() == player && board[yPos - 2][xPos - 2].getOwner() == player && board[yPos + 1][xPos + 1].getOwner() == 0 && board[yPos - 3][xPos - 3].getOwner() == 0) {
				threeCount++;
				negDiag = true;
			}
		}
		if (yPos > 1 && yPos < 17 && xPos > 1 && xPos < 17) {
			if (board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == 0 && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos + 2][xPos - 2].getOwner() == 0) {
				threeCount++;
				posDiag = true;
			}
		}
		if (!posDiag && yPos > 0 && yPos < 16 && xPos > 2 && xPos < 18) {
			if (board[yPos - 1][xPos + 1].getOwner() == 0 && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos + 2][xPos - 2].getOwner() == player && board[yPos + 3][xPos - 3].getOwner() == 0) {
				threeCount++;
				posDiag = true;
			}
		}
		if (!posDiag && yPos > 2 && yPos < 18  && xPos > 0 && xPos < 16) {
			if (board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == player && board[yPos + 1][xPos - 1].getOwner() == 0 && board[yPos - 3][xPos + 3].getOwner() == 0) {
				threeCount++;
				posDiag = true;
			}
		}
		return threeCount >= 2;
	}
	public boolean checkForFourFour(int player) { // NOT DONE
		boolean horizontal = false;
		boolean vertical = false;
		boolean negDiag = false;
		boolean posDiag = false;
		int fourCount = 0;
		int xPos = Integer.parseInt(sp.getTag().toString().substring(0, 2));
		int yPos = Integer.parseInt(sp.getTag().toString().substring(2));
		if (xPos > 0 && xPos < 15) {
			if ((board[yPos][xPos - 1].getOwner() == 0 || board[yPos][xPos + 4].getOwner() == 0 ) && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == player && board[yPos][xPos + 3].getOwner() == player) {
				horizontal = true;
				fourCount++;
			}
		}
		if (!horizontal && xPos > 1 && xPos < 16) {
			if ((board[yPos][xPos - 2].getOwner() == 0 || board[yPos][xPos + 3].getOwner() == 0) && board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == player) {
				horizontal = true;
				fourCount++;
			}
		}
		if (!horizontal && xPos > 2 && xPos < 17) {
			if ((board[yPos][xPos - 3].getOwner() == 0 || board[yPos][xPos + 2].getOwner() == 0) && board[yPos][xPos - 2].getOwner() == player && board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos + 1].getOwner() == player) {
				horizontal = true;
				fourCount++;
			}
			
		}
		if (!horizontal && xPos > 3 && xPos < 18) {
			if ((board[yPos][xPos - 4].getOwner() == 0 || board[yPos][xPos + 1].getOwner() == 0) && board[yPos][xPos - 3].getOwner() == player && board[yPos][xPos - 2].getOwner() == player && board[yPos][xPos - 1].getOwner() == player) {
				horizontal = true;
				fourCount++;
			}
		}
		if (yPos > 0 && yPos < 15) {
			if ((board[yPos - 1][xPos].getOwner() == 0 || board[yPos + 4][xPos].getOwner() == 0) && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == player && board[yPos + 3][xPos].getOwner() == player) {
				vertical = true;
				fourCount++;
			}
		}
		if (!vertical && yPos > 1 && yPos < 16) {
			if ((board[yPos - 2][xPos].getOwner() == 0 || board[yPos + 3][xPos].getOwner() == 0) && board[yPos - 1][xPos].getOwner() == player && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == player) {
				vertical = true;
				fourCount++;
			}
		}
		if (!vertical && yPos > 2 && yPos < 17) {
			if ((board[yPos - 3][xPos].getOwner() == 0 || board[yPos + 2][xPos].getOwner() == 0) && board[yPos - 2][xPos].getOwner() == player && board[yPos - 1][xPos].getOwner() == player && board[yPos + 1][xPos].getOwner() == player) {
				vertical = true;
				fourCount++;
			}
			
		}
		if (!vertical && yPos > 3 && yPos < 18) {
			if ((board[yPos - 4][xPos].getOwner() == 0 || board[yPos + 1][xPos].getOwner() == 0) && board[yPos - 3][xPos].getOwner() == player && board[yPos - 2][xPos].getOwner() == player && board[yPos - 1][xPos].getOwner() == player) {
				vertical = true;
				fourCount++;
			}
		}
		if (xPos > 0 && xPos < 15 && yPos > 0 && yPos < 15) {
			if ((board[yPos - 1][xPos - 1].getOwner() == 0 || board[yPos + 4][xPos + 4].getOwner() == 0) && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == player && board[yPos + 3][xPos + 3].getOwner() == player) {
				posDiag = true;
				fourCount++;
			}
		}
		if (!posDiag && xPos > 1 && xPos < 16 && yPos > 1 && yPos < 16) {
			if ((board[yPos - 2][xPos - 2].getOwner() == 0 || board[yPos + 3][xPos + 3].getOwner() == 0) && board[yPos - 1][xPos - 1].getOwner() == player && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == player) {
				posDiag = true;
				fourCount++;
			}
		}
		if (!posDiag && xPos > 2 && xPos < 17 && yPos > 2 && yPos < 17) {
			if ((board[yPos - 3][xPos - 3].getOwner() == 0 || board[yPos + 2][xPos + 2].getOwner() == 0) && board[yPos - 2][xPos - 2].getOwner() == player && board[yPos - 1][xPos - 1].getOwner() == player && board[yPos + 1][xPos + 1].getOwner() == player) {
				posDiag = true;
				fourCount++;
			}
			
		}
		if (!posDiag && xPos > 3 && xPos < 18 && yPos > 3 && yPos < 18) {
			if ((board[yPos - 4][xPos - 4].getOwner() == 0 || board[yPos + 1][xPos + 1].getOwner() == 0) && board[yPos - 3][xPos - 3].getOwner() == player && board[yPos - 2][xPos - 2].getOwner() == player && board[yPos - 1][xPos - 1].getOwner() == player) {
				posDiag = true;
				fourCount++;
			}
		}
		if (xPos > 0 && xPos < 15 && yPos > 3 && yPos < 18) {
			if ((board[yPos + 1][xPos - 1].getOwner() == 0 || board[yPos - 4][xPos + 4].getOwner() == 0) && board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == player && board[yPos - 3][xPos + 3].getOwner() == player) {
				negDiag = true;
				fourCount++;
			}
		}
		if (!negDiag && xPos > 1 && xPos < 16 && yPos > 2 && yPos < 17) {
			if ((board[yPos + 2][xPos - 2].getOwner() == 0 || board[yPos - 3][xPos + 3].getOwner() == 0) && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == player) {
				negDiag = true;
				fourCount++;
			}
		}
		if (!negDiag && xPos > 2 && xPos < 17 && yPos > 1 && yPos < 16) {
			if ((board[yPos + 3][xPos - 3].getOwner() == 0 || board[yPos - 2][xPos + 2].getOwner() == 0) && board[yPos + 2][xPos - 2].getOwner() == player && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos - 1][xPos + 1].getOwner() == player) {
				negDiag = true;
				fourCount++;
			}
			
		}
		if (!negDiag && xPos > 3 && xPos < 18 && yPos > 0 && yPos < 15) {
			if ((board[yPos + 4][xPos - 4].getOwner() == 0 || board[yPos - 1][xPos + 1].getOwner() == 0) && board[yPos + 3][xPos - 3].getOwner() == player && board[yPos + 2][xPos - 2].getOwner() == player && board[yPos + 1][xPos - 1].getOwner() == player) {
				negDiag = true;
				fourCount++;
			}
		}
		return fourCount >= 2;
	}
	public void aiPlay() {
		Space lastPlayed = sp;
		for (int row = 0; row < 19; row++) { //if it's played, don't go there
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 1 || board[row][col].getOwner() == 2) {
					board[row][col].setScore(Integer.MIN_VALUE);
				}
			}
		}
		for (int row = 0; row < 19; row++) { //if it makes a 3-3 and we can't, don't go there
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					sp = board[row][col];
					board[row][col].play(2);
					if (!whiteAllowThreeThree && checkForThreeThree(2)) {
						board[row][col].setScore(Integer.MIN_VALUE);
					}
					board[row][col].play(0);
				}
			}
		}
		for (int row = 0; row < 19; row++) { //if it makes a 4-4 and we can't, don't go there
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					sp = board[row][col];
					board[row][col].play(2);
					if (!whiteAllowFourFour && checkForFourFour(2)) {
						board[row][col].setScore(Integer.MIN_VALUE);
					}
					board[row][col].play(0);
				}
			}
		}
		for (int row = 0; row < 19; row++) { //if it let's us win, play there
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					sp = board[row][col];
					board[row][col].play(2);
					if (checkForWin(2)) {
						board[row][col].play(0);
						onClick(board[row][col]);
						onClick(yes);
						return;
					}
					board[row][col].play(0);
				}
			}
		}
		for (int row = 0; row < 19; row++) { //if it prevents an immediate loss, play there
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					sp = board[row][col];
					board[row][col].play(2);
					if (checkForBlockingWin(2, row, col)) {
						board[row][col].play(0);
						onClick(board[row][col]);
						onClick(yes);
						return;
					}
					board[row][col].play(0);
				}
			}
		}
		for (int row = 0; row < 19; row++) { //if it makes a four-three, play there
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					sp = board[row][col];
					board[row][col].play(2);
					if (checkForFourThree(2, row, col)) {
						board[row][col].play(0);
						onClick(board[row][col]);
						onClick(yes);
						return;
					}
					board[row][col].play(0);
				}
			}
		}
		for (int row = 0; row < 19; row++) { //if it makes an open four, play there
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					sp = board[row][col];
					board[row][col].play(2);
					if (checkForOpenFour(2, row, col)) {
						board[row][col].play(0);
						onClick(board[row][col]);
						onClick(yes);
						return;
					}
					board[row][col].play(0);
				}
			}
		}
		for (int row = 0; row < 19; row++) { //if it blocks possible 4-3, go there
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					sp = board[row][col];
					board[row][col].play(2);
					if (checkForBlockingFourThree(2, row, col)) {
						board[row][col].play(0);
						onClick(board[row][col]);
						onClick(yes);
						return;
					}
					board[row][col].play(0);
				}
			}
		}
		for (int row = 0; row < 19; row++) { //if it blocks an existing 3, go there
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					sp = board[row][col];
					board[row][col].play(2);
					if (checkForBlockingThree(2, row, col)) {
						board[row][col].play(0);
						onClick(board[row][col]);
						onClick(yes);
						return;
					}
					board[row][col].play(0);
				}
			}
		}
		for (int row = 0; row < 19; row++) { //setting the scores...
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					board[row][col].play(2);
					if (checkForClosedFour(2, row, col)) {
						board[row][col].addToScore(MAKES_CLOSED_FOUR_FACTOR);
					}
					if (checkForThree(2, row, col)) {
						board[row][col].addToScore(MAKES_THREE_FACTOR);
					}
					board[row][col].addToScore((int)(CLOSE_BY_FACTOR / avgSpacesAway(lastPlayed, row, col)));
					board[row][col].play(0);
				}
			}
		}
		int maxScore = -1;
		Space playHere = null;
		for (int row = 0; row < 19; row++) {//figuring out the best score
			for (int col = 0; col < 19; col++) {
				if (board[row][col].getOwner() == 0) {
					if (board[row][col].getScore() > maxScore) {
						playHere = board[row][col];
						maxScore = playHere.getScore();
					}
				}
			}
		}
		onClick(playHere);//playing at the best score
		onClick(yes);
	}
	public boolean checkForBlockingFourThree(int player, int row, int col) {
		int antiPlayer;
		if (player == 2) {
			antiPlayer = 1;
		} else {
			antiPlayer = 2;
		}
		board[row][col].play(antiPlayer);
		if (checkForFourThree(antiPlayer, row, col)) {
			board[row][col].play(player);
			return true;
		}
		board[row][col].play(player);
		return false;
	}
	public boolean checkForThree(int player, int row, int col) {
		int xPos = col;
		int yPos = row;
		if (xPos > 1 && xPos < 17) {
			if (board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos - 2].getOwner() == 0 && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 0 && xPos < 16) {
			if (board[yPos][xPos - 1].getOwner() == 0 && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == player && board[yPos][xPos + 3].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 2 && xPos < 18) {
			if (board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos - 2].getOwner() == player && board[yPos][xPos + 1].getOwner() == 0 && board[yPos][xPos - 3].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 1 && yPos < 17) {
			if (board[yPos - 1][xPos].getOwner() == player && board[yPos - 2][xPos].getOwner() == 0 && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 0 && yPos < 16) {
			if (board[yPos - 1][xPos].getOwner() == 0 && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == player && board[yPos + 3][xPos].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 2 && yPos < 18) {
			if (board[yPos - 1][xPos].getOwner() == player && board[yPos - 2][xPos].getOwner() == player && board[yPos + 1][xPos].getOwner() == 0 && board[yPos - 3][xPos].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 1 && yPos < 17 && xPos > 1 && xPos < 17) {
			if (board[yPos - 1][xPos - 1].getOwner() == player && board[yPos - 2][xPos - 2].getOwner() == 0 && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 0 && yPos < 16 && xPos > 0 && xPos < 16) {
			if (board[yPos - 1][xPos - 1].getOwner() == 0 && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == player && board[yPos + 3][xPos + 3].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 2 && yPos < 18  && xPos > 2 && xPos < 18) {
			if (board[yPos - 1][xPos - 1].getOwner() == player && board[yPos - 2][xPos - 2].getOwner() == player && board[yPos + 1][xPos + 1].getOwner() == 0 && board[yPos - 3][xPos - 3].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 1 && yPos < 17 && xPos > 1 && xPos < 17) {
			if (board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == 0 && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos + 2][xPos - 2].getOwner() == 0) {
				return true;
			}
		}
		if ( yPos > 0 && yPos < 16 && xPos > 2 && xPos < 18) {
			if (board[yPos - 1][xPos + 1].getOwner() == 0 && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos + 2][xPos - 2].getOwner() == player && board[yPos + 3][xPos - 3].getOwner() == 0) {
				return true;
			}
		}
		if ( yPos > 2 && yPos < 18  && xPos > 0 && xPos < 16) {
			if (board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == player && board[yPos + 1][xPos - 1].getOwner() == 0 && board[yPos - 3][xPos + 3].getOwner() == 0) {
				return true;
			}
		}
		return false;
	}
	public boolean checkForOpenFour(int player, int row, int col) {
		int xPos = col;
		int yPos = row;
		if (xPos > 0 && xPos < 15) {
			if (board[yPos][xPos - 1].getOwner() == 0 && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == player && board[yPos][xPos + 3].getOwner() == player && board[yPos][xPos + 4].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 1 && xPos < 16) {
			if (board[yPos][xPos - 2].getOwner() == 0 && board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == player && board[yPos][xPos + 3].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 2 && xPos < 17) {
			if (board[yPos][xPos - 3].getOwner() == 0 && board[yPos][xPos - 2].getOwner() == player && board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == 0) {
				return true;
			}
			
		}
		if (xPos > 3 && xPos < 18) {
			if (board[yPos][xPos - 4].getOwner() == 0 && board[yPos][xPos - 3].getOwner() == player && board[yPos][xPos - 2].getOwner() == player && board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos + 1].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 0 && yPos < 15) {
			if (board[yPos - 1][xPos].getOwner() == 0 && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == player && board[yPos + 3][xPos].getOwner() == player && board[yPos + 4][xPos].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 1 && yPos < 16) {
			if (board[yPos - 2][xPos].getOwner() == 0 && board[yPos - 1][xPos].getOwner() == player && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == player && board[yPos + 3][xPos].getOwner() == 0) {
				return true;
			}
		}
		if (yPos > 2 && yPos < 17) {
			if (board[yPos - 3][xPos].getOwner() == 0 && board[yPos - 2][xPos].getOwner() == player && board[yPos - 1][xPos].getOwner() == player && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == 0) {
				return true;
			}
			
		}
		if (yPos > 3 && yPos < 18) {
			if (board[yPos - 4][xPos].getOwner() == 0 && board[yPos - 3][xPos].getOwner() == player && board[yPos - 2][xPos].getOwner() == player && board[yPos - 1][xPos].getOwner() == player && board[yPos + 1][xPos].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 0 && xPos < 15 && yPos > 0 && yPos < 15) {
			if (board[yPos - 1][xPos - 1].getOwner() == 0 && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == player && board[yPos + 3][xPos + 3].getOwner() == player && board[yPos + 4][xPos + 4].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 1 && xPos < 16 && yPos > 1 && yPos < 16) {
			if (board[yPos - 2][xPos - 2].getOwner() == 0 && board[yPos - 1][xPos - 1].getOwner() == player && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == player && board[yPos + 3][xPos + 3].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 2 && xPos < 17 && yPos > 2 && yPos < 17) {
			if (board[yPos - 3][xPos - 3].getOwner() == 0 && board[yPos - 2][xPos - 2].getOwner() == player && board[yPos - 1][xPos - 1].getOwner() == player && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == 0) {
				return true;
			}
			
		}
		if (xPos > 3 && xPos < 18 && yPos > 3 && yPos < 18) {
			if (board[yPos - 4][xPos - 4].getOwner() == 0 && board[yPos - 3][xPos - 3].getOwner() == player && board[yPos - 2][xPos - 2].getOwner() == player && board[yPos - 1][xPos - 1].getOwner() == player && board[yPos + 1][xPos + 1].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 0 && xPos < 15 && yPos > 3 && yPos < 18) {
			if (board[yPos + 1][xPos - 1].getOwner() == 0 && board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == player && board[yPos - 3][xPos + 3].getOwner() == player && board[yPos - 4][xPos + 4].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 1 && xPos < 16 && yPos > 2 && yPos < 17) {
			if (board[yPos + 2][xPos - 2].getOwner() == 0 && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == player && board[yPos - 3][xPos + 3].getOwner() == 0) {
				return true;
			}
		}
		if (xPos > 2 && xPos < 17 && yPos > 1 && yPos < 16) {
			if (board[yPos + 3][xPos - 3].getOwner() == 0 && board[yPos + 2][xPos - 2].getOwner() == player && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == 0) {
				return true;
			}
			
		}
		if (xPos > 3 && xPos < 18 && yPos > 0 && yPos < 15) {
			if (board[yPos + 4][xPos - 4].getOwner() == 0 && board[yPos + 3][xPos - 3].getOwner() == player && board[yPos + 2][xPos - 2].getOwner() == player && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos - 1][xPos + 1].getOwner() == 0) {
				return true;
			}
		}
		return false;
	}
	public boolean checkForFourThree(int player, int row, int col) {
		return (checkForOpenFour(player, row, col) || checkForClosedFour(player, row, col)) && checkForThree(player, row, col);
	}
	public boolean checkForClosedFour(int player, int row, int col) {
		int xPos = col;
		int yPos = row;
		if (xPos > 0 && xPos < 15) {
			if ((board[yPos][xPos - 1].getOwner() == 0 ^ board[yPos][xPos + 4].getOwner() == 0 ) && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == player && board[yPos][xPos + 3].getOwner() == player) {
				return true;
			}
		}
		if (xPos > 1 && xPos < 16) {
			if ((board[yPos][xPos - 2].getOwner() == 0 ^ board[yPos][xPos + 3].getOwner() == 0) && board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos + 1].getOwner() == player && board[yPos][xPos + 2].getOwner() == player) {
				return true;
			}
		}
		if (xPos > 2 && xPos < 17) {
			if ((board[yPos][xPos - 3].getOwner() == 0 ^ board[yPos][xPos + 2].getOwner() == 0) && board[yPos][xPos - 2].getOwner() == player && board[yPos][xPos - 1].getOwner() == player && board[yPos][xPos + 1].getOwner() == player) {
				return true;
			}
			
		}
		if (xPos > 3 && xPos < 18) {
			if ((board[yPos][xPos - 4].getOwner() == 0 ^ board[yPos][xPos + 1].getOwner() == 0) && board[yPos][xPos - 3].getOwner() == player && board[yPos][xPos - 2].getOwner() == player && board[yPos][xPos - 1].getOwner() == player) {
				return true;
			}
		}
		if (yPos > 0 && yPos < 15) {
			if ((board[yPos - 1][xPos].getOwner() == 0 ^ board[yPos + 4][xPos].getOwner() == 0) && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == player && board[yPos + 3][xPos].getOwner() == player) {
				return true;
			}
		}
		if (yPos > 1 && yPos < 16) {
			if ((board[yPos - 2][xPos].getOwner() == 0 ^ board[yPos + 3][xPos].getOwner() == 0) && board[yPos - 1][xPos].getOwner() == player && board[yPos + 1][xPos].getOwner() == player && board[yPos + 2][xPos].getOwner() == player) {
				return true;
			}
		}
		if (yPos > 2 && yPos < 17) {
			if ((board[yPos - 3][xPos].getOwner() == 0 ^ board[yPos + 2][xPos].getOwner() == 0) && board[yPos - 2][xPos].getOwner() == player && board[yPos - 1][xPos].getOwner() == player && board[yPos + 1][xPos].getOwner() == player) {
				return true;
			}
			
		}
		if (yPos > 3 && yPos < 18) {
			if ((board[yPos - 4][xPos].getOwner() == 0 ^ board[yPos + 1][xPos].getOwner() == 0) && board[yPos - 3][xPos].getOwner() == player && board[yPos - 2][xPos].getOwner() == player && board[yPos - 1][xPos].getOwner() == player) {
				return true;
			}
		}
		if (xPos > 0 && xPos < 15 && yPos > 0 && yPos < 15) {
			if ((board[yPos - 1][xPos - 1].getOwner() == 0 ^ board[yPos + 4][xPos + 4].getOwner() == 0) && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == player && board[yPos + 3][xPos + 3].getOwner() == player) {
				return true;
			}
		}
		if (xPos > 1 && xPos < 16 && yPos > 1 && yPos < 16) {
			if ((board[yPos - 2][xPos - 2].getOwner() == 0 ^ board[yPos + 3][xPos + 3].getOwner() == 0) && board[yPos - 1][xPos - 1].getOwner() == player && board[yPos + 1][xPos + 1].getOwner() == player && board[yPos + 2][xPos + 2].getOwner() == player) {
				return true;
			}
		}
		if (xPos > 2 && xPos < 17 && yPos > 2 && yPos < 17) {
			if ((board[yPos - 3][xPos - 3].getOwner() == 0 ^ board[yPos + 2][xPos + 2].getOwner() == 0) && board[yPos - 2][xPos - 2].getOwner() == player && board[yPos - 1][xPos - 1].getOwner() == player && board[yPos + 1][xPos + 1].getOwner() == player) {
				return true;
			}
			
		}
		if (xPos > 3 && xPos < 18 && yPos > 3 && yPos < 18) {
			if ((board[yPos - 4][xPos - 4].getOwner() == 0 ^ board[yPos + 1][xPos + 1].getOwner() == 0) && board[yPos - 3][xPos - 3].getOwner() == player && board[yPos - 2][xPos - 2].getOwner() == player && board[yPos - 1][xPos - 1].getOwner() == player) {
				return true;
			}
		}
		if (xPos > 0 && xPos < 15 && yPos > 3 && yPos < 18) {
			if ((board[yPos + 1][xPos - 1].getOwner() == 0 ^ board[yPos - 4][xPos + 4].getOwner() == 0) && board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == player && board[yPos - 3][xPos + 3].getOwner() == player) {
				return true;
			}
		}
		if (xPos > 1 && xPos < 16 && yPos > 2 && yPos < 17) {
			if ((board[yPos + 2][xPos - 2].getOwner() == 0 ^ board[yPos - 3][xPos + 3].getOwner() == 0) && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos - 1][xPos + 1].getOwner() == player && board[yPos - 2][xPos + 2].getOwner() == player) {
				return true;
			}
		}
		if (xPos > 2 && xPos < 17 && yPos > 1 && yPos < 16) {
			if ((board[yPos + 3][xPos - 3].getOwner() == 0 ^ board[yPos - 2][xPos + 2].getOwner() == 0) && board[yPos + 2][xPos - 2].getOwner() == player && board[yPos + 1][xPos - 1].getOwner() == player && board[yPos - 1][xPos + 1].getOwner() == player) {
				return true;
			}
			
		}
		if (xPos > 3 && xPos < 18 && yPos > 0 && yPos < 15) {
			if ((board[yPos + 4][xPos - 4].getOwner() == 0 ^ board[yPos - 1][xPos + 1].getOwner() == 0) && board[yPos + 3][xPos - 3].getOwner() == player && board[yPos + 2][xPos - 2].getOwner() == player && board[yPos + 1][xPos - 1].getOwner() == player) {
				return true;
			}
		}
		return false;
	}
	public boolean checkForBlockingWin(int player, int row, int col) {
		int antiPlayer;
		if (player == 2) {
			antiPlayer = 1;
		} else {
			antiPlayer = 2;
		}
		board[row][col].play(antiPlayer);
		sp = board[row][col];
		if (checkForWin(antiPlayer)) {
			board[row][col].play(player);
			return true;
		}
		board[row][col].play(player);
		return false;
	}
	public boolean checkForBlockingThree(int player, int row, int col) {
		int antiPlayer;
		if (player == 2) {
			antiPlayer = 1;
		} else {
			antiPlayer = 2;
		}
		board[row][col].play(antiPlayer);
		if (checkForOpenFour(antiPlayer, row, col)) {
			board[row][col].play(player);
			return true;
		}
		board[row][col].play(player);
		return false;
	}
	public double avgSpacesAway(Space lastPlayed, int row, int col) {
		int xPos = Integer.parseInt(lastPlayed.getTag().toString().substring(0, 2));
		int yPos = Integer.parseInt(lastPlayed.getTag().toString().substring(2));
		return Math.sqrt(Math.pow((Math.abs(xPos - col)), 2) + Math.pow(Math.abs(yPos - row), 2));
	}
}