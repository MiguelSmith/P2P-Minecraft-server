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

var client_id = -1;
var server_id = -1;

//buffer queue for packets
var packets = [];
var packStrings = [];

//map of connected clients
var client_list = [];

//map of connected servers and their channels
var server_list = new Map();

//map of subscribers for a channel
var subscribers = new Map();

//map of channels of subscribers
var channels = [
    [],
    []
];

//temporary socket -> ID map
var clientIDs = new Map();
var serverIDs = new Map();

//serve html file
app.get('/', function(req, res)
{
    res.sendFile(__dirname + '/index.html');
});

//handle client connnection
io.on('connection', function(socket)
{
    var tempServerID = server_id+1;
    var tempClientID = client_id+1;
    socket.emit("type", tempServerID, tempClientID);

    socket.on("server connected", function(data)
    {
        server_id++;
        console.log("Connection received from server "+server_id);
        server_list.set(server_id,
                        [socket,            //server socket
                         new Map()          //map of channels subscribed to
                        ]);

        serverIDs.set(socket,server_id);
    });

    socket.on("client connected", function(clientID)
    {
        client_id++;
        console.log("Connection received from client "+client_id);

        //populate client list
        var arr = [
            socket,                                     //0 - socket of connection between client and server
            Math.floor((Math.random()*300)-150),        //1 - x coordinate of client
            Math.floor((Math.random()*300)-150),        //2 - y coordinate of client
            0,                                          //3 - AOI
            client_id,                                  //4 - client ID
            new Map()                                   //5 - subscriptions
        ];

        client_list[client_id] = arr;

        //map ID to socket for disconnect
        clientIDs.set(socket,client_id);

        //return ID and client list to client
        socket.emit('clients', clientList_to_string(client_id));

        //emit message to all clients that client has joined
        //io.emit('client joined', client_to_string(client_id));
    });

    //handle a disconnect
    socket.on('disconnect', function(id)
    {
        var id = clientIDs.get(socket);
        var map = client_list[id][5];

        for (var hash of map.keys())
        {
            subscribers.delete(hash);
        }
        delete client_list[id];
        io.emit('disconnect', id);
        console.log('user disconnected with an id of:' + id);

        return false;
    });

    //publish to subscribers
    socket.on('publish', function(msg)
    {
        var temp = msg.split(';');
        var msg = temp[0]+';'+temp[4]+';'+temp[1]+';'+temp[2]+';'+temp[3]+';'+temp[5];
        console.log('Client ' + temp[0]+' said: "' + temp[4] + '" at ('+temp[1]+','+temp[2]+') in a radius of '+temp[3]+' on channel '+temp[5]);

        //loop through subscribers and send message if within range
        for (var keys of subscribers.keys())
        {
            var pack = subscribers.get(keys);
            var dx = Math.pow(parseInt(temp[1])-pack.x,2);
            var dy = Math.pow(parseInt(temp[2])-pack.y,2);
            var radius = Math.sqrt(dx+dy);
            var test = parseInt(temp[3]) + parseInt(pack.AoI);

            //check whether AoIs intersect
            if (radius <= test && pack.channel == channel)
            {
                if (packet == undefined)
                {
                    client_list[pack.id][0].emit('publish', msg);
                } else
                {
                    if (msg == "server")
                    {
                        client_list[pack.id][0].emit('publish', packet);
                    } else if (msg == "client")
                    {
                        server_list[pack.id][0].emit('publish', packet);
                    }
                }
            }
        };

        io.emit('publication', msg);

        return false;
    });

    //method used to test packet sending roder
    socket.on("test packet", function(msg)
    {
        console.log("test packet received: "+msg);
        socket.emit("test packet received", "noted "+ client_id+ msg);
    })

    //receive packet
    socket.on('packet received', function(sender, channel, packet, packetType)
    {
        packets.unshift(packet);
        packStrings.unshift(packetType);
        console.log(packetType+" unshifted");
        socket.emit("packet received", packet);

        publishPackets(channel, sender, socket);

        return false;
    });

    //handle a subscribe or unsubscribe function
    socket.on('function', function(msg)
    {
        var temp = msg.split(';');

        var ref = clientIDs.get(socket);

        switch (temp[1])
        {
            //subscribe
            case 'sub':
            {

                if (temp[2] == "true")
                {
                    var pack =  new VAST.sub(ref,client_list[ref][1],client_list[ref][2],temp[3], temp[6]);
                    //var pack = new VAST.sub(ref,0,0,temp[3],temp[6]);
                    client_list[ref][3] = temp[3];
                    console.log('Client '+ref+ ' subscribed to the area around it on channel '+temp[6]);
                } else
                {
                    var pack =  new VAST.sub(temp[0],temp[4],temp[5],temp[3], temp[6]);
                    console.log('Client '+ref+ ' subscribed to the area '+temp[3]+' units around <'+temp[4]+','+temp[5]+'> on channel '+temp[6]);
                }
                client_list[ref][5].set(pack.hash,pack);
                // TODO: Find more robust way of finding correct server. probably will be based on position.
                //      Subscribe server to position with huge radius?
                if (server_list.get(0) != undefined) {
                    server_list.get(0)[1].set(pack.id, pack);
                }
                subscribers.set(pack.hash,pack);
                io.emit('subscribe', ref+','+pack.toString());
                publishPackets(pack.channel, "client",socket);
            }
            break;

            //unsubscribe
            case 'unsub':
            {
                if (temp[2] == "true")
                {
                    client_list[ref][3] = 0;
                    var hash = ref+'/'+client_list[ref][1]+'/'+client_list[ref][2];
                    console.log('Client '+temp[0]+' unsubscribed from the area around it on channel '+temp[6]);
                    var returnStr = ref+';'+hash+';'+client_list[ref][1]+';'+client_list[ref][2]+';'+temp[6];
                } else
                {
                    var hash = ref+'/'+temp[4]+'/'+temp[5];
                    console.log('Client '+temp[0]+' unsubscribed from the area <'+temp[4]+','+temp[5]+'> on channel '+temp[6]);
                    var returnStr = ref+';'+hash+';'+temp[4]+';'+temp[5]+';'+temp[6];
                }

                client_list[ref][5].delete(hash);
                subscribers.delete(hash);
                io.emit('unsubscribe', returnStr);
            }
            break;

            //unknown command
            default:
            {
                console.log('Cannot understand function');
                client_list[temp[0]][0].emit('unknown', 'Unsupported function');
            }
            break;
        }

        return false;
    });

    //move
    socket.on('move', function(msg)
    {
        //handle input message
        var temp = msg.split(';');
        var ref = parseInt(temp[0]);
        console.log('Client '+temp[0]+ ' changed their (x,y) position to <'+temp[1]+','+temp[2]+'> with an AoI of '+temp[3]);

        var pack = new VAST.sub(temp[0],client_list[temp[0]][1],client_list[temp[0]][2],client_list[temp[0]][3]);

        //insert x, y and Aoi into 2D array
        client_list[temp[0]][1] = temp[1];
        client_list[temp[0]][2] = temp[2];
        client_list[temp[0]][3] = temp[3];

        //update subscriber list if subscribed
        console.log(subscribers.has(pack.hash));
        if (subscribers.has(pack.hash))
        {
            var oldHash = pack.hash;
            pack = new VAST.sub(temp[0], temp[1],temp[2],temp[3],client_list[ref][5].get(oldHash).channel);
            client_list[ref][5].delete(oldHash);
            client_list[ref][5].set(pack.hash,pack);
            subscribers.set(pack.hash, pack);
            //create output message
            temp = ref+" "+temp[1]+' '+temp[2]+" "+temp[3]+" "+pack.channel;
        } else
        {
            //create output message
            temp = ref+" "+temp[1]+' '+temp[2]+" "+temp[3];
        }

        io.emit('move', temp);

        return false;
    });

    return false;
});

