# Informatics Large Practical 

2nd year course at the University of Edinburgh

# Drone Control Algorithm

A route is calculated and is passed to the drone to fly over. The algorithm within the drone itself is very simple. The route that is passed to it contains a list of locations that the drone must visit in order, starting from the start location. The drone iterates through all the locations and collects data from nearby sensors once arrived at a location.

I will now talk about how my application builds a route for the drone to traverse. This is located within the RouteBuilder nested class of ChristofidesRoute class. The route builder goes through a collection of simple steps as described below.

1. Fly zone grid is initialised. This is the zone that the drone is allowed to be in.
2. A graph **A** is created where each vertex is a location in which the drone can exist and each edge is a drone path between two vertices. Each drone path is 0.0003 degrees.
    - Basic idea is to create a grid of squares, shift alternating rows by half a triangle length to form rhombuses and then add paths forming triangles from the rhombuses. See **figures 1 - 3** for a visualisation.
    - I made the graph such that the paths would form equilaterals as this allows for the following:
        - At a vertex, the drone can move in 6 directions which is better than 4 (if I used squares instead).
        - Equilateral triangles form a **tessellation** when put together. This means that I can fill the entire fly zone with equilateral triangles put side by side and the drone will be able to visit any of those vertices using valid paths.
        - The maximum distance from a point can be from a vertex within a triangle tessellation is when the point is centred within a triangle. This distance is less than 0.0002 degrees so we know that there will always be a sensor within range of a vertex (the max distance a drone can be from a sensor is 0.0002 degrees before it can read it).
3. The invalid paths and vertices within the graph **A** are removed. See **Figure 4**.
4. The vertices of the graph that are nearby sensors are made into a set **S**.
5. Using the set **S**, a list **L** of vertices is created. The list is then sorted in ascending order of euclidean distance to the given start location of the drone.
6. We then build a complete graph **B** where each vertex is a location from the list **L** and each edge is the shortest collection of drone paths between two V. Each edge has a weight equal to the number of moves between the two vertices. To achieve this we:
    - For every pair of points within the list **S** we call dijkstra's algorithm (imported) on the graph **A** to find the shortest path between the two selected locations which, as we already know, are nearby sensors. This is essentially the shortest route between the two sensors corresponding to the two points chosen to call dijkstra’s algorithm on. This is an approximation since we do not directly store or calculate the distance between any sensors, we use the sum distance of the moves of the specific path within graph **A** between the two drone locations near the sensors.
7. Using the complete graph **B** and Christofides algorithm (imported), we find the shortest tour of all the locations within graph **B**. See **Figure 5** where each gray point is a drone location near a sensor and the green is the starting location. As each vertex within the graph **B** is a drone location that is nearby a sensor, we have now found a route for the drone to traverse. However, we must check the number of paths that the tour consists of and make sure this is within limit. We do this in the next step.
8. If the number of moves in the route created in 7 is too large, we remove the last item from list **L** and perform steps 6 to 8 again. Since list **L** is the list of drone locations near sensors that is sorted by its euclidean distance to the start location, we are removing the farthest away drone location so this will hopefully have the highest impact on reducing the number of moves calculated within step (8).
9. We now have a tour of drone locations, that are nearby sensors, and have a tour size less than or equal to the maximum number of moves. See **Figure 6**. However, this tour is unordered and does not start at the start location, so we build an ordered (starting at the start location) list of drone locations from the tour.
