Project Title: Polygon Drawer
Purpose of Project
This project demonstrates a graphical application that allows users to draw polygons using mouse clicks and arrow keys, compute their centroids, and manage polygon data through file operations.

Version
November 2024

How to Start this Project
To run the application, execute the main method in the PolygonDrawerExtended class.

Authors
Graham C. Roberts

User Instructions

Drawing Polygons: 

Use the left mouse button to click on the canvas. Each click creates a vertex of the polygon, connecting to the next point until the polygon is complete. To start a new polygon, press any arrow key (up, down, left, or right).
Output: 

The coordinates of the active polygon are printed to the standard output (console) each time the arrow key is pressed.

File Operations:

The application reads coordinate points from a text file upon startup and draws them onto the canvas.
Press the space bar to save the currently drawn polygons' coordinates to a text file named polygon_coordinates.txt.

Notes on Extension

This extended version of the project enhances the functionality by allowing users to load polygon points from a text file at startup. 

Centroid Calculation

The centroids (the geometric centers of the polygons) are calculated and displayed as red circles on the panel using the calculateCentroid() method. Additionally, an alternative centroid is computed via ApproximateCentroid(), method that calculates an approximate centroid based solely on the arithmetic mean of the points and is displayed as blue circles. While approximateCentroid() provides a quick estimate, it may not always accurately reflect the true centroid of irregularly shaped polygons.
