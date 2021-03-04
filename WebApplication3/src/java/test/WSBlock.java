package test;

import com.ibm.wsdl.PortTypeImpl;
import com.ibm.wsdl.xml.WSDLReaderImpl;
import com.sun.org.glassfish.gmbal.ManagedObject;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

@ManagedObject
public class WSBlock implements Serializable {

    private URL url;
    private int id;
    private String path;
    private String name;
    private Map services;
    private int status;
    private String urlString;
    private List<String> ports;
    private List<Integer> portStatus;
    private List<WSMethodBlock> methods;

    public WSBlock() {
        ports = new ArrayList();
        portStatus = new ArrayList();
        methods = new ArrayList();
    }

    public void setURL(URL url) {
        this.url = url;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(int status) {
        //System.out.println("CheckWSBLock.setStatus: wsName : " + name + "  Status : " + status);
        this.status = status;
    }

    public void setURLString(String url)throws Exception {
        this.urlString = url;
        initializeServiceMethods();
        services = setServices();
        
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addPort(String port) {
        if (!isPortExists(port)) {
            ports.add(port);
            portStatus.add(0);
        }

    }

    public void setPortStatus(String portName, int status) {
        for (int i = 0; i < ports.size(); i++) {
            if (portName.equals(ports.get(i))) {
                portStatus.set(i, status);
                return;
            }
        }
    }

    public int getPortStatus(String portName) {
        for (int i = 0; i < ports.size(); i++) {
            if (portName.equals(ports.get(i))) {
                return portStatus.get(i);
            }
        }
        return -1;
    }

    public boolean isPortExists(String temp) {
        for (int i = 0; i < ports.size(); i++) {
            if (temp.equals(ports.get(i))) {
                return true;
            }
        }
        return false;
    }

    public URL getURL() {
        return url;
    }

    public int getID() {
        return id;
    }

    public String getWSName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public String getURLString() {
        return urlString;
    }

    public void clearPorts() {
        ports.clear();
    }

    public List<String> getPorts() {
        return ports;
    }

    public String getPath() {
        return path;
    }
    public Map getServices(){
        return services;
    }

    public void initializeServiceMethods() throws Exception{
        List<Operation> operationList = new ArrayList();
        try {
            WSDLReader reader = new WSDLReaderImpl();
            reader.setFeature("javax.wsdl.verbose", false);
            Definition definition = reader.readWSDL(urlString);
            Map<String, PortTypeImpl> defMap = definition.getPortTypes();
            Collection<PortTypeImpl> collection = defMap.values();
            for (PortTypeImpl portType : collection) {
                operationList.addAll(portType.getOperations());
            }
        } catch (WSDLException e) {
            System.out.println("get wsdl operation fail.");
            e.printStackTrace();
        }
        for(int i = 0 ; i < operationList.size() ; i++){
            WSMethodBlock temp = new WSMethodBlock(operationList.get(i));
            temp.setID(methods.size());
            methods.add(temp);
        }
    }
    
    private Map setServices() throws Exception {
        List<Operation> operationList = new ArrayList();
        try {
            WSDLReader reader = new WSDLReaderImpl();
            reader.setFeature("javax.wsdl.verbose", false);
            Definition definition = reader.readWSDL(urlString);
            return definition.getServices();
        } catch (WSDLException e) {
            System.out.println("get wsdl operation fail.");
            e.printStackTrace();
        }
        return null;
    }
    
    public List<WSMethodBlock> getMethods(){
        return methods;
    }
    
    public WSMethodBlock getMethod(int methodID){
        return methods.get(methodID);
    }
}
