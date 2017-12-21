package com.tong.testpay;

import java.util.Map;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.tong.testpay.util.OrderInfoUtil2_0;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 *  重要说明:
 *  
 *  这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
 *  真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
 *  防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
 */
public class PayDemoActivity extends FragmentActivity {
	
	/** 支付宝支付业务：入参app_id */
	public static final String APPID = "2017071707785970";
	
	/** 支付宝账户登录授权业务：入参pid值 */
	public static final String PID = "";
	/** 支付宝账户登录授权业务：入参target_id值 */
	public static final String TARGET_ID = "";

	/** 商户私钥，pkcs8格式 */
	/** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
	/** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
	/** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
	/** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
	/** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
	public static final String RSA2_PRIVATE = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCDfEJGoJHcwxfHg+NRbbGH/0HADaGU3/8MNdEaKG1SA7N6w4M0kXWPPIvhKUVOG974mDulQgAGqtbuYpJstUYbxPOSUPlJraPyOWOGlj7mpylmGmg+b7i0c/JguN556+xpP6qRghevhaoFni1GlLgUm6u0Dg3trqL2zI39u7EGfAkJHt8pq3CPIlJc9QeI/MlZSUpXKgsIYtYGLY9VPxG9hpeGcP+o6iPUPARTHn7y05bNTbiiJOEBf/eonJPxKlpdgvBKAC2xkNa4wvSQcj55NtqqWKIWIEIGkPmxS2CO5VLjB0pEWpeOqHMFVtnzc7Qw+wEhwbfcOibz6MBS/l/JAgMBAAECggEATviYzdavBMrgQ2LBayzZDfePxAvr2oDWrLy+BFn3nuS+LaOz8CTVWNxiny+sgodW2jSEznLflSGSycbGbyhJHXCjYmPj9g6mg1tYnYMr8FjCEqMVrTpo02k4UEN4y/2ZFnW4uoBKPY+oWItyYgv8q9P7wr8WOoVDN7wQJC9Q2bVU1OVH6h8UPLbbb7X8p3zyluC/pgDEkWrkwdKWG/VhwCmSZ+VQMjldpG8IzXBGkzkXdErTMQhxZKfiGefJC78uJYK434VIMiBRnR2S7EqRvTk1uqUhe7MhtbsSE/20hzPC4B6iuhlqi10YMdZfgca5N4lPLcb8fakCTYpw6LAGiQKBgQDD1IIhTiJFmzIBUBgtlfrbfZ1DtrZZiYUq7WW6jEDvXn2SAW2EZ8fd5y61DToITx1EbABO3Nrl4GAWESrFIGPyMzLHZxbi4Eahva7/x2UnhvKxZf2Xj4qNpM/1rTUyLz49S9ZX6Uvh/ZF33vSL1bvZzOWWzdDZwQG4xh8PqkWqiwKBgQCr4o93/5KzLnPE5B0yap0iPYS6Ff9qdehBWruUHxereRFNuj3zDmUD7Wlrpvkh2NmRUHFh1kd0arm6iBIKazYTo2Tdd609FeNOeqXBtLXz0ITKA6agbnBdL+rSq/ZjND6vljgEaxTsMWwUK5VTH3xWY7P4w//JEa0bjUVWDgEtewKBgGRwQRHt7hv/PkV4G/59dv1OTvOk6Z/ArcFXox5rwv5CW2sgOlh3lgYVBVqs3v/V++/U6M+J7OeMDhwjT+ls9HYLlla5Y/6XAwL/s5rSXOpKB32/DrLGV0zQpCygWrFTk7uCgvvx1w6grBRsEMcLp15M/dA73Ytht41aaCHztemDAoGAde2HSWlLmSvxudBYR9Ll5CwY+CNHjh8/Lje2IrYCY185A/8Xqb7ih4NHpNh2bUZ+Xzi10c5WUAU5UEUzO3q7K1YbSedmMFboEVwMhY4amCEkOgWvOLsM1KKfldLtxLp/l7CU1lMxk6uE34HXfaRIhmz5eV/gCm/aOOEABT6uiCkCgYBN2oyGv4zTBOKdoIqr1cs/5PsrJGsVzkrQWdvE4sumYb7Mt+582fWpY2JKCt/ukRKPDqRr0g/12AFk0g/4K87F0mybBloeMN0s1xoOaaFfa7F4R2O820BI472jGFTQy++7sAjqPjqLcczqGh5yduz6ah08hED56cBRuJxcl0NUcQ==";
	public static final String RSA_PRIVATE = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCDfEJGoJHcwxfHg+NRbbGH/0HADaGU3/8MNdEaKG1SA7N6w4M0kXWPPIvhKUVOG974mDulQgAGqtbuYpJstUYbxPOSUPlJraPyOWOGlj7mpylmGmg+b7i0c/JguN556+xpP6qRghevhaoFni1GlLgUm6u0Dg3trqL2zI39u7EGfAkJHt8pq3CPIlJc9QeI/MlZSUpXKgsIYtYGLY9VPxG9hpeGcP+o6iPUPARTHn7y05bNTbiiJOEBf/eonJPxKlpdgvBKAC2xkNa4wvSQcj55NtqqWKIWIEIGkPmxS2CO5VLjB0pEWpeOqHMFVtnzc7Qw+wEhwbfcOibz6MBS/l/JAgMBAAECggEATviYzdavBMrgQ2LBayzZDfePxAvr2oDWrLy+BFn3nuS+LaOz8CTVWNxiny+sgodW2jSEznLflSGSycbGbyhJHXCjYmPj9g6mg1tYnYMr8FjCEqMVrTpo02k4UEN4y/2ZFnW4uoBKPY+oWItyYgv8q9P7wr8WOoVDN7wQJC9Q2bVU1OVH6h8UPLbbb7X8p3zyluC/pgDEkWrkwdKWG/VhwCmSZ+VQMjldpG8IzXBGkzkXdErTMQhxZKfiGefJC78uJYK434VIMiBRnR2S7EqRvTk1uqUhe7MhtbsSE/20hzPC4B6iuhlqi10YMdZfgca5N4lPLcb8fakCTYpw6LAGiQKBgQDD1IIhTiJFmzIBUBgtlfrbfZ1DtrZZiYUq7WW6jEDvXn2SAW2EZ8fd5y61DToITx1EbABO3Nrl4GAWESrFIGPyMzLHZxbi4Eahva7/x2UnhvKxZf2Xj4qNpM/1rTUyLz49S9ZX6Uvh/ZF33vSL1bvZzOWWzdDZwQG4xh8PqkWqiwKBgQCr4o93/5KzLnPE5B0yap0iPYS6Ff9qdehBWruUHxereRFNuj3zDmUD7Wlrpvkh2NmRUHFh1kd0arm6iBIKazYTo2Tdd609FeNOeqXBtLXz0ITKA6agbnBdL+rSq/ZjND6vljgEaxTsMWwUK5VTH3xWY7P4w//JEa0bjUVWDgEtewKBgGRwQRHt7hv/PkV4G/59dv1OTvOk6Z/ArcFXox5rwv5CW2sgOlh3lgYVBVqs3v/V++/U6M+J7OeMDhwjT+ls9HYLlla5Y/6XAwL/s5rSXOpKB32/DrLGV0zQpCygWrFTk7uCgvvx1w6grBRsEMcLp15M/dA73Ytht41aaCHztemDAoGAde2HSWlLmSvxudBYR9Ll5CwY+CNHjh8/Lje2IrYCY185A/8Xqb7ih4NHpNh2bUZ+Xzi10c5WUAU5UEUzO3q7K1YbSedmMFboEVwMhY4amCEkOgWvOLsM1KKfldLtxLp/l7CU1lMxk6uE34HXfaRIhmz5eV/gCm/aOOEABT6uiCkCgYBN2oyGv4zTBOKdoIqr1cs/5PsrJGsVzkrQWdvE4sumYb7Mt+582fWpY2JKCt/ukRKPDqRr0g/12AFk0g/4K87F0mybBloeMN0s1xoOaaFfa7F4R2O820BI472jGFTQy++7sAjqPjqLcczqGh5yduz6ah08hED56cBRuJxcl0NUcQ==";
	
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				@SuppressWarnings("unchecked")
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case SDK_AUTH_FLAG: {
				@SuppressWarnings("unchecked")
				AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
				String resultStatus = authResult.getResultStatus();

				// 判断resultStatus 为“9000”且result_code
				// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
				if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
					// 获取alipay_open_id，调支付时作为参数extern_token 的value
					// 传入，则支付账户为该授权账户
					Toast.makeText(PayDemoActivity.this,
							"授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
							.show();
				} else {
					// 其他状态值则为授权失败
					Toast.makeText(PayDemoActivity.this,
							"授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

				}
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
	}
	
	/**
	 * 支付宝支付业务
	 * 
	 * @param v
	 */
	public void payV2(View v) {
		if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							//
							finish();
						}
					}).show();
			return;
		}
	
		/**
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
		 * 
		 * orderInfo的获取必须来自服务端；
		 */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
		//final String orderInfo = orderParam + "&" + sign;
		final String orderInfo="alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2017071707785970&biz_content=%7B%22body%22%3A%22%E6%94%AF%E4%BB%98%E5%AE%9DAPP%E4%B8%8B%E5%8D%95%22%2C%22out_trade_no%22%3A%22A1232132120171218155542138349480%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22app%E6%94%AF%E4%BB%98%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Ftong1995.iok.la%2Fpay-notice%2Fapp%2Fali%2Fnotice&sign=NBsLOKiF2v1ebMupnVhLQvqqN4IwlLCve4QWhDMPgQWhWxITDjcIutxXuGs0k%2Fqn86F8lupf545nsM8fGd6%2BLjnXqwUtGvQ7%2FGOLusWhunby4NF54MBM9NSiBIbcCsKjwybqMa972tno67dMwBUKipugGAPffsT1Vjdip4s2Q%2FlcYV1GV9YZzwffTLksNEitGPv8xPZN5D4PdwH0ETrhlRrPXL6x5QwYq0%2FkQdWfBdtodQc7MXk24I9Vj6tIsv2gRFqxSeKz2JQc6Q7l7eH5ngfA4knx0plekWXaaVcnpaXKx%2F6ZDKDRB%2BOOwP8rF%2FdR0Xvpi1%2BR12Q6p31N7O%2B8Lw%3D%3D&sign_type=RSA2&timestamp=2017-12-18+15%3A55%3A42&version=1.0";
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(PayDemoActivity.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.i("msp", result.toString());
				
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * 支付宝账户授权业务
	 * 
	 * @param v
	 */
	public void authV2(View v) {
		if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
				|| (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
				|| TextUtils.isEmpty(TARGET_ID)) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
						}
					}).show();
			return;
		}

		/**
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
		 * 
		 * authInfo的获取必须来自服务端；
		 */
		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
		String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
		
		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
		final String authInfo = info + "&" + sign;
		final String orderInfo="alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2017071707785970&biz_content=%7B%22body%22%3A%22%E6%94%AF%E4%BB%98%E5%AE%9DAPP%E4%B8%8B%E5%8D%95%22%2C%22out_trade_no%22%3A%22A1232132120171218103304538155167%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22app%E6%94%AF%E4%BB%98%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.02%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F220.248.226.204%3A9050%2Fpay-notice%2Fapp%2Fali%2Fnotice&sign=XMBf30Hh9hDH9c4iX9UNJkIeNhllcRSDGAJ%2F0c2ruer1IZkqJ5%2BKoGh4s1qb%2BbgCal69OQBtJW1JRRWtKxVkRcwa3wCaC7h5VWSwJfU068SbilEMpjw1ajSp%2FPS8TsZxgwdRsXUCl3I0oIURRno1bRurAbxIyAZnimKWfv33r%2FjlzBVLfL29lEUW%2BqBKnQ5ym7ohi4%2FeYpL5WdwB1gWJ9ensuOD2NOKhwuqvKLM1mB4S8WCPU6sG8E7VVIkxhDSkHLi5c21%2BLCe3R65l3uWEuZ0y%2BK86LjThFM3xiT%2FDbaYFMe5B4BmUiwblYppaTnjW2fncN1RZ1FXOF3FbHh51iQ%3D%3D&sign_type=RSA2&timestamp=2017-12-18+10%3A33%3A04&version=1.0";
		Runnable authRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造AuthTask 对象
				AuthTask authTask = new AuthTask(PayDemoActivity.this);
				// 调用授权接口，获取授权结果
				//Map<String, String> result = authTask.authV2(authInfo, true);
				Map<String, String> result = authTask.authV2(orderInfo, true);

				Message msg = new Message();
				msg.what = SDK_AUTH_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread authThread = new Thread(authRunnable);
		authThread.start();
	}
	
	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
	 * 
	 * @param v
	 */
	public void h5Pay(View v) {
		Intent intent = new Intent(this, H5PayDemoActivity.class);
		Bundle extras = new Bundle();
		/**
		 * url 是要测试的网站，在 Demo App 中会使用 H5PayDemoActivity 内的 WebView 打开。
		 *
		 * 可以填写任一支持支付宝支付的网站（如淘宝或一号店），在网站中下订单并唤起支付宝；
		 * 或者直接填写由支付宝文档提供的“网站 Demo”生成的订单地址
		 * （如 https://mclient.alipay.com/h5Continue.htm?h5_route_token=303ff0894cd4dccf591b089761dexxxx）
		 * 进行测试。
		 * 
		 * H5PayDemoActivity 中的 MyWebViewClient.shouldOverrideUrlLoading() 实现了拦截 URL 唤起支付宝，
		 * 可以参考它实现自定义的 URL 拦截逻辑。
		 */
		String url = "http://m.taobao.com";
		extras.putString("url", url);
		intent.putExtras(extras);
		startActivity(intent);
	}
}
