# did-pipeline

Metadium block chain 상에 존재하는 DID 발급 데이터를 저장소로 옮기는 data pipeline.
저장소로 옮겨진 DID 발급 데이터를 Kibana 로 시각화하면 다음과 같다.

![스크린샷 2021-05-13 오후 5 02 16](https://user-images.githubusercontent.com/41066039/118096970-07996500-b40d-11eb-9dbf-5bcbf7b7ba1f.png)

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

* ```Home``` > ```Manage``` > ```Saved Objects``` > ```Import``` > [dashboard.ndjson](./docker/kibana/dashboard.ndjson) 파일 import
* ```Home``` > ```Dashboard``` > Dashboard 열기.
* Dashboard 우측 상단 time range 조정하면서 데이터 모니터링.

<img width="1604" alt="스크린샷 2021-05-13 오후 5 53 57" src="https://user-images.githubusercontent.com/41066039/118102980-61e9f400-b414-11eb-8643-490780f888fd.png">

## IdentityRegistry wrapper class

이 pipeline 은 IdentityRegistry 라는 ```solidity``` smart contract 에서 발생한 event 데이터를 읽어오고 있다. 해당 smart contract 와 통신하기 위해
[IdentityRegistry](./src/main/java/io/omnipede/data/didpipeline/service/blockchain/IdentityRegistry.java) 라는 ```JAVA``` wrapper class 를 만들었다. 만드는 방법은 다음과 같다.

```
$ web3j generate solidity -a ./src/main/resources/smartcontract/IdentityRegistry.json -o ./src/main/java  -p io.omnipede.data.didpipeline.service.blockchain
```

```web3j``` CLI 설치방법은 [링크](http://docs.web3j.io/latest/command_line_tools/) 참조.   
혹시라도 contract 가 업데이트될 경우 새로운 contract ABI JSON 파일을 이용해서 위 방법을 이용해서 wrapper class 를 새로 만들 것.
