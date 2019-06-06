description: Some use cases of creating transactions on a Pantheon network
<!--- END of page meta data -->

# Creating and Sending Transactions

You can send signed transactions using the [`eth_sendRawTransaction`](../../Reference/Pantheon-API-Methods.md#eth_sendrawtransaction) JSON-RPC API method.

Any signed transaction is supported by this method, either simple value transaction, contract method
interaction transaction or contract creation transaction.

The following example scripts shows how to create a signed raw transaction that can be passed to 
[`eth_sendRawTransaction`](../../Reference/Pantheon-API-Methods.md#eth_sendrawtransaction) to send Ether
and create a smart contract.

!!! attention "Preserve your private keys"

    This page shows examples that include private keys as hard-coded values.
    All accounts and private keys in the examples are from the `dev.json` genesis file in the 
    [`/pantheon/ethereum/core/src/main/resources`](https://github.com/PegaSysEng/pantheon/tree/master/config/src/main/resources) directory.

    For production environment, avoid exposing your private keys, you should create signed transactions 
    offline or use [EthSigner](https://docs.ethsigner.pegasys.tech/) to isolate your private keys and 
    sign transaction with [`eth_sendTransaction`](https://docs.ethsigner.pegasys.tech/Using-EthSigner/Using-EthSigner/#eth_sendtransaction). 
                     
!!! tip
    Either with or without [EthSigner](https://docs.ethsigner.pegasys.tech/),
    other libraries such as [web3j](https://github.com/web3j/web3j) or [ethereumj](https://github.com/ethereum/ethereumj)
    and tools such as [MyEtherWallet](https://kb.myetherwallet.com/offline/making-offline-transaction-on-myetherwallet.html) 
    or [MyCrypto](https://mycrypto.com/) can also be used to create signed transactions.

You can use the example Javascript scripts to create and send raw transactions in the private network 
created by the [Private Network Quickstart](../../Tutorials/Private-Network-Quickstart.md).

You must use the `JSON-RPC endpoint` in the following examples to the endpoint for the private 
network displayed after running the `./run.sh` script.

## Example Javascript scripts

### 1 Requirements

- [Node.js (version > 10)](https://nodejs.org/en/download/) must be installed to run these Javascript scripts. 
- The examples use [web3.js 1.0.0 beta](https://github.com/ethereum/web3.js/) and [ethereumjs 1.3](https://github.com/ethereumjs/ethereumjs-tx)
libraries to create signed transactions. These dependencies are defined in the included 
[`package.json`](scripts/package.json) file.

### 2 Create A New Directory
```bash
mkdir example_scripts
```

### 3 Copy Files
Copy the following files in the new `example_scripts` directory :

- [`package.json`](scripts/package.json)
- [`create_value_raw_transaction.js`](scripts/create_value_raw_transaction.js)
- [`create_contract_raw_transaction.js`](scripts/create_contract_raw_transaction.js)

### 4 Retrieve Dependencies
```bash
cd example_scripts
npm install
```

### 5 Send Ether
The following is the example JavaScript script that displays a signed raw transaction string to send Ether.

??? example "Send Ether example : create_value_raw_transaction.js"
    ```javascript linenums="1"
{! Using-Pantheon/Transactions/scripts/create_value_raw_transaction.js !}
     
    ```

To use this script, run :

```bash tab="Command"
node create_value_raw_transaction.js <YOUR JSON-RPC HTTP ENDPOINT>
```

```bash tab="Example"
node create_value_raw_transaction.js http://localhost:32770/jsonrpc
```

!!! tip
    If your JSON-RPC HTTP endpoint is `http://localhost:8545`, this is the default and you don't need
    to specify it. Just run `node create_value_raw_transaction.js`
    
A transaction raw data will be displayed.

You will then be asked if you want to send this raw transaction by yourself or let the script send it using the web3.js library.

If you decide to send it by yourself, the cURL command will be displayed, ready to be cut and paste.

Otherwise, the script will send it and display the receipt.

### 6 Create A Smart Contract
The following is the example JavaScript script that displays a signed raw transaction string to create a contract.

??? example "Create a contract example : create_contract_raw_transaction.js"    
    ```javascript linenums="1"
{! Using-Pantheon/Transactions/scripts/create_contract_raw_transaction.js !}
     
    ```

To use this script, run :

```bash tab="Command"
node create_contract_raw_transaction.js <YOUR JSON-RPC HTTP ENDPOINT>
```

```bash tab="Example"
node create_contract_raw_transaction.js http://localhost:32770/jsonrpc
```

!!! tip
    If your JSON-RPC HTTP endpoint is `http://localhost:8545`, this is the default and you don't need
    to specify it. Just run `node create_contract_raw_transaction.js`

A transaction raw data will be displayed.

You will then be asked if you want to send this raw transaction by yourself or let the script send it using the web3.js library.

If you decide to send it by yourself, the cURL command will be displayed, ready to be cut and paste.

Otherwise, the script will send it and display the receipt.

## eth_call vs eth_sendRawTransaction

You can interact with contracts using [eth_call](../../Reference/Pantheon-API-Methods.md#eth_call) 
or [eth_sendRawTransaction](../../Reference/Pantheon-API-Methods.md#eth_sendrawtransaction). 
The table below compares the characteristics of both calls.

| eth_call                                                | eth_sendRawTransaction                                                                                                         |
|---------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| Read-only                                               | Write                                                                                                                          |
| Invokes contract function locally                       | Broadcasts to network                                                                                                          |
| Does not change state of blockchain                     | Updates blockchain (for example, transfers ether between accounts)                                                             |
| Does not consume gas                                    | Requires gas                                                                                                                   |
| Synchronous                                             | Asynchronous                                                                                                                   |
| Return value of contract function available immediately | Returns transaction hash only.  Possible transaction may not be included in a block (for example, if the gas price is too low). |
