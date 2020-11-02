package cn.loverot.login58.util;

import cn.hutool.core.io.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStream;

public class QuanzhouJSUtil {

	private static final Logger logger = LoggerFactory.getLogger(QuanzhouJSUtil.class);


	private static ScriptEngine rsaCodeEngine;


	static {

		ClassPathResource rsaCodeJs = new ClassPathResource("js/Rsa.js");
		InputStream rsaCodeIs = null;
		try {
			rsaCodeIs = rsaCodeJs.getInputStream();
			String rsaCode =	IoUtil.read(rsaCodeIs,"utf-8");
			rsaCodeEngine = new ScriptEngineManager().getEngineByName("javascript");
			rsaCodeEngine.eval(rsaCode);

		} catch (Exception e) {
			logger.error("execute javascript engine failed with " + e.getMessage());
		} finally {
			IoUtil.close(rsaCodeIs);
		}

	}
	public static String getEncryptString(String pwd) {
		Object result = "";
		try {
			result = ((Invocable)rsaCodeEngine).invokeFunction("encryptString", pwd);
		} catch (NoSuchMethodException | ScriptException e) {
		}
		return String.valueOf(result);

	}

}
