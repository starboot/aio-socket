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
package cn.starboot.socket.exception;

/**
 * aio-socket编码异常处理
 */
public class AioEncoderException extends Exception {

	/* uid */
	private static final long serialVersionUID = 7021199862501354906L;

	public AioEncoderException() {
        super();
    }

    public AioEncoderException(String message) {
        super(message);
    }

    public AioEncoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public AioEncoderException(Throwable cause) {
        super(cause);
    }

    protected AioEncoderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
