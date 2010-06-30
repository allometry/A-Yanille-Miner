import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import javax.imageio.ImageIO;

import com.quirlion.script.Script;
import com.quirlion.script.types.Area;
import com.quirlion.script.types.Location;
import com.quirlion.script.types.Thing;

public class AYanilleMiner extends Script {
	private int bankBoothID = 2213;
	private Image basketImage, rubyImage, clockImage;
	private int[] ironIDs = { 37307, 37308, 37309 };

	private long minedIron = 0, minedGems = 0;

	private long startTime;
	private Area yanilleBank = new Area(new Location(2613, 3097), new Location(2609, 3088));

	private Area yanilleMine = new Area(new Location(2629, 3153), new Location(2623, 3145));

	private Location[] yanilleMinePath = { new Location(2611, 3092),
			new Location(2607, 3099), new Location(2615, 3104),
			new Location(2620, 3116), new Location(2623, 3133),
			new Location(2626, 3146), new Location(2626, 3149) };

	@Override
	public int loop() {
		if (players.getCurrent().isInArea(yanilleMine) && !inventory.isFull()) {
			Thing currentRock = things.getNearest(ironIDs);

			Location currentRockLocation = currentRock.getLocation();

			input.moveMouse(currentRock.getAbsLoc().X, currentRock.getAbsLoc().Y);

			if (currentRock.click()) {
				boolean failsafe = false;
				long failsafeTime = System.currentTimeMillis() + 4500;

				try {
					while (!thingIdChanged(ironIDs, things.getAt(currentRockLocation).getID()) && !failsafe) {
						if (failsafeTime <= System.currentTimeMillis())
							failsafe = true;
					}
				} catch (Exception e) {
				}
			}
		}

		if (inventory.isFull() && !players.getCurrent().isInArea(yanilleBank)) {
			walker.walkPathMM(Location.reversePath(yanilleMinePath));

			return random(3000, 4000);
		}

		if (!inventory.isFull() && !players.getCurrent().isInArea(yanilleMine)) {
			walker.walkPathMM(yanilleMinePath);

			return random(3000, 4000);
		}

		if (inventory.isFull() && players.getCurrent().isInArea(yanilleBank)
				&& !bank.isOpen()) {
			Thing bankBooth = things.getNearest(bankBoothID);

			input.moveMouse(bankBooth.getAbsLoc().X, bankBooth.getAbsLoc().Y);
			bankBooth.click("Quickly");

			boolean timeout = false;

			long later = System.currentTimeMillis() + 2500;

			while (!bank.isOpen() && !timeout) {
				if (later <= System.currentTimeMillis())
					timeout = true;
			}

			return random(500, 1000);
		}

		if (inventory.isFull() && bank.isOpen()) {
			bank.depositAll();

			return random(1000, 1500);
		}

		return 1;
	}

	private String millisToClock(long milliseconds) {
		long seconds = (milliseconds / 1000), minutes = 0, hours = 0;

		if (seconds >= 60) {
			minutes = (seconds / 60);
			seconds -= (minutes * 60);
		}

		if (minutes >= 60) {
			hours = (minutes / 60);
			minutes -= (hours * 60);
		}

		return (hours < 10 ? "0" + hours + ":" : hours + ":")
				+ (minutes < 10 ? "0" + minutes + ":" : minutes + ":")
				+ (seconds < 10 ? "0" + seconds : seconds);
	}

	@Override
	public void onStart() {
		startTime = System.currentTimeMillis();

		try {
			basketImage = ImageIO.read(new URL("http://scripts.allometry.com/app/webroot/icons/basket.png"));
			rubyImage = ImageIO.read(new URL("http://scripts.allometry.com/app/webroot/icons/ruby.png"));
			clockImage = ImageIO.read(new URL("http://scripts.allometry.com/app/webroot/icons/clock.png"));
		} catch (IOException e) {
			logStackTrace(e);
		}
	}

	@Override
	public void onStop() {
		return;
	}

	@Override
	public void paint(Graphics g2) {
		if (!players.getCurrent().isLoggedIn()
				|| players.getCurrent().isInLobby())
			return;

		Graphics2D g = (Graphics2D) g2;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Rectangles
		RoundRectangle2D clockBackground = new RoundRectangle2D.Float(interfaces.getMinimap().getRealX() - 144, 20, 89, 26, 5, 5);

		RoundRectangle2D scoreboardBackground = new RoundRectangle2D.Float(20, 20, 89, 47, 5, 5);

		g.setColor(new Color(0, 0, 0, 127));
		g.fill(clockBackground);
		g.fill(scoreboardBackground);

		// Text
		g.setColor(Color.white);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);

		g.drawString(nf.format(minedIron), 48, 39);
		g.drawString(nf.format(minedGems), 48, 58);

		g.drawString(millisToClock(System.currentTimeMillis() - startTime), interfaces.getMinimap().getRealX() - 139, 37);

		// Images
		ImageObserver observer = null;

		g.drawImage(basketImage, 25, 25, observer);
		g.drawImage(rubyImage, 25, 25 + 16 + 4, observer);
		g.drawImage(clockImage, interfaces.getMinimap().getRealX() - 75, 25, observer);

		return;
	}

	@Override
	public void serverMessageReceived(String message) {
		if (message.contains("iron"))
			minedIron++;
		if (message.contains("sapphire"))
			minedGems++;
		if (message.contains("emerald"))
			minedGems++;
		if (message.contains("ruby"))
			minedGems++;
		if (message.contains("diamond"))
			minedGems++;

		return;
	}

	private boolean thingIdChanged(int[] ids, int id) {
		for (int i : ids)
			if (i == id)
				return false;

		return true;
	}
}
