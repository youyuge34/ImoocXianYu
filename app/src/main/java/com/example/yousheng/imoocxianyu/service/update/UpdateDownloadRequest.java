package com.example.yousheng.imoocxianyu.service.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.yousheng.imoocsdk.constant.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**********************************************************
 * @文件名称：UpdateDownloadRequest.java
 * @文件作者：renzhiqiang
 * @创建时间：2015年8月26日 下午10:58:03
 * @文件描述：真正执行下载任务的Runnable,负责文件下载,里面还有handler负责线程间通信的回调
 * 			注意，只有run()方法会执行在线程池，其余都在主线程
 * @修改历史：2015年8月26日创建初始版本
 **********************************************************/
public class UpdateDownloadRequest implements Runnable {
	private int startPos = 0;
	private String downloadUrl;
	private String localFilePath;
	private UpdateDownloadListener downloadListener;
	private DownloadResponseHandler downloadHandler;
	private boolean isDownloading = false;
	private long contentLength;

	public UpdateDownloadRequest(String downloadUrl, String localFilePath, UpdateDownloadListener downloadListener) {
		LogUtils.d("download","UpdateDownloadRequest current thread----->"+Thread.currentThread().getName()+"  id="+Thread.currentThread().getId());
		this.downloadUrl = downloadUrl;
		this.localFilePath = localFilePath;
		this.downloadListener = downloadListener;
		downloadHandler = new DownloadResponseHandler();
		isDownloading = true;
	}

	private void makeRequest() throws IOException, InterruptedException {
		if (!Thread.currentThread().isInterrupted()) {
			try {
				URL url = new URL(downloadUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Range", "bytes=" + startPos + "-");
				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.connect();
				contentLength = connection.getContentLength();
				if (!Thread.currentThread().isInterrupted()) {
					if (downloadHandler != null) {
						//真正文件的下载
						downloadHandler.sendResponseMessage(connection.getInputStream());//取得与远程文件的流
					}
				}
			} catch (IOException e) {
				if (!Thread.currentThread().isInterrupted()) {
					throw e;
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			makeRequest();
		} catch (IOException e) {
			if (downloadHandler != null) {
				downloadHandler.sendFailureMessage(FailureCode.IO);
			}
		} catch (InterruptedException e) {
			if (downloadHandler != null) {
				downloadHandler.sendFailureMessage(FailureCode.Interrupted);
			}
		}
	}

	public boolean isDownloading() {
		return isDownloading;
	}

	public void stopDownloading() {
		isDownloading = false;
	}

	/**********************************************************
	 * @文件名称：UpdateDownloadRequest.java
	 * @文件作者：renzhiqiang
	 * @创建时间：2015年8月3日 上午11:16:40
	 * @文件描述：下载消息转发器,执行真正的文件流下载，位于主线程
	 * @修改历史：2015年8月3日创建初始版本
	 **********************************************************/
	public class DownloadResponseHandler {
		protected static final int SUCCESS_MESSAGE = 0;
		protected static final int FAILURE_MESSAGE = 1;
		protected static final int START_MESSAGE = 2;
		protected static final int FINISH_MESSAGE = 3;
		protected static final int NETWORK_OFF = 4;
		private Handler handler;

		public DownloadResponseHandler() {
			LogUtils.d("download","DownloadResponseHandler current thread----->"+Thread.currentThread().getName()+"  id="+Thread.currentThread().getId());
			if (Looper.myLooper() != null) {
				handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						LogUtils.d("download","handleMessage current thread----->"+Thread.currentThread().getName()+"  id="+Thread.currentThread().getId());
						handleSelfMessage(msg);
					}
				};
			}
		}

		public void onFinish() {
			downloadListener.onFinished(mCompleteSize, "");
		}

		public void onFailure(FailureCode failureCode) {
			downloadListener.onFailure();
		}

		private void sendPausedMessage() {
			sendMessage(obtainMessage(PAUSED_MESSAGE, null));
		}

		private void sendProgressChangedMessage(int progress) {
			sendMessage(obtainMessage(PROGRESS_CHANGED, new Object[] { progress }));
		}

		protected void sendFailureMessage(FailureCode failureCode) {
			sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { failureCode }));
		}

