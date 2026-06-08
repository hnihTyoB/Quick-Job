package vn.thinher.quickjob.util;

import org.springframework.http.MediaType;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.thinher.quickjob.domain.RestResponse;

// Format rest response sẽ chạy trước GlobalException, nên nó trả về mã lỗi 500 thay vì 400, vì vậy viết thêm RestResponse trong GlobalException.
@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> { 
 
    @Override 
    public boolean supports(MethodParameter returnType, Class converterType) { 
        return true; 
    } 
 
    @Override 
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {  
        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = httpResponse.getStatus();

        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(status);

        if (body instanceof String) { 
            return body;
        }

        if (status >= 400) { 
            //case error 
            return body;
        } else { 
            //case success 
            restResponse.setData(body);
            restResponse.setMessage("Success");
        } 
        return restResponse;
    } 
} 
