# Follower Maze ![Maze Icon](https://i.ibb.co/ZfBjP71/Finding-Solutions-512.png)

### The Challenge
The challenge proposed here is to build a system which acts as a socket
server, reading events from an *event source* and forwarding them when
appropriate to *user clients*.

Clients will connect through TCP and use the simple protocol described in a
section below. There will be two types of clients connecting to your server:

- **One** *event source*: It will send you a
stream of events which may or may not require clients to be notified
- **Many** *user clients*: Each one representing a specific user,
these wait for notifications for events which would be relevant to the
user they represent

##### Events
There are five possible events. The table below describe payloads
sent by the *event source* and what they represent:

| Payload       | Sequence #| Type         | From User Id | To User Id |
|---------------|-----------|--------------|--------------|------------|
|666\|F\|60\|50 | 666       | Follow       | 60           | 50         |
|1\|U\|12\|9    | 1         | Unfollow     | 12           | 9          |
|542532\|B      | 542532    | Broadcast    | -            | -          |
|43\|P\|32\|56  | 43        | Private Msg  | 32           | 56         |
|634\|S\|32     | 634       | Status Update| 32           | -          |

Events may generate notifications for *user clients*. **If there is a
*user client*** connected for them, these are the users to be
informed for different event types:

* **Follow**: Only the `To User Id` should be notified
* **Unfollow**: No clients should be notified
* **Broadcast**: All connected *user clients* should be notified
* **Private Message**: Only the `To User Id` should be notified
* **Status Update**: All current followers of the `From User ID` should be notified

If there are no *user client* connected for a user, any notifications
for them must be silently ignored. *user clients* expect to be notified of
events **in the correct order**, regardless of the order in which the
*event source* sent them.

### Functional Specification
* The events will arrive out of order
* *user clients* expect to be notified of events **in the correct order**, regardless of the order in which the *event source* sent them.
 
***
## Architecture
![Maze Icon](https://i.imgur.com/N5JO3lu.png)

## Modules

Modules  | Functionality
------------- | -------------
**event-server**  | Event Server to receive event from event server program
**clients**  | Client Server to accept/process client connections
**commons** | Commons functionality to be used acorss moduule like Kafka Messaging

You can run the full module from `followermaze` multi module java project
##### Steps to execute :

* **Start Event Server** -
    * Accept connections from event server on `9090`
    * Push to `Local Storage` Periodically in bacthes
```javascript
java -jar event-server/target/event-server-1.0-jar-with-dependencies.jar
```
* **Start Client Server** - 
    * Accept client connections from event server on `9099`
    * File consumer to listen from `Local Storage` & push to active client connections
```javascript
java -jar clients/target/clients-1.0-jar-with-dependencies.jar
```
## Property File Description

**Server Config** - `config.properties`

| Property       | Default | Description                     |
|----------------|---------|---------------------------------|
| server.host    | localhost    | Server Host                  |
| server.port    | 8080    | Server Port                  |
| server.threads | 10      | Number of IO Threads for Server |

**File Config** - `file.proprties`

| Property                     | Default            | Description                                                               |
|------------------------------|--------------------|---------------------------------------------------------------------------|
| batch.file.path            | /tmp     | Local Storage Path to store batch event files |
| batch.size            | 100     | Event Batch Size |
| batch.poll            | 5000      | Polling time to check for new batch|
| max.retry             | 1| Retry attempt|

## Development References

We use Java7 NIO library to handle heavy file processing & Apache Commons Compress for GZIP Compression

- `Paths` - static methods that return a Path by converting a path string or URI.
- `Files` - static methods that operate on files, directories, or other types of files

More information about Paths module can be seen :
- [Paths](https://docs.oracle.com/javase/7/docs/api/java/nio/file/Paths.html)
- [Files](https://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html)
- [GZIP](https://commons.apache.org/proper/commons-compress/index.html)


## Future Scope
* CPU Monitoring for JVM Optmization
* Test case coverage for Code Durability
* Dockerfile for full fledged shippable solution
* ELK integration to check stats of the events
* Distributed Tracing for event tracking