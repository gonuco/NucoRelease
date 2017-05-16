package com.nuco.api.tools;

import com.nuco.api.IContract;
import com.nuco.api.INuco;
import com.nuco.api.IUtils;
import com.nuco.api.impl.Contract;
import com.nuco.api.impl.NucoImpl;
import com.nuco.api.sol.IAddress;
import com.nuco.api.sol.ISString;
import com.nuco.api.sol.impl.SolidityAbstractType;
import com.nuco.api.sol.IUint;
import com.nuco.api.types.ApiMsg;
import com.nuco.api.types.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * <p>This class show the basic operation of the nuco javaapi.</p>
 * <p>There are four testcases in the ApiDemo. you can check the testcase by type in</p>
 * <p>"java -jar nucoapi.jar -h"</p>
 * <p>Case 1: get the highest block number in the connecting blockchain network.</p>
 * <p>Case 2: get Accounts been stored in the connecting kernel.</p>
 * <p>Case 3: unlock the first account been stored in the connecting kernel.</p>
 * <p>Case 4: deploy a Hello world contract and print the string.</p>
 * <p>Case 5: deploy a token contract and execute a transaction then check the final balance.</p>
 */
public class ApiDemo {
  private static boolean runTest = false;
  private static String url = INuco.LOCALHOST_URL;
  private final static String tokenSC = "contract MyToken{  \n" +
      "    event Transfer(address indexed from, address indexed to, uint256 value); \n" +
      "    string public name;  \n" +
      "    string public symbol;  \n" +
      "    uint8 public decimals; \n" +
      "    mapping(address=>uint256) public balanceOf; \n" +
      "    function MyToken(uint256 initialSupply, string tokenName, uint8 decimalUnits, string tokenSymbol){ \n" +
      "        balanceOf[msg.sender]=initialSupply;    \n" +
      "        name = tokenName;    \n" +
      "        symbol = tokenSymbol;    \n" +
      "        decimals = decimalUnits;  \n" +
      "    } \n" +
      "    function transfer(address _to,uint64 _value){    \n" +
      "        if (balanceOf[msg.sender] < _value || balanceOf[_to] + _value < balanceOf[_to]) throw;    \n" +
      "            balanceOf[msg.sender] -= _value;    \n" +
      "            balanceOf[_to] += _value;    \n" +
      "            Transfer(msg.sender, _to, _value);\n" +
      "    }}\n";

  private final static String helloworldSC = "pragma solidity ^0.4.0;\n" + "contract HelloWorld {\n"
          + "    function greeting () constant returns (string){\n" + "        return \"Hello, World!\";\n" + "    }\n"
          + "}";

  public static void main(String[] args) {
    int caseNum = -1;
    if (args.length > 0) {
      for (int i = 0; i < args.length; ++i) {
        String arg = args[i];
        switch (arg) {
          case "--test":
          case "-t":
            try {
              if (++i < args.length) {
                caseNum = Integer.parseInt(args[i]);
              } else {
                System.out.println("No case number input.");
                return;
              }
            } catch (Exception e) {
              System.err.println("Can not recognize input args");
              return;
            }
            runTest = true;
            break;
          case "--help":
          case "-h":
            System.out.println("[OPTION] [NUM|STRING] [OPTION] [NUM|STRING] [OPTION] [NUM|STRING]");
            System.out.println(
                "  Ex. java -jar nucoapi.jar -t 1 -l tcp://localhost:8547");
            System.out.println(
                "Mandatory arguments to long options are mandatory for short options too.");
            System.out.println("  -v, --version             get api version.");
            System.out.println("  -l, --url                 set the kernel url and port.");
            System.out.println("  -t, --test [NUM]          demo api tests");
            System.out.println("              NUM = 1       demo get BlockNumber.");
            System.out.println("              NUM = 2       demo GetAccounts.");
            System.out.println("              NUM = 3       demo UnlockAccount.");
            System.out.println("              NUM = 4       demo Helloworld Contract.");
            System.out.println("              NUM = 5       demo Token Smart Contract.");
            System.out.println("              NUM = 0       demo all api .");
            return;
          case "--version":
          case "-v":
            INuco api = new NucoImpl();
            System.out.println(api.getApiVersion());
            return;
          case "--url":
          case "-l":
            if (++i < args.length) {
              url = args[i];
            } else {
              System.out.println("No url input.");
              return;
            }
            break;
        }
      }

      if (runTest) {
        int finalCaseNum = caseNum;
        switch (finalCaseNum) {
          case 0:
            new ApiDemo().DemoBlockNumber();
            new ApiDemo().DemoTokenContract();
            new ApiDemo().DemoGetAccounts();
            new ApiDemo().DemoUnlockAccount();
            new ApiDemo().DemoHelloWorld();
            break;
          case 1:
            new ApiDemo().DemoBlockNumber();
            break;
          case 2:
            new ApiDemo().DemoGetAccounts();
            break;
          case 3:
            new ApiDemo().DemoUnlockAccount();
            break;
          case 4:
            new ApiDemo().DemoHelloWorld();
            break;
          case 5:
            new ApiDemo().DemoTokenContract();
            break;
          default:
            System.out.println("Wrong input test case number");
            return;
        }
      }
    } else {
      System.out.println("Must input arg, please use -h or --help to see the details");
      System.out.println(
          "if you are unable to run this jar, try \"java -jar -Djava.library.path=./ nucoapi.jar -t 1\" .");
    }

    exit(0);
  }

