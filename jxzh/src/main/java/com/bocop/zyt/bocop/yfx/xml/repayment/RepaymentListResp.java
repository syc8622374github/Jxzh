package com.bocop.zyt.bocop.yfx.xml.repayment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * 剩余还款计划
 * 
 * @author ftl
 * 
 */
public class RepaymentListResp {
	
	public RepaymentListResp() {
		
	}

	/**
	 * 将XML字符串解析，转换对象
	 * @param cspXML
	 * @return
	 */
	public static RepaymentListXmlBean readStringXml(String cspXML) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		byte[] binaryData = cspXML.getBytes();
		InputStream inStream = new ByteArrayInputStream(binaryData);
		// 获取DOM解析的工厂
		// Log.i("tag",binaryData.toString());
		factory = DocumentBuilderFactory.newInstance();
		RepaymentListXmlBean info = new RepaymentListXmlBean();
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(inStream); // 获取解析器
			Element root = document.getDocumentElement();
			NodeList nodeh = root.getElementsByTagName("CONST_HEAD");
			Element dfheadElement = (Element) (nodeh.item(0));
			// 获取ERR_CODE节点
			Element err_code = (Element) dfheadElement.getElementsByTagName("ERR_CODE").item(0);
			info.setErrorcode(err_code.getFirstChild().getNodeValue());
			Log.i("tag", "CSP查询返回码：" + info.getErrorcode());

			if (!info.getErrorcode().toString().equals("00")) {
				// 获取ERR_CODE节点
				Log.i("tag", "CSP返回错误码");
				Element err_msg = (Element) dfheadElement.getElementsByTagName("ERR_MSG").item(0);
				info.setErrormsg(err_msg.getFirstChild().getNodeValue());
				Log.i("tag", info.getErrormsg());
			} else {
				// 获取根节点下的DATA_AREA节点
				NodeList nodes = root.getElementsByTagName("DATA_AREA");
				// 获取DATA_AREA元素节点
				Element infoElement = (Element) (nodes.item(0));
				NodeList dataList = infoElement.getElementsByTagName("DATA_LIST");
				List<RepaymentBean> repaymentList = new ArrayList<RepaymentBean>();
				for (int i = 0; i < dataList.getLength(); i++) {
					Log.i("tag","i:" + String.valueOf(i));
					Element repaymentElement = (Element) dataList.item(i);
					Node periodNode = repaymentElement.getElementsByTagName("NCL_PERIOD").item(0).getFirstChild();
					Node loanBalNode = repaymentElement.getElementsByTagName("NCL_LOAN_BAL").item(0).getFirstChild();
					String period = periodNode == null ? "" : periodNode.getNodeValue() ;
					String repayDate = repaymentElement.getElementsByTagName("NCL_REL_RE_DT").item(0).
							getFirstChild().getNodeValue();
					String repayAmt = repaymentElement.getElementsByTagName("NCL_REPAY_AMT").item(0).
							getFirstChild().getNodeValue();
					String repayCpt = repaymentElement.getElementsByTagName("NCL_REPAY_CPT").item(0).
							getFirstChild().getNodeValue();
					String repayIst = repaymentElement.getElementsByTagName("NCL_REPAY_IST").item(0).
							getFirstChild().getNodeValue();
					String loanBal = loanBalNode == null ? "" : loanBalNode.getNodeValue() ;
					RepaymentBean repaymentBean = new RepaymentBean(period, repayDate, repayAmt, 
							repayCpt, repayIst, loanBal);
					repaymentList.add(repaymentBean);
				}
				info.setRepaymentList(repaymentList);
			}
			
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
