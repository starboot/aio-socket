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
package cn.starboot.http.server.handler;

public class RestResult<T> {
	/**
	 * 成功
	 */
	public static final int SUCCESS = 200;
	/**
	 * 失败
	 */
	public static final int FAIL = 500;

	/**
	 * 是否成功
	 */
	private boolean success;

	/**
	 * 返回码
	 */
	private int code;

	/**
	 * 失败提示
	 */
	private String message;
	/**
	 * 响应数据
	 */
	private T data;

	public static <T> RestResult<T> ok(T data) {
		RestResult<T> restResult = new RestResult<>();
		restResult.setSuccess(true);
		restResult.setCode(SUCCESS);
		restResult.setData(data);
		return restResult;
	}

	public static <T> RestResult<T> fail(String message) {
		RestResult<T> restResult = new RestResult<>();
		restResult.setSuccess(false);
		restResult.setCode(FAIL);
		restResult.setMessage(message);
		return restResult;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
