package org.wang.poi.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Component
public class FileUtil {

    /**
     *  下载文件
     * @param is  输入流
     * @param fileName  文件名
     * @param response
     */
    public void download(InputStream is, String fileName, HttpServletResponse response){
            response.setContentType("multipart/form-data");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            byte[] buffer=new byte[1024];
            BufferedInputStream bis=null;
            OutputStream os=null;
            try{
                bis=new BufferedInputStream(is);
                os=response.getOutputStream();
                int i;
                while((i=bis.read(buffer))!=-1){
                    os.write(buffer,0,i);
                }
            }catch(Exception e){
                log.error(e.getMessage(),e);
            }finally {
                try{
                    if(bis!=null){
                        bis.close();
                    }
                }catch (Exception e){
                    log.error("缓冲输入流关闭异常");
                }
                try{
                    if(os!=null){
                        os.close();
                    }
                }catch (Exception e){
                    log.error("输出流流关闭异常");
                }
            }
    }

    public void upload(MultipartFile file){
        if(file.isEmpty()){
            return;
        }
        String fileNmae=file.getOriginalFilename();
        String filePath=System.getProperty("user.dir")+"/temp";
        File dir=new File(filePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        try{
            file.transferTo(dir);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }

    }

}
