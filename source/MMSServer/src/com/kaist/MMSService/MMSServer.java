package com.kaist.MMSService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class MMSServer {
	private static final int PORT=8080;
	
	public static void main(String[] args){
		System.out.println("[MMS Server] Now starting MMS server");
		EventLoopGroup parentGroup = new NioEventLoopGroup(1);
		EventLoopGroup childGroup = new NioEventLoopGroup();
		try{
			// asdfasdf asdfd  
			// ���� ��Ʈ��Ʈ���� ����ϴ�. �� Ŭ������ ������ ���� Ŭ�����Դϴ�.
			// �� Ŭ������ ����ϸ� �������� Channel�� ���� ���� �� �� �ֽ��ϴ�.
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(parentGroup, childGroup)
			// ��Ŀ�� Ŀ�ؼ��� �׼����ϱ� ���� ���ο� ä���� ��üȭ �ϴ� Ŭ���� �����մϴ�.
			.channel(NioServerSocketChannel.class)
			// ���� Channel ������ ���� �ɼ��� ������ �� �ֽ��ϴ�.
			.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.INFO))
			// ���Ӱ� �׼����� Channel�� ó���մϴ�.
			// ChannelInitializer�� Ư���� �ڵ鷯�� ���ο� Channel��
			// ȯ�� ������ ���� �ִ� ���� �����Դϴ�.
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel sc) throws Exception {
					ChannelPipeline cp = sc.pipeline();
					cp.addLast(new EhcoServerHandler());
				}
			});

			// ��Ŀ�� Ŀ�ؼ��� �׼����ϱ� ���� ���ε��ϰ� �����մϴ�.
			ChannelFuture cf = sb.bind(PORT).sync();

			// ���� ������ ���������� ����մϴ�.
			cf.channel().closeFuture().sync();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}
}

class EhcoServerHandler extends ChannelHandlerAdapter {
	
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ctx.write(msg); // �޽����� �״�� �ٽ� write �մϴ�.
	}

	// ä�� �д� ���� �Ϸ����� �� ������ �ڵ带 ���� �մϴ�.
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush(); // ���ؽ�Ʈ�� ������ �÷����մϴ�.
	};

	// ���ܰ� �߻��� �� ������ �ڵ带 ���� �մϴ�.
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace(); // �׿��ִ� Ʈ���̽��� ����մϴ�.
		ctx.close(); // ���ؽ�Ʈ�� �����ŵ�ϴ�.
	}
}