var publishPackets = function(channel,sender,socket)
{
    if (channel == null && sender == "server")
    {
        var id = serverIDs.get(socket);
        channel = (server_list.get(id)[1].get(id)).channel;
    }

    //loop through subscribers and send message if within range
    for (var keys of subscribers.keys())
    {
        var pack = subscribers.get(keys);


        //check whether AoIs intersect
        if (pack.channel == channel)
        {
            while (packets.length != 0){
                var packet = packets.pop();
                var packType = packStrings.pop();
                if (sender == "server")
                {
                    console.log("publishing packet to client proxy: "+packType);
                    server_list.get(pack.id)[0].emit('publish acknowledge', packet);
                    client_list[pack.id][0].emit('publish', packet);
                } else
                {
                    console.log("publishing packet to server proxy: "+packType);
                    client_list[pack.id][0].emit('publish acknowledge', packet);
                    server_list.get(pack.id)[0].emit('publish', packet);
                }
            }
        }
    };
};

var clientList_to_string = function(clientID)
{
    var string = clientID+" "+client_list[clientID][1]+" "+client_list[clientID][2]+" "+client_list[clientID][3];
    //run through list of clients and create client string
    for (var key in client_list)
    {
        if (clientID == key) continue;
        string += " "+client_list[key][1]+","+client_list[key][2]+","+client_list[key][3]+","+key;
        //add in the subscriptions of the client
        var map = client_list[key][5];
        for (var subs of map.keys())
        {
            var pack = client_list[key][5].get(subs);
            string += ','+ pack.toString();
        }
    }
    return(string);
}

var client_to_string = function(clientID)
{
    var string = clientID+" "+client_list[clientID][1]+" "+client_list[clientID][2]+" "+client_list[clientID][3];
    return string;
}

//set the server to listening
http.listen(3000, function()
{

  console.log('listening on localhost:3000');
});
