package sisaku;

public class Main{
	public static void main(String args[]) {

		double simTime = 0.1;
		double endTime = 15;
		double lapseTime = 0;

		long sleepTime = (long) (simTime * 1000);

		int[][] field = new int[24][24];
		for(int i = 0; i < 24; i++) {
			for(int j = 0; j < 24; j++) {
				field[i][j] = 0;
			}
		}


		int[][] area = new int[9][2];
		int x = 0, y = 0;
		for(int i = 0; i < 9; i++) {
			if(i % 3 == 0) {
				x = 0;
				y += 240;
			}
			area[i][0] = x;
			area[i][1] = y;
			x += 240;
		}

		Drone[] drone = new Drone[9];

		for(int i = 0; i < 9; i++) {
			drone[i] = new Drone(i + 1, area[i][0], area[i][1]);
		}

		EdgeServer edgeServer = new EdgeServer();

		while(lapseTime < endTime) {

			for(int i = 0; i < 9; i++) {
				drone[i].move(simTime);
				drone[i].dataGet(field);
			}


			lapseTime += simTime;
			System.out.println(lapseTime);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}


	}
}