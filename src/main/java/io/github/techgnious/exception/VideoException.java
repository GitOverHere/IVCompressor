/*
 * Copyright 2020 Srikanth Reddy Anreddy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.techgnious.exception;

/**
 * Exception class to handle errors thrown during video compression
 * 
 * @author srikanth.anreddy
 *
 */
public class VideoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 378604187699000920L;

	public VideoException() {
		super();
	}

	/**
	 * Throws Video Exception error with custom message and casue
	 * 
	 * @param message
	 * @param cause
	 */
	public VideoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Throws Video Exception error with custom message
	 * 
	 * @param message
	 */
	public VideoException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public VideoException(Throwable cause) {
		super(cause);
	}

}
