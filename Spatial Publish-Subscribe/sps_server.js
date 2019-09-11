/*
    server for a spatial publish/subscribe system
*/
/*
    functions:
    on()                    handles client connection
    subscribe()             subscribe to an area
    unsubscribe()           unsubscribe from an area
    publish()               send a message to subscribers whose AoI intersects the clients
    move()                  change the coordinates and AoI of the client
    ID()                    return the client id to the client on connnection
*/
var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var VAST = require('./types');

//var client_id = -1;
//var server_id = -1;
var connectionID = -1;
var channelSubID =
{
    "status":-1,
    "login":-1,
    "ingame":-1
};

//buffer queue for packets
var packets = [];
var packStrings = [];

//map of connected clients
//var client_list = [];

//map of connected servers and their channels
//var server_list = {};

//var connectionList = {};

// map of subscribers for a channel
//var subscribers = {};

// map of channels of subscribers
//var channels = [
//    [],
//    []
//];

//temporary socket -> ID map
var socketToConnectionIDMap = {};
var connectionInfoList = {};

//var clientIDs = {};
//var serverIDs = {};

//serve html file
app.get('/', function(req, res)
{
    res.sendFile(__dirname + '/index.html');
});



/*

SPS server must handle incoming connections and assign an ID.
SPS server must handle disconnections and removeID from subscribers
SPS server must keep track of channels and users subscribed to channels
SPS server must allow any connection to publish a message to a specific channel
SPS server must allow any connection to spatially publish a message to a specific channel
 */


//handle client connnection
io.on('connection', function(socket) {
    connectionID++;
    console.log("Someone connected. Sending connectionID <" + connectionID + ">");
    socket.emit("ID", connectionID)
    socketToConnectionIDMap[socket] = connectionID;
    connectionInfoList[connectionID] =
        {
            id: connectionID,                          // id
            socket: socket,                            // socket
            x: Math.floor((Math.random() * 300) - 150),   // x coordinate of client
            y: Math.floor((Math.random() * 300) - 150),   // y coordinate of client
            AoIRadius: 0,                             // radius of AoI
            subscriptions: {}   // channels to which connection is subscribed
        };

    //handle a disconnect
    socket.on('disconnect', function(id)
    {
        var id = socketToConnectionIDMap[socket];
        delete connectionInfoList[id];
        console.log("Connection <"+id+"> has disconnected");

        return false;
    });

    // publish message to subscribers
    socket.on('publish', function(connectionID, player, x, y, radius, payload, channel)
    {
        socket.broadcast.emit('publication', connectionID, player, x, y, radius, payload, channel);

        return false;
    });

    socket.on('subscribe', function(channel, x, y, AoI) {
        var peerID = socketToConnectionIDMap[socket];
        var connection = connectionInfoList[id];

        // create subID for a channel if it doesn't exist
        if (!channelSubID.hasOwnProperty(channel)) {
            channelSubID[channel] = -1;
        }
        channelSubID[channel]++;

        if (x == undefined) {
            var pack = new VAST.sub(peerID, channelSubID[channel], connection.x, connection.y, connection.AoIRadius, channel);
        } else {
            var pack = new VAST.sub(peerID,channelSubID[channel], x,y,AoI,channel);
        }

        // create subscription channel if it doesn't exist
        if (!connection.subscriptions.hasOwnProperty(channel)) {
            connection.subscriptions[channel] = {};
        }
        connection.subscriptions[channel][subID] = pack;

    });

    //handle a subscribe or unsubscribe function
    socket.on('function', function(msg)
    {
        console.log(msg);
        var temp = msg.split(';');
        var query = temp[1];
        var id = temp[0];
        var newSubscription = temp[2];
        var AoIRadius = temp[3];
        var x = temp[4];
        var y = temp[5]
        var channel = temp[6];


        var refId = socketToConnectionIDMap[socket];
        var connectionInfo = connectionInfoList[refId];

        switch (query)
        {
            //subscribe
            case 'sub':
            {

                if (newSubscription == "true") {
                    subID++;
                    var pack =  new VAST.sub(refId, subID, connectionInfo.x, connectionInfo.y, AoIRadius, channel);
                    //var pack = new VAST.sub(ref,0,0,temp[3],temp[6]);
                    connectionInfoList[refId].AoIRadius = AoIRadius;
                    console.log('Client '+refId+ ' subscribed to the area around it on channel '+channel);
                } else {
                    var pack =  new VAST.sub(id, x, y, AoIRadius, channel);
                    console.log('Client '+refId+ ' subscribed to the area '+AoIRadius+' units around <'+x+','+y+'> on channel '+channel);
                }
            }
            break;

            //unsubscribe
            case 'unsub':
            {
                if (newSubscription == "true")
                {
                    connectionInfoList[refId].AoIRadius = AoIRadius;
                    var hash = refId+'/'+connectionInfo.x+'/'+connectionInfo.y;
                    console.log('Client '+id+' unsubscribed from the area around it on channel '+channel);
                    var returnStr = ref+';'+hash+';'+connectionInfo.x+';'+connectionInfo.y+';'+channel;
                } else
                {
                    var hash = refId+'/'+x+'/'+y;
                    console.log('Client '+id+' unsubscribed from the area <'+x+','+y+'> on channel '+channel);
                    var returnStr = refId+';'+hash+';'+x+';'+y+';'+channel;
                }
            }
            break;

            //unknown command
            default:
            {
                console.log('Cannot understand function');
                socket.emit('unknown', 'Unsupported function');
            }
            break;
        }

        return false;
    });

    //move
    socket.on('move', function(msg)
    {
        var connectionInfo = connectionInfoList[id];

        return false;
    });

    return false;
});

//set the server to listening
http.listen(3000, function()
{
  console.log(http.address());
});
