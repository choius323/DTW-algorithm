import java.lang.Math;
import java.util.Random;;

public class DTW {
	//fileds
	static final int SIZE = 5, MAXNUM = 5;
	int[] X; // �ð迭 ������1
	int[] Y; // �ð迭 ������2
	int[][] dtw; // �Ÿ� ����
	
	//mothods
	public void makeData() { // �׽�Ʈ�� ������ ����
		X = new int[SIZE];
		Y = new int[SIZE]; // �� �������� ũ�� ����
		Random rand = new Random();
		for(int i=0;i<X.length;i++) { 
			X[i] = rand.nextInt(MAXNUM);
			Y[i] = rand.nextInt(MAXNUM);
		}
	}
	
	public void makeMatrix() { // �ʱ� �Ÿ� ���� �޼ҵ�
		int i,j;
		dtw = new int[X.length][Y.length];
		for(i=0;i<X.length;i++) {
			for(j=0;j<X.length;j++) {
				dtw[i][j] = Math.abs(X[i]-Y[j]); // �� �� ���� �Ÿ� ����
			}
		}
	}
	
	public void warpingDistance() { // DTW ����
		int i, j;
		for(i=1;i<X.length;i++) { // ù��° �� warping distance
			dtw[i][0] = dtw[i-1][0] + dtw[i][0];
		}
		for(j=1;j<Y.length;j++) { // ù��° �� warping distance 
			dtw[0][j] = dtw[0][j-1] + dtw[0][j];
		}
		for(i=1;i<X.length;i++) { // �� warping distance ���
			for(j=1;j<Y.length;j++) {
				dtw[i][j] = dtw[i][j]
						+ min(dtw[i-1][j], dtw[i][j-1], dtw[i-1][j-1]);
			}
		}
	}
	
	public int min(int a, int b, int c) { //�ּҰ� ���
		return Math.min(a, Math.min(b, c));
	}
	
	public void result() { // ��� ���
		int i=0,j=0;
		System.out.print("\t");
		
		for(i=0;i<X.length;i++) // ������1 �� ���
			System.out.print(X[i] + "\t");
		System.out.println();
		
		for(j=0;j<Y.length;j++) {
			for(i=0;i<X.length;i++) {
				if(i==0) {
					System.out.print(Y[j] + "\t"); // ������2 �� ���
				}
				System.out.print(dtw[i][j] + "\t"); // DTW ��� �� �Ÿ� ���
			}
			System.out.println();
		}
	}
	
	public static void main(String args[]) {
		DTW start = new DTW();
		start.makeData();
		start.makeMatrix();
		start.result(); // DTW ���� �� ���
		System.out.println();
		
		start.warpingDistance(); 
		start.result(); // DTW ���� �� ���
	}
}
