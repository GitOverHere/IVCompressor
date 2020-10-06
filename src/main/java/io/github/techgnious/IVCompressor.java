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
package io.github.techgnious;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import io.github.techgnious.constants.IVConstants;
import io.github.techgnious.dto.IVAudioAttributes;
import io.github.techgnious.dto.IVVideoAttributes;
import io.github.techgnious.dto.ImageFormats;
import io.github.techgnious.dto.ResizeResolution;
import io.github.techgnious.dto.VideoFormats;
import io.github.techgnious.exception.ImageException;
import io.github.techgnious.exception.VideoException;
import io.github.techgnious.utils.IVFileUtils;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.VideoAttributes;
import ws.schild.jave.VideoAttributes.X264_PROFILE;
import ws.schild.jave.VideoSize;

/**
 * Base Class to handle compression or conversion of Image/Video Files
 * 
 * Operations supported:
 * 
 * Resize Image Convert Image Resize Video Resize and Convert Video
 * 
 * @author srikanth.anreddy
 *
 */
public class IVCompressor {

	/**
	 * Encoder that is used to reduce the quality of video
	 */
	private Encoder encoder;

	/**
	 * Encoder attributes for handling the quality of audio
	 */
	private AudioAttributes audioAttributes;

	/**
	 * Encoder attributes for handling the quality of video
	 */
	private VideoAttributes videoAttributes;

	/**
	 * Attributes controlling the encoding process.
	 */
	private EncodingAttributes encodingAttributes;

	/**
	 * Defines the image resolution
	 */
	private ResizeResolution imageResolution = ResizeResolution.IMAGE_DEFAULT;
	/**
	 * Defines the video resolution
	 */
	private ResizeResolution videoResolution = ResizeResolution.VIDEO_DEFAULT;

	/**
	 * Instance invokes with default encode settings and attributes
	 */
	public IVCompressor() {
		super();
		encoder = new Encoder();
		videoAttributes = new VideoAttributes();
		videoAttributes.setCodec(IVConstants.VIDEO_CODEC);
		videoAttributes.setX264Profile(X264_PROFILE.BASELINE);
		// Here 160 kbps video is 160000
		videoAttributes.setBitRate(160000);
		// More the frames more quality and size, but keep it low based on devices like
		// mobile
		videoAttributes.setFrameRate(15);
		videoAttributes.setSize(new VideoSize(videoResolution.getWidth(), videoResolution.getHeight()));
		audioAttributes = new AudioAttributes();
		audioAttributes.setCodec(IVConstants.AUDIO_CODEC);
		// here 64kbit/s is 64000
		audioAttributes.setBitRate(64000);
		audioAttributes.setChannels(2);
		audioAttributes.setSamplingRate(44100);
		encodingAttributes = new EncodingAttributes();
		encodingAttributes.setVideoAttributes(videoAttributes);
		encodingAttributes.setAudioAttributes(audioAttributes);
	}

	/**
	 * This method attempts to resize the image byte stream to lower resolution
	 * 
	 * Returns resized byte stream
	 * 
	 * @param file            - file data in byte array that is to be compressed
	 * @param fileFormat      - file type
	 * @param imageResolution - Resolution of output image. Optional Field. Can be
	 *                        passed as null to use the default values
	 * @return - returns the compressed image in byte array
	 * @throws ImageException
	 */
	public byte[] resizeImage(byte[] data, ImageFormats fileFormat, ResizeResolution resolution) throws ImageException {
		if (resolution != null)
			imageResolution = resolution;
		int width = imageResolution.getWidth();
		int height = imageResolution.getHeight();
		String contentType = fileFormat.getType();
		try {
			BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(data));
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage resizedImage = new BufferedImage(width, height, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, width, height, null);
			g.dispose();
			ImageIO.write(resizedImage, contentType, outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new ImageException("ByteStream doesn't contain valid Image");
		}
	}

