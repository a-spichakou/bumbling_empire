/*-----------------------------------------------------------------------------
 A simple echo bot for the Microsoft Bot Framework.
 -----------------------------------------------------------------------------*/

const restify = require('restify');
const builder = require('botbuilder');
const spawn = require('child_process').spawn;

// Setup Restify Server
var server = restify.createServer();
server.listen(3978, function () {
    console.log('%s listening to %s', server.name, server.url);
});

const extProces = spawn('java', ['-jar','./target/robot.verter-0.0.1-SNAPSHOT-jar-with-dependencies.jar'],
    {cwd:"c:/work/robot-verter/bot"});
// Create chat connector for communicating with the Bot Framework Service
var connector = new builder.ChatConnector({
    appId: "4a07690b-a888-4626-8d62-060687dac3d9",
    appPassword: "yr99Y5pkR7j1t78dAp6Zith"
});

// Listen for messages from users
var bot = new builder.UniversalBot(connector);
server.post('/api/messages', connector.listen());
var exSession;
extProces.stdout.setEncoding("utf-8");
extProces.stdout.on('data', function(data) {
    console.log(data.toString('utf8'));
    if (exSession) {
        exSession.send("%s", data);
    }
});
extProces.stderr.on('data', function(data) {
    console.log("ERR "+data.toString('utf8'));
});
bot.dialog('/', function (session, args, next) {
    let mes = session.message.text;
    let mesOb = session.message;
    console.log(mes);
    for (let v in mesOb) {
        console.log(v + ": " + mesOb[v]);
    }

    if (mes.length == 0 || (mesOb.source === "slack" && mes.indexOf("@eisbebot")) < 0) {
        console.log("Not mine. return");
        return;
    }
    if (mesOb.source === "slack") {
        console.log("REPLACING");
        mes = mes.replace("@eisbebot", "").trim();
    }
    console.log(mes);
    session.sendTyping();
    exSession = session;
		
	var smiles=new Array("(think)","hold on","(waiting)","...","(learn)","let me see","hang on");
	var item = smiles[Math.floor(Math.random()*smiles.length)];
	
    exSession.send(item);
    extProces.stdin.write(mes+"\n","utf-8");

});
// Create your bot with a function to receive messages from the user
