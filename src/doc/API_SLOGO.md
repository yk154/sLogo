# Slogo API Discussion

Amy Kim (yk154), Seung-Woo Choi (sc442)

* What is the result of parsing and who receives it?

    * The parser will take in user input parse the raw String into Commands as defined by a Command parent class and possibly subclasses (subclass for forward, turn etc). 
    A list of Commands will be passed to the Behavior class.

* When does parsing need to take place and what does it need to start properly?
    
    * After the user enters their code and presses enter or clicks a Send button, parsing will take place. It must be formatted correctly in order to start properly.
    
* When are errors detected and how are they reported?
    
    * The parser should use Syntax.properties to detect any syntax errors in the user input. It will detect an error and prompt the user to input different code with correct syntax.
    
    * When the user tries to leave the screen, the Behavior class should be able to detect if the dynamicTurtle's position + forward input is outside the boundaries of the screen.
    It will notify the user with a warning dialog that they are attempting to leave the screen, and will not update the dynamicTurtle view on the view.
    
* What do commands know, when do they know it, and how do they get it?
    
    * Commands should hold their values (int, float) and their corresponding command (Strings, "fd", "rt")
    
    * These Command objects will be created in the parser. For example, if user inputs "fd 50", new Command("fd", 50) will be created. These commands will be placed into a list which the Behavior class can interpret chronologically.
    
* How is the view updated after a command has completed execution?
    
    * The view should go through each Command one by one and each time the view should step.
    
    