	/**
	 * This method attempts to resize the image file to lower resolution and saves
	 * it back in the path provided.
	 * 
	 * Returns path location of the object
	 * 
	 * @param file            - file that is to be compressed
	 * @param fileFormat      - file type
	 * @param path            - location where the file to be saved
	 * @param imageResolution - Resolution of output image.Optional Field. Can be
	 *                        passed as null to use the default values
	 * @return
	 * @throws ImageException
	 */
	public String resizeAndSaveImageToAPath(File file, ImageFormats fileFormat, String path,
			ResizeResolution imageResolution) throws ImageException {
		try {
			byte[] data = resizeImage(IVFileUtils.copyToByteArray(file), fileFormat, imageResolution);
			String fileName = file.getName();
			if (!file.getName().contains(fileFormat.getType()))
				fileName = fileName.substring(0, fileName.indexOf(".")) + "." + fileFormat.getType();
			return createAndStoreNewFile(fileName, path, data);
		} catch (IOException e) {
			throw new ImageException(e);
		}
	}

	/**
	 * This method attempts to resize the image file to lower resolution and saves
	 * it back in the path provided.
	 * 
	 * Returns path location of the object
	 * 
	 * @param fileData        - file that is to be compressed
	 * @param fileFormat      - file type
	 * @param path            - location where the file to be saved
	 * @param imageResolution - Resolution of output image.Optional Field. Can be
	 *                        passed as null to use the default values
	 * @return
	 * @throws ImageException
	 */
	public String resizeAndSaveImageToAPath(byte[] fileData, String fileName, ImageFormats fileFormat, String path,
			ResizeResolution imageResolution) throws ImageException {
		try {
			byte[] data = resizeImage(fileData, fileFormat, imageResolution);
			if (!fileName.contains(fileFormat.getType()))
				fileName = fileName.substring(0, fileName.indexOf(".")) + "." + fileFormat.getType();
			return createAndStoreNewFile(fileName, path, data);
		} catch (IOException e) {
			throw new ImageException(e);
		}
	}

	/**
	 * This method attempts to resize the image file to lower resolution
	 * 
	 * Returns resized image in byte aaray
	 * 
	 * @param file            - file that is to be compressed
	 * @param fileFormat      - file type
	 * @param imageResolution - Resolution of output image.Optional Field. Can be
	 *                        passed as null to use the default values
	 * @return
	 * @throws ImageException
	 * @throws IOException
	 */
	public byte[] resizeImageUsingFile(File file, ImageFormats fileFormat, ResizeResolution imageResolution)
			throws ImageException, IOException {
		return resizeImage(IVFileUtils.copyToByteArray(file), fileFormat, imageResolution);

	}

	/**
	 * This method attempts to resize the image received as Inputstream object to
	 * lower resolution
	 * 
	 * Returns resized stream object
	 * 
	 * @param stream          - Image input stream that is to be compressed
	 * @param fileFormat      - type of the file
	 * @param imageResolution - Resolution of the output image file.Optional Field.
	 *                        Can be passed as null to use the default values
	 * @return InputStream - returns output as a stream
	 * @throws ImageException
	 */
	public InputStream resizeImage(InputStream stream, ImageFormats fileFormat, ResizeResolution imageResolution)
			throws ImageException {
		InputStream targetStream = null;
		try {
			byte[] data = resizeImage(IVFileUtils.copyToByteArray(stream), fileFormat, imageResolution);
			targetStream = new ByteArrayInputStream(data);
			return targetStream;
		} catch (IOException e) {
			throw new ImageException(e);
		}
	}

	/**
	 * This method helps in converting the video content to reduced size with lower
	 * resolution
	 * 
	 * Encodes the video with default attributes thereby reducing the size of the
	 * video with better quality
	 * 
	 * Maintains the best compressed quality
	 * 
	 * @param data       -indicates the video content to be compressed
	 * @param fileFormat -to indicate the video type
	 * @param resolution -Resolution of the output video
	 * @return -byte stream object with compressed video data
	 * @throws VideoException -throws exception when the data is incompatible for
	 *                        the enconding
	 */
	public byte[] reduceVideoSize(byte[] data, VideoFormats fileFormat, ResizeResolution resolution)
			throws VideoException {
		String fileType = fileFormat.getType();
		encodingAttributes.setFormat(fileType);
		if (resolution != null)
			encodingAttributes.getVideoAttributes()
					.setSize(new VideoSize(resolution.getWidth(), resolution.getHeight()));
		return encodeVideo(data, fileType);
	}

