# Author: Chomba Waihenya

import numpy as np
import matplotlib.pyplot as plt
from tkinter import *

class FinalProject:
    # GUI that allows user to select number of points to simulate and preferred algorithm
    def __init__(self, master):
        master.minsize(width=600, height=600)
        master.maxsize(width=600, height=600)
        master.wm_title("Computational Geometry Final Project")

        self.PointsLabel = Label(master, text="Number of points:")
        self.PointsLabel.config(font=("Open Sans Light", 15))
        self.PointsLabel.pack(side="top", fill="none", expand=False)
        self.PointsLabel.pack(pady=25)

        self.entryFramePoints = Frame(master, width = 300, height = 100)
        self.entryFramePoints.pack(side="top", fill="both", expand=False)

        self.pointsNum = Entry(self.entryFramePoints, justify = CENTER)
        self.pointsNum.config(font=("Open Sans Light", 12))
        self.pointsNum.pack(pady=25)

        self.generatePoints = Button(master, text="Gift Wrapping Algorithm", command=self.genGift, width=20, default = 'active')
        self.generatePoints.pack(side="top", fill="none", expand=False)
        self.generatePoints.config(font=("Open Sans Light", 15))
        self.generatePoints.pack(pady=25)

        self.generateHull = Button(master, text="Monotone Chain", command=self.monChain, width=20, default = 'active')
        self.generateHull.pack(side="top", fill="none", expand=False)
        self.generateHull.config(font=("Open Sans Light", 15))
        self.generateHull.pack(pady=25)

        self.quitButton = Button(master, text="Quit", command=quit)
        self.quitButton.pack(side="top", fill="none", expand=False)
        self.quitButton.config(font=("Open Sans Light", 15))
        self.quitButton.pack(pady=50)

    # popup if points entry box is left empty
    def noPoints(self):
        self = Toplevel()
        self.wm_title("Points field is empty!")
        self.minsize(width=400, height=400)
        self.maxsize(width=400, height=400)

        self.noPoints = Label(self, text="Sorry, the points field cannot be empty. \n Please enter the number of points to be simulated.", font=("Open Sans Light", 12))
        self.noPoints.pack(side="top", fill="none", expand=False)
        self.noPoints.pack(pady=80)

        self.returnButton = Button(self, text="Return", command=self.destroy, width=20)
        self.returnButton.pack(side="top", fill="none", expand=False)
        self.returnButton.config(font=("Open Sans Light", 12))
        self.returnButton.pack(pady=45)

    # calls gift wrapping algorithm function if points entry field is not empty
    def genGift(self):
        if self.pointsNum.index("end") == 0:
            self.noPoints()

        else:
            # a random set of points is generated that has min x,y coordinates (0,0) and max x,y coordinates (500,500) and stored in a numpy array 
            # which is passed as a parameter to the giftwrap function
            pointArr = np.array([(np.random.randint(0,500),np.random.randint(0,500)) for iterVal in range(int(self.pointsNum.get()))])
            self.giftWrap(pointArr)   

    # checks to see if traversing three points in counterclockwise direction
    def counterClockW(self,point1, point2, point3):
        if (point2[1]-point1[1])*(point3[0]-point1[0]) <= (point3[1]-point1[1])*(point2[0]-point1[0]):
            return True
        return False

    # function runs gift wrapping algorithm on supplied points and plots simulation
    def giftWrap(self,pointsParam):
        # min x val from array of random points selected, possible list of hull vertices initialized to 
        # 0, and first hull vertex initialized to point with min x val 
        minPoint = np.where(pointsParam[:,0] == np.min(pointsParam[:,0]))
        hullList = [None] * len(pointsParam)
        hullV = pointsParam[minPoint[0][0]]

        iterVal = 0
        while True:
            hullList[iterVal] = hullV
            startPoint = pointsParam[0]
            plotPoints = np.array([hullList[k] for k in range(len(pointsParam)) if hullList[k] is not None])
            for j in range(1,len(pointsParam)):
                if (startPoint[0] == hullV[0] and startPoint[1] == hullV[1]) or not self.counterClockW(pointsParam[j],hullList[iterVal],startPoint):
                    startPoint = pointsParam[j]
                    # points and lines between points plotted on canvas, with slight pause after each plot
                    # to enable viewing of the algortihm process
                    plt.clf()               
                    plt.plot(plotPoints[:,0],plotPoints[:,1], 'r-')  
                    plt.plot(pointsParam[:,0],pointsParam[:,1],".b")              
                    plt.axis('off')        
                    plt.pause(0.00005)

                else:
                    # points and lines between points plotted on canvas, with slight pause after each plot
                    # to enable viewing of the algortihm process
                    plt.clf()               
                    plt.plot(plotPoints[:,0],plotPoints[:,1], 'r-')  
                    plt.plot(pointsParam[:,0],pointsParam[:,1],".b")              
                    plt.axis('off')        
                    plt.pause(0.00005) 
            iterVal += 1
            hullV = startPoint


            # once the current point matches the point set as the first hull vertex, the hull is complete
            if startPoint[0] == hullList[0][0] and startPoint[1] == hullList[0][1]:
                break

        # clear non entries from hullList
        for iterVal in range(len(pointsParam)):
            if hullList[-1] is None:
                del hullList[-1]
        hullList = np.array(hullList)
            
        # plot the final hull
        plt.plot(plotPoints[:,0],plotPoints[:,1], 'c-')
        plt.plot([plotPoints[-1,0],plotPoints[0,0]],[plotPoints[-1,1],plotPoints[0,1]], 'c-')
        plt.plot(pointsParam[:,0],pointsParam[:,1],".g")
        plt.axis('off')
        plt.show()   

    # calls monotone chain algorithm function if points entry field is not empty
    def monChain(self):
        if self.pointsNum.index("end") == 0:
            self.noPoints()

        else:
            # a random set of points is generated that has min x,y coordinates (0,0) and max x,y coordinates (500,500) and stored in a list comprehension 
            # which is passed as a parameter to the giftwrap function
            pointArr = [(np.random.randint(0,500),np.random.randint(0,500)) for i in range(int(self.pointsNum.get()))]
            self.monotoneChain(pointArr)

    # function runs monotone chain algorithm on supplied points and plots simulation
    def monotoneChain(self,pointsParam):
        # points are sorted and stored in a numpy array
        pointsParam.sort()
        pointsParam = np.array(pointsParam)
        # upper points are set 
        upList = [pointsParam[0], pointsParam[1]]		
        # upper hull is generated first in clockwise order
        for i in range(2,len(pointsParam)):
            upList.append(pointsParam[i])
            while len(upList) > 2 and not self.counterClockW(upList[-1],upList[-2],upList[-3]):
                del upList[-2]
            pointArr = np.array(upList)
            plt.clf()		
            plt.plot(pointArr[:,0],pointArr[:,1], 'b-')	
            plt.plot(pointsParam[:,0],pointsParam[:,1],".r")		
            plt.axis('off')		
            plt.pause(0.00005)	
        # lower points are set
        downList = [pointsParam[-1], pointsParam[-2]]	
        # lower hull is generated in clockwise order
        for i in range(len(pointsParam)-3,-1,-1):
            downList.append(pointsParam[i])
            while len(downList) > 2 and not self.counterClockW(downList[-1],downList[-2],downList[-3]):
                del downList[-2]
            pointArr = np.array(upList + downList)
            plt.clf()               
            plt.plot(pointArr[:,0],pointArr[:,1], 'b-')  
            plt.plot(pointsParam[:,0],pointsParam[:,1],".r")              
            plt.axis('off')         
            plt.pause(0.00005)	
        plt.clf()               
        plt.plot(pointArr[:,0],pointArr[:,1], 'c-')  
        plt.plot(pointsParam[:,0],pointsParam[:,1],".g")              
        plt.axis('off')         
        plt.pause(0.00005)	
        del downList[0]
        del downList[-1]
        # upper and lower hulls combined
        pointArr = upList + downList 		
        plt.axis('off')
        plt.show()

master = Tk()
simulate = FinalProject(master)
master.mainloop()
