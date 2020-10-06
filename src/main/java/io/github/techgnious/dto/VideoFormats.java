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
package io.github.techgnious.dto;

/**
 * Enum Class that defines the type of video formats allowed
 * 
 * @author srikanth.anreddy
 *
 */
public enum VideoFormats {

	MP4("mp4"), MKV("mkv"), FLV("flv"), MOV("mov"), AVI("avi"), WMV("wmv");

	private String type;

	VideoFormats(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
}
