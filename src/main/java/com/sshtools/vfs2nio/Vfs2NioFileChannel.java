package com.sshtools.vfs2nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.RandomAccessContent;

public class Vfs2NioFileChannel
	extends FileChannel
{
	protected RandomAccessContent content;

//	public Vfs2NioFileChannel(Vfs2NioPath path, RandomAccessContent content) {
//		FileObject fileObject = toVFSPath(path).toFileObject();
//		RandomAccessContent content = fileObject.getContent().getRandomAccessContent(accessMode);
//
//	}
	
	public Vfs2NioFileChannel(RandomAccessContent content) {
		super();
		this.content = content;
	}

//	public Vfs2NioFileChannel(Vfs2NioPath path) {
//		super();
//		this.path = path;
//		// this.content = content;
//		this.content = path.toFileObject().getContent();
//	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		int result;
		
		boolean isArrayBacked;
		byte[] tmp;
		try {
			tmp = dst.array();
			isArrayBacked = true;
		} catch (UnsupportedOperationException e) {
			isArrayBacked = false;
			tmp = new byte[dst.remaining()];
		}

		if (isArrayBacked) {
			result = content.getInputStream().read(tmp, dst.position(), dst.remaining());
			if (result > 0) {
				dst.position(dst.position() + result);
			}
		} else {
			result = content.getInputStream().read(tmp);
			if (result > 0) {
				dst.put(tmp, 0, result);
			}
		}

		return result;
	}

	@Override
	public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		byte[] tmp;
		
		long start = content.getFilePointer();
		
		int pos;
		int len;
		boolean isArrayBacked;
		try {
			tmp = src.array();
			isArrayBacked = true;
		} catch (UnsupportedOperationException e) {
			isArrayBacked = false;
			tmp = new byte[src.remaining()];
		}

		len = src.remaining();			

		if (isArrayBacked) {
			pos = src.position();
		} else {
			src.get(tmp, src.position(), len);
			pos = 0;			
		}
		
		content.write(tmp, pos, len);

		long end = content.getFilePointer();

		int result = (int)(end - start);
		
		return result;
	}

	@Override
	public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long position() throws IOException {
		return content.getFilePointer();
	}

	@Override
	public FileChannel position(long newPosition) throws IOException {
		content.seek(newPosition);
		return this;
	}

	@Override
	public long size() throws IOException {
		return content.length();
	}

	@Override
	public FileChannel truncate(long size) throws IOException {
		content.setLength(size);
		return this;
	}

	@Override
	public void force(boolean metaData) throws IOException {
		// Silently ignored
		// throw new UnsupportedOperationException();
	}

	@Override
	public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
		position(position);
		int blockSize = 4096;
		byte[] b = new byte[blockSize];
		long remaining = count;
		
		long written = 0;
		InputStream in = content.getInputStream();
		while (remaining > 0) {
			int l = remaining > blockSize ? blockSize : (int)remaining;
			int n = in.read(b, 0, l);
			if (n < 0) {
				break;
			} else {
				ByteBuffer bb = ByteBuffer.wrap(b);
				while (written < n) {
					int writeContrib = target.write(bb);
					written += writeContrib;
				}
				remaining -= n;
			}
		}
		
		return written;
	}

	@Override
	public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int read(ByteBuffer dst, long position) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int write(ByteBuffer src, long position) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileLock lock(long position, long size, boolean shared) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileLock tryLock(long position, long size, boolean shared) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void implCloseChannel() throws IOException {
		content.close();
	}

}
