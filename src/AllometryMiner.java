import java.awt.Graphics;

import com.quirlion.script.Script;
import com.quirlion.script.types.Area;
import com.quirlion.script.types.Location;

public class AllometryMiner extends Script {
	//private int bankBoothID = 2213;
	//private int[] ironIDs = { 37307, 37308, 37309 };
	
	private Location[] yanilleMinePath = {
		new Location(2611, 3092),
		new Location(2607, 3099),
		new Location(2615, 3104),
		new Location(2620, 3116),
		new Location(2623, 3133),
		new Location(2626, 3146)
	};
	
	private Area yanilleBank = new Area(new Location(2613, 3097), new Location(2609, 3088));
	private Area yanilleMine = new Area(new Location(2627, 3149), new Location(2623, 3144));
	
	private boolean debugScript = true;
	
	public void onStart() {
		
	}
	
	public int loop() {
		if(debugScript) {
			if(inventory.isFull()) {
				walker.walkPathMM(Location.reversePath(yanilleMinePath));
				log("Walking to bank...");
				while(players.getCurrent().isMoving()) {}
			} else {
				walker.walkPathMM(yanilleMinePath);
				log("Walking to mine...");
				while(players.getCurrent().isMoving()) {}
			}
			
			return random(1, 300);
		}
		
		if(players.getCurrent().isInArea(yanilleBank) && inventory.isFull()) {
			//TODO Player activates bank
		}
		
		if(players.getCurrent().isInArea(yanilleMine) && !inventory.isFull()) {
			//TODO Player begins to mine iron
		}
		
		return 1;
	}
	
	public void chatMessageReceived(String user, String message) {
		return ;
	}
	
	public void onStop() {
		return ;
	}
	
	public void paint(Graphics g) {
		return ;
	}
}
