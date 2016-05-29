package com.jeffcaijf.nettyinaction;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by jeff on 5/29/16.
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
//        if (args.length == 1) {
//            System.err.println("Usage: " + EchoServer.class.getSimpleName() + " <port>");
//        }
//        int port = Integer.parseInt(args[0]);
        new EchoServer(9999).start();
    }

    public void start() throws Exception {
        final EchoServerHandler handler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class) // Specifies the use of an NIO transport Channel
                    .localAddress(new InetSocketAddress(port)) // Sets the socket address using the specified port
                    .childHandler(new ChannelInitializer<SocketChannel>() { // Adds a EchoServerHandler to the Channel's ChannelPipeline
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(handler); // EchoServerHandler is @Sharable so we can always use the same one
                }
            });
            ChannelFuture f = b.bind().sync(); // Binds the server async; sync() waits for the bind to complete
            f.channel().closeFuture().sync(); // Gets the CloseFuture of the Channel and blocks the current thread until it's complete
        } finally {
            group.shutdownGracefully().sync(); // Shuts down the EventLoopGroup, releasing all resources
        }
    }

}
