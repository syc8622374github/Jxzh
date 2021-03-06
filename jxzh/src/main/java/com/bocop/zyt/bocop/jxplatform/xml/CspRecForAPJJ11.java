package com.bocop.zyt.bocop.jxplatform.xml;

import android.util.Log;

import com.bocop.zyt.bocop.jxplatform.bean.APJJ08ListXmlBean;
import com.bocop.zyt.bocop.jxplatform.bean.CarPeccancyBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * 根据用户ID查询 车牌列表
 * 
 * @author zhongye
 * 
 */
public class CspRecForAPJJ11 {
	public CspRecForAPJJ11() {
	}

	/**
	 * 将XML字符串解析，转换对象
	 * @param cspXML
	 * @return
	 */
	public static APJJ08ListXmlBean readStringXml(String cspXML) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		// Log.i("tag","000000000");
		// Log.i("tag",cspXML);
		byte[] binaryData = cspXML.getBytes();
		InputStream inStream = new ByteArrayInputStream(binaryData);
		// 获取DOM解析的工厂
		// Log.i("tag",binaryData.toString());
		factory = DocumentBuilderFactory.newInstance();
		APJJ08ListXmlBean info = new APJJ08ListXmlBean();
		List<CarPeccancyBean> carPeccancyListBean = null;
		
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(inStream); // 获取解析器
			Element root = document.getDocumentElement();
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

			if (!info.getErrorcode().toString().equals("00")) {
				// 获取ERR_CODE节点
				Log.i("tag", "CSP返回错误码");
				Element err_msg = (Element) dfheadElement.getElementsByTagName(
						"ERR_MSG").item(0);
				info.setErrormsg(err_msg.getFirstChild().getNodeValue());
				Log.i("tag", info.getErrormsg());
			} else {
				// Log.i("tag","1313");
				// 获取根节点下的DATA_AREA节点
				NodeList nodes = root.getElementsByTagName("DATA_AREA");
				// 获取DATA_AREA元素节点
				Element infoElement = (Element) (nodes.item(0));
				// 获取违法条数
				Element noList = (Element) infoElement.getElementsByTagName(
						"CarNum").item(0);
				if(noList == null){
					info.setErrorcode("11");
					info.setErrormsg("没有违法记录");
				}else{
				int num = Integer.parseInt(noList.getFirstChild()
						.getNodeValue());
				Log.i("tag", String.valueOf(num));
				info.setNoList(num);
				carPeccancyListBean = new ArrayList<CarPeccancyBean>(num);
				// 获取车牌号码
//				Element licenseNumNode = (Element) infoElement.getElementsByTagName(
//						"LicenseNum").item(0);
//				String licenseNum = licenseNumNode.getFirstChild()
//						.getNodeValue().toString();
//				info.setLicenseNum(licenseNum);
//				Log.i("tag", licenseNum);
				
				Log.i("tag", String.valueOf(num));
				NodeList dataList = root.getElementsByTagName("DATA_LIST");
				for(int i=0;i<num;i++){
					CarPeccancyBean bean = new CarPeccancyBean();
					Element carElement = (Element) dataList.item(i);
					//处罚决定书编号
					Element peccancyNum = (Element) carElement.getElementsByTagName(
							"PeccancyNum").item(0);
					bean.setPeccancyNum(peccancyNum.getFirstChild().getNodeValue().toString());
					Log.i("tag", "0");
					//驾驶证号 
					Element driveNum = (Element) carElement.getElementsByTagName(
							"DriveNum").item(0);
					bean.setDriveNum(driveNum.getFirstChild().getNodeValue().toString());
					Log.i("tag", "1");
					
					//车牌号码
					Element licenseNum = (Element) carElement.getElementsByTagName(
							"LicenseNum").item(0);
					bean.setPeccancyLicenseNum(licenseNum.getFirstChild().getNodeValue().toString());
					Log.i("tag", "2");
					
					//违法内容
					Element peccancyAct = (Element) carElement.getElementsByTagName(
							"PeccancyAct").item(0);
					bean.setPeccancyAct(peccancyAct.getFirstChild().getNodeValue().toString());
					Log.i("tag", "3");
					
					//罚款金额
					Element peccancyMoney = (Element) carElement.getElementsByTagName(
							"PeccancyMoney").item(0);
					bean.setPeccancyMoney(peccancyMoney.getFirstChild().getNodeValue().toString());
					Log.i("tag", "4");
					
					//滞纳金
					Element otherMoney = (Element) carElement.getElementsByTagName(
							"OtherMoney").item(0);
					bean.setOtherMoney(otherMoney.getFirstChild().getNodeValue().toString());
					Log.i("tag", "5");
					
					//实际缴纳金额
					Element factMoney = (Element) carElement.getElementsByTagName(
							"FactMoney").item(0);
					bean.setFactMoney(factMoney.getFirstChild().getNodeValue().toString());
					Log.i("tag", "6");
					
					//处罚时间
					Element peccancyTime = (Element) carElement.getElementsByTagName(
							"PeccancyTime").item(0);
					bean.setPeccancyTime(peccancyTime.getFirstChild().getNodeValue().toString());
					Log.i("tag", "PeccancyTime" + bean.getPeccancyTime());
					
					//缴纳罚款事件
					Element payTime = (Element) carElement.getElementsByTagName(
							"PayTime").item(0);
					bean.setPapTime(payTime.getFirstChild().getNodeValue().toString());
					Log.i("tag", "PayTime" + bean.getPapTime());
					
					carPeccancyListBean.add(bean);
				}
				}
			}
			info.setCarPeccancyListBean(carPeccancyListBean);
			Log.i("tag","返回");
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