  public void DemoGetAccounts() {
    System.out.println("===============  Demo GetAccounts ======================");

    INuco api = new NucoImpl();
    ApiMsg apiMsg = api.connect(url);
    if (apiMsg.error()) {
      System.out.println("Connect server failed, exit test! " + apiMsg.getErrString());
      return;
    }

    System.out.println("Get server connected!");

    System.out.println("Get accounts from server, please press enter the key to go next step!");
    Scanner scan = new Scanner(System.in);
    scan.nextLine();

    apiMsg.set(api.getWallet().getAccounts());
    if (apiMsg.error()) {
      System.out.println("GetAccounts failed! " + apiMsg.getErrString());
      return;
    }
    List accs = apiMsg.getObject();

    System.out.println("Total " + accs.size() + " accounts!");
    for (int i=0 ; i<accs.size() ; i++) {
      System.out.println("Found account: " + IUtils.bytes2Hex((byte[])accs.get(i)));
    }

    api.destroyApi();
    System.out.println("===== Demo GetAccounts finish =====");
    System.out.println();
  }

  public void DemoUnlockAccount() {
    System.out.println("===============  Demo UnlockAccount ======================");

    INuco api = new NucoImpl();
    ApiMsg apiMsg = api.connect(url);
    if (apiMsg.error()) {
      System.out.println("Connect server failed, exit test! " + apiMsg.getErrString());
      return;
    }

    System.out.println("Get server connected!");

    System.out.println("Get accounts from server, please press enter the key to go next step!");
    Scanner scan = new Scanner(System.in);
    scan.nextLine();

    apiMsg.set(api.getWallet().getAccounts());
    if (apiMsg.error()) {
      System.out.println("GetAccounts failed! " + apiMsg.getErrString());
      return;
    }
    List accs = apiMsg.getObject();

    System.out.println("Total " + accs.size() + " accounts!");
    for (int i=0 ; i<accs.size() ; i++) {
      System.out.println("Found account: " + IUtils.bytes2Hex((byte[])accs.get(i)));
    }

    byte[] acc = (byte[]) accs.get(0);
    System.out.println("Get the first account: " + IUtils.bytes2Hex(acc));

    // unlockAccount before deployContract or send a transaction.
    System.out.println("Try to unlock the first account.");
    System.out.println("Please press the enter key to go next step!");
    scan = new Scanner(System.in);
    scan.nextLine();
    System.out.println("Please input the password of the first account: ");
    String password = scan.nextLine();

    apiMsg.set(api.getWallet().unlockAccount(acc, password, 300));
    if (apiMsg.error()) {
      System.out.println("Unlock account failed! Please check your password input! " + apiMsg.getErrString());
      return;
    }

    System.out.println("Account unlocked!");
    System.out.println();

    api.destroyApi();
    System.out.println("===== Demo UnlockAccount finish =====");
    System.out.println();
  }

  public void DemoBlockNumber() {
    System.out.println("===============  Demo BlockNumber ======================");

    INuco api = new NucoImpl();
    ApiMsg apiMsg = api.connect(url);
    if (apiMsg.error()) {
      System.out.println("Connect server failed, exit test! " + apiMsg.getErrString());
      return;
    }

    System.out.println("Get server connected!");

    apiMsg.set(api.getChain().blockNumber());
    if (apiMsg.error()) {
      System.out.println("Get blockNumber error: " + apiMsg.getErrString());
    }

    long bn = api.getChain().blockNumber().getObject();
    System.out.println("The highest block number is: " + bn);


    api.destroyApi();
    System.out.println("===== Demo BlockNumber finish =====");
    System.out.println();
  }

