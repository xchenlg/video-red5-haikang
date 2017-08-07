package chen.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
 
@ControllerAdvice
public class GlobalDefaultExceptionHandler {
   
    @ExceptionHandler(value = Exception.class)
    public void defaultErrorHandler(HttpServletRequest req, Exception e)  {
      
      //打印异常信息：
       e.printStackTrace();
       System.out.println("GlobalDefaultExceptionHandler.defaultErrorHandler()");
 
       /*
        * 返回json数据或者String数据：
        * 那么需要在方法上加上注解：@ResponseBody
        * 添加return即可。
        */
       
       /*
        * 返回视图：
        * 定义一个ModelAndView即可，
        * 然后return;
        * 定义视图文件(比如：error.html,error.ftl,error.jsp);
        *
        */
//     ModelAndView mav = new ModelAndView();
//     mav.addObject("exception", e);
//     mav.addObject("url", req.getRequestURL());
//     mav.setViewName(DEFAULT_ERROR_VIEW);
//     return mav;
  }
   
}