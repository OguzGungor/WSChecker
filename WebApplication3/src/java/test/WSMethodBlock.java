/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;
import javax.wsdl.Operation;

public class WSMethodBlock {

    int id;
    Operation method;
    String operationName;
    String outputTag;
    String path;
    String[] inputTags;
    String[] inputs;
    String requestXMLFormat;
    String output = "testdefaultoutput";

    public WSMethodBlock(Operation method) {
        this.method = method;
        setPath();
        setOutputTag();
        setOperationName();
        setInputTags();
        generateXMLRequestString();
        System.out.println("Path : " + path + "\n"
                + "output : " + outputTag + "\n"
                + "name : " + operationName + "\n"
                + "XMLString : " + requestXMLFormat + "\n" + "Parameters : " );
        for (int i = 0; i < inputTags.length; i++) {
            System.out.println("parameter " + i + " : " + inputTags[i]);
        }
    }
    public void setID(int id){
    
        this.id = id;
    }

    private void setPath() {
        String temp;
        try {
            temp = method.getInput().toString().substring(method.getInput().toString().indexOf("Message"), method.getInput().toString().indexOf("Part"));
        } catch (Exception et) {
            temp = method.getOutput().toString().substring(method.getOutput().toString().indexOf("Message"), method.getOutput().toString().indexOf("Part"));
        }
        temp = temp.substring(temp.indexOf("{") + 1, temp.lastIndexOf("}"));
        path = temp;
    }

    private void setOperationName() {
        operationName = method.getName();
    }

    private void setOutputTag() {
        try {
            String temptxt = method.getOutput().toString().substring(method.getOutput().toString().indexOf("Part: name="), method.getOutput().toString().indexOf("typeName"));
            temptxt = temptxt.substring(temptxt.indexOf("=") + 1);
            outputTag = temptxt;
        } catch (Exception estr) {
            //System.out.println("[WSMethoodBlock.setOutputtagException] : " + estr);
        }
    }

    private void setInputTags() {
        List<String> tempList = new ArrayList();
        String temptest = method.getInput().toString();
        String temptest2 = "";
        try {
            while (temptest.indexOf("Part:") != -1) {

                temptest2 = temptest.substring(temptest.indexOf("Part:"), temptest.indexOf("\ntypeName="));
                temptest2 = temptest2.substring(temptest2.indexOf("=") + 1);
                tempList.add(temptest2);
                temptest = temptest.substring(temptest.indexOf("typeName="));
                temptest = temptest.substring(temptest.indexOf("Part:"));
            }
        } catch (Exception e) {
            //System.out.println("[WSMethoodBlock.setInputtagException] : " + e);
        }
        inputTags = new String[tempList.size()];
        for (int i = 0; i < tempList.size(); i++) {
            inputTags[i] = tempList.get(i);
        }
    }
    
    public void setInputs(List<String> inputs){
        for(int i = 0; i < inputs.size() ; i++){
            this.inputs[i] = inputs.get(i);
        }
        generateXMLRequestString();
    }

    public void generateXMLRequestString() {
        requestXMLFormat
                = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:jav=\"" + path + "\">\n"
                + "<soapenv:Header/>\n"
                + "<soapenv:Body>\n"
                + "<jav:" + operationName + ">\n";
        try {
            for (int i = inputTags.length - 1; i >= 0; i--) {
                requestXMLFormat += "<" + inputTags[i] + ">" + inputs[i] + "</" + inputTags[i] + ">\n";
            }
        } catch (Exception ex) {
            //System.out.println("[WSMethodBlock.generateXMLRequest-Loop] Exception : " + ex);
        }
        requestXMLFormat
                += "</jav:" + operationName + ">\n"
                + "</soapenv:Body>\n"
                + "</soapenv:Envelope>";

    }

    public String getOperationName() {
        return operationName;
    }

    public String getOutputTag() {
        return outputTag;
    }

    public String[] getinputTags() {
        return inputTags;
    }

    public String getPath() {
        return path;
    }

    public String getXMLRequestString() {
        return requestXMLFormat;
    }
    public int getID(){
        return id;
    }
    
    public void setOutput(String output){
        this.output = output;
    }
    
    public String getOutput(){
        return output;
    }
    public String[] getInputs(){
        return inputs;
    }
}
