package com.mx.redis;

import com.mx.mxmq.Message;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;

import java.util.concurrent.Semaphore;

public class RedissonTest {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.168.131:6379");
        config.useClusterServers().setScanInterval(2000).addNodeAddress().addNodeAddress().addNodeAddress();
        RedissonClient redissonClient = Redisson.create(config);
    }
}
