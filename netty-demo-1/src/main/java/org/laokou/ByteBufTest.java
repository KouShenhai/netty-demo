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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author laokou
 */
public class ByteBufTest {
    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(9,100);
        print("allocate ByteBuf(9,10)",byteBuf);
        // write方法改变写指针，写完之后写指针未到capacity
    }

    private static void print(String action,ByteBuf buffer) {
        System.out.println("after====" + action + "====");
        System.out.println("capacity():" + buffer.capacity());
        System.out.println("maxCapacity():" + buffer.maxCapacity());
        System.out.println("readIndex():" + buffer.readerIndex());
        System.out.println("readyBytes():" + buffer.readableBytes());
        System.out.println("isReadable():" + buffer.isReadable());
        System.out.println("writerIndex():" + buffer.readerIndex());
        System.out.println("writableBytes():" + buffer.writableBytes());
        System.out.println("isWriteable():" + buffer.isWritable());
        System.out.println("maxWriteableBytes():" + buffer.maxWritableBytes());
        System.out.println();
    }

}