		//
		// Pre-processing of messages (in original calling thread, typically the
		// UI thread)
		//

		protected void handlePausedMessage() {
			downloadListener.onPaused(progress, mCompleteSize, "");
		}

		protected void handleProgressChangedMessage(int progress) {
			downloadListener.onProgressChanged(progress, "");
		}

		protected void handleFailureMessage(FailureCode failureCode) {
			onFailure(failureCode);
		}

		protected void sendFinishMessage() {
			sendMessage(obtainMessage(FINISH_MESSAGE, null));
		}

		// Methods which emulate android's Handler and Message methods
		protected void handleSelfMessage(Message msg) {
			LogUtils.d("download","handleSelfMessage current thread----->"+Thread.currentThread().getName()+"  id="+Thread.currentThread().getId());
			Object[] response;
			switch (msg.what) {
			case FAILURE_MESSAGE:
				response = (Object[]) msg.obj;
				handleFailureMessage((FailureCode) response[0]);
				break;
			case PROGRESS_CHANGED:
				response = (Object[]) msg.obj;
				handleProgressChangedMessage(((Integer) response[0]).intValue());
				break;
			case PAUSED_MESSAGE:
				handlePausedMessage();
				break;
			case FINISH_MESSAGE:
				onFinish();
				break;
			}
		}

		protected void sendMessage(Message msg) {
			if (handler != null) {
				handler.sendMessage(msg);
			} else {
				handleSelfMessage(msg);
			}
		}

		protected Message obtainMessage(int responseMessage, Object response) {
			Message msg = null;
			if (handler != null) {
				msg = this.handler.obtainMessage(responseMessage, response);
			} else {
				msg = Message.obtain();
				msg.what = responseMessage;
				msg.obj = response;
			}
			return msg;
		}

		/**
		 * 格式化数字
		 * @param value
		 * @return
		 */
		private String getTwoPointFloatStr(float value) {
			DecimalFormat fnum = new DecimalFormat("0.00");
			return fnum.format(value);
		}

		private int mCompleteSize = 0;
		private int progress = 0;
		private static final int PROGRESS_CHANGED = 5;
		private static final int PAUSED_MESSAGE = 7;

		/**
		 * 真正的文件流下载，工作在后台线程，出现情况时，发送消息到主线程的handler处理
		 * @param is
		 */
		void sendResponseMessage(InputStream is) {
			LogUtils.d("download","sendResponseMessage current thread----->"+Thread.currentThread().getName()+"  id="+Thread.currentThread().getId());
			RandomAccessFile randomAccessFile = null;
			mCompleteSize = 0;
			try {
				byte[] buffer = new byte[1024];
				int length = -1;
				int limit = 0;
				randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
				randomAccessFile.seek(startPos);
				boolean isPaused = false;
				while ((length = is.read(buffer)) != -1) {
					if (isDownloading) {
						randomAccessFile.write(buffer, 0, length);
						mCompleteSize += length;
						if ((startPos + mCompleteSize) < (contentLength + startPos)) {
							progress = (int) (Float.parseFloat(getTwoPointFloatStr(
									(float) (startPos + mCompleteSize) / (contentLength + startPos))) * 100);
							//每隔读取30次，才去更新progress,避免更新太快
							if (limit % 150 == 0 && progress <= 100) {
								sendProgressChangedMessage(progress); //在子线程中读取流数据，后转发到主线程中去。
							}
						}
						limit++;
					} else {
						isPaused = true;
						sendPausedMessage();
						break;
					}
				}
				stopDownloading();
				if (!isPaused) {
					sendFinishMessage();
				}
			} catch (IOException e) {
				sendPausedMessage();
				stopDownloading();
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
					if (randomAccessFile != null) {
						randomAccessFile.close();
					}
				} catch (IOException e) {
					stopDownloading();
					e.printStackTrace();
					sendFailureMessage(FailureCode.IO);
				}
			}
		}
	}

	//包含了下载过程中所有可能出现的异常情况
	public enum FailureCode {
		UnknownHost, Socket, SocketTimeout, ConnectTimeout, IO, HttpResponse, JSON, Interrupted
	}
}