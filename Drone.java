package sisaku;

public class Drone {

	final static int EDGE_SERVER = 100;

	final static int WAIT = 0;
	final static int GO = 1;
	final static int SENSING = 2;
	final static int GATHERING = 3;
	final static int BACK = 4;

	final static short NORTH = 0;
	final static short EAST = 1;
	final static short SOUTH = 2;
	final static short WEST = 3;

	final static double CONSUMPTION = 0.06;

	int id;
	double x,y,z;
	double battery;
	int state;
	//int area;
	int initX, initY;;
	int discover;

	short direction;
	double speed;
	double firstMove;
	double arrivalTime;
	double lapseTime;

	Udp1 udp;


	Drone(int id, int initX, int initY){
		this.id = id;
		this.initX = initX;
		this.initY = initY;
		//this.area = area;
		x = 0.0;
		y = 0.0;
		battery = 100.0;
		state = WAIT;
		firstMove = Math.sqrt(Math.pow(initX - x, 2) + Math.pow(initY - y, 2));
		arrivalTime = firstMove / speed;
		lapseTime = 0.0;

		udp = new Udp1(id);
		udp.makeMulticastSocket() ;
		udp.startListener() ;
	}

	void move(double simTime) {
		battery -= CONSUMPTION * simTime;
		lapseTime += simTime;

		if(battery < 10.0) state = BACK;

		switch(state) {
		case GO:
			double goTheta = Math.atan2(initY, initX);
			double goDistance = speed * simTime;
			x += goDistance * Math.cos(goTheta);
			y += goDistance * Math.sin(goTheta);

			if(lapseTime >= arrivalTime){
				x = initX;
				y = initY;
				lapseTime = 0.0;
				state = SENSING;
				direction = SOUTH;
			}
			break;
		case SENSING:
			if(lapseTime >= 3.0) {
				switch(direction) {
				case NORTH:
					y += 30;
					if(y >= initY) direction = EAST;
					break;
				case EAST:
					x += 30;
					if(y >= initY) direction = SOUTH;
					else direction = NORTH;
					break;
				case SOUTH:
					y -= 30;
					if(y <= initY - 240) direction = EAST;
					break;

				case WEST: break;
				default: break;

				}
				lapseTime = 0.0;
			}
			if(x >= initX + 240 && y <= initY - 240) state = BACK;

			break;
		case GATHERING:

			break;
		case BACK:
			double backTheta = Math.atan2(initY, initX);
			double backDistance = speed * simTime;
			x -= backDistance * Math.cos(backTheta);
			y -= backDistance * Math.sin(backTheta);
			if(x <= 0 && y <= 0) {
				x = 0;
				y = 0;
				state = WAIT;
			}
			break;
		default:
			break;
		}
	}

	void dataGet(int[][] area){
		if(state == SENSING || state == GATHERING) {
			discover = area[(int)(x / 30) - 1][(int)(y / 30) - 1];
			udp.sendMsgs(discover, x, y, battery, EDGE_SERVER);
			byte[] rcvData = udp.lisner.getData();
			if(rcvData != null) {
				String str = new String(rcvData);
				if(str.equals("0")) state = GATHERING;
				udp.lisner.resetData();
			}
		}
	}


}