	/**
	 * This method helps in converting the video content to reduced size with lower
	 * resolution
	 * 
	 * Encodes the video with default attributes thereby reducing the size of the
	 * video with better quality
	 * 
	 * Maintains the best compressed quality
	 * 
	 * @param file       -indicates the video file object to be compressed
	 * @param fileFormat -to indicate the video type
	 * @param resolution -Resolution of the output video
	 * @return byte array object with encoded video
	 * @throws VideoException
	 * @throws IOException
	 */
	public byte[] reduceVideoSize(File file, VideoFormats fileFormat, ResizeResolution resolution)
			throws VideoException, IOException {
		String fileType = fileFormat.getType();
		encodingAttributes.setFormat(fileType);
		if (resolution != null)
			encodingAttributes.getVideoAttributes()
					.setSize(new VideoSize(resolution.getWidth(), resolution.getHeight()));
		return encodeVideo(IVFileUtils.copyToByteArray(file), fileType);
	}

	/**
	 * This method helps in converting the video content to reduced size with lower
	 * resolution
	 * 
	 * Encodes the video with default attributes thereby reducing the size of the
	 * video with better quality and stores it in the user defined path .
	 * 
	 * Maintains the best compressed quality
	 * 
	 * @param file       -indicates the video file object to be compressed
	 * @param fileFormat -to indicate the video type
	 * @param resolution -Resolution of the output video
	 * @param path       -location to store the output file
	 * @return path location for the file stored
	 * @throws VideoException
	 * @throws IOException
	 */
	public String reduceVideoSizeAndSaveToAPath(File file, VideoFormats fileFormat, ResizeResolution resolution,
			String path) throws VideoException, IOException {
		String fileType = fileFormat.getType();
		encodingAttributes.setFormat(fileType);
		if (resolution != null)
			encodingAttributes.getVideoAttributes()
					.setSize(new VideoSize(resolution.getWidth(), resolution.getHeight()));
		byte[] data = encodeVideo(IVFileUtils.copyToByteArray(file), fileType);
		String fileName = file.getName();
		if (!fileName.contains(fileFormat.getType()))
			fileName = fileName.substring(0, fileName.indexOf(".")) + "." + fileFormat.getType();
		return createAndStoreNewFile(fileName, path, data);

	}

	/**
	 * This method helps in converting the video content to reduced size with lower
	 * resolution
	 * 
	 * Encodes the video with default attributes thereby reducing the size of the
	 * video with better quality and stores it in the user defined path .
	 * 
	 * Maintains the best compressed quality
	 * 
	 * @param fileData   -indicates the video file object byte array to be
	 *                   compressed
	 * @param fileFormat -to indicate the video type
	 * @param resolution -Resolution of the output video
	 * @param path       -location to store the output file
	 * @return path location for the file stored
	 * @throws VideoException
	 * @throws IOException
	 */
	public String reduceVideoSizeAndSaveToAPath(byte[] fileData, String fileName, VideoFormats fileFormat,
			ResizeResolution resolution, String path) throws VideoException, IOException {
		String fileType = fileFormat.getType();
		encodingAttributes.setFormat(fileType);
		if (resolution != null)
			encodingAttributes.getVideoAttributes()
					.setSize(new VideoSize(resolution.getWidth(), resolution.getHeight()));
		byte[] data = encodeVideo(fileData, fileType);
		if (!fileName.contains(fileFormat.getType()))
			fileName = fileName.substring(0, fileName.indexOf(".")) + "." + fileFormat.getType();
		return createAndStoreNewFile(fileName, path, data);

	}

	/**
	 * This method helps in converting the video content to reduced size with lower
	 * resolution with user defined attributes
	 * 
	 * Encodes the video with custom attributes thereby reducing the size of the
	 * video with better quality
	 * 
	 * Maintains the best compressed quality
	 * 
	 * @param data           indicates the video content to be compressed
	 * @param fileFormat     to indicate the video type
	 * @param audioAttribute to customize audio encoding
	 * @param videoAttribute to customize video encoding
	 * @return
	 * @throws VideoException
	 */
	public byte[] encodeVideoWithAttributes(byte[] data, VideoFormats fileFormat, IVAudioAttributes audioAttribute,
			IVVideoAttributes videoAttribute) throws VideoException {
		String filetype = fileFormat.getType();
		setAudioAndVideoAttributes(filetype, audioAttribute, videoAttribute);
		return encodeVideo(data, filetype);
	}

