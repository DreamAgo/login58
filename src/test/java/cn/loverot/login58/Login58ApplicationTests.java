package cn.loverot.login58;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.loverot.login58.util.QuanzhouJSUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.HttpCookie;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
class Login58ApplicationTests {

    @Test
    void contextLoads() {

        Scanner ip = new Scanner(System.in);
        System.out.print("Enter a number: ");
       int num = ip.nextInt();
        System.out.println("The input number is " + num);
        /*
        * JsonpCallBack1604320610343859({"code":0,"data":{"voice":true,"times":"10","length":6,"action":"0","label":"收不到短信验证码？","type":1,"requesthost":"passport.58.com","exist":0,"tokencode":"roIgA6znQbrl-IdnPXEWkJN8abfGZ_hh"},"msg":"动态码已发送"})
        * */
       // System.out.println(s);
    }



}
