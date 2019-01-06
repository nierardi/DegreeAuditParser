package DegreeAuditParser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLFrameElement;

import java.util.ArrayList;

public class Controller {

	@FXML
	private WebView webView;

	@FXML
	private Button reloadBtn;

	@FXML
	private Button goBtn;

	@FXML
	private TextField urlField;

	@FXML
	private Button zoomPlusBtn;

	@FXML
	private Button zoomMinusBtn;

	@FXML
	private Button backBtn;

	@FXML
	private Button printHtmlBtn;

	@FXML
	void goBtnClicked(ActionEvent event) {
		// https://lp5-beis.radford.edu/ssomanager/c/SSB?pkg=bzskckin.P_SSB?p_packproc=bzlkdwss.P_StuSignOn
		// go to the url
		try {
			urlField.setText("https://lp5-beis.radford.edu/ssomanager/c/SSB?pkg=bzskckin.P_SSB?p_packproc=bzlkdwss.P_StuSignOn");
			webView.getEngine().load(urlField.getText());
			webView.setZoom(0.8);
		} catch (Exception e) {
			new Alert(Alert.AlertType.WARNING, "Could not load that url. Make sure its typed correctly").showAndWait();
		}
	}

	@FXML
	void reloadBtnClicked(ActionEvent event) {
		webView.getEngine().reload();
	}

	@FXML
	void zoomMinusBtnClicked(ActionEvent event) {
		webView.setZoom(webView.getZoom() - 0.1);
		System.out.println(webView.getZoom());
	}

	@FXML
	void zoomPlusBtnClicked(ActionEvent event) {
		webView.setZoom(webView.getZoom() + 0.1);
	}

	@FXML
	void backBtnClicked(ActionEvent event) {
		webView.getEngine().getHistory().go(-1);
	}

	private HTMLFrameElement frameByName(org.w3c.dom.Document source, String name) {
		System.out.println("========= finding frame : " + name);
		NodeList allElements = source.getElementsByTagName("*");
		for (int i = 0 ; i < allElements.getLength() ; i++) {
			org.w3c.dom.Element item = (org.w3c.dom.Element) allElements.item(i);
			System.out.println("current element @ " + i + " : " + item.getTagName());
			if (item.hasAttribute("name")) {
				System.out.println("element has name = " + item.getAttribute("name"));
				if (item.getAttribute("name").equals(name)) {
					return (HTMLFrameElement) item;
				}
			}
		}
		return null;
	}

	@FXML
	void printHtmlBtnClicked(ActionEvent event) {

		org.w3c.dom.Document wcdoc = webView.getEngine().documentProperty().get();
		HTMLFrameElement bodyContainer = frameByName(wcdoc, "frBodyContainer");
		HTMLFrameElement body = frameByName(bodyContainer.getContentDocument(), "frBody");
		String html = new org.jsoup.helper.W3CDom().asString(body.getContentDocument());
		//System.out.println(html);

		// parent to all the blocks
		Element auditFormBase = Jsoup.parse(html).getElementById("frmAudit");
		ArrayList<Block> blocks = new ArrayList<>();

		for (Element elem : auditFormBase.children()) {
			if (elem.hasAttr("name")) {
				if (elem.attr("name").contains("Block-RA")) {
					// found a block!
					Element blockIdElem = elem;
					String blockId = BlockParser.parseBlockId(blockIdElem);
					// goes down in the tree
					Element blockNameElem = blockIdElem.nextElementSibling();
					String blockName = BlockParser.parseBlockName(blockNameElem);
					// skip the empty table and return the element table
					Element blockData = blockNameElem.nextElementSibling().nextElementSibling();
					Block block = new Block(blockId, blockName, blockData);
					blocks.add(block);

					// for now, just print what we found
					//System.out.println();
					//System.out.println("Found a block:");
					//System.out.println("Block ID: " + blockId);
					//System.out.println("Block name: " + blockName);
				}
			}
		}

		// output each block
		OutWriter.outputLn("starting blocks:");
		for (Block block : blocks) {
			OutWriter.outputLn(block.toString());
		}

		// output the inner frame html
		OutWriter.outputHtml(html);

		// inform our dear user
		new Alert(Alert.AlertType.INFORMATION, "It worked! Wrote two files (output.txt and degAudit.html) to the directory you ran the jar from. " +
			"(THESE DO NOT CONTAIN ANY PERSONAL INFO besides name and student id, which won't be used for anything at all.) " +
			"Please send these to Nolan! Thanks for testing this!").showAndWait();

		/*
		NodeList allElements = wcdoc.getElementsByTagName("*");

		for (int i = 0 ; i < allElements.getLength() ; i++) {
			org.w3c.dom.Element item = (org.w3c.dom.Element) allElements.item(i);
			System.out.println("current element @ " + i + " : " + item.getTagName());
			if (item.hasAttribute("name")) {

				if (item.getAttribute("name").equals("frBodyContainer")) {
					// we've found it...well, only half of it

					HTMLFrameElement daFrame = (HTMLFrameElement) item;
					org.w3c.dom.Document innerFrameDoc = daFrame.getContentDocument();
					for (int j = 0 ; j < allElements.getLength() ; j++) {
						org.w3c.dom.Element item2 = (org.w3c.dom.Element) allElements.item(j);
						System.out.println("current element [inner] @ " + j + " : " + item.getTagName());
						if (item2.hasAttribute("name")) {
							System.out.println("[inner] has name = " + item2.getAttribute("name"));

							if (item2.getAttribute("name").equals("frBody")) {
								// we've found the second one!
								HTMLFrameElement daInner = (HTMLFrameElement) item2;
								String html = new org.jsoup.helper.W3CDom().asString(daFrame.getContentDocument());
								System.out.println(html);
							}

						}
					}

				}

			}
		}
		*/

	}


}
