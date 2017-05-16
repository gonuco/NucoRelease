const fs = require('fs');
const web3 = require('web3');

let unlockAccount = require('./utils/unlockAccount.js');
let contractCompile = require('./utils/contractCompile.js');
let contractDeploy = require('./utils/contractDeploy.js');
let w3 = new web3(new web3.providers.HttpProvider("http://127.0.0.1:8545"));
let contractSol = fs.readFileSync(process.cwd() + '/sols/ticker.sol').toString('utf-8');

Promise.all([
	unlockAccount(w3),
	contractCompile(w3, contractSol)	
]).then((res)=>{
	let acc = res[0];
	let abi = res[1].abi;
	let code = res[1].code;
	contractDeploy(w3, acc, abi, code)
	.then((tx)=>{
		let contract = w3.eth.contract(abi).at(tx.address);
		let init_ticks = parseInt(contract.getTicker({ from: acc }));
		let count = 2;
		console.log('[log] initial ticks: ' + init_ticks.toString());
		console.log('[log] ticking times: ' + count);
		for(let i = 0; i < count; i ++){
			contract.ticking({
				from: acc
			});	
		}
		let loop = setInterval(()=>{
			contract.getTicker({ from: acc }, (err, ticks)=>{
				if(err)
					console.log('[err] ' + err);
				else{
					let current_ticks = parseInt(ticks.toString());
					console.log('[log] current ticks: ' + current_ticks);
					if(current_ticks == (init_ticks + count)){
						clearInterval(loop);
						console.log('[log] demo finished');
					}
				}
			});
		}, 2000);
	}, (err)=>{
		console.log('[err] ' + err);
	});
});