const unlockAccount = require('./unlockAccount.js');

module.exports = function(w3, acc, abi, data){
	return new Promise((resolve, reject)=>{
		w3.eth.contract(abi)
	    .new({
	    	from: acc,
	    	data: data
	    }, (err, tx)=>{
	    	if(err)
	    		reject(err);
	    	if(tx && tx.address){
	    		console.log('[log] tx addr: ' + tx.address);
	    		resolve(tx);
	    	}
	    });	
	});
}