  public void DemoHelloWorld() {

    System.out.println("===============  Demo Hello contract transaction ======================");

    System.out.println("Create api instance and connect, please press enter key to go next step!");
    Scanner scan = new Scanner(System.in);
    scan.nextLine();

    INuco api = new NucoImpl();
    ApiMsg apiMsg = api.connect(url);
    if (apiMsg.error()) {
      System.out.println("Connect server failed, exit test! " + apiMsg.getErrString());
      return;
    }
    System.out.println("Get server connected!");
    System.out.println();


    System.out.println("Get accounts from server, please press enter the key to go next step!");
    scan = new Scanner(System.in);
    scan.nextLine();

    apiMsg.set(api.getWallet().getAccounts());
    if (apiMsg.error()) {
      System.out.println("GetAccounts failed! " + apiMsg.getErrString());
      return;
    }
    List accs = apiMsg.getObject();

    if (accs.size() < 1) {
      System.out.println("The number of accounts in the server is lower than 1, please check the server has a least 1 accounts to support the test!");
      return;
    }

    System.out.println("Get " + accs.size() + " accounts!");

    byte[] acc = (byte[]) accs.get(0);
    System.out.println("Get the first account: " + IUtils.bytes2Hex(acc));
    System.out.println();

    // unlockAccount before deployContract or send a transaction.
    System.out.println("Unlock account before deploy smart contract or send transactions.");
    System.out.println("Please press the enter key to go next step!");
    scan = new Scanner(System.in);
    scan.nextLine();
    System.out.println("Please input the password of the first account: ");
    String password = scan.nextLine();


    apiMsg.set(api.getWallet().unlockAccount(acc, password, 300));
    if (apiMsg.error() || !(boolean)apiMsg.getObject()) {
      System.out.println("Unlock account failed! Please check your password input! " + apiMsg.getErrString());
      return;
    }

    System.out.println("Account unlocked!");
    System.out.println();

    System.out.println("Prepare to deploy the token contract.");
    System.out.println("Please press the enter key to go next step!");
    scan = new Scanner(System.in);
    scan.nextLine();

    IContract contract = Contract.createFromSource(helloworldSC, api, acc);
    if (contract.error()) {
      System.out.println("Deploy contract failed!" + contract.getErrorCode());
      return;
    }

    System.out.println("Contract deployed!");
    System.out.println();

    //Check initial default account balance
    System.out.println("Prepare print greenting!");
    scan = new Scanner(System.in);
    scan.nextLine();

    apiMsg.set(contract.newFunction("greeting")
            .build()
            .execute());

    if (apiMsg.error()) {
      System.out.println("Function exceution error! " + apiMsg.getErrString());
      return;
    }

    Types.ContractResponse contractResponse = apiMsg.getObject();
    if (contractResponse.error()) {
        System.out.println("ContractResponse error! " + contractResponse.statusToString());
    }

    for (Object a : contractResponse.data) {
        System.out.println(a.toString());
    }

    System.out.println();
    System.out.println("Disconnect connection between api and node!");
    api.destroyApi();
    System.out.println("===============  Demo helloworld contract finish ======================");
    System.out.println();
  }

