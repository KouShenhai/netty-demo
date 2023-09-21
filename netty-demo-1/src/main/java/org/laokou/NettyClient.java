/*
 * Copyright (c) 2022 KCloud-Platform-Alibaba Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.laokou;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

/**
 * @author laokou
 */
public class NettyClient {

    public static void main(String[] args) {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        // 指定线程模型
        bootstrap.group(workGroup)
                // 指定IO类型为NIO
                .channel(NioSocketChannel.class)
                // IO处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {

                    }
                });
        conn(bootstrap,3,1);
    }

    private static void conn(Bootstrap bootstrap,final int resty,final int i) {
        // 建立连接
        bootstrap.connect("localhost",85).addListener(f -> {
            if (resty == 0) {
                System.out.println("重试次数用完");
                return;
            }
            if (f.isSuccess()) {
                System.out.println("连接成功");
            } else {
                System.out.println("连接失败");
                int restyFinal = resty - 1;
                int iFinal = i + 1;
                int delay = 1 << iFinal;
                System.out.println(delay + "秒后重试");
                bootstrap.config().group().schedule(() -> conn(bootstrap,restyFinal,iFinal),delay, TimeUnit.SECONDS);
            }
        });
    }

}
