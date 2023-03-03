## **(JAVA) SpringBoot - MySQL Binlog CDC**

**Debezuim에서 사용되는 MySQL Binlog Connector 라이브러리를 활용**

> MySQL Binlog Connector 기능

* Auto binlog 파일 이름 /위치 추적
* TLS 보안 통신
* JMX 친화적
* 실시간 통계
* 타사 종속성 없음
* Maven Central의 가용성

---

### 1. MySQL settings

실행을 위한 기본 설정  
```
-- CDC 용 계정 권한 부여 
mysql> GRANT SELECT, PROCESS, FILE, SUPER, REPLICATION CLIENT, REPLICATION SLAVE, RELOAD ON *.* TO mysql@'%';
mysql> flush privileges;
```

```
-- 플러그인을 caching_sha2_password => mysql_native_password 로 변경 (선택)
mysql> ALTER USER 'mysql'@'%' IDENTIFIED WITH mysql_native_password BY 'mysql';
```

```
-- my.cnf 설정 (선택)  
$ vi /etc/mysql/my.cnf

[mysqld]
binlog-do-db = test
```

### 2. SpringBoot 코드 작성

Tapping into MySQL Replication Stream  
```
-- BinlogConfiguration.java
-- BinlogEvnetHandler.java
-- BinlogEventRunner.java
```

### 3. 테스트

INSERT/DELETE/UPDATE 결과를 로그로 확인  
```
-- 프로시저 작성
CREATE PROCEDURE `INSERT_DATA` ()
BEGIN
	DECLARE i INT DEFAULT 1;
    WHILE i <= 50 DO
        INSERT INTO test.post (id, name) VALUES (i, 'TEST');
        SET i = i + 1;
        COMMIT;
    END WHILE;
END

-- 실행
CALL INSERT_DATA();
```

### 업데이트(23-03-03)

MySQL 버전 8.0.1 이상에서는 이진 로그에 열 이름이 추가되어 응용 프로그램이 테이블의 특정 열을 더 쉽게 식별하고 처리할 수 있게 됨.    
그러나 기본적으로 로그 크기를 줄이고 성능을 향상시키기 위해 열 이름은 이진 로그에 포함되지 않음   
즉, 이진 로그에 열 이름을 포함하려면binlog_row_metadata 변수를 FULL로 설정  
```
SET GLOBAL binlog_row_metadata='FULL';
```

### 참고
* [mysql-binlog-connector-java](https://github.com/shyiko/mysql-binlog-connector-java)    
* [mysql-binlog-connector-java-master](https://www.javatips.net/api/mysql-binlog-connector-java-master/src/test/java/com/github/shyiko/mysql/binlog/BinaryLogClientTest.java#)
