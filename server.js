var http = require('http');

http.createServer(function(req, res) {
    res.writeHead(200, {
        'Content-Type': 'text/html'
    });
    setTimeout(() => {
        res.end('<!DOCTYPE html><html><body><a href="http://google.com">hello</a></body></html>');
    }, 5000);
}).on('connection', function(socket) {
    socket.setTimeout(10000);
}).listen(3000);