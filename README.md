[![Build Status](https://travis-ci.org/rotilho/jnano-client.svg?branch=master)](https://travis-ci.org/rotilho/jnano-client)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/aa6e73f5b9964928877ad7aed3a17652)](https://www.codacy.com/app/rotilho/jnano-client?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=rotilho/jnano-client&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/aa6e73f5b9964928877ad7aed3a17652)](https://www.codacy.com/app/rotilho/jnano-client?utm_source=github.com&utm_medium=referral&utm_content=rotilho/jnano-client&utm_campaign=Badge_Coverage)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.rotilho.jnano/jnano-client/badge.svg)](https://mvnrepository.com/artifact/com.rotilho.jnano/jnano-client)


# JNano Client
JNano Client is the first Java RPC client which allows you to fully manage yours keys localy. Together with [JNano Commons](https://github.com/rotilho/jnano-commons) you will have all needed operations to create a light wallet without the need to dig in Nano implementation and focus more in delivery new awesome features.

**JNano Client is complience with [external key management](https://developers.nano.org/guides/external-management)**.

## How to use it?
_First, take a look in [JNano Commons](https://github.com/rotilho/jnano-commons). It will provide you basic knowledge about low level operations._

**Gradle**
`compile 'com.rotilho.jnano:jnano-client:1.1.2`

**Maven**
```xml
<dependency>
    <groupId>com.rotilho.jnano</groupId>
    <artifactId>jnano-client</artifactId>
    <version>1.1.2</version>
</dependency>
```

**Sample**
```java
NanoAPI nanoAPI = NanoAPI.builder().endpoint("http://my-wallet").build();
NanoAccountOperations accountOperations = NanoAccountOperations.of(NanoBaseAccountType.NANO, nanoAPI);

Optional<NanoAccountInfo> info = accountOperations.getInfo(account);
info.ifPresent(i-> log.info(i));

// Work may take several seconds, better have higher timeout
NanoAPI workNanoAPI = NanoAPI.builder().endpoint("http://my-wallet").readTimeoutMillis(100_000).build();
NanoWorkOperations workOperations = NanoRemoteWorkOperations.of(workNanoAPI);

NanoTransactionOperations transactionOperations = NanoTransactionOperations.of(NanoBaseAccountType.NANO, api, accountOperations, workOperations);

List<NanoTransaction<NanoStateBlock>> transactions = transactionOperations.receive(privateKey);
transactions.forEach(t -> log.info(t));
```

**All classes are thread safe and can be used as singleton**

## How to cache work?
The possibility to pre-cache the work give to the user a nice user experience and one of the main fatures which make Nano be almost instant.

To pre-cache work you need to just wrap a NanoWorkOperations with NanoCachedWorkOperations

```java
NanoCachedWorkOperations workOperations = NanoCachedWorkOperations.of(NanoRemoteWorkOperations.of(workNanoAPI));

// Now after finish to process a transaction you can simpltly call NanoCachedWorkOperations to pre-calculated the work
workOperations.cache(newTransaction.getHash())
```


## Missing feature?
If you notice a missing feature let me know and I'll try to do my best to implement ASAP.

Just keep in mind that I don't plan to support any wallet RPC method, they are not going to exist for too long.
