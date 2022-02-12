
1. `NioServerSocketChannel`  与`NioSocketChannel`  都继承自 `AbstractChannel`

   

2. `NioServerSocketChannel`  的创建时机

   在服务器端，服务启动时就会创建该对象，关键代码为: 

   ```java
   		// ... 省去无关代码
           try {
               ServerBootstrap b = new ServerBootstrap();
               b.group(bossGroup, workerGroup)
                   // 在这里注册该channel
                .channel(NioServerSocketChannel.class)
           // ... 省去无关代码
   ```

   

3. `NioSocketChannel`的创建时机