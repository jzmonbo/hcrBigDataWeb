package com.hcrcms.cms.action.admin.assist;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.hcrcms.core.web.util.FileUtil;

@Controller
public class CmsResourcesAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsResourcesAct.class);

	@RequiresPermissions("uploadfile:v_list")
	@RequestMapping("/uploadfile/v_list.so")
	public String list(HttpServletRequest request,ModelMap model) {
		return "uploadfile/list";
	}
	
	@RequiresPermissions("uploadfile:uploadBatch")
	@RequestMapping("/uploadfile/uploadBatch.so")
	public void uploadBatch(@RequestParam("pic")CommonsMultipartFile pic,HttpServletRequest request,HttpServletResponse response,ModelMap model) {
		String local = getClass().getResource("/").getFile().toString();
		String rootLocal = local.substring(0,local.indexOf("WEB-INF")); 
		String filepath = request.getParameter("filePath");
		String savePath = rootLocal + filepath;  
  
        File f1 = new File(savePath);  
        if (!f1.exists()) {  
            f1.mkdirs();  
        }  
        //设置文件保存的本地路径
        String fileName = pic.getOriginalFilename();
		//为了避免文件名重复，在文件名前加UUID
		String uuid = UUID.randomUUID().toString().replace("-","");
		String uuidFileName = uuid + fileName;
		//将文件保存到服务器
		try {
			FileUtil.upFile(pic.getInputStream(), uuidFileName, savePath);
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print("/"+filepath+"/"+uuidFileName);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public void uploadBatch(HttpServletRequest request,HttpServletResponse response,ModelMap model) {
		String local = getClass().getResource("/").getFile().toString();
		String rootLocal = local.substring(0,local.indexOf("WEB-INF")); 
		Map<String, String[]> maps = request.getParameterMap();
		String[] filenames = maps.get("Filename");
		String filepath = request.getParameter("filePath");
		String savePath = rootLocal + filepath;  
  
        File f1 = new File(savePath);  
        System.out.println(savePath);  
        if (!f1.exists()) {  
            f1.mkdirs();  
        }  
        DiskFileItemFactory fac = new DiskFileItemFactory();  
        ServletFileUpload upload = new ServletFileUpload(fac);  
        upload.setHeaderEncoding("utf-8");  
        List fileList = null;  
        try {  
            fileList = upload.parseRequest(request);  
        } catch (FileUploadException ex) {  
            return;  
        }  
        Iterator<FileItem> it = fileList.iterator();  
        String name = "";  
        String extName = "";  
        while (it.hasNext()) {  
            FileItem item = it.next();  
            if (!item.isFormField()) {  
                name = item.getName();  
                long size = item.getSize();  
                String type = item.getContentType();  
                System.out.println(size + " " + type);  
                if (name == null || name.trim().equals("")) {  
                    continue;  
                }  
                // 扩展名格式：  
                if (name.lastIndexOf(".") >= 0) {  
                    extName = name.substring(name.lastIndexOf("."));  
                }  
                File file = null;  
                do {  
                    // 生成文件名：  
                    name = UUID.randomUUID().toString();  
                    file = new File(savePath + name + extName);  
                } while (file.exists());  
                File saveFile = new File(savePath + name + extName);  
                try {  
                    item.write(saveFile);  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        try {
			response.getWriter().print(name + extName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	@RequiresPermissions("uploadfile:findImage")
	@RequestMapping("/uploadfile/findImage.so")
	public void findImage(HttpServletRequest request,HttpServletResponse response,ModelMap model) {
		String result = "";
		StringBuilder fileSb = new StringBuilder();
		try {
			String filepath = request.getParameter("filepath");
			String local = getClass().getResource("/").getFile().toString();
			String rootLocal = local.substring(0, local.indexOf("WEB-INF"));
			String savePath = rootLocal + filepath;
			File fileRoot = new File(savePath);
			if (fileRoot.isDirectory()){
				File[] files = fileRoot.listFiles();
				if (files.length > 0){
					fileSb.append("<table>");
					fileSb.append("<tr>");
					int count = 1;
					for (File file : files){
						String fp = file.getParent().replaceAll("\\\\","/");
						fp = fp.substring(fp.indexOf(filepath)) + "/" + file.getName();
						fileSb.append("<td>").append("<img src=\""+request.getContextPath()+"/"+fp+"\" width=\"200\" height=\"200\"><p><input type=\"button\" onclick=\"deleteImg('"+file.getAbsolutePath().replaceAll("\\\\","/")+"')\" value=\"删除\"></p>");
						if (count%4 == 0){
							fileSb.append("</tr>");
							fileSb.append("<tr>");
							count = 1;
						}else{
							count++;
						}
					}
					fileSb.append("</tr>");
					fileSb.append("</table>");
				}
			}
			result = fileSb.toString();
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@RequiresPermissions("uploadfile:deleteImage")
	@RequestMapping("/uploadfile/deleteImage.so")
	public void deleteImage(HttpServletRequest request,HttpServletResponse response,ModelMap model) {
		String result = "";
		StringBuilder fileSb = new StringBuilder();
		try {
			String filepath = request.getParameter("filepath");
			String currentFile = request.getParameter("currentFile");
			String local = getClass().getResource("/").getFile().toString();
			String rootLocal = local.substring(0, local.indexOf("WEB-INF"));
			String savePath = rootLocal + filepath;
			//先删除文件,再取目录文件
			File delFile = new File(currentFile);
			delFile.delete();
			File fileRoot = new File(savePath);
			if (fileRoot.isDirectory()){
				File[] files = fileRoot.listFiles();
				if (files.length > 0){
					fileSb.append("<table>");
					fileSb.append("<tr>");
					int count = 1;
					for (File file : files){
						String fp = file.getParent().replaceAll("\\\\","/");
						fp = fp.substring(fp.indexOf(filepath)) + "/" + file.getName();
						fileSb.append("<td>").append("<img src=\""+request.getContextPath()+"/"+fp+"\" width=\"200\" height=\"200\"><p><input type=\"button\" onclick=\"deleteImg('"+file.getAbsolutePath().replaceAll("\\\\","/")+"')\" value=\"删除\"></p>");
						if (count%4 == 0){
							fileSb.append("</tr>");
							fileSb.append("<tr>");
							count = 1;
						}else{
							count++;
						}
					}
					fileSb.append("</tr>");
					fileSb.append("</table>");
				}
			}
			result = fileSb.toString();
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}