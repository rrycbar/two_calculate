package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.mfx.*;

import entity.Beans;
import net.sf.json.JSONArray;
/**
 * Servlet implementation class AtithControlle
 */
@WebServlet("/AtithControlle")
public class AtithControlle extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AtithControlle() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
		File file = new File(savePath);
		InputStreamReader isr = null;
		BufferedReader br = null;
		DealAtith dealAtith=new DealAtith();
		ArrayList<Beans> tempList=new ArrayList<Beans>();
		//判断上传文件的保存目录是否存在
		if (!file.exists() && !file.isDirectory()) {
		System.out.println(savePath+"目录不存在，需要创建");
		//创建目录
		file.mkdir();
		}
		//消息提示
		String message = "";
		try{
		//使用Apache文件上传组件处理文件上传步骤：
		//1、创建一个DiskFileItemFactory工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//2、创建一个文件上传解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		//解决上传文件名的中文乱码
		upload.setHeaderEncoding("UTF-8"); 
		//3、判断提交上来的数据是否是上传表单的数据
		if(!ServletFileUpload.isMultipartContent(request)){
		//按照传统方式获取数据
		return;
		}
		//4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
		List<FileItem> list = upload.parseRequest(request);
		for(FileItem item : list){
			//如果fileitem中封装的是普通输入项的数据
			if(item.isFormField()){
			String name = item.getFieldName();
			//解决普通输入项的数据的中文乱码问题
			String value = item.getString("UTF-8");
			//value = new String(value.getBytes("iso8859-1"),"UTF-8");
			System.out.println(name + "=" + value);
			}else{//如果fileitem中封装的是上传文件
			//得到上传的文件名称，
			String filename = item.getName();
			System.out.println(filename);
			if(filename==null || filename.trim().equals("")){
			continue;
			}
			//注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如： c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
			//处理获取到的上传文件的文件名的路径部分，只保留文件名部分
			filename = filename.substring(filename.lastIndexOf("\\")+1);
			//获取item中的上传文件的输入流
			InputStream in = item.getInputStream();
			 isr = new InputStreamReader(in,"gbk");
			 br = new BufferedReader(isr);
			 String str = "";
			   String str1 = "";
			 while ((str = br.readLine()) != null) {
				    //str1 += str;
				    Beans beans=new Beans();
				    beans.setAtith(str);
				    beans.setResult(dealAtith.getResult(str, true));
				    tempList.add(beans);
			 }
				   // 当读取的一行不为空时,把读到的str的值赋给str1
		    System.out.println(str1);
		
			//创建一个文件输出流
			FileOutputStream out = new FileOutputStream(savePath + "\\" + filename);
			//创建一个缓冲区
			byte buffer[] = new byte[1024];
			//判断输入流中的数据是否已经读完的标识
			int len = 0;
			//循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
			while((len=in.read(buffer))>0){
			//使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
			out.write(buffer, 0, len);
			}
			//关闭输入流
			in.close();
			//关闭输出流
			out.close();
			//删除处理文件上传时生成的临时文件
			item.delete();
			message = "文件上传成功！";
			}
		}
		response.getWriter().write(JSONArray.fromObject(tempList).toString());
		}catch (Exception e) {
		message= "文件上传失败！";
		e.printStackTrace();

		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	private int getResult(String atith){
		int arithLength=atith.length();
		//ArrayList<String> temp=new ArrayList<String>();
		//System.out.print(arithLength);
		String[] temp=new String[30];
		int count=0;
		String tempString="";
		for(int i=0;i<arithLength;i++){
			String subString=atith.charAt(i)+"";
			//System.out.print(subString);
			if(isOperator(subString)){//if it is operter
				//temp.add(subString)
				if(!tempString.equals("")){
					//temp.add(tempString);
					temp[count++]=tempString;
				}
				if(subString.equals(")")){
					temp[count]=subString;
				}else{
				   temp[count++]=subString;
				}
				tempString="";
			}else{
				tempString+=subString;
			}
			
		}
		if(!(atith.charAt(atith.length()-1)+"").equals(")")){
			//temp.add(tempString);
			temp[count]=tempString;
		}
		String[] arayTemp=new String[count+1];
		for(int i=0;i<=count;i++){
			arayTemp[i]=temp[i];
		}
		return 0;
	}
	public boolean isOperator(String operator){
		if(operator.equals("+")||operator.equals("-")||operator.equals("*")||operator.equals("÷")||operator.equals("(")||operator.equals(")")){
			return true;
		}
		return false;
	}
	public static void main(String[] args){
		System.out.println(new DealAtith().getResult("3+6+(7-5)",true));
	}

}
