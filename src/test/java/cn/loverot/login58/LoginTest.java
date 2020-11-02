package cn.loverot.login58;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.loverot.login58.util.QuanzhouJSUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.net.HttpCookie;
import java.util.*;

/**
 * @author hui se
 * @description
 * @create 2020-11-02 21:31
 **/
public class LoginTest {
    final static String TOKEN_URL="https://passport.58.com/58/mobile/init?source=58-homepage-pc&path=https%253A%252F%252Fnc.58.com%252Fjob.shtml%253FPGTID%253D0d100000-0029-dba7-3005-f84426cd651f%2526ClickID%253D2%2526pts%253D1604319485542&psdk-d=jsdk&psdk-v=1.0.6";
    final static String GET_CODE_URL="https://passport.58.com/58/mobile/getcode?path=https%253A%252F%252Fnc.58.com%252Fjob.shtml%253FPGTID%253D0d100000-0029-dba7-3005-f84426cd651f%2526ClickID%253D2%2526pts%253D1604320539572&mobile={0}&codetype=0&token={1}&voicetype=0&source=58-homepage-pc&psdk-d=jsdk&psdk-v=1.0.6";
    final static String LOGIN_URL="https://passport.58.com/58/mobile/pc/login?mobile={0}&mobilecode={1}&source=58-homepage-pc&path=https%253A%252F%252Fnc.58.com%252Fjob.shtml%253FPGTID%253D0d100000-0029-dba7-3005-f84426cd651f%2526ClickID%253D2%2526pts%253D1604320539572&domain=58.com&isremember=false&autologin=false&tokencode={2}&token={3}&finger2=zh-CN%7C24%7C1%7C8%7C1920_1080%7C1920_1030%7C-480%7C1%7C1%7C1%7Cundefined%7C1%7Cunknown%7CWin32%7Cunknown%7C21%7Ctrue%7Cfalse%7Cfalse%7Cfalse%7Cfalse%7C0_false_false%7Cd41d8cd98f00b204e9800998ecf8427e%7C627c2bc531f591b521c9f4df8c74777d&fingerprint=4y9Bqypq_LVIjg9XexKQ1LnaoKfW_dGM&isredirect=false&psdk-d=jsdk&psdk-v=1.0.6";
    final static String VALID_CODE_URL="https://passport.58.com/sec/58/validcode/get?vcodekey={0}&time={1}";
    final static String FINGERPRINT_URL="https://passport.58.com/sec/58/fingerprint?source=58-homepage-pc&finger2=zh-CN%7C24%7C1%7C8%7C1920_1080%7C1920_1030%7C-480%7C1%7C1%7C1%7Cundefined%7C1%7Cunknown%7CWin32%7Cunknown%7C21%7Ctrue%7Cfalse%7Cfalse%7Cfalse%7Cfalse%7C0_false_false%7Cd41d8cd98f00b204e9800998ecf8427e%7C627c2bc531f591b521c9f4df8c74777d&psdk-d=jsdk&psdk-v=1.0.6";
    final static String PHONE="189xxxxxxxx";
    static Set<HttpCookie> cookies = new HashSet<HttpCookie>();
    private static HttpRequest http;
    private static long initTime = (new Date()).getTime();
    static {
        cookies.add(new HttpCookie("xxzl_smartid","10aa36de12d792b5037a81903b395180"));
        cookies.add(new HttpCookie("xxzl_deviceid","n702X2EHwXH3SMiVsgnsd%2F2yenK8l%2BY5sPRPQr%2BMfz2R0wblWJzsyv0MivnTBH7e"));
        http = HttpUtil.createGet("https://passport.58.com")
                .header("Connection", "keep-alive")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "en-US,en;q=0.9")
                .header("pragma", "no-cache")
                .header("upgrade-insecure-requests", "1")
                .header("user-agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                .header("Host", "passport.58.com")
                .header("referer", "https://passport.58.com/login/?path=https://nc.58.com/job.shtml");
    }