	/**
	 * This method is used to convert the video from existing format to another
	 * format without compressing the data
	 * 
	 * @param data       - data that is to be converted
	 * @param fileFormat - video format the data is to be converted
	 * @return
	 * @throws VideoException
	 */
	public byte[] convertVideoFormat(byte[] data, VideoFormats fileFormat) throws VideoException {
		String filetype = fileFormat.getType();
		AudioAttributes audioAttr = new AudioAttributes();
		VideoAttributes videoAttr = new VideoAttributes();
		encodingAttributes.setAudioAttributes(audioAttr);
		encodingAttributes.setVideoAttributes(videoAttr);
		return encodeVideo(data, filetype);
	}

	/**
	 * Method to set the user defined Audio and Video Attributes for encoding
	 * 
	 * @param fileFormat
	 * @param audioAttribute
	 * @param videoAttribute
	 */
	private void setAudioAndVideoAttributes(String fileFormat, IVAudioAttributes audioAttribute,
			IVVideoAttributes videoAttribute) {
		if (videoAttribute != null) {
			videoAttributes.setCodec(IVConstants.VIDEO_CODEC);
			videoAttributes.setX264Profile(X264_PROFILE.BASELINE);
			if (videoAttribute.getBitRate() != null)
				videoAttributes.setBitRate(videoAttribute.getBitRate());
			if (videoAttribute.getFrameRate() != null)
				videoAttributes.setFrameRate(videoAttribute.getFrameRate());
			if (videoAttribute.getSize() != null)
				videoAttributes.setSize(
						new VideoSize(videoAttribute.getSize().getWidth(), videoAttribute.getSize().getHeight()));
			encodingAttributes.setVideoAttributes(videoAttributes);
		}
		if (audioAttribute != null) {
			audioAttributes.setCodec(IVConstants.AUDIO_CODEC);
			if (audioAttribute.getBitRate() != null)
				audioAttributes.setBitRate(audioAttribute.getBitRate());
			if (audioAttribute.getChannels() != null)
				audioAttributes.setChannels(audioAttribute.getChannels());
			if (audioAttribute.getSamplingRate() != null)
				audioAttributes.setSamplingRate(audioAttribute.getSamplingRate());
			encodingAttributes.setAudioAttributes(audioAttributes);
		}
		encodingAttributes.setFormat(fileFormat);
	}

	/**
	 * This method encodes the video stream with custom encoding attributes and
	 * reduces the resolution and size of the file
	 * 
	 * @param data
	 * @param fileFormat
	 * @return
	 * @throws VideoException
	 */
	private byte[] encodeVideo(byte[] data, String fileFormat) throws VideoException {
		File target = null;
		File file = null;
		try {
			target = File.createTempFile(IVConstants.TARGET_FILENAME, fileFormat);
			file = File.createTempFile(IVConstants.SOURCE_FILENAME, fileFormat);
			FileUtils.writeByteArrayToFile(file, data);
			MultimediaObject source = new MultimediaObject(file);
			encoder.encode(source, target, encodingAttributes);
			return FileUtils.readFileToByteArray(target);
		} catch (Exception e) {
			throw new VideoException("Error Occurred while resizing the video");
		} finally {
			try {
				if (file != null)
					Files.deleteIfExists(file.toPath());
				if (target != null)
					Files.deleteIfExists(target.toPath());
			} catch (IOException e) {
				// ignore
			}

		}
	}

	/**
	 * Method to create a file and store data in local path
	 * 
	 * @param fileName -indicates the file name of the output object
	 * @param path     -location to store the output file
	 * @param data     -byte array of the output file
	 * @return
	 * @throws IOException
	 */
	private String createAndStoreNewFile(String fileName, String path, byte[] data) throws IOException {
		checkForValidPath(path);
		String fullPath = path;
		if (!path.endsWith(File.separator))
			fullPath += File.separator;
		fullPath += fileName;
		File newFile = new File(fullPath);
		FileUtils.writeByteArrayToFile(newFile, data);
		return "File is saved in path::" + newFile.getAbsolutePath();
	}

	/**
	 * Method to check if input path is valid or not
	 * 
	 * @param path
	 * @throws IOException
	 */
	private void checkForValidPath(String path) throws IOException {
		try {
			Paths.get(path);
		} catch (Exception e) {
			throw new IOException("Invalid Path");
		}

	}

}