  public void DemoTokenContract() {

    System.out.println("===============  Demo token contract transaction ======================");

    System.out.println("Create api instance and connect, please press enter key to go next step!");
    Scanner scan = new Scanner(System.in);
    scan.nextLine();

    INuco api = new NucoImpl();
    ApiMsg apiMsg = api.connect(url);
    if (apiMsg.error()) {
      System.out.println("Connect server failed, exit test! " + apiMsg.getErrString());
      return;
    }
    System.out.println("Get server connected!");
    System.out.println();


    System.out.println("Get accounts from server, please press enter the key to go next step!");
    scan = new Scanner(System.in);
    scan.nextLine();

    apiMsg.set(api.getWallet().getAccounts());
    if (apiMsg.error()) {
      System.out.println("GetAccounts failed! " + apiMsg.getErrString());
      return;
    }
    List accs = apiMsg.getObject();

    if (accs.size() < 2) {
      System.out.println("The number of accounts in the server is lower than 2, please check the server has a least 2 accounts to support the test!");
      return;
    }

    System.out.println("Get " + accs.size() + " accounts!");

    byte[] acc = (byte[]) accs.get(0);
    System.out.println("Get the first account: " + IUtils.bytes2Hex(acc));
    byte[] acc2 = (byte[]) accs.get(1);
    System.out.println("Get the second account: " + IUtils.bytes2Hex(acc2));
    System.out.println();

    // unlockAccount before deployContract or send a transaction.
    System.out.println("Unlock account before deploy smart contract or send transactions.");
    System.out.println("Please press the enter key to go next step!");
    scan = new Scanner(System.in);
    scan.nextLine();
    System.out.println("Please input the password of the first account: ");
    String password = scan.nextLine();


    apiMsg.set(api.getWallet().unlockAccount(acc, password, 300));
    if (apiMsg.error() || !(boolean)apiMsg.getObject()) {
      System.out.println("Unlock account failed! Please check your password input! " + apiMsg.getErrString());
      return;
    }

    System.out.println("Account unlocked!");
    System.out.println();

    ArrayList<SolidityAbstractType> param = new ArrayList<>();
    param.add(IUint.copyFrom(100000));
    param.add(ISString.copyFrom("Nuco coin"));
    param.add(IUint.copyFrom(10));
    param.add(ISString.copyFrom("NUC"));

    System.out.println("Prepare to deploy the token contract.");
    System.out.println("Please press the enter key to go next step!");
    scan = new Scanner(System.in);
    scan.nextLine();

    IContract contract = Contract.createFromSource(tokenSC, api, acc, param);
    if (contract.error()) {
      System.out.println("Deploy contract failed!" + contract.getErrorCode());
      return;
    }

    System.out.println("Contract deployed!");
    System.out.println();

    //Check initial default account balance
    System.out.println("Check the balance of the first account, please press the enter key to go next step!");
    scan = new Scanner(System.in);
    scan.nextLine();

    apiMsg.set(contract.newFunction("balanceOf")
        .setParam(IAddress.copyFrom(acc))
        .build()
        .nonBlock()
        .execute());

    if (apiMsg.error()) {
      System.out.println("Function exceution error! " + apiMsg.getErrString());
      return;
    }

    Types.ContractResponse contractResponse = apiMsg.getObject();

    for (Object a : contractResponse.data) {
      System.out.println("The initial balance Of the first account " + IUtils.bytes2Hex(acc) + " is " + a.toString());
    }

    IContract tmp = contract.newFunction("transfer")
        .setFrom(acc)
        .setParam(IAddress.copyFrom(acc2))
        .setParam(IUint.copyFrom(1))
        .build();

    if (tmp.error()) {
      System.out.println("Function build error! " + tmp.getErrString());
      return;
    }

    System.out.println("Prepare to send 1 transaction, send 1 unit each transaction from the first account to the second account!");
    System.out.println("Please press the enter key to go next step!");
    scan.nextLine();


    apiMsg.set(tmp.execute());
    if (apiMsg.error()) {
      System.out.println("Send token failed! " + apiMsg.getErrString());
    } else {
      System.out.println("Token sent!");
    }

    System.out.println();
    System.out.println("Check the blance of the first account!");
    System.out.println("Please press the enter key to go next step!");
    scan.nextLine();

    apiMsg.set(contract.newFunction("balanceOf")
        .setParam(IAddress.copyFrom(acc))
        .build()
        .nonBlock()
        .execute());

    if (apiMsg.error()) {
      System.out.println("Function balanceOf error! Account: " + IUtils.bytes2Hex(acc) + apiMsg.getErrString());
      return;
    }

    contractResponse = apiMsg.getObject();

    if (contractResponse.error()) {
      System.out.println("ContractResponse error! " + contractResponse.statusToString());
    }

    for (Object a : contractResponse.data) {
      System.out.println("The balance of " + IUtils.bytes2Hex(acc) + ": " + a.toString());
    }

    System.out.println();
    System.out.println("Check the blance of the second account!");
    System.out.println("Please press the enter key to go next step!");
    scan.nextLine();

    apiMsg.set(contract.newFunction("balanceOf")
        .setParam(IAddress.copyFrom(acc2))
        .build()
        .nonBlock()
        .execute());

    if (apiMsg.error()) {
        System.out.println("Function balanceOf error! Account: " + IUtils.bytes2Hex(acc2) + apiMsg.getErrString());
        return;
    }

    contractResponse = apiMsg.getObject();

    for (Object a : contractResponse.data) {
        System.out.println("The balance of " + IUtils.bytes2Hex(acc2) + ": " + a.toString());
    }

    System.out.println();
    System.out.println("Disconnect connection between api and node!");
    api.destroyApi();
    System.out.println("===============  Demo token contract finish ======================");
    System.out.println();
  }
}