    public static void main(String[] args) {
        String pwd = encryptStr(PHONE);
        String phone = QuanzhouJSUtil.getEncryptString(pwd);
        //初始化cookies
        String ver = doGet(FINGERPRINT_URL+getJQueryCallBackStr());
        getJsonObject(ver);
        //获取token
        String res = doGet(TOKEN_URL+getJQueryCallBackStr());
        String token = getToken(res);
        //获取tokencode
        String mobileCodeRes = doGet(StrUtil.indexedFormat(GET_CODE_URL+getJQueryCallBackStr(), phone, token));
        String tokenCode = getTokenCode(mobileCodeRes,phone,token);
        Scanner ip = new Scanner(System.in);
        System.out.print("请输入手机验证码: ");
        int code = ip.nextInt();
        String session = doGet(StrUtil.indexedFormat(LOGIN_URL+getJQueryCallBackStr(),phone,String.valueOf(code), tokenCode, token));
        System.out.println(getPage());
    }

    private static String encryptStr(String txt) {
        Long timeSpan = 1411093327735L;
        Long randomLong = timeSpan + System.currentTimeMillis() - initTime;
        String timeSign = String.valueOf(randomLong);
        return timeSign + txt;
    }

    /**
     * 可加可不加
     * @return
     */
    private static String getJQueryCallBackStr() {
        return "&callback=JsonpCallBack" +System.currentTimeMillis()+ RandomUtil.randomNumbers(3);
    }

    private static String getTokenCode(String res,String phone,String token){
        String json = getJsonString(res);
        JSONObject object = JSON.parseObject(json);
        //请输入验证码
        if(object.getIntValue("code")==785){
            //输入验证码
            String vcodekey = object.getJSONObject("data").getString("vcodekey");
            InputStream vcode = doGetImage(StrUtil.indexedFormat(VALID_CODE_URL, vcodekey, String.valueOf(System.currentTimeMillis())));
            FileUtil.writeFromStream(vcode,"D:\\c.jpg");
            Scanner ip = new Scanner(System.in);
            System.out.print("请输入图形验证码: ");
            String code = ip.nextLine();
             String codeRes = doGet(StrUtil.indexedFormat(GET_CODE_URL + "&validcode=" + code+getJQueryCallBackStr(), phone, token));
            return getTokenCode(codeRes,phone,token);
        }
        Assert.isTrue(object.getIntValue("code")==0);
        return object.getJSONObject("data").getString("tokencode");
    }

    private static String getToken(String res){
        JSONObject object = getJsonObject(res);
        return object.getJSONObject("data").getString("token");
    }

    private static JSONObject getJsonObject(String res) {
        String json = getJsonString(res);
        JSONObject object = JSON.parseObject(json);
        Assert.isTrue(object.getIntValue("code")==0);
        return object;
    }

    private static String getJsonString(String res) {
        return res.substring(res.indexOf("(")+1, res.lastIndexOf(")"));
    }

    /**
     * 获取页面数据
     * @return
     */
    private static String getPage(){
       HttpRequest httpRequest = HttpUtil.createGet("https://gz.58.com/qzyewu/?pts=1604317623440")
                .header("Connection", "keep-alive")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "en-US,en;q=0.9")
                .header("pragma", "no-cache")
                .header("upgrade-insecure-requests", "1")
                .header("user-agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                .header("Host", "gz.58.com")
               .cookie(CollUtil.join(cookies,";"))
                .header("referer", "https://gz.58.com/qzyewu/?pts=1604317623440");
       return httpRequest.execute().body();
    }

    private static String doGet(String url){
        HttpResponse execute = http.setUrl(url).cookie(CollUtil.join(cookies,";")).execute();
        if(CollUtil.isNotEmpty(execute.getCookies())){
            cookies.addAll(execute.getCookies());
        }
        return execute.body();
    }
    private static InputStream doGetImage(String url){
        HttpResponse execute = http.setUrl(url).cookie(CollUtil.join(cookies,";")).execute();
        return execute.bodyStream();
    }
}
