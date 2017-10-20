let http = require('http');

http.createServer(function (req, res) {
    res.writeHead(200, {
        'Content-Type': 'text/html'
    });
    setTimeout(() => {
        let resp = '<!DOCTYPE html><html><body><a href="http://localhost:4000">hello</a></body></html>';
        res.end(resp);
    }, 20);
}).on('connection', function (socket) {
    socket.setTimeout(10000);
}).listen(3000);