package com.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class Room {
	private int realWidth, realHeight, width, height;
	private float scale = 5.0f;
	private boolean checkForFurnitureCollisions = false;
	private boolean checkForBorderCollisions = false;
	private boolean closetDoorsOpen = true;
	private boolean doorOpen = false;
	private Rectangle closedDoor, openDoor, window, closetDoors;
	private ArrayList<Polygon> initialState, mobileFurniture;
	private ArrayList<Color> furnitureColors;
	private int furnitureSelected = -1;
	private int px, py;

	public Room(int w, int h) {
		realWidth = w;
		realHeight = h;
		width = (int) Math.ceil(w * scale);
		height = (int) Math.ceil(h * scale);

		initialState = new ArrayList<Polygon>();
		mobileFurniture = new ArrayList<Polygon>();
		furnitureColors = new ArrayList<Color>();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getRealWidth() {
		return realWidth;
	}

	public int getRealHeight() {
		return realHeight;
	}

	private Color getRandomColor() {
		Random rand = new Random();

		int r = 0, g = 0, b = 0;
		final int min = 25, max = 256;
		;

		while (r < min) {
			r = rand.nextInt(max);
		}
		while (g < min) {
			g = rand.nextInt(max);
		}
		while (b < min) {
			b = rand.nextInt(max);
		}

		return new Color(r, g, b);
	}

	public void addFurniture(float[] xpoints, float[] ypoints, int npoints) {

		int[] x = new int[npoints];
		int[] y = new int[npoints];

		for (int i = 0; i < xpoints.length; i++) {
			x[i] = (int) Math.ceil(xpoints[i] * scale);
		}
		for (int i = 0; i < ypoints.length; i++) {
			y[i] = (int) Math.ceil(ypoints[i] * scale);
		}

		initialState.add(new Polygon(x, y, npoints));
		mobileFurniture.add(new Polygon(x, y, npoints));
		furnitureColors.add(getRandomColor());
	}

	public void addClosedDoor(float dx, float dy, float dw, float dh) {
		int x, y, w, h;

		x = (int) Math.ceil(dx * scale);
		y = (int) Math.ceil(dy * scale);
		w = (int) Math.ceil(dw * scale);
		h = (int) Math.ceil(dh * scale);

		closedDoor = new Rectangle(x, y, w, h);
	}

	public void addOpenDoor(float dx, float dy, float dw, float dh) {
		int x, y, w, h;

		x = (int) Math.ceil(dx * scale);
		y = (int) Math.ceil(dy * scale);
		w = (int) Math.ceil(dw * scale);
		h = (int) Math.ceil(dh * scale);

		openDoor = new Rectangle(x, y, w, h);
	}

	public void addClosetDoors(float dx, float dy, float dw, float dh) {
		int x, y, w, h;

		x = (int) Math.ceil(dx * scale);
		y = (int) Math.ceil(dy * scale);
		w = (int) Math.ceil(dw * scale);
		h = (int) Math.ceil(dh * scale);

		closetDoors = new Rectangle(x, y, w, h); // closet doors when open
	}

	public void addWindow(float dx, float dy, float dw, float dh) {
		int x, y, w, h;

		x = (int) Math.ceil(dx * scale);
		y = (int) Math.ceil(dy * scale);
		w = (int) Math.ceil(dw * scale);
		h = (int) Math.ceil(dh * scale);

		window = new Rectangle(x, y, w, h);
	}

	public void press(int x, int y) {

		if (doorOpen)
			doorOpen = !openDoor.contains(x, y);
		else
			doorOpen = closedDoor.contains(x, y);

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

	public void drag(int x, int y) {

		if (furnitureSelected < 0)
			return;

		int dx = x - px;
		int dy = y - py;

		Polygon poly = mobileFurniture.get(furnitureSelected);
		poly.translate(dx, dy);

		boolean furnitureColision = false;
		if (checkForFurnitureCollisions) {
			int i = 0;

			while (i < mobileFurniture.size() && !furnitureColision) {
				if (i != furnitureSelected) {
					furnitureColision = poly.getBounds().intersects(mobileFurniture.get(i).getBounds());
				}
				i++;
			}
		}

		if (checkForBorderCollisions) {
			Rectangle fullRoom = new Rectangle(0, 0, width, height);
			if (!fullRoom.contains(poly.getBounds()) || furnitureColision || poly.intersects(closetDoors.getBounds())) {
				poly.translate(-dx, -dy);
			}
		}

		px = x;
		py = y;

	}

	public void release() {
		furnitureSelected = -1;
	}

	public void rotateSelected(double angle) {

		if (furnitureSelected < 0)
			return;

		mobileFurniture.set(furnitureSelected, rotatePolygon(furnitureSelected, angle));
	}

	private Polygon rotatePolygon(int index, double angle) {

		Polygon polygon = mobileFurniture.get(index);
		Point.Double center = new Point.Double(polygon.getBounds().getCenterX(), polygon.getBounds().getCenterY());
		Polygon newPolygon = new Polygon();

		double sin = Math.sin(angle);
		double cos = Math.cos(angle);

		for (int i = 0; i < polygon.npoints; i++) {
			Point p = new Point(polygon.xpoints[i], polygon.ypoints[i]);

			double x = p.x * cos - p.y * sin + center.x * (1 - cos) + center.y * sin;
			double y = p.x * sin + p.y * cos + center.y * (1 - cos) - center.x * sin;

			Point.Double point = new Point.Double(x, y);

			newPolygon.addPoint((int) Math.ceil(point.x), (int) Math.ceil(point.y));
		}

		return newPolygon;
	}

	public void reset() {
		mobileFurniture.clear();
		for (int i = 0; i < initialState.size(); i++) {
			Polygon poly = initialState.get(i);
			mobileFurniture.add(new Polygon(poly.xpoints, poly.ypoints, poly.npoints));
		}
	}

	public void draw(Graphics2D g2d) {

		for (int i = 0; i < mobileFurniture.size(); i++) {
			g2d.setColor(furnitureColors.get(i));
			g2d.fill(mobileFurniture.get(i));
		}

		g2d.setColor(Color.BLUE);
		g2d.fill(window);

		g2d.setColor(Color.GREEN);
		if (doorOpen)
			g2d.fill(openDoor);
		else
			g2d.fill(closedDoor);

		if (closetDoorsOpen) {
			g2d.setColor(Color.DARK_GRAY);
			g2d.fill(closetDoors);
		}
	}
}