# did-pipeline

Metadium block chain 상에 존재하는 DID 발급 기록을 저장소로 옮기는 data pipeline.

## TODO
* Add kibana graph screenshot to README.md
* Kibana discovery 기본 화면 설정하기

## Data flow
Metadium blockchain --> Spring boot scheduler --> Elasticsearch --> Visualization tool

## Requirements
* ***JAVA8***
* ***Docker***

## How to use

* JAR build 및 docker container 시작
```
$ ./gradlew clean build --exclude-task test
$ cd docker && docker-compose up -d
```

* 브라우저로 ```http://localhost:5601``` 에 접속하여 ID/PW 입력 ([application-docker.yaml](src/main/resources/application-docker.yaml) 파일의 elasticsearch ID/PW 참조)

## IdentityRegistry wrapper class

이 pipeline 은 IdentityRegistry 라는 ```solidity``` smart contract 에서 발생한 event 데이터를 읽어오고 있다. 해당 smart contract 와 통신하기 위해
[IdentityRegistry](./src/main/java/io/omnipede/data/didpipeline/service/blockchain/IdentityRegistry.java) 라는 ```JAVA``` wrapper class 를 만들었다. 만드는 방법은 다음과 같다.

```
$ web3j generate solidity -a ./src/main/resources/smartcontract/IdentityRegistry.json -o ./src/main/java  -p io.omnipede.data.didpipeline.service.blockchain
```

```web3j``` CLI 설치방법은 [링크](http://docs.web3j.io/latest/command_line_tools/) 참조.   
혹시라도 contract 가 업데이트될 경우 위 방법을 이용해서 wrapper class 를 새로 만들 것.
