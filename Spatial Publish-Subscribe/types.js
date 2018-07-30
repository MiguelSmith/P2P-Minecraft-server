/*
    data structures
*/

/*
    history

    2018/04/18
*/

var sub = exports.sub = function(id,x, y, AoI, channel="") {
    //this.layer = layer;
    this.x = x || 0;
    this.y = y || 0;
    this.AoI = AoI || 0;
    this.id = id;
    this.channel = channel;

    this.hash = id+'/'+x+'/'+y;

    this.toString = function(){
        return x+','+y+','+AoI+','+channel;
    }
}
