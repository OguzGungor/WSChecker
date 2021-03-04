package test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlImporter;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.impl.wsdl.support.assertions.*;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.xml.rpc.wsdl.document.Port;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import javax.xml.ws.Endpoint;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractList;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.bean.ManagedBean;
import javax.jws.soap.SOAPBinding;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.handler.HandlerResolver;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@ManagedBean
@Named(value = "newJSFManagedBean")
public class ServiceManager {

    private final ServiceModel serviceModel;
    List<String> ttt;

    /*@WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/helloservice/HelloService.wsdl")
    private Service serviceTemp;*/
    public ServiceManager() {
        serviceModel = new ServiceModel();

        ttt = new ArrayList();
    }

    public boolean addWS(String url) {

        for (int i = 0; i < serviceModel.getWSBlockCount(); i++) {

            if (serviceModel.getWSBlock(i).getURLString().equals(url)) {
                System.out.println("already exists");
                return false;
            }
        }

        try {
            serviceModel.addWS(url, url.substring(url.lastIndexOf("/") + 1, url.length()));
            System.out.println("added: " + serviceModel.getWSBlockCount());
            return true;
        } catch (Exception ex) {
            System.out.println("Error : " + ex);
            return false;
        }

    }

    public void addWSList(ArrayList<String> list) {
        for (String line : list) {
            System.out.println("line : " + line);
            addWS(line);

        }

    }

    public void removeWSList(ArrayList<String> list) {
        for (String line : list) {
            removeWS(line);

        }

    }

    public void removeWS(String url) {
        for (int i = 0; i < serviceModel.getWSBlockCount(); i++) {

            if (serviceModel.getWSBlock(i).getURLString().equals(url)) {
                try {
                    serviceModel.removeWS(i);
                } catch (Exception ex) {
                    System.out.println("Error : " + ex);
                }
            }
        }
    }

    public List<WSBlock> getWSList() throws Exception {
        updateWSStatus();
        List<WSBlock> temp = new ArrayList<WSBlock>();

        for (int i = 0; i < serviceModel.getWSBlockCount(); i++) {
            temp.add(serviceModel.getWSBlock(i));
        }
        return temp;

    }

    public WSBlock getWS(int epID) {
        return serviceModel.getWSBlock(epID);
    }

    public void updateWSStatus() throws Exception {
        for (int i = 0; i < serviceModel.getWSBlockCount(); i++) {
            if (checkWSStatus(i)) {
                serviceModel.getWSBlock(i).setStatus(1);
                serviceModel.getWSBlock(i).addPort(Integer.toString(new URL(serviceModel.getWSBlock(i).getURLString()).getPort()));

            } else {
                serviceModel.getWSBlock(i).setStatus(0);
            }
        }

    }

    public List<WSMethodBlock> getMethods(int index) {
        return serviceModel.getWSBlock(index).getMethods();
    }

    @WebEndpoint
    private boolean checkWSStatus(int wsID) throws Exception {

        ///////////////////////////////////
        Service service;
        try {
            //serviceModel.getWSBlock(wsID); 
            service = Service.create(new URL(serviceModel.getWSBlock(wsID).getURLString() + "?wsdl"), new QName("http://wssupervisory/", "WSImplementorService"));

            return true;
        } catch (Exception ex) {
            //System.out.println("[ServiceManager.checkWSStatus()]Exception : " + ex + "\n");
            serviceModel.getWSBlock(wsID).clearPorts();
            return false;
        }
    }
    
    public String checkMethod(int wsID, int methodID)throws MalformedURLException,
            IOException {
        String service = serviceModel.getWSBlock(wsID).getServices().toString();
        service = service.substring(service.indexOf("}") + 1, service.indexOf("="));
        String responseString = "";
        String outputString = "";
        String wsURL = serviceModel.getWSBlock(wsID).getURLString();
        URL url = new URL(wsURL);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        String xmlInput= serviceModel.getWSBlock(wsID).getMethod(methodID).getXMLRequestString();
        byte[] buffer = new byte[xmlInput.length()];
        buffer = xmlInput.getBytes();
        bout.write(buffer);
        byte[] b = bout.toByteArray();
        String SOAPAction
                = service;

// Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length",
                String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        httpConn.setRequestProperty("SOAPAction", SOAPAction);
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();
//Write the content of the request to the outputstream of the HTTP Connection.

        System.out.println("here");
        out.write(b);
        out.close();
//Ready with sending the request.

//Read the response.
        InputStreamReader isr
                = new InputStreamReader(httpConn.getInputStream());

        BufferedReader in = new BufferedReader(isr);
//Write the SOAP message response to a String.
        while ((responseString = in.readLine()) != null) {
            outputString = outputString + responseString;
        }
//Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
        Document document = parseXmlFile(outputString);
        try {
            NodeList nodeLst = document.getElementsByTagName(serviceModel.getWSBlock(wsID).getMethod(methodID).getOutputTag());

            String weatherResult = nodeLst.item(0).getTextContent();
            System.out.println("Weather: " + weatherResult);

//Write the SOAP message formatted to the console.
            String formattedSOAPResponse = formatXML(outputString);

            //System.out.println(formattedSOAPResponse);
            return weatherResult;
        } catch (Exception etc) {
            System.out.println(etc);
            return null;
        }
    }

    public String[] getWSNames() {
        String[] temp = new String[serviceModel.getWSBlockCount()];
        for (int i = 0; i < serviceModel.getWSBlockCount(); i++) {
            //temp[i] = serviceModel.getWSBlock(i).getWSName();
            temp[i] = serviceModel.getWSBlock(i).getWSName();
        }
        return temp;
    }

    public int[] getWSStatus() {
        //System.out.println("ERRORCHECK : " + serviceModel.getWSBlock(0).getWSName());
        int[] temp = new int[serviceModel.getWSBlockCount()];
        for (int i = 0; i < serviceModel.getWSBlockCount(); i++) {
            temp[i] = serviceModel.getWSBlock(i).getStatus();
        }
        return temp;
    }

    public int getWSCount() throws Exception {

        return serviceModel.getWSBlockCount();
    }

    public List<String> getPorts(int index) {
        return serviceModel.getPorts(index);
    }
    
    public String formatXML(String unformattedXml) {
        try {
            Document document = parseXmlFile(unformattedXml);
            OutputFormat format = new OutputFormat(document);
            format.setIndenting(true);
            format.setIndent(3);
            format.setOmitXMLDeclaration(true);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
