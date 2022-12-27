/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.socket.utils.pool.memory;

/**
 * 创建虚拟ByteBuffer缓冲区的工厂
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface MemoryUnitFactory {

    /**
     * 在制定内存页内申请虚拟内存
     *
     * @param memoryBlock  指定内存页
     * @return            虚拟内存
     */
    MemoryUnit createBuffer(MemoryBlock memoryBlock);

}
