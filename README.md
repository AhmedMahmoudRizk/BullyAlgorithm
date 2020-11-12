# BullyAlgorithm<br /><br />

## Introduction

The bully algorithm is a method in a distributed computing system for dynamically electing a coordinator by process ID number. The process with the highest process ID number is selected as the coordinator. The bully algorithm assumes that the system is synchronous. <br />Each process knows which processor has the higher identifier number and communicates with that. <br />There is another assumption that the connection is reliable. However, prior information about other process id’s must be known. In the following sections, we have discussed our implementation of the bully algorithm.<br /><br />


## Bully algorithm steps

 * If P has the highest process ID, it sends a Victory message to all other processes and becomes the new Coordinator. Otherwise, P broadcasts an Election message to all other processes with higher process IDs than itself.
 * If P receives no Answer after sending an Election message, then it broadcasts a Victory message to all other processes and becomes the Coordinator.
 * If P receives an Answer from a process with a higher ID, it sends no further messages for this election and waits for a Victory message. (If there is no Victory message after a period of time, it restarts the process at the beginning.)
 * If P receives an Election message from another process with a lower ID it sends an Answer message back and starts the election process at the beginning, by sending an Election message to higher-numbered processes.
 * If P receives a Coordinator message, it treats the sender as the coordinator.<br /><br />


## Communication between processes

Based on the requirements it’s not allowed to use threading, so decided to use **Remote Method Invocation (RMI) RMI** which used to create a public remote server object that enables client and server side communications through simple method calls on the server object.<br /><br />
Each time the server creates an object, it registers this object with the RMIregistry (using** bind() **method). These are registered using a unique name known as bind name. To invoke a remote object, the client needs a reference of that object. At that time, the client fetches the object from the registry using its bind name (using **lookup()** method).<br /><br />
The client and server program is executed on the same machine so localhost is used. In order to access the remote object from another machine, localhost is to be replaced with the IP address where the remote object is present.<br /><br />


## RMI Pros 

* Minimize the complexity of the application
* Pure java solution to Remote Procedure Calls (RPC).
* preserve type safety.<br /><br />

## Applying RMI

The communication between client and server is handled by using two intermediate objects: Stub object (on client side) and Skeleton object (on server side).<br />

### Stub Object
The stub object on the client machine builds an information block and sends this information to the server. The block consists of an identifier of the remote object to be used Method name which is to be invoked Parameters to the remote JVM.<br />
### Skeleton Object
The skeleton object passes the request from the stub object to the remote object. It performs the following tasks: It calls the desired method on the real object present on the server. It forwards the parameters received from the stub object to the method.<br />


Based on the concept of client and server in rmi and applying it on the processes. the project was built as each process can run as a client that have access on the methods in the server and receive replies when the process act as ordinary process so it need to send messages to the coordinator, and also each process start the election act like server which check the other processes which have id’s greater than its.


## Class Diagram


## Implementation <br />

* Bully Interface : this is and interface which  each process implement it, contains the main functions of the bully algorithm :
  * start Election : this function is called when any new process starts or if any report of failure of the coordinator. And call all processes which have id greater then its from the registry and send messages wait for their replies, if no one reply then this means this process becomes the coordinator and won, but if receive reply then make this higher process to go and start election.
  * iWon : in this function the selected process which becomes the coordinator sets the broadcast messages to all living processes and notify them that it becomes the coordinator.
  * sendOK : in this function the process needs to send the message to the coordinator or the reporter process that confirms the current state of living process and also replying to the broadcast message from the coordinator.
* Process : this class which implements the bully interface’s functions. Each class starts by initiating its id number. The static method UnicastRemoteObject returns the stub for the remote object to pass to clients. As a result of the exportObject call, the runtime may begin to listen on a new server socket or may use a shared server socket to accept incoming remote calls for the remote object.  and also register in the rmi by binding the id of the process and the stub which return from UnicastRemoteObject .
  * Timer check : run in each process and notify if it happens any delay due to the failure reports that there is a crash and need to do new election 
  * Note: as assumed the process may be client or server so it’s required to check at the start of the methods if the id of the executed process equal to my id so in this case i act as a server if not so this process act as a client and just need to receive messages.
<br />

## How to Run

The project contains 11 files one os the interface and the rest of them are the processes their name (process_id)  and priority based on their ids. 
  * To run all it's required to compile first.<br />
   `javac -d . BullyInterface.java Process1.java Process2.java Process3.java Process4.java Process5.java Process6.java Process7.java`
  * The start rmi registry<br />
    `start rmiregistry`
  * Then start the processes<br />
  `start java -classpath . -Djava.rmi.server.codebase=file:classDir/ ;com.java.bullyalgorithm.Process1`<br />
  `start java -classpath . -Djava.rmi.server.codebase=file:classDir/ ;com.java.bullyalgorithm.Process2`<br />
  `start java -classpath . -Djava.rmi.server.codebase=file:classDir/ ;com.java.bullyalgorithm.Process3`<br />
  `start java -classpath . -Djava.rmi.server.codebase=file:classDir/ ;com.java.bullyalgorithm.Process4`<br />
  `start java -classpath . -Djava.rmi.server.codebase=file:classDir/ ;com.java.bullyalgorithm.Process5`<br />
  `start java -classpath . -Djava.rmi.server.codebase=file:classDir/ ;com.java.bullyalgorithm.Process6`<br />
  `start java -classpath . -Djava.rmi.server.codebase=file:classDir/ ;com.java.bullyalgorithm.Process7`



