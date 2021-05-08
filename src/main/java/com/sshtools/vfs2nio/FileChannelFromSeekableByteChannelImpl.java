package com.sshtools.vfs2nio;

import java.nio.channels.SeekableByteChannel;

public class FileChannelFromSeekableByteChannelImpl
	extends FileChannelFromSeekableByteChannelBase
{
	public static final int DEFAULT_BLOCK_SIZE = 4096;
	
	protected SeekableByteChannel delegate;
	protected int blockSize;

	public FileChannelFromSeekableByteChannelImpl(SeekableByteChannel delegate) {
		this(delegate, DEFAULT_BLOCK_SIZE);
	}

	public FileChannelFromSeekableByteChannelImpl(SeekableByteChannel delegate, int blockSize) {
		super();
		this.delegate = delegate;
		this.blockSize = blockSize;
	}

	@Override
	protected SeekableByteChannel getSeekableByteChannel() {
		return delegate;
	}

	@Override
	protected int getBlockSize() {
		return blockSize;
	}
}

