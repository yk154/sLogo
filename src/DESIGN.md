# Design.md

## High-level design goals

At a high-level, we aimed to separate our program into two modules: frontend and backend. While frontend does depend directly on backend, we ensured that the converse was not true (except for with the toggles on the Turtles).

The frontend's dependency on the backend was implemented through listeners. The frontend relied on notifications from the TurtleModel--which were relative changes--to adjust the absolute position of the TurtleView, as well as cues from the Interpreter to change information about the background.

In the backend, we make a distinction between Tokens (which represent each lexeme in a command) and Commands (which contain instructions about what should be executed). Commands are created in the CommandFactory, passed to CmdParser and executed by the Interpreter.

## How to add new features to our project

To add a new command to the language, our program would first require a new subclass of Command (assuming that this new command does not belong to an existing subclass- i.e. an Operation or a general purpose Command that moves the turtle). After creating the new subclass, one would need to modify the getCommandList method of CmdParser and the getNewCommand method of CommandFactory to generate the proper type of Command when it has been typed by the user.  Finally,  the Interpreter would need to be amended so that our program would know how to process the returned Command and execute it properly. If the command altered the state of the Turtle, additional logic may need to be written in the TurtleModel and/or TurtleView classes as well.

In order to add components to frontend, the new component must be added to UISetup, which is called by ViewModule to set up the interface in each tab. If this component interacts with backend, then a listener will need to be created and added within Controller.

## Major design choices/trade-offs

One of the major aspects of our code design that we discussed was whether or not to use reflection.  Currently, because we do not use reflection, supporting new types of Commands means that we would need to add conditional blocks to handle these cases in at least one if not all of the following classes: CmdParser, CommandFactory, Interpreter.

Using reflection would have allowed us to eliminate the conditional blocks in these three classes, improving their readability as a result.  As a tradeoff, we would have had a vast number of classes (one for each Command).  Additionally, some commands (like SetBackground and SetPenColor) require the Interpreter to notify various listeners of the change.  If we had taken the reflection approach, we would have had to call notifyListeners at every command's execution which would have broken functionality (these listeners only expect to be notified when a change has actually occurred).  Alternatively, each Command would have needed to contain a reference to each listener: a change that would have been very difficult and time-consuming to implement, not to mention unnecessary for most use cases.


## Assumptions

We assume that all commands have a concrete maximum number of arguments that can be passed to them.  This assumption hindered us from implementing the "grouping" extension.