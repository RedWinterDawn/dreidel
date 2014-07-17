Dreidel
=======

##Getting Started

+ [dreidel-spinnit-postgres](dreidel-spinnit-postgres)
+ [dreidel-spinnit-jinst](dreidel-spinnit-jinst)



###US4750/3989 Release notes

###Jinst/Jim integration

+ Dreidel can now spin up Jinst images in QA using Jim
+ Dreidel will now notify you when the server is booted and jinsting is complete

===

###US3176 Release Notes


### Dreidel WebSockets Refactoring


71 source code files were changed as a part of this refactoring:

 - 51 files were added
 - 12 files were modified
 - 8 files were deleted
 
####General Changes made:
- The REST client and server were replaced by websockets and a new message API using the jivewire API
- A Dreidel API project was created and organized into exceptions, interfaces, messages, replies, and transport
- Feature parity was achieved by implementing `PostgresCreateMessage`, `PostgresSqlMessage`, and `PostgresRestoreMessage`

####Server (Rabbi) changes:
- The old REST server has been completely removed along with it's dependencies.
- The server (Rabbi) now talks over websockets using the Jivewire API
- A `ResourceFactory` interface was created and implemented for getting a postgres resource
- A `WebSocketService` was created for running Dreidel in a jazz container
- A `WebSocketServiceModule` were created for dependency injection using Guice
- `DreidelTransportConnectionListener` and `DreidelTransportListener` for handling incoming websocket requests

####Client (Spinnit) changes:
- The client (Spinnit) now talks over websockets using the Jivewire API
- The old REST client has been completely removed along with it's dependencies.
- Integration tests that require the presence of the server have been modified and renamed so that Jenkins builds do not require them to succeed in order to build properly. Otherwise they would failed before a deploy and succeed afterwords.
- `DreidelConnection` was abstracted out to an interface so that a mocked connection (`MockedDreidelConnection`) could be created.
- Unit tests were added that use the mocked Driedel connection and can run as part of a Jenkins build project.

####API contents:
######The following API exceptions were created: 
 * MessageDecodeException()
 * ResourceDestructionException()
 * ResourceInitializationException()    

######The following API interfaces were created:
 * MessageCategoryVisitable<Reply, Context>
 * MessageCategoryVisitor<Reply, Context>
 * PostgresVisitable<Reply, Context>
 * PostgresVisitor<Reply, Context>      

######The following API messages were created:
 * ConnectionInformationMessage(referenceId,connections)
 * ExceptionMessage(exceptionType,exceptionMessage,referenceId)
 * (abstract) Message
 * (abstract) OneWayMessage
 * (abstract) ReplyMessage
 * (abstract) RequestMessage
 * SuccessMessage(referenceId)
 * (abstract) TwoWayMessage
 * VisitorContext(id)
 * /postgres/PostgresCreateMessage(referenceId)
 * /postgres/PostgresExecSqlMessage(referenceId,sql,id)
 * (abstract) /postgres/PostgresRequestMessage
 * /postgres/PostgresRestoreMessage(referenceId,id,pgDump)  

######The following API replies were created:  
 * ConnectionInformation
 * ConnectionInformationReply
 * Credential
 * Reply
 * SuccessReply
 * UsernamePasswordCredential

######The following API transports were created:
 * DreidelObjectMapper
 * DreidelTransportCodec
 * MessageCorrelationStrategy
