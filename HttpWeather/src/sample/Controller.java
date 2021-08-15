/*********************************************
*  23  能澤 一颯　                             *
*********************************************/
package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField urlField;
    @FXML
    private MenuButton menu;
    @FXML
    private WebView webView;

    private WebEngine engine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = webView.getEngine();
        engine.load("");
    }

    private void action (String strUrl){
        try {
            URL url= new URL(strUrl);
            GetWeather gw = new GetWeather(url);
            String[] out = gw.get(3);
            textArea.setText("");
            urlField.setText(strUrl);
            for(int i = 0; i < out.length; i++){
                textArea.appendText(out[i] + "\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void Nigata_Action(ActionEvent event){
        action("https://www.drk7.jp/weather/xml/15.xml");
        engine.load("http://weather-gpv.info/parts/bpm.php?model=msm&element=cp&latsc=1&w=203&h=315&area=cb&lx=495&ly=54");
        menu.setText("Nigata");
    }

    @FXML
    protected void Toyama_Action(ActionEvent event){
        action("https://www.drk7.jp/weather/xml/16.xml");
        engine.load("http://weather-gpv.info/parts/bpm.php?model=msm&element=cp&latsc=1&w=203&h=315&area=cb&lx=284&ly=128");
        menu.setText("Toyama");

    }

    @FXML
    protected void Isikawa_Action(ActionEvent event){
        action("https://www.drk7.jp/weather/xml/17.xml");
        engine.load("http://weather-gpv.info/parts/bpm.php?model=msm&element=cp&latsc=1&w=203&h=315&area=cb&lx=205&ly=117");
        menu.setText("Isikawa");

    }

    @FXML
    protected void Hukui_Action(ActionEvent event){
        action("https://www.drk7.jp/weather/xml/18.xml");
        engine.load("http://weather-gpv.info/parts/bpm.php?model=msm&element=cp&latsc=1&w=203&h=315&area=cb&lx=113&ly=265");
        menu.setText("Hukui");

    }

}

class GetWeather{                                                       //天気予報を取得するクラス GetWeather
    private HttpURLConnection urlConn;
    private InputStream inputStream;

    GetWeather(URL url){                                                //天気予報の書かれたURLを取得する
        try {                                                           //URLからストリームを生成する
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            inputStream = urlConn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String[] get(int x){                                                 //天気予報を取得する関数Get
        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);

            Element rootElement = document.getDocumentElement();         //rootになるエレメントを作成する
            String root = rootElement.getTagName();

            NodeList prefNodes = rootElement.getElementsByTagName("pref");

            Element pref = (Element)prefNodes.item(0);

            NodeList areaElements = pref.getElementsByTagName("area");  //データ取得用のエレメントと出力用の配列を作る
            NodeList infoElements = pref.getElementsByTagName("info");
            String[] outputStream = new String[areaElements.getLength() + (areaElements.getLength() * x)];

            for (int i = 0; i<areaElements.getLength(); i++){           //地域を表示した後に天気をx表示する。
                Element area = (Element)areaElements.item(i);
                outputStream[i + (i * x)] = area.getAttribute("id").toString();
                for(int j = 0; j < 7; j++){
                    if(j < x) {                                         //x以降の天気を弾く
                        Element info = (Element) infoElements.item(i * 7 + j);
                        NodeList weatherElements = pref.getElementsByTagName("weather");

                        outputStream[(i * x) + j + i + 1]               //日付の後に天気を書く
                                = info.getAttribute("date").toString() + "   "+
                                  weatherElements.item(i * 7 + j).getFirstChild().getNodeValue();
                    }
                }
            }
            return outputStream;
        } catch (SAXException e) {
            e.printStackTrace();
            return new String[0];
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return new String[0];
        }
    }
}