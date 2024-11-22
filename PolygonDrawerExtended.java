import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Draw polygons dictated by mouse click and arrow key in BlueJ. 
 * Centroids drawn. 
 * Panel Points saved to a file. A new instantiation loads Points from the file and draws them.
 *
 * @author Graham C Roberts
 * @version Nov 21st 2024
 */

/**
 * An extended version of the PolygonDrawer that adds the ability to load and save polygons from/to a text file.
 * This class allows users to draw polygons using mouse clicks, display centroids,
 * and manage polygon data through file operations.
 */
public class PolygonDrawerExtended extends JPanel implements MouseListener, KeyListener, MouseMotionListener {

    private List<List<Point>> polygons;     /** Stores all completed polygons. */
    private List<Point> currentPolygon;     /** Stores points for the polygon currently being drawn. */
    private Color currentPolygonColor = Color.RED; /** Color used for the currently active polygon. */
    private Point cursorPosition = new Point(0, 0); /** To store the current mouse cursor position */

    /**
     * Constructor for the PolygonDrawerExtended class.
     * Initializes lists for polygons, sets up event listeners, and ensures the panel can gain focus for key input.
     */
    public PolygonDrawerExtended() {
        polygons = new ArrayList<>();
        currentPolygon = new ArrayList<>();
        addMouseListener(this);
        addMouseMotionListener(this); // Add the mouse motion listener
        addKeyListener(this);
        setFocusable(true);
    }

    /**
     * Calculates the centroid of a polygon.
     * @param polygon List of Points representing the polygon vertices.
     * @return A Point representing the centroid of the given polygon.
     */
    private Point calculateCentroid(List<Point> polygon) {
        double cx = 0, cy = 0;
        double area = 0;
        int n = polygon.size();

        for (int i = 0; i < n; i++) {
            Point p1 = polygon.get(i);
            Point p2 = polygon.get((i + 1) % n); // wrap around to the first point
            double temp = p1.x * p2.y - p2.x * p1.y;
            area += temp;
            cx += (p1.x + p2.x) * temp;
            cy += (p1.y + p2.y) * temp;
        }

        area *= 0.5;
        cx /= (6 * area);
        cy /= (6 * area);

        return new Point((int)Math.round(cx), (int)Math.round(cy));
    }

    /**
     * Paints the component, drawing all stored polygons and the current polygon.
     * Additionally, it draws centroids for completed polygons.
     * @param g The Graphics context to draw on.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        // Draw completed polygons
        for (List<Point> polygon : polygons) {
            drawPolygon(g, polygon, Color.BLACK);
            Point centroid = calculateCentroid(polygon);
            drawCentroid(g, centroid);
        }

        // Draw the active polygon
        if (!currentPolygon.isEmpty()) {
            drawPolygon(g, currentPolygon, currentPolygonColor);
        }

        // Draw current cursor position at (10, 20)
        g.setColor(Color.BLACK);
        g.drawString("Cursor Position: (" + cursorPosition.x + ", " + cursorPosition.y + ")", 10, 20);
    }

    /**
     * Draws the centroid of a polygon on the panel.
     * @param g The Graphics context used for drawing.
     * @param centroid The Point representing the centroid to be drawn.
     */
    private void drawCentroid(Graphics g, Point centroid) {
        g.setColor(Color.RED);
        g.fillOval(centroid.x - 5, centroid.y - 5, 10, 10);
    }

