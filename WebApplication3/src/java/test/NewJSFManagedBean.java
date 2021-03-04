package test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.UploadedFile;
import java.util.Scanner;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

@ManagedBean
@Named(value = "newJSFManagedBean")
@ApplicationScoped
public class NewJSFManagedBean {

    ServiceManager serviceManager;
    private UploadedFile uploadedFile;
    private List<WSBlock> wslist;
    private List<String> ports;
    private List<WSMethodBlock> methods;
    private List<List> portList;
    private List<List> methodList;
    //private List<String> parameters;
    private String[] parameters;

    public NewJSFManagedBean() {
        serviceManager = new ServiceManager();
        Thread t1 = new Thread(new MyClass());
        t1.start();
    }

    public UploadedFile getFile() {
        return uploadedFile;
    }

    public void setFile(UploadedFile file) {
        System.out.println("set!");
        uploadedFile = file;
    }

    public void add(String add) {
        System.out.println("newJSFManager.add   " + add);
        serviceManager.addWS(add);
    }

    public void addList() {
        /*System.out.println("newJSFManager.addlist   " + path);
        Path temp = Paths.get(path.substring(0, path.lastIndexOf("\\")),
                path.substring(path.lastIndexOf("\\") + 1, path.length()));
        Charset temp1 = Charset.forName("ISO-8859-1");*/

        ArrayList<String> temp2 = new ArrayList<String>();
        ArrayList<String> temp3 = new ArrayList<String>();
        System.out.println(uploadedFile == null);
        try {
            Scanner scanner = new Scanner(uploadedFile.getInputstream());

            while (scanner.hasNextLine()) {
                temp2.add(scanner.nextLine());
                // process the line
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        serviceManager.addWSList(temp2);
        try {
            wslist = serviceManager.getWSList();
        } catch (Exception ex) {
        }

    }

    /*public void upload() {
        //ArrayList<String> temp2 = new ArrayList<String>();
        try {
            System.out.println(uploadedFile == null);
            Scanner scanner = new Scanner((uploadedFile).getInputstream());

            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());

                // process the line
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }*/
    public String getStatus() throws Exception {
        System.out.println("newJSFManager.showstatus:" + serviceManager.getWSCount());

        String temp = "";
        try {
            //System.out.println(Inet4Address.getLocalHost().getHostAddress());
            //System.out.println(Inet4Address.getLocalHost().getAddress());
            serviceManager.updateWSStatus();
        } catch (Exception ex) {
        }
        for (int i = 0; i < serviceManager.getWSCount(); i++) {
            temp += serviceManager.getWSNames()[i] + "     " + serviceManager.getWSStatus()[i] + "\n";
        }
        System.out.println(temp);
        return temp;
    }

    public List<WSBlock> getAll() throws Exception {
        //wslist = serviceManager.getWSList();
        return wslist;
    }

    /*public List<List> getMethods() throws Exception{
        return methodList;
    }*/
    public List<WSMethodBlock> getMethods(int id) throws Exception {
        return serviceManager.getWS(id).getMethods();
    }

    public List<List> getPorts() throws Exception {
        return portList;
    }

    public List<String> getParameterNames(int WSID, int methodID) {
        WSMethodBlock temp = serviceManager.getWS(WSID).getMethod(methodID);
        List<String> temp2 = new ArrayList();
        for (int i = 0; i < temp.getinputTags().length; i++) {
            temp2.add(temp.getinputTags()[i]);
        }
        return temp2;//parameter names
    }

    public void listener(AjaxBehaviorEvent event) {

    }

    public String getOutput(List<String> input, int WSID, int methodID) {
        System.out.println("////////////input : " + input);
        serviceManager.getWS(WSID).getMethod(methodID).setInputs(input);
        serviceManager.getWS(WSID).getMethod(methodID).generateXMLRequestString();
        try {
            return serviceManager.checkMethod(WSID, methodID);
        } catch (Exception ex) {
            return "Error in newJSFManagedBean.getOutput. Error: " + ex;
        }
    }

    public void update() throws Exception {
        wslist = serviceManager.getWSList();
        portList.clear();
        portList = new ArrayList<List>();
        for (int i = 0; i < wslist.size(); i++) {
            ports = serviceManager.getPorts(i);

            portList.add(ports);
        }
        methodList.clear();
        methodList = new ArrayList<List>();
        for (int i = 0; i < wslist.size(); i++) {
            methods = serviceManager.getMethods(i);

            methodList.add(methods);
        }
    }

    public class MyClass implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    update();
                    Thread.sleep(10000);
                } catch (Exception ex) {
                }
            }
        }
    }

}
