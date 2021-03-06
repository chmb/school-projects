# Computational Geometry Project
### Spring 2017

A program created using Python to give a visual simulation of the computation of the convex hull of a set of points using the gift wrapping and monotone chain algorithms.

Tkinter was used to create the graphical user interface using which a user could specify how many random points existed in the set and which algorithm they wanted to simulate by clicking on one of the respective buttons.

Numpy was used for its arrays as well as for its overloaded math functions such as np.min which was especially useful when returning the position of the smallest value in an array of 2d values and np.where which returns all the positions of values that meet a certain criteria in an array.

Matplotlib was used to plot the points. Its plt.pause function was used between plots to allow the user to view the simulations at each point.

### Algorithms used:
Gift Wrapping Algorithm: This algorithm is known to be computable in O(n) time best case and O(n^2) time in the worst case. I model this algorithm by starting at the left most point and working through the rest of the points in a counter clockwise fashion.

![gifgiftwrap](https://user-images.githubusercontent.com/12681675/28742528-234c770c-7400-11e7-8bf8-be5a33af75bd.gif)

Monotone Chain Algorithm: This algorithm is known to be computable in O(nlogh) time best case and O(nlogn) time in the worst case. I model this algorithm by starting with an extreme left point, computing the upper and lower hulls respectively, and combining the output to form the completed hull.

![gifmonotonechain](https://user-images.githubusercontent.com/12681675/28742534-4798a824-7400-11e7-93ec-fce07d54b9c9.gif)