    /**
     * Draws a polygon given a list of points and a color.
     * @param g The Graphics context.
     * @param polygon The list of points defining the polygon.
     * @param color The color to draw the polygon.
     */
    private void drawPolygon(Graphics g, List<Point> polygon, Color color) {
        if (polygon != null && !polygon.isEmpty()) {
            g.setColor(color);
            for (int i = 0; i < polygon.size() - 1; i++) {
                g.drawLine(polygon.get(i).x, polygon.get(i).y, polygon.get(i + 1).x, polygon.get(i + 1).y);
            }
            // Draw vertices
            g.setColor(Color.BLUE);
            for (Point p : polygon) {
                g.fillOval(p.x - 3, p.y - 3, 6, 6);
                g.drawString("(" + p.x + ", " + p.y + ")", p.x + 5, p.y + 15);
            }
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        cursorPosition.setLocation(e.getX(), e.getY()); // Update cursor position on mouse moved
        repaint(); // Optional: repaint panel if you want to update immediately
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Optional: if you want to update during dragging as well
        cursorPosition.setLocation(e.getX(), e.getY()); // Update cursor position
        repaint(); // Optional: repaint panel if you want
    }

    /**
     * Handles mouse clicks; adds the click coordinates to the current polygon and repaints.
     * @param e The MouseEvent containing the click details.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        currentPolygon.add(new Point(e.getX(), e.getY()));
        cursorPosition.setLocation(e.getX(), e.getY()); // Update cursor position
        System.out.println("Point added: (" + e.getX() + ", " + e.getY() + ")");
        repaint();
    }

    // Call this method when you want to finish the current polygon
    private void finalizeCurrentPolygon() {
        if (!currentPolygon.isEmpty()) {
            polygons.add(new ArrayList<>(currentPolygon));
            currentPolygon.clear();
            System.out.println("Polygon added: " + polygons.size());
        }
    }

    // Unused MouseListener methods
    @Override public void mousePressed(MouseEvent e) {}

    @Override public void mouseReleased(MouseEvent e) {}

    @Override public void mouseEntered(MouseEvent e) {}

    @Override public void mouseExited(MouseEvent e) {}

    /**
     * Handles key presses; clears the canvas or starts a new polygon based on key pressed.
     * @param e The KeyEvent containing the key details.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_SPACE) {
            // Save polygons to file before clearing
            if (polygons.isEmpty()) {
                System.out.println("No polygons to save!");
                return; // Early return if no polygons to save
            }
            System.out.println("Saving polygons...");
            savePolygonsToFile("polygon_coordinates.txt");
            clearCanvas(); // Clear after saving
        } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
            startNewPolygon(); // Start a new polygon
        }
    }

    /**
     * Clears the canvas by removing all polygons and the current polygon.
     */
    private void clearCanvas() {
        polygons.clear();
        currentPolygon.clear();
        repaint();
    }

    /**
     * Starts a new polygon if the current polygon is not empty. 
     * Adds the current polygon to the list of completed polygons and clears the current polygon.
     */
    private void startNewPolygon() {
        finalizeCurrentPolygon(); // Ensure it finalizes current before starting
        repaint();
    }

    // Unused KeyListener methods
    @Override public void keyTyped(KeyEvent e) {}

    @Override public void keyReleased(KeyEvent e) {}

    /**
     * Saves the currently stored polygons to a specified file path.
     * @param filePath The path of the file where polygons will be saved.
     */
    public void savePolygonsToFile(String filePath) {
        if (polygons.isEmpty()) {
            System.out.println("No polygons to save!");
            return; // Early return if no polygons to save
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (List<Point> polygon : polygons) {
                for (Point point : polygon) {
                    writer.write("(" + point.x + ", " + point.y + ")");
                    writer.newLine();
                }
                writer.newLine(); // Separate polygons with a new line
            }
            System.out.println("Polygons saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving polygon data to file: " + e.getMessage());
        }
    }

    /**
     * Loads polygon coordinates from a text file and adds them to the polygons list.
     * Each polygon is separated by a blank line.
     * @param filePath The path to the text file containing polygon coordinates.
     */
    public void loadPolygonsFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<Point> currentPolygon = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Ignore comment lines
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.isEmpty()) {
                    if (!currentPolygon.isEmpty()) {
                        polygons.add(new ArrayList<>(currentPolygon));
                        currentPolygon.clear();
                    }
                    continue;
                }
                String[] coordinates = line.substring(1, line.length() - 1).split(",");
                int x = Integer.parseInt(coordinates[0].trim());
                int y = Integer.parseInt(coordinates[1].trim());
                currentPolygon.add(new Point(x, y));
            }
            if (!currentPolygon.isEmpty()) {
                polygons.add(new ArrayList<>(currentPolygon));
            }
        } catch (IOException e) {
            System.err.println("Error loading polygon data from file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in file: " + e.getMessage());
        }
        repaint();
    }

    /**
     * Main method to set up and display the GUI. Loads polygons from a file on startup.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Polygon Drawer Extended");
        PolygonDrawerExtended panel = new PolygonDrawerExtended();
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Load polygons from file on startup
        panel.loadPolygonsFromFile("polygon_coordinates.txt");
    }
}