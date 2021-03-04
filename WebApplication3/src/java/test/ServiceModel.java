package test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.Endpoint;

public class ServiceModel {

    //private WSBlock[] wsBlocks;
    private List<WSBlock> wsBlocks;

    private int count;

    public ServiceModel() {
        wsBlocks = new ArrayList<WSBlock>();
    }

    public boolean addWS(String url, String name) {
        int index = isWSExists(name);
        try {
            if (index != -1) {
                wsBlocks.get(index).addPort(Integer.toString(new URL(url).getPort()));
                System.out.println(new URL(url).getAuthority());
                System.out.println(new URL(url).getUserInfo());
                System.out.println(new URL(url).getHost());
                System.out.println(new URL(url));
                return true;
            } else {

                WSBlock temp = new WSBlock();
                temp.setID(wsBlocks.size());
                temp.setName(name);
                temp.setURLString(url);

                wsBlocks.add(temp);
            }
            System.out.println("Block " + name + " is added");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void removeWS(int wsID) {
        wsBlocks.remove(wsID);
    }

    public WSBlock getWSBlock(int wsID) {
        return wsBlocks.get(wsID);
    }

    public int getWSBlockCount() {
        return wsBlocks.size();
    }

    public List<String> getPorts(int index) {
        return wsBlocks.get(index).getPorts();
    }

    public int isWSExists(String name) {
        for (int i = 0; i < wsBlocks.size(); i++) {
            if (wsBlocks.get(i).getWSName().equals(name)) {
                return i;
            }
        }

        return -1;

    }
}
