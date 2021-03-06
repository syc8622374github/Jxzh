package com.bocop.zyt.bocop.jxplatform.xml;

import android.util.Log;

import com.bocop.zyt.bocop.jxplatform.bean.LicenseInfoBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CspRecForLicenseInfo {
	public CspRecForLicenseInfo() {
	}

	public static LicenseInfoBean readStringXml(String cspXML)
			throws UnsupportedEncodingException {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		// Log.i("tag","000000000");
		// Log.i("tag",cspXML);
		byte[] binaryData = cspXML.getBytes();
		// byte[] binaryData = cspXML.getBytes("UTF-8");
		InputStream inStream = new ByteArrayInputStream(binaryData);
		// 获取DOM解析的工厂
		// Log.i("tag",binaryData.toString());
		// Log.i("tag","22222222");
		factory = DocumentBuilderFactory.newInstance();
		LicenseInfoBean info = new LicenseInfoBean();
		try {
			// Log.i("tag","44444444444");
			builder = factory.newDocumentBuilder();
			// Log.i("tag","555555555");
			document = builder.parse(inStream); // 获取解析器
			// Log.i("tag","666666666");
			// 找到根
			Element root = document.getDocumentElement();
			// Log.i("tag","777777777");
			// 获取CONST_HEAD节点，判断电费查询是否成功。
			NodeList nodeh = root.getElementsByTagName("CONST_HEAD");
			Element dfheadElement = (Element) (nodeh.item(0));
			// Log.i("tag","888888888");
			// 获取ERR_CODE节点
			Element err_code = (Element) dfheadElement.getElementsByTagName(
					"ERR_CODE").item(0);
			// Log.i("tag","999999999");
			info.setErrorcode(err_code.getFirstChild().getNodeValue());

			Log.i("tag", "CSP查询返回码：" + info.getErrorcode());

			if (!info.getErrorcode().toString().equals("00") && !info.getErrorcode().toString().equals("99")) {
				// 获取ERR_CODE节点
				Element err_msg = (Element) dfheadElement.getElementsByTagName(
						"ERR_MSG").item(0);
				info.setErrormsg(err_msg.getFirstChild().getNodeValue());
				Log.i("tag", info.getErrormsg());
			} else if (info.getErrorcode().toString().equals("00")) {
				// 获取ERR_CODE节点
				Element err_msg = (Element) dfheadElement.getElementsByTagName(
						"ERR_MSG").item(0);
				info.setErrormsg(err_msg.getFirstChild().getNodeValue());
				Log.i("tag", info.getErrormsg());
				 Log.i("tag","DATA_AREA");
				// 获取根节点下的DATA_AREA节点
				NodeList nodes = root.getElementsByTagName("DATA_AREA");
				// 获取DATA_AREA元素节点
				Element infoElement = (Element) (nodes.item(0));
				Log.i("tag","DriveNum");
				// 驾驶证
				Element driveNum = (Element) infoElement.getElementsByTagName(
						"DriveNum").item(0);
				info.setDrivenum(driveNum.getFirstChild().getNodeValue());
				Log.i("tag","FileNum");
				// 档案编号
				Element licenseType = (Element) infoElement
						.getElementsByTagName("FileNum").item(0);
				info.setFilenum(licenseType.getFirstChild()
						.getNodeValue());
				Log.i("tag","OwnerName");
				// 车主姓名
				Element ownerName = (Element) infoElement.getElementsByTagName(
						"OwnerName").item(0);
				info.setOwnername(ownerName.getFirstChild().getNodeValue());
				Log.i("tag","Tel");
				// 手机
				Element tel = (Element) infoElement
						.getElementsByTagName("Tel").item(0);
				info.setTel(tel.getFirstChild().getNodeValue());
				Log.i("tag","QuasiDriving");
				// 准驾车型
				Element quasiDriving = (Element) infoElement
						.getElementsByTagName("QuasiDriving").item(0);
				info.setQuasidriving(quasiDriving.getFirstChild().getNodeValue());
				Log.i("tag","PenaltyScore");
				// 罚分
				Element penaltyScore = (Element) infoElement
						.getElementsByTagName("PenaltyScore").item(0);
				info.setPenaltyscore(penaltyScore.getFirstChild().getNodeValue());
				Log.i("tag","State");
				// 状态
				Element state = (Element) infoElement
						.getElementsByTagName("State").item(0);
				info.setState(state.getFirstChild().getNodeValue());
				Log.i("tag","State:" + info.getState());
				Log.i("tag","ReplacementDate");
				// 准驾车型
				Element replacementDate = (Element) infoElement
						.getElementsByTagName("ReplacementDate").item(0);
				info.setReplacementdate(replacementDate.getFirstChild().getNodeValue());
			} if (info.getErrorcode().toString().equals("99")){
				// 获取ERR_CODE节点
				Element err_msg = (Element) dfheadElement.getElementsByTagName(
						"ERR_MSG").item(0);
				info.setErrormsg(err_msg.getFirstChild().getNodeValue());
				Log.i("tag", info.getErrormsg());
				 Log.i("tag","DATA_AREA");
					// 获取根节点下的DATA_AREA节点
					NodeList nodes = root.getElementsByTagName("DATA_AREA");
					// 获取DATA_AREA元素节点
					Element infoElement = (Element) (nodes.item(0));
					Log.i("tag","DriveNum");
					// 驾驶证
					Element driveNum = (Element) infoElement.getElementsByTagName(
							"DriveNum").item(0);
					info.setDrivenum(driveNum.getFirstChild().getNodeValue());
					Log.i("tag","FileNum");
			}
			return info;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
