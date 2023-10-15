//package cn.starboot.socket.demo.http.fileupload;
//
//import cn.starboot.http.server.HttpBootstrap;
//import cn.starboot.http.server.HttpRequest;
//import cn.starboot.http.server.HttpResponse;
//import cn.starboot.http.server.HttpServerHandler;
//import cn.starboot.http.server.handler.HttpRouteHandler;
//import org.apache.commons.fileupload.FileItemIterator;
//import org.apache.commons.fileupload.FileItemStream;
//import org.apache.commons.fileupload.util.Streams;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//public class FileUploadDemo {
//	public static void main(String[] args) {
//
//		HttpRouteHandler routeHandler = new HttpRouteHandler();
//		routeHandler.route("/", new HttpServerHandler() {
//			final byte[] body = ("<html>" +
//					"<head><title>smart-http demo</title></head>" +
//					"<body>" +
//					"GET 表单提交<form action='/get' method='get'><input type='text' name='text'/><input type='submit'/></form></br>" +
//					"POST 表单提交<form action='/post' method='post'><input type='text' name='text'/><input type='submit'/></form></br>" +
//					"文件上传<form action='/upload' method='post' enctype='multipart/form-data'><input type='file' name='text'/><input type='submit'/></form></br>" +
//					"</body></html>").getBytes();
//
//			@Override
//			public void handle(HttpRequest request, HttpResponse response) throws IOException {
//
//				response.setContentLength(body.length);
//				response.getOutputStream().write(body);
//			}
//		})
//				.route("/upload", new HttpServerHandler() {
//					@Override
//					public void handle(HttpRequest request, HttpResponse response) throws IOException {
//						try {
//							System.out.println("输入流：" + request.getInputStream());
//							SmartHttpFileUpload upload = new SmartHttpFileUpload();
//							FileItemIterator iterator = upload.getItemIterator(request);
//							while (iterator.hasNext()) {
//								FileItemStream item = iterator.next();
//								String name = item.getFieldName();
//								InputStream stream = item.openStream();
//								if (item.isFormField()) {
//									System.out.println("Form field " + name + " with value "
//											+ Streams.asString(stream) + " detected.");
//								} else {
//									System.out.println("File field " + name + " with file name "
//											+ item.getName() + " detected.");
//									// Process the input stream
//
//								}
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				});
//
//
//		HttpBootstrap bootstrap = new HttpBootstrap();
//		//配置HTTP消息处理管道
//		bootstrap.addHandler(routeHandler);
//
//		//设定服务器配置并启动
//		bootstrap.start();
//	}
//}
