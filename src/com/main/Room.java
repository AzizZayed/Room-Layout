package com.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * The room itself, contains the components of the room
 * 
 * @author Zayed
 *
 */
public class Room {

	private static final float ANGLE_STEP = (float) Math.toRadians(5.0f);

	private int width, height; // width and height of the room in pixels
	private float scale = 5.0f; // scale from real world to pixels

	private boolean checkForFurnitureCollisions = false; // check for collisions between furniture
	private boolean checkForBorderCollisions = false; // check for collisions with walls

	private boolean doorOpen = false; // door is open or closed

	// all furniture components
	private Rectangle closedDoor, openDoor, window, closetDoors;
	private ArrayList<Polygon> initialState, mobileFurniture;
	private ArrayList<Color> furnitureColors;
	private ArrayList<Float> rotationAngles;

	private int furnitureSelected = -1; // selected piece of furniture

	private int px, py; // previous mouse positions

	/**
	 * constructor
	 * 
	 * @param w - width of room in inches
	 * @param h - height of room in inches
	 */
	public Room(int w, int h) {
		// create dimensions from real world values (inches) to pixels and scale it
		width = (int) Math.ceil(w * scale);
		height = (int) Math.ceil(h * scale);

		initialState = new ArrayList<Polygon>();
		mobileFurniture = new ArrayList<Polygon>();
		furnitureColors = new ArrayList<Color>();
		rotationAngles = new ArrayList<Float>();
	}

