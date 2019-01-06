# MDLib
Message Driven Library

```java
MDContext context = new MDContext();
context.registerService("name", MDService);
```

## Send message directly to MDService
```java
result = content.message("name", message);
```

## You can even send deffered messages, which will be processed later
```java
// To ONE service
content.deferMessage("serviceName", message, callback);

// To multiple services at once
content.deferMessage("name1, name2", message, callback);
```

## To proccess the deffered messages, need to UPDATE
```java
// You can do it directly (one 'update' - one processed message)
context.update();

// Or you can start a Thread
context.startThread(interval, isDaemon);
```

# Message Types
```java
// Type1
Object message = new ...;

// Type2
MDMessage message = new MDMessage("text", object); // This is more recommended! :)
```

# MDService Example
For example: Our MDService will accept one number `n` and return `n+1`<br>
### We can do it as Big Class :) ...
```java
public class AdderService implements MDService {
  public Object accept(MDMessage msg, MDContext ctx) {
    int n = (int) msg.messageObject;
    return n + 1;
  }
}
```
### ... or as lambda (for compactness, u know)
```java
context.registerService("adder", (msg, ctx) -> ((int) msg.messageObject) + 1); // YEP!
```
