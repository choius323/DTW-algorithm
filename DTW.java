import java.lang.Math;
import java.util.Random;;

public class DTW {
	//fileds
	static final int SIZE = 5, MAXNUM = 5;
	int[] X; // 시계열 데이터1
	int[] Y; // 시계열 데이터2
	int[][] dtw; // 거리 저장
	
	//mothods
	public void makeData() { // 테스트용 데이터 생성
		X = new int[SIZE];
		Y = new int[SIZE]; // 두 데이터의 크기 같음
		Random rand = new Random();
		for(int i=0;i<X.length;i++) { 
			X[i] = rand.nextInt(MAXNUM);
			Y[i] = rand.nextInt(MAXNUM);
		}
	}
	
	public void makeMatrix() { // 초기 거리 저장 메소드
		int i,j;
		dtw = new int[X.length][Y.length];
		for(i=0;i<X.length;i++) {
			for(j=0;j<X.length;j++) {
				dtw[i][j] = Math.abs(X[i]-Y[j]); // 두 점 사이 거리 저장
			}
		}
	}
	
	public void warpingDistance() { // DTW 실행
		int i, j;
		for(i=1;i<X.length;i++) { // 첫번째 행 warping distance
			dtw[i][0] = dtw[i-1][0] + dtw[i][0];
		}
		for(j=1;j<Y.length;j++) { // 첫번째 열 warping distance 
			dtw[0][j] = dtw[0][j-1] + dtw[0][j];
		}
		for(i=1;i<X.length;i++) { // 총 warping distance 계산
			for(j=1;j<Y.length;j++) {
				dtw[i][j] = dtw[i][j]
						+ min(dtw[i-1][j], dtw[i][j-1], dtw[i-1][j-1]);
			}
		}
	}
	
	public int min(int a, int b, int c) { //최소값 계산
		return Math.min(a, Math.min(b, c));
	}
	
	public void result() { // 결과 출력
		int i=0,j=0;
		System.out.print("\t");
		
		for(i=0;i<X.length;i++) // 데이터1 값 출력
			System.out.print(X[i] + "\t");
		System.out.println();
		
		for(j=0;j<Y.length;j++) {
			for(i=0;i<X.length;i++) {
				if(i==0) {
					System.out.print(Y[j] + "\t"); // 데이터2 값 출력
				}
				System.out.print(dtw[i][j] + "\t"); // DTW 사용 후 거리 출력
			}
			System.out.println();
		}
	}
	
	public static void main(String args[]) {
		DTW start = new DTW();
		start.makeData();
		start.makeMatrix();
		start.result(); // DTW 실행 전 결과
		System.out.println();
		
		start.warpingDistance(); 
		start.result(); // DTW 실행 후 결과
	}
}
