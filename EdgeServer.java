package sisaku;

public class EdgeServer {
	final static int ID = 100;;

	Udp1 udp;

	EdgeServer(){
		udp = new Udp1(ID);
		udp.makeMulticastSocket();
		udp.startListener();
	}

	void receiveData() {
		byte[] rcvData = udp.lisner.getData();
		if(rcvData != null) {

			String str = new String(rcvData);




			udp.lisner.resetData();
		}
	}

}
