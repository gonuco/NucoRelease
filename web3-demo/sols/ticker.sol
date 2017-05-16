pragma solidity ^0.4.0;
contract Ticker {
    uint8 ticks;
    function ticking(){
        ticks++;
    }
    function getTicker() constant returns (uint8){
        return ticks;
    }
}