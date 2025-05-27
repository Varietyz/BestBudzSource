package com.bestbudz.core;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelException;

import com.bestbudz.GameDataLoader;
import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.network.PipelineFactory;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.StonerBackupTask;
import com.bestbudz.core.util.LineCounter;
import com.bestbudz.core.util.Stopwatch;
import com.bestbudz.core.util.SystemLogger;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.impl.GroundItemHandler;
import com.bestbudz.rs2.entity.object.ObjectManager;

public class GameThread extends Thread {

  private static final Logger logger = Logger.getLogger(GameThread.class.getSimpleName());

  private GameThread() {
    setName("Main Thread");
    setPriority(Thread.MAX_PRIORITY);
    start();
  }

  public static void init() {
    try {
      try {
        startup();
      } catch (Exception e) {
        e.printStackTrace();
      }
      new GameThread();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void startup() throws Exception {
    Stopwatch timer = new Stopwatch().reset();

    logger.info("Launching BestBudz..");

    if (!BestbudzConstants.DEV_MODE) {
      System.setErr(new SystemLogger(System.err, new File("./data/logs/err")));
    }

    if (BestbudzConstants.DEV_MODE) {
      LineCounter.run();
    }

    logger.info("Loading game data..");

    GameDataLoader.load();

    logger.info("Loading character backup strategy..");
    TaskQueue.queue(new StonerBackupTask());

    while (!GameDataLoader.loaded()) {
      Thread.sleep(200);
    }

    logger.info("Binding port and initializing threads..");

	  EventLoopGroup bossGroup;
	  EventLoopGroup workerGroup;
	  Class<? extends ServerSocketChannel> serverChannelClass;
	  IoHandlerFactory ioHandlerFactory;

	  if (Epoll.isAvailable()) {
		  ioHandlerFactory = EpollIoHandler.newFactory();
		  serverChannelClass = EpollServerSocketChannel.class;
		  logger.info("Using Epoll Event Loop");
	  } else if (KQueue.isAvailable()) {
		  ioHandlerFactory = KQueueIoHandler.newFactory();
		  serverChannelClass = KQueueServerSocketChannel.class;
		  logger.info("Using KQueue Event Loop");
	  } else {
		  ioHandlerFactory = NioIoHandler.newFactory();
		  serverChannelClass = NioServerSocketChannel.class;
		  logger.warning("Falling back to NIO Event Loop");
	  }

	  bossGroup = new MultiThreadIoEventLoopGroup(1, ioHandlerFactory);
	  workerGroup = new MultiThreadIoEventLoopGroup(ioHandlerFactory);

	  ServerBootstrap serverBootstrap = new ServerBootstrap();
	  serverBootstrap.group(bossGroup, workerGroup)
		  .channel(serverChannelClass)
		  .childHandler(new PipelineFactory());


	  new LoginThread();
    new NetworkThread();

    while (true) {
      try {
        serverBootstrap.bind(new InetSocketAddress(42000));
        break;
      } catch (ChannelException e2) {
        logger.info("Server could not bind port - sleeping..");
        Thread.sleep(2000);
      }
    }

    logger.info("Server successfully launched. [Took " + timer.elapsed() / 1000 + " seconds]");
  }

  private void cycle() {
    try {
      TaskQueue.process();
      GroundItemHandler.process();
      ObjectManager.process();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
		long start = System.nanoTime();
		World.process();
		System.out.println("World.process(): " + ((System.nanoTime() - start) / 1_000_000.0) + "ms");

	} catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    int gameTick = 300; // Normal: 600 (300 = Double speed)
    try {
      while (!Thread.interrupted()) {
        long s = System.nanoTime();
        cycle();
        long e = (System.nanoTime() - s) / 1_000_000;
        if (e < gameTick) {

          if (e < 400) {
            for (int i = 0; i < 30; i++) {
              long sleep = (gameTick - e) / 30;
              Thread.sleep(sleep);
            }
          } else {
            Thread.sleep(gameTick - e);
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