	/**
	 * @return the width in pixels
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height in pixels
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * add a piece of furniture
	 * 
	 * @param xpoints - x coordinates of a vertex of furniture
	 * @param ypoints - y coordinates of a vertex of furniture
	 */
	public void addFurniture(float[] xpoints, float[] ypoints) {

		int[] x = new int[xpoints.length];
		int[] y = new int[ypoints.length];

		for (int i = 0; i < xpoints.length; i++) {
			x[i] = (int) Math.ceil(xpoints[i] * scale);
		}
		for (int i = 0; i < ypoints.length; i++) {
			y[i] = (int) Math.ceil(ypoints[i] * scale);
		}

		initialState.add(new Polygon(x, y, xpoints.length));
		mobileFurniture.add(new Polygon(x, y, xpoints.length));
		furnitureColors.add(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
		rotationAngles.add(0.0f);
	}

	/**
	 * add a closed door component
	 * 
	 * @param dx - real world x coordinate
	 * @param dy - real world y coordinate
	 * @param dw - real world width
	 * @param dh - real world height
	 */
	public void addClosedDoor(float dx, float dy, float dw, float dh) {
		int x, y, w, h;

		x = (int) Math.ceil(dx * scale);
		y = (int) Math.ceil(dy * scale);
		w = (int) Math.ceil(dw * scale);
		h = (int) Math.ceil(dh * scale);

		closedDoor = new Rectangle(x, y, w, h);
	}

	/**
	 * add a open door component
	 * 
	 * @param dx - real world x coordinate
	 * @param dy - real world y coordinate
	 * @param dw - real world width
	 * @param dh - real world height
	 */
	public void addOpenDoor(float dx, float dy, float dw, float dh) {
		int x, y, w, h;

		x = (int) Math.ceil(dx * scale);
		y = (int) Math.ceil(dy * scale);
		w = (int) Math.ceil(dw * scale);
		h = (int) Math.ceil(dh * scale);

		openDoor = new Rectangle(x, y, w, h);
	}

	/**
	 * add closet doors component
	 * 
	 * @param dx - real world x coordinate
	 * @param dy - real world y coordinate
	 * @param dw - real world width
	 * @param dh - real world height
	 */
	public void addClosetDoors(float dx, float dy, float dw, float dh) {
		int x, y, w, h;

		x = (int) Math.ceil(dx * scale);
		y = (int) Math.ceil(dy * scale);
		w = (int) Math.ceil(dw * scale);
		h = (int) Math.ceil(dh * scale);

		closetDoors = new Rectangle(x, y, w, h); // closet doors when open
	}

	/**
	 * add a window component
	 * 
	 * @param dx - real world x coordinate
	 * @param dy - real world y coordinate
	 * @param dw - real world width
	 * @param dh - real world height
	 */
	public void addWindow(float dx, float dy, float dw, float dh) {
		int x, y, w, h;

		x = (int) Math.ceil(dx * scale);
		y = (int) Math.ceil(dy * scale);
		w = (int) Math.ceil(dw * scale);
		h = (int) Math.ceil(dh * scale);

		window = new Rectangle(x, y, w, h);
	}

	/**
	 * if the room board is pressed, used to see which piece of furniture is pressed
	 * 
	 * @param x - mouse x position
	 * @param y - mouse y position
	 */
	public void press(int x, int y) {

		boolean initDoor = doorOpen;

		if (doorOpen) {
			doorOpen = !openDoor.contains(x, y);
		} else {
			doorOpen = closedDoor.contains(x, y);
		}

		if (initDoor != doorOpen)
			return;

		boolean clicked = false;
		int i = mobileFurniture.size() - 1;

		while (i >= 0 && !clicked) {
			Polygon poly = mobileFurniture.get(i);
			if (poly.contains(x, y)) {
				clicked = true;
				furnitureSelected = i;
			}
			i--;
		}

		px = x;
		py = y;
	}

	/**
	 * if the mouse is dragging, check which piece of furniture is being dragged
	 * 
	 * @param x - mouse x position
	 * @param y - mouse y position
	 */
	public void drag(int x, int y) {

		if (furnitureSelected < 0)
			return;

		int dx = x - px;
		int dy = y - py;

		Polygon poly = mobileFurniture.get(furnitureSelected);
		poly.translate(dx, dy);

		boolean furnitureCollided = false;
		if (checkForFurnitureCollisions) {
			int i = 0;

			while (i < mobileFurniture.size() && !furnitureCollided) {
				if (i != furnitureSelected) {
					furnitureCollided = poly.getBounds().intersects(mobileFurniture.get(i).getBounds());
				}
				i++;
			}

			if (furnitureCollided)
				poly.translate(-dx, -dy);
		}

		if (checkForBorderCollisions) {
			Rectangle fullRoom = new Rectangle(0, 0, width, height);
			if (!fullRoom.contains(poly.getBounds()) && !furnitureCollided || poly.intersects(closetDoors.getBounds()))
				poly.translate(-dx, -dy);
		}

		px = x;
		py = y;
	}

	/**
	 * release the currently pressed furniture
	 */
	public void release() {
		furnitureSelected = -1;
	}

	/**
	 * rotate the currently pressed furniture
	 * 
	 * @param angle - rotating angle
	 */
	public void rotateSelected(float direction) {

		if (furnitureSelected < 0)
			return;

		float newAngle = rotationAngles.get(furnitureSelected) + direction * ANGLE_STEP;
		float absAngle = Math.abs(newAngle);

		final int n = (int) (Math.PI / ANGLE_STEP) * 2;

		if (absAngle < ANGLE_STEP || (absAngle > ANGLE_STEP * (n - 1) && absAngle < ANGLE_STEP * (n + 1)))
			newAngle = 0.0f;

		rotationAngles.set(furnitureSelected, newAngle);
	}

	/**
	 * reset the room
	 */
	public void reset() {
		for (int i = 0; i < initialState.size(); i++) {
			Polygon poly = initialState.get(i);
			mobileFurniture.set(i, new Polygon(poly.xpoints, poly.ypoints, poly.npoints));
			rotationAngles.set(i, 0.0f);
		}
	}

	/**
	 * draw the room
	 * 
	 * @param g2d - tool to draw 2D AWT Objects
	 */
	public void draw(Graphics2D g2d) {

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		AffineTransform old = g2d.getTransform();

		// draw furniture
		for (int i = 0; i < mobileFurniture.size(); i++) {
			Polygon poly = mobileFurniture.get(i);
			g2d.rotate(rotationAngles.get(i), poly.getBounds().getCenterX(), poly.getBounds().getCenterY());

			g2d.setColor(furnitureColors.get(i));
			g2d.fill(poly);

			g2d.setTransform(old);
		}

		g2d.setColor(Color.BLUE);
		g2d.fill(window); // draw window

		// draw correct door
		g2d.setColor(Color.GREEN);
		if (doorOpen)
			g2d.fill(openDoor);
		else
			g2d.fill(closedDoor);

		// illustrate open closet doors
		g2d.setColor(Color.DARK_GRAY);
		g2d.fill(closetDoors);
	}
}