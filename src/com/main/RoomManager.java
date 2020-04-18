package com.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * main class, manages room and adds components to room, draws it too
 * 
 * @author Zayed
 *
 */
public class RoomManager extends JPanel {

	private static final long serialVersionUID = 1L;
	private Room room;

	/**
	 * constructor
	 */
	public RoomManager() {

		int roomW = 142;
		int roomH = 159;

		room = new Room(roomW, roomH);
		Dimension size = new Dimension(room.getWidth(), room.getHeight());

		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);

		this.setFocusable(true);

		initRoom();

		this.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				room.rotateSelected(Math.signum(e.getWheelRotation()));
				repaint();
			}

		});

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				room.release();
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				room.press(e.getX(), e.getY());
				repaint();
			}

		});

		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				room.drag(e.getX(), e.getY());
				repaint();
			}

		});

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();

				if (keyCode == KeyEvent.VK_R)
					room.reset();

				repaint();
			}

		});
	}

	/**
	 * setup the room
	 */
	private void initRoom() {

		float[][] x = { { 33.5f, 94.5f, 94.5f, 33.5f }, // bed
				{ 95, 141.25f, 141.25f, 116.75f, 116.75f, 95 }, // desk
				{ 120, 141.5f, 141.5f, 120 }, // shelf
				{ 12.25f, 33, 33, 12.25f }, // bedside table
				{ 0, 10, 10, 0 }, // lamp
		};
		float[][] y = { { 1, 1, 75, 75 }, // bed
				{ 0.5f, 0.5f, 67, 67, 27.5f, 27.5f }, // desk
				{ 120, 120, 158.5f, 158.5f }, // shelf
				{ 4.5f, 4.5f, 21.5f, 21.5f }, // bedside table
				{ 0, 0, 10, 10 }, // lamp
		};
		
		int nObjects = x.length;

		for (int i = 0; i < nObjects; i++) {
			room.addFurniture(x[i], y[i]);
		}

		room.addClosedDoor(0, 123.5f, 1, 36.5f);
		room.addOpenDoor(0, 154f, 30, 1);
		room.addWindow(140f, 51.25f, 2, 59);
		room.addClosetDoors(0, 16.75f, 11, 60);
	}

	@Override
	public void paintComponent(Graphics g) {
		drawBackground(g);
		room.draw((Graphics2D) g);
	}

	/**
	 * draw the background
	 * @param g - tool to draw
	 */
	private void drawBackground(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, room.getWidth(), room.getHeight());
	}

	/**
	 * main method
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("My Room");
		RoomManager roomManager = new RoomManager();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(roomManager);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		roomManager.setDoubleBuffered(true);

		roomManager.repaint();
	}

}
