# did-pipeline

Metadium block chain 상에 존재하는 DID 발급 기록을 저장소로 옮기는 data pipeline.  
데이터 저장소는 구체적으로 정해지진 않았으나 아마 elastic search 를 사용하지 않을까 함.

## Data flow
Metadium blockchain --> Spring boot scheduler --> Elasticsearch --> Visualization tool

## Requirements
* JAVA8
* Docker

## How to create wrapper class of smart contract
```IdentityRegistry``` 라는 smart contract 에서 발생한 event 를 읽어오고 있다. 
해당 contract 에서 이벤트를 읽기 위해서는 ```web3j``` CLI 를 이용해서 JAVA wrapper class 를 만들어야 한다. 만드는 방법은 다음과 같다.

```
$ web3j generate solidity -a ./src/main/resources/smartcontract/IdentityRegistry.json -o ./src/main/java  -p io.omnipede.data.didpipeline.service.blockchain
```

```web3j``` CLI 설치방법은 [링크](http://docs.web3j.io/latest/command_line_tools/) 참조. 