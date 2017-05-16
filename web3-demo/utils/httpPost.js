const http = require('http');

module.exports = function(ip, port, json){
	return new Promise((resolve, reject)=>{
        let jsonStr = JSON.stringify(json);
        let post = new http.request({
            hostname: ip,
            port: port,
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Content-Length": jsonStr.length
            }
        }, (res)=>{
            res.setEncoding('utf8');
            res.on('data', (chunk) => {
                let res = JSON.parse(chunk);
           		resolve(res);
            });
            res.on('end', ()=>{
                
            });
        });
        post.on('error', (err)=>{
            resolve({});
        });
        post.write(jsonStr);
        post.end();
    });
}