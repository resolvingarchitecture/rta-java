package ra.rta.transform;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;

public class XMLTransformer extends BaseTransformer {

    @Override
    protected void select() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(raw.getBytes()));
        XPath xPath = XPathFactory.newInstance().newXPath();
        for(String fieldNameXpath : fieldMetaMap.keySet()) {
            fieldNameValues.put(fieldNameXpath, xPath.compile(fieldNameXpath).evaluate(document, XPathConstants.STRING));
        }
    }
}
