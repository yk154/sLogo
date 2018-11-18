# Cell Society API Critique

Yeon Woo Kim (yk154), Seung-Woo Choi (sc442)

We analyzed Team 06's Cell Society project

* **Grid class**
    * Since the Grid class is used extensively by the Simulation classes/subclasses, all of the methods in Grid are part of the **external API**. They provide access to the states of the Cells in the grid and to manipulate the Grid.
   
    
* **Simulation class**
    * getColorMatrix, getMyGrid, getBlackListed colors should be part of the **external API** because it is used by the view to update the view of the Grid and its colors
    * Every other method should be part of the **internal API** because it is used in the actual simulation runs.
    
 
 
* **Menu view**
    * public static final variables for font size, padding size etc should be part of the 
    **internal API** since they are used to organize elements within the view.
    
    * Methods such as updateBarChart, populateParameterFields should be part of the **external API**
    since they are used elsewhere to update the view.
    
* **Graph view**

    * resetGraph and update functions should be **internal API** because they are used within the class to update the actual values of the line graph
    
    * toRGBCode should be part of the **external API** because the value it returns is used to update the view of the graph
    
* **ForagingAnts class**

    * getColorMatrix should be part of the **external API** because it is called by the view class to obtain the different possible colors of the Cells in the grid
    * Every other method should be part of the **internal API**, it can be used to further add functionality to the ForagingAnts game
    
* **Point class**
    * Everything in the Point class should be in the **external API** because it is used to implement features in the Grid.
    
  
    
    