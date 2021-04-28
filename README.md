# did-pipeline

Metadium block chain 상에 존재하는 DID 발급 기록을 저장소로 옮기는 data pipeline.  
데이터 저장소는 구체적으로 정해지진 않았으나 아마 elastic search 를 사용하지 않을까 함.

## Data flow
Metadium blockchain --> Spring boot scheduler --> Elasticsearch --> Visualization tool